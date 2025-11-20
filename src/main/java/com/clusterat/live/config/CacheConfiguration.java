package com.clusterat.live.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache Configuration with Caffeine
 */
@Configuration
@EnableCaching
public class CacheConfiguration {

    /**
     * Configures the cache manager with Caffeine
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("searxng_searches");

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(100)                           // Maximum of 100 entries in cache
                .expireAfterWrite(1, TimeUnit.HOURS)       // Expire after 1 hour of write
                .expireAfterAccess(30, TimeUnit.MINUTES)   // Expire after 30 minutes of access
                .recordStats()                                          // Record statistics
        );

        return cacheManager;
    }
}
