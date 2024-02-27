package com.pochemuto.homealone.bot;

import java.io.IOException;

import javax.annotation.Nullable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.pochemuto.homealone.spring.ApplicationProperties;
import com.pochemuto.homealone.strida.Bike;
import com.pochemuto.homealone.strida.StridaChecker;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TelegramBot implements Receiver {

    @Autowired
    private AbsSender messageSender;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StridaChecker strida;

    @Autowired
    private MeterRegistry registry;

    @Autowired
    private ApplicationProperties properties;

    @Override
    public void onUpdateReceived(Update update) {
        registry.timer("bot.update").record(() -> {
            if (update.hasMessage() && update.getMessage().hasText()) {
                var text = update.getMessage().getText().toLowerCase().strip();
                switch (text) {
                    case "/ping", "ping" -> pong(update);
                    case "/version" -> version(update);
                    case "/strida" -> strida(update);
                }
            }
        });
    }

    private void strida(Update update) {
        try {
            var actual = strida.getActual();
            var sb = new StringBuilder("Велики:");
            for (Bike bike : actual) {
                sb.append("\n🚲 [").append(bike.title()).append("]")
                        .append("(https://strida.ru/catalog/?id=").append(bike.id()).append(")")
                        .append(", ")
                        .append(bike.description())
                        .append(" `").append(bike.price()).append(" руб`")
                        .append(" • ").append(bike.availability());
            }

            messageSender.execute(SendMessage.builder()
                    .chatId(String.valueOf(update.getMessage().getChatId()))
                    .parseMode("markdown")
                    .disableWebPagePreview(true)
                    .text(sb.toString())
                    .build()
            );

        } catch (IOException | TelegramApiException e) {
            handleError(update.getMessage().getChatId(), e);
        }
    }

    private void version(Update update) {
        var version = properties.getVersion();
        if (version.startsWith("$")) {
            version = "<undefined>";
        }
        sendMessage(update.getMessage().getChatId(), version);
    }

    private void pong(Update update) {
        log.info("Sending pong");
        sendMessage(update.getMessage().getChatId(), "pong");
        log.info("Pong!");
    }

    private void handleError(long chatId, Throwable e) {
        log.error("Error", e);
        sendMessage(chatId, "*Произошла ошибка:* " + e);
    }

    @Nullable
    private Integer sendMessage(long chatId, String text) {
        try {
            Message response = messageSender.execute(SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text(text)
                    .parseMode("markdown")
                    .build()
            );
            return response.getMessageId();
        } catch (TelegramApiException telegramApiException) {
            log.error("Cannot send reply", telegramApiException);
        }
        return null;
    }
}
