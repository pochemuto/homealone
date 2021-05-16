package com.pochemuto.homealone.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Slf4j
@Component
public class TelegramHealthIndicator extends AbstractHealthIndicator {

    @Autowired
    private AbsSender bot;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        log.debug("Checking bot status");
        User botUser = bot.getMe();
        log.debug("Registered as {}", botUser.getUserName());

        builder.up()
                .withDetail("login", botUser.getUserName())
                .withDetail("id", botUser.getId());
    }
}
