package com.tennisfantasy.backend.service;

import com.tennisfantasy.backend.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
@EnableScheduling
public class ScheduledDataService {

    @Autowired
    private SupabaseService supabaseService;

    // Runs every 5 minutes (300000 milliseconds)
    @Scheduled(fixedRate = 300000)
    public void syncData() {
        System.out.println("🔄 Scheduled sync: Updating Supabase with latest data...");
        
        List<Player> currentPlayers = Arrays.asList(
            new Player("Novak", "Djokovic", "Serbia", 1, new BigDecimal("15.50")),
            new Player("Carlos", "Alcaraz", "Spain", 2, new BigDecimal("14.75")),
            new Player("Daniil", "Medvedev", "Russia", 3, new BigDecimal("13.25")),
            new Player("Jannik", "Sinner", "Italy", 4, new BigDecimal("12.80")),
            new Player("Andrey", "Rublev", "Russia", 5, new BigDecimal("11.90")),
            new Player("Roger", "Dai", "China", 500, new BigDecimal("2.00"))
        );

        supabaseService.createPlayers(currentPlayers)
                .subscribe(
                    players -> System.out.println("✅ Scheduled sync completed: " + players.size() + " players updated"),
                    error -> System.out.println("❌ Scheduled sync failed: " + error.getMessage())
                );
    }
}
