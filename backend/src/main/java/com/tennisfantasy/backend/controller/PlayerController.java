package com.tennisfantasy.backend.controller;

import com.tennisfantasy.backend.model.Player;
import com.tennisfantasy.backend.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/players")

public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    // Get all players
    @GetMapping
    public ResponseEntity<List<Player>> getAllPlayers() {
        List<Player> players = playerService.getAllPlayers();
        return ResponseEntity.ok(players);
    }

    // Get player by ID
    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        Optional<Player> player = playerService.getPlayerById(id);
        return player.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create new player
    @PostMapping
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        Player createdPlayer = playerService.createPlayer(player);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPlayer);
    }

    // Update existing player
    @PutMapping("/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable Long id, @RequestBody Player playerDetails) {
        try {
            Player updatedPlayer = playerService.updatePlayer(id, playerDetails);
            return ResponseEntity.ok(updatedPlayer);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete player
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }

    // Get players by country
    @GetMapping("/country/{country}")
    public ResponseEntity<List<Player>> getPlayersByCountry(@PathVariable String country) {
        List<Player> players = playerService.getPlayersByCountry(country);
        return ResponseEntity.ok(players);
    }

    // Get players by position
    @GetMapping("/position/{position}")
    public ResponseEntity<List<Player>> getPlayersByPosition(@PathVariable String position) {
        List<Player> players = playerService.getPlayersByPosition(position);
        return ResponseEntity.ok(players);
    }

    // Get active players
    @GetMapping("/active")
    public ResponseEntity<List<Player>> getActivePlayers() {
        List<Player> players = playerService.getActivePlayers();
        return ResponseEntity.ok(players);
    }

    // Get players by ranking range
    @GetMapping("/ranking")
    public ResponseEntity<List<Player>> getPlayersByRankingRange(
            @RequestParam Integer minRanking,
            @RequestParam Integer maxRanking) {
        List<Player> players = playerService.getPlayersByRankingRange(minRanking, maxRanking);
        return ResponseEntity.ok(players);
    }

    // Search players by name
    @GetMapping("/search")
    public ResponseEntity<List<Player>> searchPlayersByName(@RequestParam String name) {
        List<Player> players = playerService.searchPlayersByName(name);
        return ResponseEntity.ok(players);
    }

    // Get top ranked players
    @GetMapping("/top-ranked")
    public ResponseEntity<List<Player>> getTopRankedPlayers() {
        List<Player> players = playerService.getTopRankedPlayers();
        return ResponseEntity.ok(players);
    }

    // Get players by price range
    @GetMapping("/price-range")
    public ResponseEntity<List<Player>> getPlayersByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        List<Player> players = playerService.getPlayersByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(players);
    }

    // Get player count by country
    @GetMapping("/count/country/{country}")
    public ResponseEntity<Long> getPlayerCountByCountry(@PathVariable String country) {
        long count = playerService.getPlayerCountByCountry(country);
        return ResponseEntity.ok(count);
    }

    // Initialize sample data
    @PostMapping("/init-sample-data")
    public ResponseEntity<String> initializeSampleData() {
        playerService.initializeSampleData();
        return ResponseEntity.ok("Sample data initialized successfully");
    }
}
