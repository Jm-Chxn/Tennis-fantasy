package com.tennisfantasy.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ClientRequest;


@Configuration
public class SportsRadarConfig {

    @Value("${sportsradar.api.key}")
    private String sportsradarApiKey;

    @Value("${sportsradar.api.base-url}")
    private String baseUrl;

    @Bean
    public WebClient sportsradarWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .filter((request, next) -> {
                    // Append the api_key query parameter automatically
                    String urlWithKey = request.url().toString();
                    if (!urlWithKey.contains("api_key=")) {
                        if (urlWithKey.contains("?")) {
                            urlWithKey += "&api_key=" + sportsradarApiKey;
                        } else {
                            urlWithKey += "?api_key=" + sportsradarApiKey;
                        }
                    }
                    return next.exchange(
                            ClientRequest.from(request)
                                    .url(java.net.URI.create(urlWithKey))
                                    .build()
                    );
                })
                .build();
    }
}
