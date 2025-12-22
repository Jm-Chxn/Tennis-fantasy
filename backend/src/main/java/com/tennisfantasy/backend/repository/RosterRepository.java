package com.tennisfantasy.backend.repository;

import com.tennisfantasy.backend.model.Roster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Roster entity.
 */
@Repository
public interface RosterRepository extends JpaRepository<Roster, Long> {

    // Find all roster entries for a team
    List<Roster> findByLeagueMemberId(Long leagueMemberId);

    // Find starters for a team
    List<Roster> findByLeagueMemberIdAndIsStarterTrue(Long leagueMemberId);

    // Find bench players for a team
    List<Roster> findByLeagueMemberIdAndIsStarterFalse(Long leagueMemberId);

    // Check if player is on any roster in a league
    @Query("SELECT r FROM Roster r WHERE r.player.id = :playerId AND r.leagueMember.league.id = :leagueId")
    Optional<Roster> findPlayerInLeague(@Param("playerId") Long playerId, @Param("leagueId") Long leagueId);

    // Check if player is already on team
    boolean existsByLeagueMemberIdAndPlayerId(Long leagueMemberId, Long playerId);

    // Count roster size
    long countByLeagueMemberId(Long leagueMemberId);

    // Find roster entry by team and player
    Optional<Roster> findByLeagueMemberIdAndPlayerId(Long leagueMemberId, Long playerId);

    // Get all rosters for a league
    @Query("SELECT r FROM Roster r WHERE r.leagueMember.league.id = :leagueId")
    List<Roster> findAllByLeagueId(@Param("leagueId") Long leagueId);

    // Get drafted players in a league
    @Query("SELECT r.player.id FROM Roster r WHERE r.leagueMember.league.id = :leagueId")
    List<Long> findDraftedPlayerIdsByLeagueId(@Param("leagueId") Long leagueId);
}
