package com.tennisfantasy.backend.repository;

import com.tennisfantasy.backend.model.FantasyPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for FantasyPoints entity.
 */
@Repository
public interface FantasyPointsRepository extends JpaRepository<FantasyPoints, Long> {

    // Find points for a team
    List<FantasyPoints> findByLeagueMemberIdOrderByAwardedAtDesc(Long leagueMemberId);

    // Find points for a player on a team
    List<FantasyPoints> findByLeagueMemberIdAndPlayerId(Long leagueMemberId, Long playerId);

    // Find points from a specific match
    List<FantasyPoints> findByMatchId(Long matchId);

    // Sum total points for a team
    @Query("SELECT COALESCE(SUM(fp.points), 0) FROM FantasyPoints fp WHERE fp.leagueMember.id = :leagueMemberId")
    Integer sumPointsByLeagueMemberId(@Param("leagueMemberId") Long leagueMemberId);

    // Sum points by week
    @Query("SELECT COALESCE(SUM(fp.points), 0) FROM FantasyPoints fp WHERE fp.leagueMember.id = :leagueMemberId AND fp.weekNumber = :week")
    Integer sumPointsByLeagueMemberIdAndWeek(@Param("leagueMemberId") Long leagueMemberId, @Param("week") Integer week);

    // Find points in date range
    List<FantasyPoints> findByLeagueMemberIdAndAwardedAtBetween(
            Long leagueMemberId, LocalDateTime start, LocalDateTime end);

    // Find recent scoring activity in a league
    @Query("SELECT fp FROM FantasyPoints fp WHERE fp.leagueMember.league.id = :leagueId ORDER BY fp.awardedAt DESC")
    List<FantasyPoints> findRecentByLeagueId(@Param("leagueId") Long leagueId);
}
