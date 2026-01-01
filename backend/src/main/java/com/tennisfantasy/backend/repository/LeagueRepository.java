package com.tennisfantasy.backend.repository;

import com.tennisfantasy.backend.model.League;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for League entity.
 */
@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {

    // Find league by join code
    Optional<League> findByJoinCode(String joinCode);

    // Find leagues owned by a user
    List<League> findByOwnerId(Long ownerId);

    // Find all public leagues
    List<League> findByIsPublicTrue();

    // Find public leagues that can be joined (public, active, and have room)
    @Query("SELECT l FROM League l WHERE l.isPublic = true AND l.status = 'ACTIVE' AND l.currentTeams < l.maxTeams")
    List<League> findJoinablePublicLeagues();

    // Find active leagues
    List<League> findByStatus(String status);

    // Find leagues with available spots
    @Query("SELECT l FROM League l WHERE l.currentTeams < l.maxTeams AND l.status = 'ACTIVE'")
    List<League> findLeaguesWithAvailableSpots();

    // Search leagues by name
    @Query("SELECT l FROM League l WHERE LOWER(l.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<League> searchByName(@Param("name") String name);

    // Find leagues where draft has not started
    List<League> findByDraftStatus(String draftStatus);

    // Check if join code exists
    boolean existsByJoinCode(String joinCode);
}
