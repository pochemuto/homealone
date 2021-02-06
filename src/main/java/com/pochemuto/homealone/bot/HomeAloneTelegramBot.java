package com.pochemuto.homealone.bot;

import com.pochemuto.homealone.spring.ApplicationProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class HomeAloneTelegramBot extends TelegramLongPollingBot {
    @Autowired
    private ApplicationProperties properties;

    @Override
    public String getBotUsername() {
        return properties.getBot().getUsername();
    }

    @Override
    public String getBotToken() {
        return properties.getBot().getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            log.info("Message received {}", update);
            var message = SendMessage.builder()
                    .chatId(String.valueOf(update.getMessage().getChatId()))
                    .text("Отвечаю тебе: " + update.getMessage().getText())
                    .build();

            sendApiMethod(message);
        } catch (TelegramApiException e) {
            log.error("Error when sending message", e);
        }
    }
}
