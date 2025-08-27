package com.tennisfantasy.backend.controller;

import com.tennisfantasy.backend.model.Player;
import com.tennisfantasy.backend.service.SupabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import com.tennisfantasy.backend.service.PlayerSyncOrchestrator;

@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    private SupabaseService supabaseService;
    
    @Autowired
    private PlayerSyncOrchestrator playerSyncOrchestrator;

    @PostMapping("/test-data")
    public Mono<ResponseEntity<String>> insertTestData() {
        // Create test player data
        List<Player> testPlayers = Arrays.asList(
            new Player("Novak", "Djokovic", "Serbia", 1, new BigDecimal("15.50")),
            new Player("Carlos", "Alcaraz", "Spain", 2, new BigDecimal("14.75")),
            new Player("Daniil", "Medvedev", "Russia", 3, new BigDecimal("13.25")),
            new Player("Jannik", "Sinner", "Italy", 4, new BigDecimal("12.80")),
            new Player("Andrey", "Rublev", "Russia", 5, new BigDecimal("11.90")),
            new Player("roger", "dai", "china", 500, new BigDecimal("2")),
            new Player("dandan", "hu", "china", 499, new BigDecimal("3")),
            new Player("ivan", "luo", "china", 488, new BigDecimal("3.5"))
        );

        return supabaseService.createPlayersIfNotExists(testPlayers)
                .map(result -> ResponseEntity.ok(result))
                .onErrorReturn(ResponseEntity.badRequest().body("Failed to import players"));
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<String>> addSinglePlayer(@RequestBody Player player) {
        return supabaseService.createPlayer(player)
                .map(savedPlayer -> ResponseEntity.ok("Successfully added player: " + savedPlayer.getFirstName() + " " + savedPlayer.getLastName()))
                .onErrorReturn(ResponseEntity.badRequest().body("Failed to add player"));
    }

    @DeleteMapping("/delete-all")
    public Mono<ResponseEntity<String>> deleteAllPlayers() {
        return supabaseService.deleteAllPlayers()
                .map(result -> ResponseEntity.ok(result))
                .onErrorReturn(ResponseEntity.badRequest().body("Failed to delete players"));
    }

    @DeleteMapping("/clear-all")
    public Mono<ResponseEntity<String>> clearAllPlayers() {
        return supabaseService.deleteAllPlayers()
                .map(result -> ResponseEntity.ok(result));
    }

    @PostMapping("/smart-import")
    public Mono<ResponseEntity<String>> smartImportPlayers() {
        // Smart import that checks for duplicates
        List<Player> testPlayers = Arrays.asList(
            new Player("Novak", "Djokovic", "Serbia", 1, new BigDecimal("15.50")),
            new Player("Carlos", "Alcaraz", "Spain", 2, new BigDecimal("14.75")),
            new Player("Daniil", "Medvedev", "Russia", 3, new BigDecimal("13.25")),
            new Player("Jannik", "Sinner", "Italy", 4, new BigDecimal("12.80")),
            new Player("Andrey", "Rublev", "Russia", 5, new BigDecimal("11.90")),
            new Player("Roger", "Dai", "China", 500, new BigDecimal("2.00"))
        );

        return supabaseService.createPlayersIfNotExists(testPlayers)
                .map(result -> ResponseEntity.ok(result));
    }

    @PostMapping("/sync-from-sportsradar")
    public Mono<ResponseEntity<String>> syncPlayersFromSportsRadar() {
        String locale = "en"; // maybe have a param for this later
        
        return playerSyncOrchestrator.syncPlayersFromSportsRadar(locale)
                .map(result -> ResponseEntity.ok(result))
                .onErrorReturn(ResponseEntity.badRequest().body("Failed to sync players from SportsRadar"));
    }

    @GetMapping("/list")
    public Mono<ResponseEntity<List<Player>>> getAllPlayers() {
        return supabaseService.getAllPlayers()
                .map(players -> ResponseEntity.ok(players))
                .onErrorReturn(ResponseEntity.badRequest().build());
    }

    @GetMapping("/check/{firstName}/{lastName}")
    public Mono<ResponseEntity<String>> checkPlayerExists(@PathVariable String firstName, @PathVariable String lastName) {
        return supabaseService.playerExists(firstName, lastName)
                .map(exists -> {
                    if (exists) {
                        return ResponseEntity.ok("Player " + firstName + " " + lastName + " already exists in Supabase");
                    } else {
                        return ResponseEntity.ok("Player " + firstName + " " + lastName + " does not exist in Supabase");
                    }
                })
                .onErrorReturn(ResponseEntity.badRequest().body("Failed to check player"));
    }
}