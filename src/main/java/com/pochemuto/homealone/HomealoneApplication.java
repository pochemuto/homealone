package com.pochemuto.homealone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ConfigurationPropertiesScan
@PropertySource(name = "secret", value = {"file:${SECRET_PROPERTIES:/run/secrets/secret-properties}"})
public class HomealoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomealoneApplication.class, args);
    }
}
