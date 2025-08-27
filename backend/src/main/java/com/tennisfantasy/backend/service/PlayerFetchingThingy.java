package com.tennisfantasy.backend.service;

import com.tennisfantasy.backend.model.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import reactor.core.publisher.Mono;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
@Service
public class PlayerFetchingThingy {

    private final WebClient sportsradarWebClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public PlayerFetchingThingy(@Qualifier("sportsradarWebClient") WebClient sportsradarWebClient) {
        this.sportsradarWebClient = sportsradarWebClient;
        this.objectMapper = new ObjectMapper();
    }

    public void fetchAndSavePlayers(String locale, String apiKey) throws Exception {
        System.out.println("🎾 Fetching ATP rankings from SportsRadar...");
        
        String json = sportsradarWebClient.get()
                .uri(builder -> builder
                        .path("/{locale}/rankings")
                        .queryParam("api_key", apiKey)
                        .build(locale))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    
        System.out.println("📡 API Response received, length: " + json.length());
        System.out.println("📊 First 500 characters of response:");
        System.out.println(json.substring(0, Math.min(500, json.length())));
        
        List<Player> players = parseRankings(json);
        System.out.println("✅ Parsed " + players.size() + " players successfully!");
        
        // Display first few players for verification
        System.out.println("📋 First 10 players:");
        for (int i = 0; i < Math.min(10, players.size()); i++) {
            Player p = players.get(i);
            System.out.println("  " + (i+1) + ". " + p.getFirstName() + " " + p.getLastName() + 
                             " (" + p.getCountry() + ") - Rank: " + p.getRank() + " - Cost: $" + p.getCost());
        }
    
        // Write to CSV file for verification
        writePlayersToCSV(players);
    }
    
    /**
     * 📝 Write players to CSV file for verification
     */
    private void writePlayersToCSV(List<Player> players) {
        try {
            String fileName = "sportsradar_players_" + System.currentTimeMillis() + ".csv";
            FileWriter csvWriter = new FileWriter(fileName);
            
            // Write CSV header
            csvWriter.append("Rank,First Name,Last Name,Country,Cost\n");
            
            // Write player data
            for (Player player : players) {
                csvWriter.append(String.valueOf(player.getRank()));
                csvWriter.append(",");
                csvWriter.append(player.getFirstName());
                csvWriter.append(",");
                csvWriter.append(player.getLastName());
                csvWriter.append(",");
                csvWriter.append(player.getCountry());
                csvWriter.append(",");
                csvWriter.append(String.valueOf(player.getCost()));
                csvWriter.append("\n");
            }
            
            csvWriter.flush();
            csvWriter.close();
            
            System.out.println("✅ Successfully wrote " + players.size() + " players to CSV file: " + fileName);
            System.out.println("📁 File location: " + System.getProperty("user.dir") + "\\" + fileName);
            
        } catch (IOException e) {
            System.err.println("❌ Failed to write CSV file: " + e.getMessage());
        } 
    }


    public List<Player> parseRankings(String json) throws Exception {
        System.out.println("🔄 Parsing SportsRadar rankings...");
        RankingResponse response = objectMapper.readValue(json, RankingResponse.class);

        List<Player> players = new ArrayList<>();
        int totalParsed = 0;

        for (Rankings rankingGroup : response.getRankings()) {
            System.out.println("📊 Processing ranking group: " + rankingGroup.getName() + 
                             " (Gender: " + rankingGroup.getGender() + ")");
            
            // Only process men's ATP rankings
            if ("men".equalsIgnoreCase(rankingGroup.getGender()) && 
                "ATP".equalsIgnoreCase(rankingGroup.getName())) {
                
                System.out.println("✅ Found ATP Men's rankings with " + rankingGroup.getRankings().size() + " players");
                
                for (CompetitorRanking compRank : rankingGroup.getRankings()) {
                    Competitor c = compRank.getCompetitor();

                    Player playa = new Player();

                    // Parse player name (handle different formats)
                    String fullName = c.getName();
                    String firstName = "";
                    String lastName = "";
                    if (fullName != null && !fullName.isEmpty()) {
                        if (fullName.contains(",")) {
                            // Format: "Djokovic, Novak" 
                            String[] parts = fullName.split(",", 2);
                            lastName = parts[0].trim();
                            firstName = parts.length > 1 ? parts[1].trim() : "";
                        } else {
                            // Format: "Novak Djokovic"
                            String[] parts = fullName.split(" ", 2);
                            firstName = parts[0].trim();
                            lastName = parts.length > 1 ? parts[1].trim() : "";
                        }
                    }

                    playa.setFirstName(firstName);
                    playa.setLastName(lastName);
                    playa.setCountry(c.getCountry());
                    playa.setRank(compRank.getRank());
                    
                    // Calculate cost based on ranking
                    playa.setCost(calculatePlayerCost(compRank.getRank()));

                    players.add(playa);
                    totalParsed++;
                    
                    // Limit to top 500 players
                    if (totalParsed >= 500) {
                        System.out.println("🔢 Reached 500 players limit, stopping...");
                        break;
                    }
                }
                
                if (totalParsed >= 500) break;
            }
        }

        System.out.println("✅ Successfully parsed " + players.size() + " male singles players");
        return players;
    }
    
    /**
     * Calculate fantasy cost based on ATP ranking
     */
    private java.math.BigDecimal calculatePlayerCost(Integer rank) {
        if (rank == null) return new java.math.BigDecimal("2.00");
        
        // Pricing tiers based on ranking
        if (rank <= 5) return new java.math.BigDecimal("15.00");      // Top 5: Premium
        if (rank <= 10) return new java.math.BigDecimal("12.00");     // Top 10: Expensive  
        if (rank <= 20) return new java.math.BigDecimal("10.00");     // Top 20: High
        if (rank <= 50) return new java.math.BigDecimal("7.00");      // Top 50: Medium-High
        if (rank <= 100) return new java.math.BigDecimal("5.00");     // Top 100: Medium
        if (rank <= 200) return new java.math.BigDecimal("3.50");     // Top 200: Low-Medium
        if (rank <= 300) return new java.math.BigDecimal("2.50");     // Top 300: Low
        return new java.math.BigDecimal("2.00");                      // 300+: Budget
    }
}