package com.tennisfantasy.backend.controller;

import com.tennisfantasy.backend.dto.sportradar.RankingsResponse;
import com.tennisfantasy.backend.dto.sportradar.ScheduleResponse;
import com.tennisfantasy.backend.service.DataSyncService;
import com.tennisfantasy.backend.service.SportsRadarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for SportsRadar API endpoints.
 * 
 * These endpoints act as a proxy to the SportsRadar API,
 * keeping the API key secure on the server side.
 */
@RestController
@RequestMapping("/sportradar")
@CrossOrigin(origins = "http://localhost:3000")
public class SportsRadarController {

    private final SportsRadarService sportsRadarService;
    private final DataSyncService dataSyncService;

    public SportsRadarController(SportsRadarService sportsRadarService, DataSyncService dataSyncService) {
        this.sportsRadarService = sportsRadarService;
        this.dataSyncService = dataSyncService;
    }

    /**
     * Get all tennis rankings (ATP and WTA).
     * 
     * GET /api/sportradar/rankings
     */
    @GetMapping("/rankings")
    public ResponseEntity<RankingsResponse> getRankings() {
        RankingsResponse response = sportsRadarService.getRankings();
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Get ATP rankings only.
     * 
     * GET /api/sportradar/rankings/atp?limit=50
     */
    @GetMapping("/rankings/atp")
    public ResponseEntity<List<RankingsResponse.CompetitorRanking>> getAtpRankings(
            @RequestParam(defaultValue = "50") int limit) {
        List<RankingsResponse.CompetitorRanking> rankings = sportsRadarService.getAtpRankings(limit);
        return ResponseEntity.ok(rankings);
    }

    /**
     * Get WTA rankings only.
     * 
     * GET /api/sportradar/rankings/wta?limit=50
     */
    @GetMapping("/rankings/wta")
    public ResponseEntity<List<RankingsResponse.CompetitorRanking>> getWtaRankings(
            @RequestParam(defaultValue = "50") int limit) {
        List<RankingsResponse.CompetitorRanking> rankings = sportsRadarService.getWtaRankings(limit);
        return ResponseEntity.ok(rankings);
    }

    /**
     * Get today's match schedule.
     * 
     * GET /api/sportradar/schedule/today
     */
    @GetMapping("/schedule/today")
    public ResponseEntity<ScheduleResponse> getTodaySchedule() {
        ScheduleResponse response = sportsRadarService.getTodaySchedule();
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Get match schedule for a specific date.
     * 
     * GET /api/sportradar/schedule/2024-01-15
     */
    @GetMapping("/schedule/{date}")
    public ResponseEntity<ScheduleResponse> getScheduleByDate(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        ScheduleResponse response = sportsRadarService.getSchedule(localDate);
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Get live match scores.
     * 
     * GET /api/sportradar/live
     */
    @GetMapping("/live")
    public ResponseEntity<ScheduleResponse> getLiveScores() {
        ScheduleResponse response = sportsRadarService.getLiveScores();
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Manually trigger a data sync from SportsRadar.
     * This updates our database with latest rankings.
     * 
     * POST /api/sportradar/sync
     */
    @PostMapping("/sync")
    public ResponseEntity<Map<String, String>> triggerSync() {
        dataSyncService.triggerSync();

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Sync triggered successfully. Check logs for details.");

        return ResponseEntity.ok(response);
    }

    /**
     * Initialize sample data (for testing without API key).
     * 
     * POST /api/sportradar/init-sample
     */
    @PostMapping("/init-sample")
    public ResponseEntity<Map<String, String>> initializeSampleData() {
        dataSyncService.initializeSampleData();

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Sample data initialized. You can now view players at /api/players");

        return ResponseEntity.ok(response);
    }
}
