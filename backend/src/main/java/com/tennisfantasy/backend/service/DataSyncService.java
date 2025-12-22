package com.tennisfantasy.backend.service;

import com.tennisfantasy.backend.dto.sportradar.RankingsResponse;
import com.tennisfantasy.backend.model.Player;
import com.tennisfantasy.backend.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for syncing data from SportsRadar API to our database.
 * 
 * Uses Spring's @Scheduled annotation to periodically fetch updates.
 * This keeps our player rankings and match data up to date.
 * 
 * IMPORTANT: This only runs when you have a valid SportsRadar API key!
 */
@Service
@EnableScheduling
public class DataSyncService {

    private static final Logger logger = LoggerFactory.getLogger(DataSyncService.class);

    private final SportsRadarService sportsRadarService;
    private final PlayerRepository playerRepository;

    public DataSyncService(SportsRadarService sportsRadarService, PlayerRepository playerRepository) {
        this.sportsRadarService = sportsRadarService;
        this.playerRepository = playerRepository;
    }

    /**
     * Sync player rankings from SportsRadar.
     * Runs every 6 hours automatically.
     * 
     * You can also call this manually via the API.
     */
    @Scheduled(fixedRate = 6 * 60 * 60 * 1000) // Every 6 hours
    public void syncRankings() {
        logger.info("Starting rankings sync from SportsRadar...");

        try {
            // Sync ATP rankings (top 100)
            syncTourRankings("ATP", 100);

            // Sync WTA rankings (top 100)
            syncTourRankings("WTA", 100);

            logger.info("Rankings sync completed successfully");

        } catch (Exception e) {
            logger.error("Error during rankings sync: {}", e.getMessage());
        }
    }

    /**
     * Sync rankings for a specific tour (ATP or WTA).
     * 
     * @param tour  The tour to sync ("ATP" or "WTA")
     * @param limit Number of players to sync
     */
    private void syncTourRankings(String tour, int limit) {
        List<RankingsResponse.CompetitorRanking> rankings;

        if ("ATP".equalsIgnoreCase(tour)) {
            rankings = sportsRadarService.getAtpRankings(limit);
        } else {
            rankings = sportsRadarService.getWtaRankings(limit);
        }

        if (rankings == null || rankings.isEmpty()) {
            logger.warn("No {} rankings data received from SportsRadar", tour);
            return;
        }

        int created = 0;
        int updated = 0;

        for (RankingsResponse.CompetitorRanking ranking : rankings) {
            if (ranking.getCompetitor() == null)
                continue;

            RankingsResponse.CompetitorInfo competitor = ranking.getCompetitor();
            String sportradarId = competitor.getId();

            // Check if player already exists
            Player player = playerRepository.findBySportradarId(sportradarId).orElse(null);

            if (player == null) {
                // Create new player
                player = createPlayerFromRanking(ranking, tour);
                created++;
            } else {
                // Update existing player
                updatePlayerFromRanking(player, ranking);
                updated++;
            }

            playerRepository.save(player);
        }

        logger.info("{} sync complete: {} created, {} updated", tour, created, updated);
    }

    /**
     * Create a new Player entity from SportsRadar ranking data.
     */
    private Player createPlayerFromRanking(RankingsResponse.CompetitorRanking ranking, String tour) {
        RankingsResponse.CompetitorInfo competitor = ranking.getCompetitor();

        // Parse name (SportsRadar returns "Last, First" or "First Last")
        String[] nameParts = parsePlayerName(competitor.getName());

        Player player = new Player();
        player.setSportradarId(competitor.getId());
        player.setFirstName(nameParts[0]);
        player.setLastName(nameParts[1]);
        player.setCountry(competitor.getCountry() != null ? competitor.getCountry() : "Unknown");
        player.setCountryCode(competitor.getCountryCode());
        player.setRanking(ranking.getRank());
        player.setPoints(ranking.getPoints());
        player.setTour(tour);
        player.setPosition("Singles");
        player.setIsActive(true);
        player.setPrice(calculateFantasyPrice(ranking.getRank()));
        player.setLastSyncedAt(LocalDateTime.now());

        return player;
    }

    /**
     * Update an existing Player with new ranking data.
     */
    private void updatePlayerFromRanking(Player player, RankingsResponse.CompetitorRanking ranking) {
        player.setRanking(ranking.getRank());
        player.setPoints(ranking.getPoints());
        player.setPrice(calculateFantasyPrice(ranking.getRank()));
        player.setLastSyncedAt(LocalDateTime.now());
    }

    /**
     * Parse player name from SportsRadar format.
     * Handles both "Djokovic, Novak" and "Novak Djokovic" formats.
     */
    private String[] parsePlayerName(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return new String[] { "Unknown", "Player" };
        }

        // If name contains comma, it's "Last, First" format
        if (fullName.contains(",")) {
            String[] parts = fullName.split(",");
            String lastName = parts[0].trim();
            String firstName = parts.length > 1 ? parts[1].trim() : "";
            return new String[] { firstName, lastName };
        }

