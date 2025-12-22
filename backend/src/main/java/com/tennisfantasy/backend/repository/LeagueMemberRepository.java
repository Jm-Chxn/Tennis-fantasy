package com.tennisfantasy.backend.repository;

import com.tennisfantasy.backend.model.LeagueMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for LeagueMember entity.
 */
@Repository
public interface LeagueMemberRepository extends JpaRepository<LeagueMember, Long> {

    // Find all members of a league
    List<LeagueMember> findByLeagueId(Long leagueId);

    // Find all leagues a user is in
    List<LeagueMember> findByUserId(Long userId);

    // Find specific membership
    Optional<LeagueMember> findByLeagueIdAndUserId(Long leagueId, Long userId);

    // Check if user is member of league
    boolean existsByLeagueIdAndUserId(Long leagueId, Long userId);

    // Find league standings (ordered by total points)
    @Query("SELECT lm FROM LeagueMember lm WHERE lm.league.id = :leagueId ORDER BY lm.totalPoints DESC")
    List<LeagueMember> findLeagueStandings(@Param("leagueId") Long leagueId);

    // Find active members of a league
    List<LeagueMember> findByLeagueIdAndIsActiveTrue(Long leagueId);

    // Count members in a league
    long countByLeagueId(Long leagueId);

    // Find commissioner of a league
    Optional<LeagueMember> findByLeagueIdAndIsCommissionerTrue(Long leagueId);
}
