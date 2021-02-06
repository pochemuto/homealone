package com.pochemuto.homealone.ikea;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Component
public class IkeaChecker {
    private static final String URL = "https://www.ikea.com/ru/ru/cat/posudomoechnye-mashiny-20825/";

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private List<IkeaListener> listeners;

    @Scheduled(fixedDelay = 30 * 60 * 1000)
    @Transactional
    public void check() throws IOException {
        var known = itemRepository.findAll()
                .stream()
                .collect(toMap(Item::getName, identity()));

        log.info("Known {} items: {}", known.size(), display(known.values()));

        var items = getActual();

        itemRepository.saveAll(items);

        var actual = items.stream()
                .collect(toMap(Item::getName, identity()));

        var difference = Maps.difference(actual, known);

        if (!difference.areEqual()) {
            log.info("Found difference, new names: {}, removed: {}",
                    difference.entriesOnlyOnLeft(), difference.entriesOnlyOnRight());

            for (IkeaListener listener : listeners) {
                var added = List.copyOf(difference.entriesOnlyOnLeft().values());
                var removed = List.copyOf(difference.entriesOnlyOnRight().values());
                listener.onItemsChanged(added, removed);
            }
        } else {
            log.info("No difference found");
        }

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
        item.setName(element.selectFirst(".range-revamp-header-section__title--small").text());
        item.setPrice(parsePrice(element.selectFirst(".range-revamp-price__integer").text()));
        item.setReduced(!element.select(".range-revamp-compact-price-package__last-chance").isEmpty());
        return item;
    }

    public static BigDecimal parsePrice(String price) {
        return new BigDecimal(price.replaceAll(" ", "")).setScale(2, RoundingMode.CEILING);
    }

    public List<Item> getActual() throws IOException {
        log.info("Checking {}", URL);
        Document page = Jsoup.connect(URL).get();

        Instant now = Instant.now();

        List<Item> items = page.select(".range-revamp-compact-price-package")
                .stream()
                .map(this::parse)
                .peek(item -> item.setLastSeen(now))
                .collect(Collectors.toList());

        log.info("Found {} items: {}", items.size(), items);
        return items;
    }

}

