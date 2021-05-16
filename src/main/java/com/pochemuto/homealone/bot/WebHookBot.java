package com.pochemuto.homealone.bot;

import java.util.Objects;

import javax.annotation.PostConstruct;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

@Slf4j
public class WebHookBot extends SpringWebhookBot {

    @Autowired
    private BotProperties properties;

    @Autowired
    private Receiver receiver;

    public WebHookBot(String webhookUrl) {
        super(SetWebhook.builder()
                .url(Preconditions.checkNotNull(webhookUrl))
                .build());
    }

    @PostConstruct
    public void postConstruct() {
        log.info("Registering as webhook bot {} with token {}",
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

    @Override
    public String getBotPath() {
        return properties.getWebhookPath();
    }
    //endregion

    @Override
    @SuppressWarnings("rawtypes")
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        receiver.onUpdateReceived(update);
        return null;
    }
}
