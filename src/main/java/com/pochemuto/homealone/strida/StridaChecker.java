package com.pochemuto.homealone.strida;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.bots.AbsSender;

import static java.util.function.Function.identity;

@Slf4j
@Component
public class StridaChecker {
    @Autowired
    private AbsSender messageSender;

    @Autowired
    private StridaItemRepository repository;

    @Scheduled(fixedDelay = 30 * 60 * 1000)
    public void check() {
    }

    public void changes(Consumer<MapDifference<Integer, Item>> processor) throws IOException {
        Map<Integer, Item> known = repository.findAll().stream()
                .collect(Collectors.toMap(Item::id, identity()));
        log.info("Known {} items", known.size());
        Map<Integer, Item> actual = getActual().stream()
                .collect(Collectors.toMap(Item::id, identity()));
        log.info("Actual {} items", actual.size());

        MapDifference<Integer, Item> difference = Maps.difference(known, actual);
        processor.accept(difference);
        repository.saveAll(actual.values());
    }

    public List<Item> getActual() throws IOException {
        Document document = Jsoup.connect("https://strida.ru/catalog/").get();
        Elements blocks = document.select(".catalog_block");
        return blocks.stream()
                .map(StridaChecker::parseBlock)
                .toList();
    }

    public static Item parseBlock(Element block) {
        var link = block.selectFirst("h3 > a");
        var description = block.selectFirst("h4").text();
        var price = Integer.parseInt(block.selectFirst(".price").text().split(" ")[0]);
        var availability = block.selectFirst(".availability").text();

        return new Item(
                extractId(link.attr("href")),
                link.text(),
                description,
                price,
                availability
        );
    }

    private static Integer extractId(String urlPart) {
        var matcher = Pattern.compile("id=(\\d+)").matcher(urlPart);
        if (!matcher.find()) {
            throw new RuntimeException("wrong url: " + urlPart);
        }
        return Integer.valueOf(matcher.group(1));
    }
}
