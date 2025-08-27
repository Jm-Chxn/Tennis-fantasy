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
        );

        supabaseService.createPlayersIfNotExists(currentPlayers)
                .subscribe(
                    result -> System.out.println("✅ Scheduled sync completed: " + result),
                    error -> System.out.println("❌ Scheduled sync failed: " + error.getMessage())
                );
    }
}
