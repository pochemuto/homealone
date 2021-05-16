package com.pochemuto.homealone.bot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties("bot")
@Component
public class BotProperties {
    public static final String EMPTY_WEBHOOK = "http://localhost";
    private String token;
    private String username;
    private String webhookPath = EMPTY_WEBHOOK;
    private String webhookUrl;
}
