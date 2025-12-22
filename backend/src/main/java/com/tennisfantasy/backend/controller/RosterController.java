package com.tennisfantasy.backend.controller;

import com.tennisfantasy.backend.model.Roster;
import com.tennisfantasy.backend.repository.LeagueMemberRepository;
import com.tennisfantasy.backend.repository.RosterRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for Roster endpoints.
 * 
 * Handles roster viewing and lineup management.
 */
@RestController
@RequestMapping("/leagues/{leagueId}/roster")
@CrossOrigin(origins = "http://localhost:3000")
public class RosterController {

    private final RosterRepository rosterRepository;
    private final LeagueMemberRepository leagueMemberRepository;

    public RosterController(RosterRepository rosterRepository, LeagueMemberRepository leagueMemberRepository) {
        this.rosterRepository = rosterRepository;
        this.leagueMemberRepository = leagueMemberRepository;
    }

    /**
     * Get a user's roster in a league.
     * 
     * GET /api/leagues/{leagueId}/roster?userId=1
     */
    @GetMapping
    public ResponseEntity<?> getRoster(@PathVariable Long leagueId, @RequestParam Long userId) {
        return leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
                .map(member -> {
                    List<Roster> roster = rosterRepository.findByLeagueMemberId(member.getId());
                    return ResponseEntity.ok(roster);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get starters for a team.
     * 
     * GET /api/leagues/{leagueId}/roster/starters?userId=1
     */
    @GetMapping("/starters")
    public ResponseEntity<?> getStarters(@PathVariable Long leagueId, @RequestParam Long userId) {
        return leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
                .map(member -> {
                    List<Roster> starters = rosterRepository.findByLeagueMemberIdAndIsStarterTrue(member.getId());
                    return ResponseEntity.ok(starters);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update lineup (set starters/bench).
     * 
     * PUT /api/leagues/{leagueId}/roster
     * Body: { "userId": 1, "starterIds": [1, 2, 3], "benchIds": [4] }
     */
    @PutMapping
    public ResponseEntity<?> updateLineup(@PathVariable Long leagueId, @RequestBody Map<String, Object> request) {
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            List<Number> starterIds = (List<Number>) request.get("starterIds");
            List<Number> benchIds = (List<Number>) request.get("benchIds");

            var member = leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
                    .orElseThrow(() -> new RuntimeException("Membership not found"));

            // Update starters
            for (Number starterIdNum : starterIds) {
                Long playerId = starterIdNum.longValue();
                rosterRepository.findByLeagueMemberIdAndPlayerId(member.getId(), playerId)
                        .ifPresent(roster -> {
                            roster.setIsStarter(true);
                            rosterRepository.save(roster);
                        });
            }

            // Update bench
            for (Number benchIdNum : benchIds) {
                Long playerId = benchIdNum.longValue();
                rosterRepository.findByLeagueMemberIdAndPlayerId(member.getId(), playerId)
                        .ifPresent(roster -> {
                            roster.setIsStarter(false);
                            rosterRepository.save(roster);
                        });
            }

            Map<String, String> response = new HashMap<>();
            response.put("message", "Lineup updated successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get all rosters in a league (for all teams).
     * 
     * GET /api/leagues/{leagueId}/roster/all
     */
    @GetMapping("/all")
    public ResponseEntity<List<Roster>> getAllRosters(@PathVariable Long leagueId) {
        List<Roster> allRosters = rosterRepository.findAllByLeagueId(leagueId);
        return ResponseEntity.ok(allRosters);
    }
}
