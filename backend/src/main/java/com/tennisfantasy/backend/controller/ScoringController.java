package com.tennisfantasy.backend.controller;

import com.tennisfantasy.backend.model.FantasyPoints;
import com.tennisfantasy.backend.model.LeagueMember;
import com.tennisfantasy.backend.repository.LeagueMemberRepository;
import com.tennisfantasy.backend.service.ScoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for Scoring and Standings endpoints.
 */
@RestController
@RequestMapping("/leagues/{leagueId}")
@CrossOrigin(origins = "http://localhost:3000")
public class ScoringController {

    private final ScoringService scoringService;
    private final LeagueMemberRepository leagueMemberRepository;

    public ScoringController(ScoringService scoringService, LeagueMemberRepository leagueMemberRepository) {
        this.scoringService = scoringService;
        this.leagueMemberRepository = leagueMemberRepository;
    }

    /**
     * Get league standings.
     * 
     * GET /api/leagues/{leagueId}/standings
     */
    @GetMapping("/standings")
    public ResponseEntity<List<LeagueMember>> getStandings(@PathVariable Long leagueId) {
        List<LeagueMember> standings = leagueMemberRepository.findLeagueStandings(leagueId);

        // Update ranks
        int rank = 1;
        for (LeagueMember member : standings) {
            member.setLeagueRank(rank++);
        }

        return ResponseEntity.ok(standings);
    }

    /**
     * Get points breakdown for a team.
     * 
     * GET /api/leagues/{leagueId}/points?userId=1
     */
    @GetMapping("/points")
    public ResponseEntity<?> getPointsBreakdown(@PathVariable Long leagueId, @RequestParam Long userId) {
        return leagueMemberRepository.findByLeagueIdAndUserId(leagueId, userId)
                .map(member -> {
                    List<FantasyPoints> points = scoringService.getPointsBreakdown(member.getId());
                    return ResponseEntity.ok(points);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Trigger point recalculation for recent matches.
     * 
     * POST /api/leagues/{leagueId}/recalculate
     */
    @PostMapping("/recalculate")
    public ResponseEntity<Map<String, String>> recalculatePoints(@PathVariable Long leagueId) {
        scoringService.processRecentMatches();

        Map<String, String> response = new HashMap<>();
        response.put("message", "Points recalculated successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Get scoring rules.
     * 
     * GET /api/leagues/{leagueId}/scoring-rules
     */
    @GetMapping("/scoring-rules")
    public ResponseEntity<Map<String, Integer>> getScoringRules(@PathVariable Long leagueId) {
        Map<String, Integer> rules = new HashMap<>();
        rules.put("matchWin", ScoringService.POINTS_MATCH_WIN);
        rules.put("matchWinGrandSlam", ScoringService.POINTS_MATCH_WIN_GRAND_SLAM);
        rules.put("setWin", ScoringService.POINTS_SET_WIN);
        rules.put("upsetBonus", ScoringService.POINTS_UPSET_BONUS);
        rules.put("finalReach", ScoringService.POINTS_FINAL_REACH);
        rules.put("tournamentWin", ScoringService.POINTS_TOURNAMENT_WIN);
        rules.put("tournamentWinGrandSlam", ScoringService.POINTS_TOURNAMENT_WIN_GRAND_SLAM);
        return ResponseEntity.ok(rules);
    }
}
