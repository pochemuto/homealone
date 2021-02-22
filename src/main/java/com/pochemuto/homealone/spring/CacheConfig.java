package com.pochemuto.homealone.spring;

import java.time.Duration;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@EnableCaching
public class CacheConfig {
    public static final String IKEA = "ikea";

    @Autowired
    private CachesProperties cachesProperties;

    @Bean
    public Cache ikeaCache() {
        return new CaffeineCache(IKEA,
                Caffeine.newBuilder()
                        .expireAfterWrite(cachesProperties.getIkea().getExpire())
                        .build()
        );
    }

    @Data
    @ConfigurationProperties("cache")
    @Component
    public static class CachesProperties {
        private Cache ikea;

        @Data
        public static class Cache {
            private Duration expire;
        }
    }
}
