package com.tennisfantasy.backend.service;

import com.tennisfantasy.backend.model.Player;
import com.tennisfantasy.backend.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for Player operations.
 * 
 * Provides business logic for managing tennis players.
 */
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    // Get all players
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    // Get player by ID
    public Optional<Player> getPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    // Create new player
    public Player createPlayer(Player player) {
        return playerRepository.save(player);
    }

    // Update existing player
    public Player updatePlayer(Long id, Player playerDetails) {
        Optional<Player> playerOptional = playerRepository.findById(id);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            player.setFirstName(playerDetails.getFirstName());
            player.setLastName(playerDetails.getLastName());
            player.setCountry(playerDetails.getCountry());
            player.setCountryCode(playerDetails.getCountryCode());
            player.setRanking(playerDetails.getRanking());
            player.setPoints(playerDetails.getPoints());
            player.setPrice(playerDetails.getPrice());
            player.setTour(playerDetails.getTour());
            player.setPosition(playerDetails.getPosition());
            player.setIsActive(playerDetails.getIsActive());
            return playerRepository.save(player);
        }
        throw new RuntimeException("Player not found with id: " + id);
    }

    // Delete player
    public void deletePlayer(Long id) {
        playerRepository.deleteById(id);
    }

    // Get players by country
    public List<Player> getPlayersByCountry(String country) {
        return playerRepository.findByCountry(country);
    }

    // Get players by position
    public List<Player> getPlayersByPosition(String position) {
        return playerRepository.findByPosition(position);
    }

    // Get active players
    public List<Player> getActivePlayers() {
        return playerRepository.findByIsActiveTrue();
    }

    // Get players by ranking range
    public List<Player> getPlayersByRankingRange(Integer minRanking, Integer maxRanking) {
        return playerRepository.findByRankingBetween(minRanking, maxRanking);
    }

    // Search players by name
    public List<Player> searchPlayersByName(String name) {
        return playerRepository.findByNameContainingIgnoreCase(name);
    }

    // Get top ranked players
    public List<Player> getTopRankedPlayers() {
        return playerRepository.findTopRankedPlayers();
    }

    // Get players by price range
    public List<Player> getPlayersByPriceRange(Double minPrice, Double maxPrice) {
        return playerRepository.findByPriceRange(minPrice, maxPrice);
    }

    // Get player count by country
    public long getPlayerCountByCountry(String country) {
        return playerRepository.countByCountry(country);
    }

    // Find player by full name
    public Optional<Player> findPlayerByFullName(String firstName, String lastName) {
        return playerRepository.findByFirstNameAndLastName(firstName, lastName);
    }

    // ===== NEW: SportsRadar integration methods =====

    // Get ATP players
    public List<Player> getAtpPlayers() {
        return playerRepository.findAtpPlayers();
    }

    // Get WTA players
    public List<Player> getWtaPlayers() {
        return playerRepository.findWtaPlayers();
    }

    // Get players by tour
    public List<Player> getPlayersByTour(String tour) {
        return playerRepository.findByTour(tour);
    }

    // Find player by SportsRadar ID
    public Optional<Player> findBySportradarId(String sportradarId) {
        return playerRepository.findBySportradarId(sportradarId);
    }

    // Initialize with sample data (legacy method kept for compatibility)
    public void initializeSampleData() {
        if (playerRepository.count() == 0) {
            // Add some sample tennis players
            Player player1 = new Player("Novak", "Djokovic", "Serbia", 1);
            player1.setPoints(12000);
            player1.setPrice(new BigDecimal("15.50"));
            player1.setPosition("Singles");
            player1.setTour("ATP");
            player1.setCountryCode("SRB");
            player1.setLastSyncedAt(LocalDateTime.now());
            playerRepository.save(player1);

            Player player2 = new Player("Carlos", "Alcaraz", "Spain", 2);
            player2.setPoints(11000);
            player2.setPrice(new BigDecimal("14.75"));
            player2.setPosition("Singles");
            player2.setTour("ATP");
            player2.setCountryCode("ESP");
            player2.setLastSyncedAt(LocalDateTime.now());
            playerRepository.save(player2);

            Player player3 = new Player("Daniil", "Medvedev", "Russia", 3);
            player3.setPoints(10000);
            player3.setPrice(new BigDecimal("13.25"));
            player3.setPosition("Singles");
            player3.setTour("ATP");
            player3.setCountryCode("RUS");
            player3.setLastSyncedAt(LocalDateTime.now());
            playerRepository.save(player3);

            Player player4 = new Player("Iga", "Swiatek", "Poland", 1);
            player4.setPoints(11500);
            player4.setPrice(new BigDecimal("16.00"));
            player4.setPosition("Singles");
            player4.setTour("WTA");
            player4.setCountryCode("POL");
            player4.setLastSyncedAt(LocalDateTime.now());
            playerRepository.save(player4);

            Player player5 = new Player("Aryna", "Sabalenka", "Belarus", 2);
            player5.setPoints(10500);
            player5.setPrice(new BigDecimal("15.25"));
            player5.setPosition("Singles");
            player5.setTour("WTA");
            player5.setCountryCode("BLR");
            player5.setLastSyncedAt(LocalDateTime.now());
            playerRepository.save(player5);
        }
    }
}
