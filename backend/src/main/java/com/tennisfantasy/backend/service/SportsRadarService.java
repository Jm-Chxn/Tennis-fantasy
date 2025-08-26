package com.tennisfantasy.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tennisfantasy.backend.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SportsRadarService {
    // Stub method to simulate fetching players from SportsRadar
    public Mono<java.util.List<com.tennisfantasy.backend.model.Player>> fetchPlayers() {
        java.util.List<com.tennisfantasy.backend.model.Player> players = new java.util.ArrayList<>();

    com.tennisfantasy.backend.model.Player p1 = new com.tennisfantasy.backend.model.Player("Test", "Player1", "USA", 10);
    p1.setPoints(1000);
    p1.setPrice(new java.math.BigDecimal("10.00"));
    p1.setPosition("Singles");
    p1.setIsActive(true);

    com.tennisfantasy.backend.model.Player p2 = new com.tennisfantasy.backend.model.Player("Sample", "Player2", "UK", 20);
    p2.setPoints(900);
    p2.setPrice(new java.math.BigDecimal("9.00"));
    p2.setPosition("Doubles");
    p2.setIsActive(true);

    com.tennisfantasy.backend.model.Player p3 = new com.tennisfantasy.backend.model.Player("Demo", "Player3", "Canada", 30);
    p3.setPoints(800);
    p3.setPrice(new java.math.BigDecimal("8.00"));
    p3.setPosition("Singles");
    p3.setIsActive(true);

    players.add(p1);
    players.add(p2);
    players.add(p3);
    return Mono.just(players);
    }
    
    private final WebClient sportsRadarWebClient;
    private final SupabaseService supabaseService;
    private final ObjectMapper objectMapper;
    
    @Value("${sportsradar.api.key}")
    private String apiKey;
    
    @Autowired
    public SportsRadarService(WebClient.Builder webClientBuilder, 
                            SupabaseService supabaseService) {
        this.sportsRadarWebClient = webClientBuilder
                .baseUrl("https://api.sportsradar.com")
                .build();
        this.supabaseService = supabaseService;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Fetch tennis players from SportsRadar API
     * This is a template - you'll need to adjust the endpoint and data structure
     * based on SportsRadar's actual API documentation
     */
    public Mono<String> fetchTennisPlayers() {
        // Example endpoint - adjust based on SportsRadar's actual API
        String endpoint = "/tennis/trial/v3/en/players.json?api_key=" + apiKey;
        
        return sportsRadarWebClient.get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("Failed to fetch players from SportsRadar");
    }
    
    /**
     * Process SportsRadar data and save filtered players to Supabase
     * This method demonstrates the complete flow you want to implement
     */
    public Mono<String> processAndSavePlayers() {
        return fetchTennisPlayers()
                .flatMapMany(response -> parseAndFilterPlayers(response))
                .flatMap(player -> supabaseService.createPlayer(player))
                .collectList()
                .map(players -> "Successfully processed and saved " + players.size() + " players to Supabase");
    }
    
    /**
     * Parse SportsRadar response and filter players based on your criteria
     * This is where you'll implement your filtering logic
     */
    private Flux<Player> parseAndFilterPlayers(String sportsRadarResponse) {
        try {
            // Parse the JSON response from SportsRadar
            JsonNode rootNode = objectMapper.readTree(sportsRadarResponse);
            
            // Extract players array - adjust based on actual SportsRadar response structure
            JsonNode playersNode = rootNode.path("players");
            
            if (playersNode.isArray()) {
                return Flux.fromIterable(playersNode)
                        .map(this::convertToPlayer)
                        .filter(this::applyFilterCriteria); // Apply your filtering logic
            }
            
            return Flux.empty();
            
        } catch (Exception e) {
            return Flux.error(new RuntimeException("Error parsing SportsRadar response", e));
        }
    }
    
    /**
     * Convert SportsRadar player data to your Player model
     * Adjust this method based on the actual SportsRadar data structure
     */
    private Player convertToPlayer(JsonNode playerNode) {
        Player player = new Player();
        
        // Extract fields from SportsRadar response - adjust field names as needed
        player.setFirstName(playerNode.path("first_name").asText(""));
        player.setLastName(playerNode.path("last_name").asText(""));
        player.setCountry(playerNode.path("country").asText(""));
        player.setRanking(playerNode.path("ranking").asInt(999));
        player.setPoints(playerNode.path("points").asInt(0));
        player.setPosition(playerNode.path("position").asText("Singles"));
        player.setIsActive(true);
        
        // Set a default price or calculate based on ranking/points
        BigDecimal price = calculatePrice(playerNode.path("ranking").asInt(999));
        player.setPrice(price);
        
        return player;
    }
    
    /**
     * Apply your filtering criteria to determine which players to save
     * This is where you implement your business logic
     */
    private boolean applyFilterCriteria(Player player) {
        // Example filtering criteria - customize based on your needs
        
        // Only include active players
        if (!player.getIsActive()) {
            return false;
        }
        
        // Only include players with a ranking (not unranked)
        if (player.getRanking() == null || player.getRanking() > 1000) {
            return false;
        }
        
        // Only include players from specific countries (optional)
        List<String> allowedCountries = List.of("USA", "Spain", "Serbia", "Switzerland", "Poland", "Belarus");
        if (!allowedCountries.contains(player.getCountry())) {
            return false;
        }
        
        // Only include players with minimum points
        if (player.getPoints() != null && player.getPoints() < 1000) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Calculate player price based on ranking and other factors
     * Customize this logic based on your fantasy sports pricing model
     */
    private BigDecimal calculatePrice(Integer ranking) {
        if (ranking == null || ranking <= 0) {
            return new BigDecimal("5.00");
        }
        
        // Simple pricing model - adjust as needed
        if (ranking <= 10) {
            return new BigDecimal("20.00");
        } else if (ranking <= 50) {
            return new BigDecimal("15.00");
        } else if (ranking <= 100) {
            return new BigDecimal("10.00");
        } else {
            return new BigDecimal("5.00");
        }
    }
    
    /**
     * Test method to verify SportsRadar API connection
     */
    public Mono<String> testConnection() {
        return fetchTennisPlayers()
                .map(response -> "SportsRadar connection successful. Response length: " + response.length())
                .onErrorReturn("SportsRadar connection failed");
    }
}
