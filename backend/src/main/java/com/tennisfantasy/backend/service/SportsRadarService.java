package com.tennisfantasy.backend.service;

import com.tennisfantasy.backend.config.SportsRadarConfig;
import com.tennisfantasy.backend.dto.sportradar.RankingsResponse;
import com.tennisfantasy.backend.dto.sportradar.ScheduleResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * Service for fetching data from SportsRadar Tennis API.
 * 
 * This service handles all communication with the SportsRadar API.
 * Results are cached to avoid hitting API rate limits.
 * 
 * Key endpoints used:
 * - /rankings.json - Get ATP/WTA rankings
 * - /schedules/{date}/schedule.json - Get matches for a date
 * - /schedules/live/schedule.json - Get live matches
 */
@Service
public class SportsRadarService {

    private static final Logger logger = LoggerFactory.getLogger(SportsRadarService.class);

    private final WebClient webClient;
    private final SportsRadarConfig config;

    public SportsRadarService(WebClient webClient, SportsRadarConfig config) {
        this.webClient = webClient;
        this.config = config;
    }

    /**
     * Fetch current ATP and WTA rankings.
     * Results are cached for 10 minutes.
     * 
     * @return RankingsResponse containing all ranking data
     */
    @Cacheable(value = "rankings")
    public RankingsResponse getRankings() {
        logger.info("Fetching rankings from SportsRadar API");

        try {
            // Full URL: https://api.sportradar.com/tennis/trial/v3/en/rankings.json
            String url = config.buildUrl("/rankings.json");

            RankingsResponse response = webClient.get()
                    .uri(url)
                    .header("accept", "application/json")
                    .header("x-api-key", config.getApiKey())
                    .retrieve()
                    .bodyToMono(RankingsResponse.class)
                    .block();

            logger.info("Successfully fetched rankings");
            return response;

        } catch (WebClientResponseException e) {
            logger.error("SportsRadar API error: {} - {}", e.getStatusCode(), e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Error fetching rankings: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get ATP rankings only.
     * 
     * @param limit Maximum number of players to return
     * @return List of competitor rankings
     */
    public List<RankingsResponse.CompetitorRanking> getAtpRankings(int limit) {
        RankingsResponse response = getRankings();
        if (response == null || response.getRankings() == null) {
            return Collections.emptyList();
        }

        return response.getRankings().stream()
                .filter(r -> "ATP".equalsIgnoreCase(r.getName()))
                .flatMap(r -> r.getCompetitorRankings().stream())
                .limit(limit)
                .toList();
    }

    /**
     * Get WTA rankings only.
     * 
     * @param limit Maximum number of players to return
     * @return List of competitor rankings
     */
    public List<RankingsResponse.CompetitorRanking> getWtaRankings(int limit) {
        RankingsResponse response = getRankings();
        if (response == null || response.getRankings() == null) {
            return Collections.emptyList();
        }

        return response.getRankings().stream()
                .filter(r -> "WTA".equalsIgnoreCase(r.getName()))
                .flatMap(r -> r.getCompetitorRankings().stream())
                .limit(limit)
                .toList();
    }

    /**
     * Fetch match schedule for a specific date.
     * 
     * @param date The date to get schedule for
     * @return ScheduleResponse containing all matches
     */
    @Cacheable(value = "schedule", key = "#date.toString()")
    public ScheduleResponse getSchedule(LocalDate date) {
        logger.info("Fetching schedule for date: {}", date);

        try {
            String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
            String url = config.buildUrl("/schedules/" + dateStr + "/schedule.json");

            ScheduleResponse response = webClient.get()
                    .uri(url)
                    .header("accept", "application/json")
                    .header("x-api-key", config.getApiKey())
                    .retrieve()
                    .bodyToMono(ScheduleResponse.class)
                    .block();

            logger.info("Successfully fetched schedule for {}", date);
            return response;

        } catch (WebClientResponseException e) {
            logger.error("SportsRadar API error: {} - {}", e.getStatusCode(), e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Error fetching schedule: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Fetch live match scores.
     * This is NOT cached because we want real-time data.
     * 
     * @return ScheduleResponse containing live matches
     */
    public ScheduleResponse getLiveScores() {
        logger.info("Fetching live scores from SportsRadar API");

        try {
            String url = config.buildUrl("/schedules/live/schedule.json");

            ScheduleResponse response = webClient.get()
                    .uri(url)
                    .header("accept", "application/json")
                    .header("x-api-key", config.getApiKey())
                    .retrieve()
                    .bodyToMono(ScheduleResponse.class)
                    .block();

            logger.info("Successfully fetched live scores");
            return response;

        } catch (WebClientResponseException e) {
            logger.error("SportsRadar API error: {} - {}", e.getStatusCode(), e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Error fetching live scores: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Get today's match schedule.
     * 
     * @return ScheduleResponse for today
     */
    public ScheduleResponse getTodaySchedule() {
        return getSchedule(LocalDate.now());
    }
}
