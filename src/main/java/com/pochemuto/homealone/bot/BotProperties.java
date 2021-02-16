package com.pochemuto.homealone.bot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties("bot")
@Component
public class BotProperties {
    private String token;
    private String username;
}
