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

@RestController
@RequestMapping("/players")
public class PlayerController {

    @Autowired
    private SupabaseService supabaseService;

    @PostMapping("/test-data")
    public Mono<ResponseEntity<String>> insertTestData() {
        // Create test player data
        List<Player> testPlayers = Arrays.asList(
            new Player("Novak", "Djokovic", "Serbia", 1, new BigDecimal("15.50")),
            new Player("Carlos", "Alcaraz", "Spain", 2, new BigDecimal("14.75")),
            new Player("Daniil", "Medvedev", "Russia", 3, new BigDecimal("13.25")),
            new Player("Jannik", "Sinner", "Italy", 4, new BigDecimal("12.80")),
            new Player("Andrey", "Rublev", "Russia", 5, new BigDecimal("11.90")),
            new Player("roger", "dai", "china", 500, new BigDecimal("2"))
        );

        return supabaseService.createPlayers(testPlayers)
                .map(players -> ResponseEntity.ok("Successfully imported " + players.size() + " players"))
                .onErrorReturn(ResponseEntity.badRequest().body("Failed to import players"));
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<String>> addSinglePlayer(@RequestBody Player player) {
        return supabaseService.createPlayer(player)
                .map(savedPlayer -> ResponseEntity.ok("Successfully added player: " + savedPlayer.getFirstName() + " " + savedPlayer.getLastName()))
                .onErrorReturn(ResponseEntity.badRequest().body("Failed to add player"));
    }
}