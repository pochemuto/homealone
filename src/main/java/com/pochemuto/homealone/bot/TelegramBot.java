package com.pochemuto.homealone.bot;

import java.io.IOException;
import java.util.List;

import com.pochemuto.homealone.ikea.IkeaChecker;
import com.pochemuto.homealone.ikea.Item;
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
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private ApplicationProperties properties;

    @Autowired
    private IkeaChecker ikeaChecker;

    //region Settings
    @Override
    public String getBotUsername() {
        return properties.getBot().getUsername();
    }

    @Override
    public String getBotToken() {
        return properties.getBot().getToken();
    }
    //endregion

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var text = update.getMessage().getText();
            switch (text) {
                case "/ikea", "/икея" -> ikea(update);
            }
        }
    }

    private void ikea(Update update) {
        try {
            var items = ikeaChecker.getActual();
            sendMessage(update, formatItems(items));
        } catch (IOException e) {
            handleError(update, e);
        }
    }

    private String formatItems(List<Item> items) {
        var sb = new StringBuilder();
        for (Item item : items) {
            sb.append("• ").append(item.getName()).append(": ").append(item.getPrice()).append("\n");
        }
        return sb.toString();
    }

    private void handleError(Update update, Throwable e) {
            log.error("Error", e);
            sendMessage(update, "Произошла ошибка: " + e);
    }

    private void sendMessage(Update update, String text) {
        try {
            sendApiMethod(SendMessage.builder()
                    .chatId(String.valueOf(update.getMessage().getChatId()))
                    .text(text)
                    .build()
            );
        } catch (TelegramApiException telegramApiException) {
            log.error("Cannot send reply", telegramApiException);
        }
    }
}
