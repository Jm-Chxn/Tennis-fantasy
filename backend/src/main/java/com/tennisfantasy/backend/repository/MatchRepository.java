package com.tennisfantasy.backend.repository;

import com.tennisfantasy.backend.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Match entity.
 * 
 * Provides database operations for tennis matches.
 */
@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    // Find match by SportsRadar ID
    Optional<Match> findBySportradarId(String sportradarId);

    // Find matches by status
    List<Match> findByStatus(String status);

    // Find live matches
    @Query("SELECT m FROM Match m WHERE m.status = 'live'")
    List<Match> findLiveMatches();

    // Find completed matches
    @Query("SELECT m FROM Match m WHERE m.status = 'completed' ORDER BY m.matchDate DESC")
    List<Match> findCompletedMatches();

    // Find matches by player
    @Query("SELECT m FROM Match m WHERE m.player1.id = :playerId OR m.player2.id = :playerId ORDER BY m.matchDate DESC")
    List<Match> findByPlayerId(@Param("playerId") Long playerId);

    // Find matches by tournament
    List<Match> findByTournamentIdOrderByMatchDateAsc(String tournamentId);

    // Find matches by date range
    @Query("SELECT m FROM Match m WHERE m.matchDate BETWEEN :start AND :end ORDER BY m.matchDate ASC")
    List<Match> findByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Find recent completed matches for a player
    @Query("SELECT m FROM Match m WHERE (m.player1.id = :playerId OR m.player2.id = :playerId) AND m.status = 'completed' ORDER BY m.matchDate DESC")
    List<Match> findRecentMatchesByPlayer(@Param("playerId") Long playerId);

    // Count matches by tournament
    long countByTournamentId(String tournamentId);

    // Check if match exists by SportsRadar ID
    boolean existsBySportradarId(String sportradarId);
}
