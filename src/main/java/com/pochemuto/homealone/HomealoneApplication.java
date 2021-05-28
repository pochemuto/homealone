package com.pochemuto.homealone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class HomealoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomealoneApplication.class, args);
    }
}
