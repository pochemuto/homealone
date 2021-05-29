package com.pochemuto.homealone.spring;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties("application")
@Component
public class ApplicationProperties {
    private Scheduling scheduling;
    private String version;

    @Data
    public static class Scheduling {
        boolean enabled;
    }
}
