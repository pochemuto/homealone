package com.pochemuto.homealone.bot;

import java.util.List;
import java.util.Objects;

import com.google.common.base.Preconditions;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

@Slf4j
public class WebHookBot extends SpringWebhookBot {

    @Autowired
    private BotProperties properties;

    @Lazy
    @Autowired
    private List<Receiver> receivers;

    public WebHookBot(String webhookUrl) {
        super(SetWebhook.builder()
                .url(Preconditions.checkNotNull(webhookUrl))
                .build());
    }

    @PostConstruct
    public void postConstruct() {
        log.info("Registering as webhook bot {} with token {}",
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

    @Override
    public String getBotPath() {
        return properties.getWebhookPath();
    }
    // endregion

    @Override
    @SuppressWarnings("rawtypes")
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        for (Receiver receiver : receivers) {
            receiver.onUpdateReceived(update);
        }
        return null;
    }
}
