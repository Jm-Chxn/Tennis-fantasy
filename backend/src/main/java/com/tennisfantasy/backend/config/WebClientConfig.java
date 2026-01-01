package com.tennisfantasy.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration for WebClient - Spring's modern HTTP client.
 * 
 * WebClient is used to make HTTP requests to external APIs like SportsRadar.
 * It's non-blocking and more modern than RestTemplate.
 */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024)) // 10MB buffer for large responses
                .build();
    }
}
