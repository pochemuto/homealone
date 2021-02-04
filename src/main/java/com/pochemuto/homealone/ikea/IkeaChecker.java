package com.pochemuto.homealone.ikea;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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
        var known = itemRepository.findNames();
        log.info("Known {} items: {}", known.size(), known);

        log.info("Checking {}", URL);
        Document page = Jsoup.connect(URL).get();

        Instant now = Instant.now();
        List<Item> items = page.select(".range-revamp-compact-price-package")
                .stream()
                .map(this::parse)
                .peek(item -> item.setLastSeen(now))
                .collect(Collectors.toList());

        log.info("Found {} items: {}", items.size(), items);

        itemRepository.saveAll(items);

        var names = items.stream()
                .map(Item::getName)
                .collect(Collectors.toSet());

        var newNames = Sets.difference(names, known);
        if (!newNames.isEmpty()) {
            log.info("Found {} new names: {}", newNames.size(), newNames);
            for (IkeaListener listener : listeners) {
                listener.onNewItems(items);
            }
        } else {
            log.info("No new items found");
        }

        var disappeared = Sets.difference(known, names);
        if (!disappeared.isEmpty()) {
            log.info("Disappeared {} items: {}", disappeared.size(), disappeared);
        }
    }

    private Item parse(Element element) {
        var item = new Item();
        item.setName(element.selectFirst(".range-revamp-header-section__title--small").text());
        item.setPrice(parsePrice(element.selectFirst(".range-revamp-price__integer").text()));
        item.setReduced(!element.select(".range-revamp-compact-price-package__last-chance").isEmpty());
        return item;
    }

    public static BigDecimal parsePrice(String price) {
        return new BigDecimal(price.replaceAll(" ", ""));
    }
}

