package com.pochemuto.homealone.ikea;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.base.Equivalence;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Component
@CacheConfig(cacheNames = "ikea")
public class IkeaChecker {
    private static final String URL = "https://www.ikea.com/ru/ru/cat/posudomoechnye-mashiny-20825/";
    private static final Pattern ID_PATTERN = Pattern.compile("(?<id>\\d+)/$");

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private List<IkeaListener> listeners;

    @Timed(value = "ikea.check")
    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void check() throws IOException {
        var known = itemRepository.findAll()
                .stream()
                .collect(toMap(Item::getId, identity()));

        log.info("Known {} items: {}", known.size(), display(known.values()));

        var items = getActual();

        var actual = items.stream()
                .collect(toMap(Item::getId, identity()));

        var difference = Maps.difference(known, actual, new Equivalence<>() {
            @Override
            protected boolean doEquivalent(@Nonnull Item a, @Nonnull Item b) {
                return Objects.equals(a.getId(), b.getId())
                        && Objects.equals(a.getName(), b.getName())
                        && Objects.equals(a.getPrice(), b.getPrice());
            }

            @Override
            protected int doHash(@Nonnull Item item) {
                return Objects.hash(item.getId(), item.getName(), item.getPrice());
            }
        });


        if (!difference.areEqual()) {
            log.info("Found difference, new names: {}, removed: {}, changed: {}",
                    difference.entriesOnlyOnRight(), difference.entriesOnlyOnLeft(), difference.entriesDiffering());

            for (IkeaListener listener : listeners) {
                var added = List.copyOf(difference.entriesOnlyOnRight().values());
                var removed = List.copyOf(difference.entriesOnlyOnLeft().values());
                var changed = List.copyOf(difference.entriesDiffering().values());
                listener.onItemsChanged(added, removed, changed);
            }
        } else {
            log.info("No difference found");
        }

        itemRepository.saveAll(items);
    }

    private static String display(Collection<Item> items) {
        if (items.isEmpty()) {
            return "<empty>";
        }

        return items.stream()
                .map(Item::getName)
                .sorted()
                .collect(joining(", "));
    }

    private Item parse(Element element) {
        var item = new Item();
        item.setId(parseId(element.selectFirst("a").attr("href")));
        item.setName(element.selectFirst(".range-revamp-header-section__title--small").text());
        item.setPrice(parsePrice(element.selectFirst(".range-revamp-price__integer").text()));
        item.setReduced(!element.select(".range-revamp-compact-price-package__last-chance").isEmpty());
        return item;
    }

    public static BigDecimal parsePrice(String price) {
        return new BigDecimal(price.replaceAll(" ", "")).setScale(2, RoundingMode.CEILING);
    }

    public static int parseId(String href) {
        Matcher matcher = ID_PATTERN.matcher(href);
        boolean found = matcher.find();
        Preconditions.checkState(found, "id not found in %s", href);
        return Integer.parseInt(matcher.group("id"));
    }

    @Cacheable
    @Timed("ikea.actual")
    public List<Item> getActual() throws IOException {
        log.info("Checking {}", URL);
        Document page = Jsoup.connect(URL).get();

        Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);

        List<Item> items = page.select(".range-revamp-product-compact__bottom-wrapper")
                .stream()
                .map(this::parse)
                .peek(item -> item.setLastSeen(now))
                .collect(Collectors.toList());

        log.info("Found {} items: {}", items.size(), items);
        return items;
    }

}

