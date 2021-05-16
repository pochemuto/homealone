package com.pochemuto.homealone.bot;

import java.util.Objects;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class LongPollingBotImpl extends TelegramLongPollingBot {

    @Autowired
    private BotProperties properties;

    @Autowired
    private Receiver receiver;

    @PostConstruct
    public void postConstruct() {
        log.info("Registering as long polling bot {} with token {}...",
                getBotUsername(),
                Objects.requireNonNullElse(getBotToken(), "null").substring(0, 4)
        );
    }

    //region Settings
    @Override
    public String getBotUsername() {
        return properties.getUsername();
    }

    @Override
    public String getBotToken() {
        return properties.getToken();
    }
    //endregion

    @Override
    public void onUpdateReceived(Update update) {
        receiver.onUpdateReceived(update);
    }

}
