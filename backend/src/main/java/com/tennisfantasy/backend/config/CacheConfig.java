package com.tennisfantasy.backend.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Cache configuration for the application.
 * 
 * We use Caffeine (a high-performance Java caching library) to cache:
 * - SportsRadar API responses (to avoid hitting rate limits)
 * - Frequently accessed data like rankings
 * 
 * Cache expires after 10 minutes by default.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(500) // Max 500 entries
                .expireAfterWrite(10, TimeUnit.MINUTES) // Expire after 10 minutes
                .recordStats()); // Track hit/miss statistics
        return cacheManager;
    }
}
