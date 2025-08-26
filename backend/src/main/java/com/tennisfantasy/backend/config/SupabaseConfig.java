package com.tennisfantasy.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SupabaseConfig {
    
    @Value("${supabase.url}")
    private String supabaseUrl;
    
    @Value("${supabase.anon.key}")
    private String supabaseAnonKey;
    
    @Bean
    public WebClient supabaseWebClient() {
        return WebClient.builder()
                .baseUrl(supabaseUrl + "/rest/v1")
                .defaultHeader("apikey", supabaseAnonKey)
                .defaultHeader("Authorization", "Bearer " + supabaseAnonKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
    
    public String getSupabaseUrl() {
        return supabaseUrl;
    }
    
    public String getSupabaseAnonKey() {
        return supabaseAnonKey;
    }
}
