package com.tennisfantasy.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for SportsRadar Tennis API.
 * 
 * This holds the API key and base URL for making requests to SportsRadar.
 * The API key should be set via environment variable SPORTRADAR_API_KEY
 */
@Configuration
public class SportsRadarConfig {

    @Value("${sportradar.api.key}")
    private String apiKey;

    @Value("${sportradar.api.base-url}")
    private String baseUrl;

    public String getApiKey() {
        return apiKey;
    }

    /**
     * Build full URL for a given endpoint.
     * Example: buildUrl("/rankings.json") ->
     * "https://api.sportradar.com/tennis/trial/v3/en/rankings.json"
     */
    public String buildUrl(String endpoint) {
        return baseUrl + endpoint;
    }
}
