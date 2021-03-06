package com.pochemuto.homealone.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ConditionalOnProperty(value = "application.scheduling.enabled", havingValue = "true", matchIfMissing = true)
@EnableScheduling
public class SchedulingConfig {
}
