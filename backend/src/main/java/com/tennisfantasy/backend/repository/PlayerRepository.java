package com.tennisfantasy.backend.repository;

import com.tennisfantasy.backend.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    
    // Find players by country
    List<Player> findByCountry(String country);
    
    // Find players by position
    List<Player> findByPosition(String position);
    
    // Find active players
    List<Player> findByIsActiveTrue();
    
    // Find players by ranking range
    List<Player> findByRankingBetween(Integer minRanking, Integer maxRanking);
    
    // Find players by name (case-insensitive)
    @Query("SELECT p FROM Player p WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Player> findByNameContainingIgnoreCase(@Param("name") String name);
    
    // Find top ranked players
    @Query("SELECT p FROM Player p WHERE p.isActive = true ORDER BY p.ranking ASC")
    List<Player> findTopRankedPlayers();
    
    // Find players by price range
    @Query("SELECT p FROM Player p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.isActive = true")
    List<Player> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    // Count players by country
    long countByCountry(String country);
    
    // Find player by full name
    Optional<Player> findByFirstNameAndLastName(String firstName, String lastName);
}
