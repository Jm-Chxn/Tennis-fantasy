package com.tennisfantasy.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class SportsRadarConfig {

    @Value("${sportsradar.api.key}")
    private String sportsradarApiKey;

    @Value("${sportsradar.api.base-url}")
    private String baseUrl;

    @Bean
    public WebClient sportsradarWebClient() {
        return WebClient.builder().baseUrl(baseUrl).defaultHeader("apikey", sportsradarApiKey).build();
    }
}