package com.pochemuto.homealone.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
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

    @Configuration
    @ConditionalOnProperty(prefix = "bot", name = "webhook-path")
    public static class WebHookConfig {
        @Autowired
        private BotProperties botProperties;

        @Autowired
        private BotController botController;

        @Bean
        public WebHookBot webHookBot() {
            return new WebHookBot(botProperties.getWebhookUrl());
        }

        @Bean
        public TelegramBotsApi telegramBotsApi() throws TelegramApiException {
            var api = new TelegramBotsApi(DefaultBotSession.class, botController);
            log.info("Created webhook at port {} for url {}", botProperties.getLocalPort(),
                    botProperties.getWebhookUrl()
                            .replace(botProperties.getToken(), "<token>")
                            .replace(botProperties.getUsername(), "<username>")
            );
            return api;
        }

    }


}

