package com.tennisfantasy.backend.service;

import com.tennisfantasy.backend.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {
    
    private final SupabaseService supabaseService;
    
    @Autowired
    public PlayerService(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }
    
    // Get all players
    public Mono<List<Player>> getAllPlayers() {
        return supabaseService.getAllPlayers();
    }
    
    // Get player by ID
    public Mono<Optional<Player>> getPlayerById(Long id) {
        return supabaseService.getPlayerById(id);
    }
    
    // Create new player
    public Mono<Player> createPlayer(Player player) {
        return supabaseService.createPlayer(player);
    }
    
    // Update existing player
    public Mono<Player> updatePlayer(Long id, Player playerDetails) {
        return supabaseService.updatePlayer(id, playerDetails);
    }
    
    // Delete player
    public Mono<Void> deletePlayer(Long id) {
        return supabaseService.deletePlayer(id);
    }
    
    // Get players by country
    public Mono<List<Player>> getPlayersByCountry(String country) {
        return supabaseService.getPlayersByCountry(country);
    }
    
    // Get players by position (placeholder - implement in SupabaseService if needed)
    public Mono<List<Player>> getPlayersByPosition(String position) {
        // This would need to be implemented in SupabaseService
        return Mono.just(List.of());
    }
    
    // Get active players (placeholder - implement in SupabaseService if needed)
    public Mono<List<Player>> getActivePlayers() {
        // This would need to be implemented in SupabaseService
        return Mono.just(List.of());
    }
    
    // Get players by ranking range (placeholder - implement in SupabaseService if needed)
    public Mono<List<Player>> getPlayersByRankingRange(Integer minRanking, Integer maxRanking) {
        // This would need to be implemented in SupabaseService
        return Mono.just(List.of());
    }
    
    // Search players by name (placeholder - implement in SupabaseService if needed)
    public Mono<List<Player>> searchPlayersByName(String name) {
        // This would need to be implemented in SupabaseService
        return Mono.just(List.of());
    }
    
    // Get top ranked players (placeholder - implement in SupabaseService if needed)
    public Mono<List<Player>> getTopRankedPlayers() {
        // This would need to be implemented in SupabaseService
        return Mono.just(List.of());
    }
    
    // Get players by price range (placeholder - implement in SupabaseService if needed)
    public Mono<List<Player>> getPlayersByPriceRange(Double minPrice, Double maxPrice) {
        // This would need to be implemented in SupabaseService
        return Mono.just(List.of());
    }
    
    // Get player count by country (placeholder - implement in SupabaseService if needed)
    public Mono<Long> getPlayerCountByCountry(String country) {
        // This would need to be implemented in SupabaseService
        return Mono.just(0L);
    }
    
    // Find player by full name (placeholder - implement in SupabaseService if needed)
    public Mono<Optional<Player>> findPlayerByFullName(String firstName, String lastName) {
        // This would need to be implemented in SupabaseService
        return Mono.just(Optional.empty());
    }
    
    // Initialize with sample data
    public Mono<String> initializeSampleData() {
        return Flux.just(
            createSamplePlayer("Novak", "Djokovic", "Serbia", 1, 12000, "15.50", "Singles"),
            createSamplePlayer("Carlos", "Alcaraz", "Spain", 2, 11000, "14.75", "Singles"),
            createSamplePlayer("Daniil", "Medvedev", "Russia", 3, 10000, "13.25", "Singles"),
            createSamplePlayer("Iga", "Swiatek", "Poland", 1, 11500, "16.00", "Singles"),
            createSamplePlayer("Aryna", "Sabalenka", "Belarus", 2, 10500, "15.25", "Singles")
        )
        .flatMap(player -> supabaseService.createPlayer(player))
        .collectList()
        .map(players -> "Sample data initialized successfully with " + players.size() + " players");
    }
    
    private Player createSamplePlayer(String firstName, String lastName, String country, 
                                   Integer ranking, Integer points, String price, String position) {
        Player player = new Player(firstName, lastName, country, ranking);
        player.setPoints(points);
        player.setPrice(new BigDecimal(price));
        player.setPosition(position);
        return player;
    }
    
    // Test Supabase connection
    public Mono<String> testSupabaseConnection() {
        return supabaseService.testConnection();
    }
}
