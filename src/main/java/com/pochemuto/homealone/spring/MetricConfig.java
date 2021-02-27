package com.pochemuto.homealone.spring;

import io.micrometer.core.aop.TimedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class MetricConfig {
    @Bean
    public TimedAspect timedAspect() {
        return new TimedAspect();
    }
}
