package com.tennisfantasy.backend.config;

import com.tennisfantasy.backend.model.Player;
import com.tennisfantasy.backend.service.SupabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DataInitializer {

    @Autowired
    private SupabaseService supabaseService;

    @Bean
    public ApplicationRunner initializeData() {
        return args -> {
            System.out.println("🚀 Auto-import: Loading initial player data...");
            
            List<Player> initialPlayers = Arrays.asList(
            );

            supabaseService.createPlayersIfNotExists(initialPlayers)
                    .subscribe(
                        result -> System.out.println("✅ Auto-import completed: " + result),
                        error -> System.out.println("❌ Auto-import failed: " + error.getMessage())
                    );
        };
    }
}
