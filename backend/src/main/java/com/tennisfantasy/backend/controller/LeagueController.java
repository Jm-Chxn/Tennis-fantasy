package com.tennisfantasy.backend.controller;

import com.tennisfantasy.backend.model.League;
import com.tennisfantasy.backend.model.LeagueMember;
import com.tennisfantasy.backend.model.User;
import com.tennisfantasy.backend.repository.UserRepository;
import com.tennisfantasy.backend.service.LeagueService;
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
@CrossOrigin(origins = "http://localhost:3000")
public class LeagueController {

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
        return ResponseEntity.ok(leagueService.getAllLeagues());
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
     * Body: { "name": "...", "userId": 1, "teamName": "..." }
     */
    @PostMapping
    public ResponseEntity<?> createLeague(@RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            Long userId = ((Number) request.get("userId")).longValue();
            String teamName = (String) request.get("teamName");

            User owner = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            League league = new League();
            league.setName(name);
            league.setDescription((String) request.getOrDefault("description", ""));

            if (request.containsKey("maxTeams")) {
                league.setMaxTeams(((Number) request.get("maxTeams")).intValue());
            }
            if (request.containsKey("rosterSize")) {
                league.setRosterSize(((Number) request.get("rosterSize")).intValue());
            }
            if (request.containsKey("tourType")) {
                league.setTourType((String) request.get("tourType"));
            }
            if (request.containsKey("isPublic")) {
                league.setIsPublic((Boolean) request.get("isPublic"));
            }

            League createdLeague = leagueService.createLeague(league, owner, teamName);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLeague);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Join a league using join code.
     * 
     * POST /api/leagues/join
     * Body: { "joinCode": "ABC123", "userId": 1, "teamName": "..." }
     */
    @PostMapping("/join")
    public ResponseEntity<?> joinLeague(@RequestBody Map<String, Object> request) {
        try {
            String joinCode = (String) request.get("joinCode");
            Long userId = ((Number) request.get("userId")).longValue();
            String teamName = (String) request.get("teamName");

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            LeagueMember member = leagueService.joinLeague(joinCode, user, teamName);
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
}
