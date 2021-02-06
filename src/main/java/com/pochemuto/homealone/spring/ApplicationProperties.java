package com.pochemuto.homealone.spring;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "application")
@Component
public class ApplicationProperties {
    private Bot bot;

    @Data
    public static class Bot {
        private String token;
        private String username;
    }
}
