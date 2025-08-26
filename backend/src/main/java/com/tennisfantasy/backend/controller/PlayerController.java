package com.tennisfantasy.backend.controller;

import com.tennisfantasy.backend.model.Player;
import com.tennisfantasy.backend.service.PlayerService;
import com.tennisfantasy.backend.service.SportsRadarService;
import com.tennisfantasy.backend.service.SupabaseService;
import reactor.core.publisher.Flux;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/players")
@CrossOrigin(origins = "http://localhost:3000")
public class PlayerController {
        @Autowired
        private SportsRadarService sportsRadarService;

        @Autowired
        private SupabaseService supabaseService;

        // Import players from SportsRadar and post to Supabase
        @PostMapping("/import-from-sportradar")
        public Mono<ResponseEntity<String>> importPlayersFromSportsRadar() {
                return sportsRadarService.fetchPlayers()
                        .flatMapMany(Flux::fromIterable)
                        .flatMap(supabaseService::createPlayer)
                        .collectList()
                        .map(players -> ResponseEntity.ok("Imported " + players.size() + " players"))
                        .onErrorResume(e -> {
                                e.printStackTrace();
                                return Mono.just(ResponseEntity.internalServerError()
                                        .body("Failed to import players: " + e.getMessage()));
                        });
        }
    
    private final PlayerService playerService;
    
    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }
    
    // Test Supabase connection
    @GetMapping("/test-connection")
    public Mono<ResponseEntity<String>> testSupabaseConnection() {
        return playerService.testSupabaseConnection()
                .map(result -> ResponseEntity.ok("Supabase connection test: " + result))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to test Supabase connection"));
    }
    
    // Get all players
    @GetMapping
    public Mono<ResponseEntity<List<Player>>> getAllPlayers() {
        return playerService.getAllPlayers()
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
    
    // Get player by ID
    @GetMapping("/{id}")
    public Mono<ResponseEntity<Player>> getPlayerById(@PathVariable Long id) {
        return playerService.getPlayerById(id)
                .map(playerOpt -> playerOpt.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build()))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
    
    // Create new player
    @PostMapping
    public Mono<ResponseEntity<Player>> createPlayer(@RequestBody Player player) {
        return playerService.createPlayer(player)
                .map(createdPlayer -> ResponseEntity.status(HttpStatus.CREATED).body(createdPlayer))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
    
    // Update existing player
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Player>> updatePlayer(@PathVariable Long id, @RequestBody Player playerDetails) {
        return playerService.updatePlayer(id, playerDetails)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.notFound().build());
    }
    
    // Delete player
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deletePlayer(@PathVariable Long id) {
        return playerService.deletePlayer(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()))
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
    
    // Get players by country
    @GetMapping("/country/{country}")
    public Mono<ResponseEntity<List<Player>>> getPlayersByCountry(@PathVariable String country) {
        return playerService.getPlayersByCountry(country)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
    
    // Get players by position
    @GetMapping("/position/{position}")
    public Mono<ResponseEntity<List<Player>>> getPlayersByPosition(@PathVariable String position) {
        return playerService.getPlayersByPosition(position)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
    
    // Get active players
    @GetMapping("/active")
    public Mono<ResponseEntity<List<Player>>> getActivePlayers() {
        return playerService.getActivePlayers()
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
    
    // Get players by ranking range
    @GetMapping("/ranking")
    public Mono<ResponseEntity<List<Player>>> getPlayersByRankingRange(
            @RequestParam Integer minRanking,
            @RequestParam Integer maxRanking) {
        return playerService.getPlayersByRankingRange(minRanking, maxRanking)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
    
    // Search players by name
    @GetMapping("/search")
    public Mono<ResponseEntity<List<Player>>> searchPlayersByName(@RequestParam String name) {
        return playerService.searchPlayersByName(name)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
    
    // Get top ranked players
    @GetMapping("/top-ranked")
    public Mono<ResponseEntity<List<Player>>> getTopRankedPlayers() {
        return playerService.getTopRankedPlayers()
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
    
    // Get players by price range
    @GetMapping("/price-range")
    public Mono<ResponseEntity<List<Player>>> getPlayersByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        return playerService.getPlayersByPriceRange(minPrice, maxPrice)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
    
    // Get player count by country
    @GetMapping("/count/country/{country}")
    public Mono<ResponseEntity<Long>> getPlayerCountByCountry(@PathVariable String country) {
        return playerService.getPlayerCountByCountry(country)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
    }
    
    // Initialize sample data
    @PostMapping("/init-sample-data")
    public Mono<ResponseEntity<String>> initializeSampleData() {
        return playerService.initializeSampleData()
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to initialize sample data"));
    }
}
