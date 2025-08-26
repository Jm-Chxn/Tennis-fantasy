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
            System.out.println("🚀 Auto-importing test data to Supabase...");
            
            List<Player> testPlayers = Arrays.asList(
                new Player("Novak", "Djokovic", "Serbia", 1, new BigDecimal("15.50")),
                new Player("Carlos", "Alcaraz", "Spain", 2, new BigDecimal("14.75")),
                new Player("Daniil", "Medvedev", "Russia", 3, new BigDecimal("13.25")),
                new Player("Jannik", "Sinner", "Italy", 4, new BigDecimal("12.80")),
                new Player("Andrey", "Rublev", "Russia", 5, new BigDecimal("11.90")),
                new Player("Roger", "Dai", "China", 500, new BigDecimal("2.00"))
            );

            supabaseService.createPlayers(testPlayers)
                    .subscribe(
                        players -> System.out.println("✅ Successfully auto-imported " + players.size() + " players to Supabase!"),
                        error -> System.out.println("❌ Failed to auto-import players: " + error.getMessage())
                    );
        };
    }
}
