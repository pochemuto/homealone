package com.pochemuto.homealone;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAdminServer
@EnableScheduling
public class HomealoneApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomealoneApplication.class, args);
    }

}
