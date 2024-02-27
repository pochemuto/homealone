package com.pochemuto.homealone.bot;

import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import jakarta.annotation.PostConstruct;

@Slf4j
public class LongPollingBotImpl extends TelegramLongPollingBot {

    @Autowired
    private BotProperties properties;

    @Lazy
    @Autowired
    private List<Receiver> receivers;

    @PostConstruct
    public void postConstruct() {
        log.info("Registering as long polling bot {} with token {}...",
                getBotUsername(),
                Objects.requireNonNullElse(getBotToken(), "null").substring(0, 4));
    }

    // region Settings
    @Override
    public String getBotUsername() {
        return properties.getUsername();
    }

    @Override
    public String getBotToken() {
        return properties.getToken();
    }
    // endregion

    @Override
    public void onUpdateReceived(Update update) {
        for (Receiver receiver : receivers) {
            receiver.onUpdateReceived(update);
        }
    }

}