        // Otherwise assume "First Last" format
        String[] parts = fullName.split(" ");
        if (parts.length >= 2) {
            String firstName = parts[0];
            String lastName = String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length));
            return new String[] { firstName, lastName };
        }

        return new String[] { fullName, "" };
    }

    /**
     * Calculate fantasy price based on ranking.
     * Higher ranked players cost more.
     * 
     * Top 10: $15-20
     * Top 25: $12-15
     * Top 50: $8-12
     * Top 100: $5-8
     */
    private BigDecimal calculateFantasyPrice(Integer ranking) {
        if (ranking == null)
            return new BigDecimal("5.00");

        if (ranking <= 5) {
            return new BigDecimal("20.00").subtract(new BigDecimal(ranking - 1).multiply(new BigDecimal("1.00")));
        } else if (ranking <= 10) {
            return new BigDecimal("15.00").subtract(new BigDecimal(ranking - 5).multiply(new BigDecimal("0.50")));
        } else if (ranking <= 25) {
            return new BigDecimal("12.00").subtract(new BigDecimal(ranking - 10).multiply(new BigDecimal("0.20")));
        } else if (ranking <= 50) {
            return new BigDecimal("9.00").subtract(new BigDecimal(ranking - 25).multiply(new BigDecimal("0.12")));
        } else if (ranking <= 100) {
            return new BigDecimal("6.00").subtract(new BigDecimal(ranking - 50).multiply(new BigDecimal("0.04")));
        }

        return new BigDecimal("4.00");
    }

    /**
     * Manually trigger a sync (called via API).
     */
    public void triggerSync() {
        syncRankings();
    }

    /**
     * Initialize with sample data if database is empty.
     * This is useful for testing without a SportsRadar API key.
     */
    public void initializeSampleData() {
        if (playerRepository.count() > 0) {
            logger.info("Database already has players, skipping sample data");
            return;
        }

        logger.info("Initializing sample player data...");

        // ATP Players
        createSamplePlayer("Novak", "Djokovic", "Serbia", "SRB", 1, 11245, "ATP");
        createSamplePlayer("Carlos", "Alcaraz", "Spain", "ESP", 2, 9255, "ATP");
        createSamplePlayer("Jannik", "Sinner", "Italy", "ITA", 3, 8710, "ATP");
        createSamplePlayer("Daniil", "Medvedev", "Russia", "RUS", 4, 6525, "ATP");
        createSamplePlayer("Andrey", "Rublev", "Russia", "RUS", 5, 4805, "ATP");
        createSamplePlayer("Alexander", "Zverev", "Germany", "GER", 6, 4705, "ATP");
        createSamplePlayer("Holger", "Rune", "Denmark", "DEN", 7, 3760, "ATP");
        createSamplePlayer("Hubert", "Hurkacz", "Poland", "POL", 8, 3495, "ATP");
        createSamplePlayer("Taylor", "Fritz", "USA", "USA", 9, 3390, "ATP");
        createSamplePlayer("Casper", "Ruud", "Norway", "NOR", 10, 3315, "ATP");

        // WTA Players
        createSamplePlayer("Iga", "Swiatek", "Poland", "POL", 1, 10715, "WTA");
        createSamplePlayer("Aryna", "Sabalenka", "Belarus", "BLR", 2, 8725, "WTA");
        createSamplePlayer("Coco", "Gauff", "USA", "USA", 3, 7150, "WTA");
        createSamplePlayer("Elena", "Rybakina", "Kazakhstan", "KAZ", 4, 5848, "WTA");
        createSamplePlayer("Jessica", "Pegula", "USA", "USA", 5, 5705, "WTA");
        createSamplePlayer("Ons", "Jabeur", "Tunisia", "TUN", 6, 4931, "WTA");
        createSamplePlayer("Qinwen", "Zheng", "China", "CHN", 7, 4640, "WTA");
        createSamplePlayer("Maria", "Sakkari", "Greece", "GRE", 8, 4451, "WTA");
        createSamplePlayer("Marketa", "Vondrousova", "Czech Republic", "CZE", 9, 4075, "WTA");
        createSamplePlayer("Karolina", "Muchova", "Czech Republic", "CZE", 10, 3960, "WTA");

        logger.info("Sample data initialized with 20 players");
    }

    /**
     * Helper method to create a sample player.
     */
    private void createSamplePlayer(String firstName, String lastName, String country,
            String countryCode, int ranking, int points, String tour) {
        Player player = new Player();
        player.setSportradarId("sample:" + firstName.toLowerCase() + "-" + lastName.toLowerCase());
        player.setFirstName(firstName);
        player.setLastName(lastName);
        player.setCountry(country);
        player.setCountryCode(countryCode);
        player.setRanking(ranking);
        player.setPoints(points);
        player.setTour(tour);
        player.setPosition("Singles");
        player.setIsActive(true);
        player.setPrice(calculateFantasyPrice(ranking));
        player.setLastSyncedAt(LocalDateTime.now());

        playerRepository.save(player);
    }
}
