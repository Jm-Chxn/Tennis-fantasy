package com.tennisfantasy.backend.config;

import com.tennisfantasy.backend.model.Player;
import com.tennisfantasy.backend.service.SupabaseService;
import com.tennisfantasy.backend.service.PlayerFetchingThingy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Autowired
    private PlayerFetchingThingy playerFetchingThingy;
    
    @Value("${sportsradar.api.key}")
    private String apiKey;

    @Bean
    public ApplicationRunner initializeData() {
        return args -> {
            System.out.println("🎾 TESTING SPORTSRADAR API CONNECTION...");
            System.out.println("� Using API Key: " + apiKey.substring(0, 8) + "...");
            
            // Test SportsRadar API first
            try {
                playerFetchingThingy.fetchAndSavePlayers("en", apiKey);
                System.out.println("✅ SportsRadar API test completed! Check the generated CSV file.");
            } catch (Exception e) {
                System.err.println("❌ SportsRadar API test failed: " + e.getMessage());
                e.printStackTrace();
                
                // Fallback to hardcoded data if API fails
                System.out.println("🔄 Falling back to hardcoded player data...");
                loadHardcodedPlayers();
            }
        };
    }
    
    private void loadHardcodedPlayers() {
        System.out.println("🚀 Auto-import: Loading fallback player data...");
        
        List<Player> initialPlayers = Arrays.asList(
            new Player("Novak", "Djokovic", "Serbia", 1, new BigDecimal("15.50")),
            new Player("Carlos", "Alcaraz", "Spain", 2, new BigDecimal("14.75")),
            new Player("Daniil", "Medvedev", "Russia", 3, new BigDecimal("13.25")),
            new Player("Jannik", "Sinner", "Italy", 4, new BigDecimal("12.80")),
            new Player("Andrey", "Rublev", "Russia", 5, new BigDecimal("11.90")),
            new Player("Roger", "Dai", "China", 500, new BigDecimal("2.00")),
            new Player("dandan", "hu", "china", 499, new BigDecimal("3")),
            new Player("ivan", "luo", "china", 498, new BigDecimal("3.5"))
        );

        supabaseService.createPlayersIfNotExists(initialPlayers)
                .subscribe(
                    result -> System.out.println("✅ Fallback data loaded: " + result),
                    error -> System.out.println("❌ Fallback failed: " + error.getMessage())
                );
    }
}
