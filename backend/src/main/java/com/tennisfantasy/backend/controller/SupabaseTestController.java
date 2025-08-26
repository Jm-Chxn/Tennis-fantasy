package com.tennisfantasy.backend.controller;

import com.tennisfantasy.backend.model.Player;
import com.tennisfantasy.backend.service.SupabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/supabase-test")
@CrossOrigin(origins = "http://localhost:3000")
public class SupabaseTestController {
    
    private final SupabaseService supabaseService;
    
    @Autowired
    public SupabaseTestController(SupabaseService supabaseService) {
        this.supabaseService = supabaseService;
    }
    
    // Test basic connection to Supabase
    @GetMapping("/connection")
    public Mono<ResponseEntity<String>> testConnection() {
        return supabaseService.testConnection()
                .map(result -> ResponseEntity.ok("Connection test result: " + result))
                .onErrorReturn(ResponseEntity.internalServerError()
                        .body("Connection test failed"));
    }
    
    // Test creating a simple player
    @PostMapping("/create-test-player")
        public Mono<ResponseEntity<String>> createTestPlayer() {
            Player testPlayer = new Player("Test", "Player", "TestCountry", 999);
            testPlayer.setPoints(1000);
            testPlayer.setPosition("Singles");

            return supabaseService.createPlayer(testPlayer)
                    .map(player -> ResponseEntity.ok("Test player created successfully with ID: " + player.getId()))
                    .onErrorResume(e -> {
                        e.printStackTrace();
                        return Mono.just(ResponseEntity.internalServerError()
                            .body("Failed to create test player: " + e.getMessage()));
                    });
        }
    
    // Test getting all players
    @GetMapping("/players")
        public Mono<ResponseEntity<String>> getAllPlayers() {
            return supabaseService.getAllPlayers()
                    .map(players -> ResponseEntity.ok("Found " + players.size() + " players"))
                    .onErrorResume(e -> {
                        e.printStackTrace();
                        return Mono.just(ResponseEntity.internalServerError()
                            .body("Failed to retrieve players: " + e.getMessage()));
                    });
        }
    
    // Test getting players by country
    @GetMapping("/players/country/{country}")
    public Mono<ResponseEntity<String>> getPlayersByCountry(@PathVariable String country) {
        return supabaseService.getPlayersByCountry(country)
                .map(players -> ResponseEntity.ok("Found " + players.size() + " players from " + country))
                .onErrorReturn(ResponseEntity.internalServerError()
                        .body("Failed to retrieve players by country"));
    }
}
