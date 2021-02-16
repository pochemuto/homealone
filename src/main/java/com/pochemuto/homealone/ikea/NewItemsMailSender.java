package com.pochemuto.homealone.ikea;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.MapDifference.ValueDifference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NewItemsMailSender implements IkeaListener {

    @Autowired
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private JavaMailSender mailSender;

    @Override
    public void onItemsChanged(List<Item> added, List<Item> removed, List<ValueDifference<Item>> change) {
        var msg = new SimpleMailMessage();
        msg.setTo("pochemuto@gmail.com");
        msg.setSubject("Обнаружено %s новых товаров!".formatted(added.size()));
        var text = added.stream()
                .map(item -> item.getName() + " : " + item.getPrice() + "руб.")
                .collect(Collectors.joining("\n"));
        msg.setText(text);

        mailSender.send(msg);
        log.info("Message sent");
    }
}
