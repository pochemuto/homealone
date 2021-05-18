package com.pochemuto.homealone.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultWebhook;

@Configuration
public class BotConfiguration {
    @Autowired
    private BotProperties botProperties;

    @Bean
    @Primary
    @ConditionalOnProperty(
            prefix = "bot", name = "webhook-path", havingValue = BotProperties.EMPTY_WEBHOOK, matchIfMissing = true)
    public LongPollingBotImpl longPollingBot() {
        return new LongPollingBotImpl();
    }

    @Bean
    @ConditionalOnProperty(prefix = "bot", name = "webhook-path")
    public WebHookBot webHookBot() {
        return new WebHookBot(botProperties.getWebhookUrl());
    }

    @Bean
    @ConditionalOnProperty(prefix = "bot", name = "webhook-path")
    public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
        var webhook = new DefaultWebhook();
        webhook.setInternalUrl("http://localhost:8081");
        return new TelegramBotsApi(DefaultBotSession.class, webhook);
    }

}

