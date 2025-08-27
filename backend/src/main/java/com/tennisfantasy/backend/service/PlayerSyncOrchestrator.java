package com.tennisfantasy.backend.service;

import com.tennisfantasy.backend.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.List;

@Service
public class PlayerSyncOrchestrator {
    
    private final PlayerFetchingThingy playerFetcher;
    private final SupabaseService supabaseService;
    
    @Autowired
    public PlayerSyncOrchestrator(PlayerFetchingThingy playerFetcher, SupabaseService supabaseService) {
        this.playerFetcher = playerFetcher;
        this.supabaseService = supabaseService;
    }
    
    public Mono<String> syncPlayersFromSportsRadar(String locale) {
        System.out.println("🚀 Starting sync process for locale: " + locale);
        
        return Mono.fromCallable(() -> {
            try {
                System.out.println("📡 Attempting to fetch players from SportsRadar...");
                List<Player> players = playerFetcher.fetchAndParsePlayers(locale);
                System.out.println("✅ Successfully fetched " + players.size() + " players from SportsRadar");
                return players;
            } catch (Exception e) {
                System.err.println("❌ Error fetching players: " + e.getMessage());
                System.err.println("🔍 Stack trace:");
                e.printStackTrace();
                throw new RuntimeException("Failed to fetch players from SportsRadar: " + e.getMessage(), e);
            }
        })
        .flatMap(players -> {
            System.out.println("💾 Attempting to save " + players.size() + " players to Supabase...");
            return supabaseService.createPlayersIfNotExists(players);
        })
        .onErrorReturn("Failed to sync players - check server logs for details");
    }
}
