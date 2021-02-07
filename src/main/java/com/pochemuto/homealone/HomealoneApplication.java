package com.pochemuto.homealone;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@EnableAdminServer
@ConfigurationPropertiesScan
@PropertySource(name = "secret", value = {"file:/run/secrets/secret-properties"}, ignoreResourceNotFound = true)
@PropertySource(name = "secret", value = {"file:${SECRET_PROPERTIES}"}, ignoreResourceNotFound = true)
public class HomealoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomealoneApplication.class, args);
    }

}
