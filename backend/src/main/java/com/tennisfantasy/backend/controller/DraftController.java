package com.tennisfantasy.backend.controller;

import com.tennisfantasy.backend.model.Player;
import com.tennisfantasy.backend.service.DraftService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for Draft endpoints.
 * 
 * Handles draft start, pick, and status operations.
 */
@RestController
@RequestMapping("/leagues/{leagueId}/draft")

public class DraftController {

    private final DraftService draftService;

    public DraftController(DraftService draftService) {
        this.draftService = draftService;
    }

    /**
     * Start the draft for a league.
     * 
     * POST /api/leagues/{leagueId}/draft/start
     */
    @PostMapping("/start")
    public ResponseEntity<?> startDraft(@PathVariable Long leagueId) {
        try {
            DraftService.DraftState state = draftService.startDraft(leagueId);
            return ResponseEntity.ok(state);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get current draft status.
     * 
     * GET /api/leagues/{leagueId}/draft/status
     */
    @GetMapping("/status")
    public ResponseEntity<?> getDraftStatus(@PathVariable Long leagueId) {
        DraftService.DraftState state = draftService.getDraftState(leagueId);
        if (state == null) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "NOT_STARTED");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.ok(state);
    }

    /**
     * Make a draft pick.
     * 
     * POST /api/leagues/{leagueId}/draft/pick
     * Body: { "leagueMemberId": 1, "playerId": 5 }
     */
    @PostMapping("/pick")
    public ResponseEntity<?> makePick(@PathVariable Long leagueId, @RequestBody Map<String, Object> request) {
        try {
            Long leagueMemberId = ((Number) request.get("leagueMemberId")).longValue();
            Long playerId = ((Number) request.get("playerId")).longValue();

            DraftService.DraftState state = draftService.makePick(leagueId, leagueMemberId, playerId);
            return ResponseEntity.ok(state);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get available players for drafting.
     * 
     * GET /api/leagues/{leagueId}/draft/available
     */
    @GetMapping("/available")
    public ResponseEntity<List<Player>> getAvailablePlayers(@PathVariable Long leagueId) {
        List<Player> players = draftService.getAvailablePlayers(leagueId);
        return ResponseEntity.ok(players);
    }
}
