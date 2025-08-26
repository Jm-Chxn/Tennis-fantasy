package com.tennisfantasy.backend.controller;

import com.tennisfantasy.backend.service.SportsRadarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/sportsradar")
@CrossOrigin(origins = "http://localhost:3000")
public class SportsRadarController {
    
    private final SportsRadarService sportsRadarService;
    
    @Autowired
    public SportsRadarController(SportsRadarService sportsRadarService) {
        this.sportsRadarService = sportsRadarService;
    }
    
    /**
     * Test connection to SportsRadar API
     */
    @GetMapping("/test-connection")
    public Mono<ResponseEntity<String>> testSportsRadarConnection() {
        return sportsRadarService.testConnection()
                .map(result -> ResponseEntity.ok("SportsRadar test: " + result))
                .onErrorReturn(ResponseEntity.internalServerError()
                        .body("SportsRadar connection test failed"));
    }
    
    /**
     * Fetch raw data from SportsRadar API
     * This is useful for debugging and seeing the actual data structure
     */
    @GetMapping("/fetch-players")
    public Mono<ResponseEntity<String>> fetchPlayersFromSportsRadar() {
        return sportsRadarService.fetchTennisPlayers()
                .map(response -> ResponseEntity.ok("Raw SportsRadar response: " + response))
                .onErrorReturn(ResponseEntity.internalServerError()
                        .body("Failed to fetch players from SportsRadar"));
    }
    
    /**
     * Complete flow: Fetch from SportsRadar → Filter → Save to Supabase
     * This demonstrates the end-to-end process you want to implement
     */
    @PostMapping("/process-and-save")
    public Mono<ResponseEntity<String>> processAndSavePlayers() {
        return sportsRadarService.processAndSavePlayers()
                .map(result -> ResponseEntity.ok(result))
                .onErrorReturn(ResponseEntity.internalServerError()
                        .body("Failed to process and save players"));
    }
    
    /**
     * Manual trigger to sync players from SportsRadar to Supabase
     * You can call this endpoint to manually sync data
     */
    @PostMapping("/sync-players")
    public Mono<ResponseEntity<String>> syncPlayers() {
        return sportsRadarService.processAndSavePlayers()
                .map(result -> ResponseEntity.ok("Sync completed: " + result))
                .onErrorReturn(ResponseEntity.internalServerError()
                        .body("Player sync failed"));
    }
}
