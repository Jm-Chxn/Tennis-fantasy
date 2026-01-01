package com.tennisfantasy.backend.controller;

import com.tennisfantasy.backend.model.League;
import com.tennisfantasy.backend.model.LeagueMember;
import com.tennisfantasy.backend.model.User;
import com.tennisfantasy.backend.repository.UserRepository;
import com.tennisfantasy.backend.service.LeagueService;
import com.tennisfantasy.backend.dto.LeagueCreateRequest;
import com.tennisfantasy.backend.dto.LeagueJoinRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for League endpoints.
 * 
 * Handles league creation, joining, and management.
 */
@RestController
@RequestMapping("/leagues")
public class LeagueController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LeagueController.class);

    private final LeagueService leagueService;
    private final UserRepository userRepository;

    public LeagueController(LeagueService leagueService, UserRepository userRepository) {
        this.leagueService = leagueService;
        this.userRepository = userRepository;
    }

    /**
     * Get all leagues.
     * 
     * GET /api/leagues
     */
    @GetMapping
    public ResponseEntity<List<League>> getAllLeagues() {
        logger.info(">>> GET /api/leagues - Fetching all leagues");
        List<League> leagues = leagueService.getAllLeagues();
        logger.info(">>> GET /api/leagues - Found: {} leagues", leagues.size());
        return ResponseEntity.ok(leagues);
    }

    /**
     * Get public leagues that can be joined.
     * 
     * GET /api/leagues/public
     */
    @GetMapping("/public")
    public ResponseEntity<List<League>> getPublicLeagues() {
        return ResponseEntity.ok(leagueService.getPublicLeagues());
    }

    /**
     * Get a league by ID.
     * 
     * GET /api/leagues/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<League> getLeagueById(@PathVariable Long id) {
        return leagueService.getLeagueById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new league.
     * 
     * POST /api/leagues
     */
    @PostMapping
    public ResponseEntity<?> createLeague(@Valid @RequestBody LeagueCreateRequest request) {
        try {
            User owner = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            League league = new League();
            league.setName(request.getName());
            league.setDescription(request.getDescription() != null ? request.getDescription() : "");
            league.setMaxTeams(request.getMaxTeams());
            league.setRosterSize(request.getRosterSize());
            league.setStarterSize(request.getStarterSize());
            league.setTourType(request.getTourType());
            league.setIsPublic(request.getIsPublic());

            logger.info(">>> POST /api/leagues - Creating league: {} for user ID: {}", request.getName(),
                    request.getUserId());
            League createdLeague = leagueService.createLeague(league, owner, request.getTeamName());
            logger.info(">>> POST /api/leagues - Created league: {} (ID: {}) for owner: {}", createdLeague.getName(),
                    createdLeague.getId(),
                    owner.getDisplayName());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLeague);

        } catch (Exception e) {
            logger.error(">>> POST /api/leagues - ERROR creating league: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Join a league using join code.
     * 
     * POST /api/leagues/join
     */
    @PostMapping("/join")
    public ResponseEntity<?> joinLeague(@Valid @RequestBody LeagueJoinRequest request) {
        try {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            LeagueMember member = leagueService.joinLeague(request.getJoinCode(), user, request.getTeamName());
            return ResponseEntity.ok(member);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Leave a league.
     * 
     * POST /api/leagues/{id}/leave
     * Body: { "userId": 1 }
     */
    @PostMapping("/{id}/leave")
    public ResponseEntity<?> leaveLeague(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            leagueService.leaveLeague(id, userId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Successfully left the league");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all members of a league.
     * 
     * GET /api/leagues/{id}/members
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<List<LeagueMember>> getLeagueMembers(@PathVariable Long id) {
        return ResponseEntity.ok(leagueService.getLeagueMembers(id));
    }

    /**
     * Get league standings.
     * 
     * GET /api/leagues/{id}/standings
     */
    @GetMapping("/{id}/standings")
    public ResponseEntity<List<LeagueMember>> getLeagueStandings(@PathVariable Long id) {
        return ResponseEntity.ok(leagueService.getLeagueStandings(id));
    }

    /**
     * Update league settings.
     * 
     * PUT /api/leagues/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLeague(@PathVariable Long id, @RequestBody League leagueDetails) {
        try {
            League updated = leagueService.updateLeague(id, leagueDetails);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Delete a league.
     * 
     * DELETE /api/leagues/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeague(@PathVariable Long id) {
        leagueService.deleteLeague(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Search leagues by name.
     * 
     * GET /api/leagues/search?name=...
     */
    @GetMapping("/search")
    public ResponseEntity<List<League>> searchLeagues(@RequestParam String name) {
        return ResponseEntity.ok(leagueService.searchLeagues(name));
    }

    /**
     * Get all leagues a user has joined.
     * 
     * GET /api/leagues/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<League>> getJoinedLeagues(@PathVariable Long userId) {
        List<LeagueMember> memberships = leagueService.getUserMemberships(userId);
        List<League> leagues = memberships.stream()
                .map(LeagueMember::getLeague)
                .toList();
        return ResponseEntity.ok(leagues);
    }
}
