package com.pochemuto.homealone.marafon;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties("marafon")
@Component
public class MarafonProperties {
    private String login;
    private String password;
}
