package com.tennisfantasy.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TennisFantasyBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TennisFantasyBackendApplication.class, args);
    }

    @org.springframework.context.annotation.Bean
    public org.springframework.boot.CommandLineRunner confirmUsers(
            org.springframework.jdbc.core.JdbcTemplate jdbcTemplate,
            @org.springframework.beans.factory.annotation.Value("${supabase.jwt.secret}") String secret) {
        return args -> {
            System.out.println(">>> JWT Secret loaded. Length: " + (secret != null ? secret.length() : 0));
            try {
                jdbcTemplate
                        .execute("UPDATE auth.users SET email_confirmed_at = NOW() WHERE email_confirmed_at IS NULL");
                System.out.println(">>> Supabase users auto-confirmed successfully.");
            } catch (Exception e) {
                System.err.println(">>> Could not auto-confirm users: " + e.getMessage());
            }
        };
    }
}
