package com.tennisfantasy.backend.controller;

import com.tennisfantasy.backend.model.LeagueMember;
import com.tennisfantasy.backend.model.Player;
import com.tennisfantasy.backend.model.Roster;
import com.tennisfantasy.backend.repository.LeagueMemberRepository;
import com.tennisfantasy.backend.repository.PlayerRepository;
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
    private final PlayerRepository playerRepository;

    public RosterController(RosterRepository rosterRepository, LeagueMemberRepository leagueMemberRepository, PlayerRepository playerRepository) {
        this.rosterRepository = rosterRepository;
        this.leagueMemberRepository = leagueMemberRepository;
        this.playerRepository = playerRepository;
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

    /**
     * Add a player to roster (buy player with budget).
     * 
     * POST /api/leagues/{leagueId}/roster/add
     * Body: { "userId": 1, "playerId": 123 }
     */
    @PostMapping("/add")
    public ResponseEntity<?> addPlayerToRoster(@PathVariable Long leagueId, @RequestBody Map<String, Object> request) {
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            Long playerId = ((Number) request.get("playerId")).longValue();

            LeagueMember member = leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
                    .orElseThrow(() -> new RuntimeException("Not a member of this league"));

            Player player = playerRepository.findById(playerId)
                    .orElseThrow(() -> new RuntimeException("Player not found"));

            // Check if player is already on roster
            if (rosterRepository.findByLeagueMemberIdAndPlayerId(member.getId(), playerId).isPresent()) {
                throw new RuntimeException("Player already on your roster");
            }

            // Check budget
            double playerPrice = player.getPrice() != null ? player.getPrice().doubleValue() : 0.0;
            if (member.getBudget() < playerPrice) {
                throw new RuntimeException("Insufficient budget. Need $" + playerPrice + " but only have $" + member.getBudget());
            }

            // Check roster size limit (get from league)
            List<Roster> currentRoster = rosterRepository.findByLeagueMemberId(member.getId());
            int rosterSize = member.getLeague().getRosterSize();
            if (currentRoster.size() >= rosterSize) {
                throw new RuntimeException("Roster is full. Maximum " + rosterSize + " players allowed.");
            }

            // Deduct budget
            member.setBudget(member.getBudget() - playerPrice);
            leagueMemberRepository.save(member);

            // Add player to roster
            Roster roster = new Roster(member, player);
            roster.setAcquisitionType("FREE_AGENT");
            roster.setSlotNumber(currentRoster.size() + 1);
            rosterRepository.save(roster);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Player added to roster");
            response.put("remainingBudget", member.getBudget());
            response.put("rosterSize", currentRoster.size() + 1);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Remove a player from roster (release/sell player, refund partial budget).
     * 
     * DELETE /api/leagues/{leagueId}/roster/remove
     * Body: { "userId": 1, "playerId": 123 }
     */
    @DeleteMapping("/remove")
    public ResponseEntity<?> removePlayerFromRoster(@PathVariable Long leagueId, @RequestBody Map<String, Object> request) {
        try {
            Long userId = ((Number) request.get("userId")).longValue();
            Long playerId = ((Number) request.get("playerId")).longValue();

            LeagueMember member = leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
                    .orElseThrow(() -> new RuntimeException("Not a member of this league"));

            Roster roster = rosterRepository.findByLeagueMemberIdAndPlayerId(member.getId(), playerId)
                    .orElseThrow(() -> new RuntimeException("Player not on your roster"));

            // Refund 50% of player price
            Player player = roster.getPlayer();
            double refund = (player.getPrice() != null ? player.getPrice().doubleValue() : 0.0) * 0.5;
            member.setBudget(member.getBudget() + refund);
            leagueMemberRepository.save(member);

            // Remove from roster
            rosterRepository.delete(roster);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Player removed from roster");
            response.put("refund", refund);
            response.put("remainingBudget", member.getBudget());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get user's budget and roster info.
     * 
     * GET /api/leagues/{leagueId}/roster/budget?userId=1
     */
    @GetMapping("/budget")
    public ResponseEntity<?> getBudget(@PathVariable Long leagueId, @RequestParam Long userId) {
        return leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
                .map(member -> {
                    List<Roster> roster = rosterRepository.findByLeagueMemberId(member.getId());
                    int rosterSize = member.getLeague().getRosterSize();
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("budget", member.getBudget());
                    response.put("currentRosterSize", roster.size());
                    response.put("maxRosterSize", rosterSize);
                    response.put("teamName", member.getTeamName());
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
