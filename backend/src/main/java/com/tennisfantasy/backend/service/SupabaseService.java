package com.tennisfantasy.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tennisfantasy.backend.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class SupabaseService {
    
    private final WebClient supabaseWebClient;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public SupabaseService(WebClient supabaseWebClient) {
        this.supabaseWebClient = supabaseWebClient;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setPropertyNamingStrategy(com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE);
    }
    
    // Create a new player in Supabase
    public Mono<Player> createPlayer(Player player) {
        try {
            // Remove the ID field for insert (Supabase auto-generates it)
            Player playerForInsert = new Player();
            playerForInsert.setFirstName(player.getFirstName());
            playerForInsert.setLastName(player.getLastName());
            playerForInsert.setCountry(player.getCountry());
            playerForInsert.setRank(player.getRank());
            playerForInsert.setCost(player.getCost());
            
            String playerJson = objectMapper.writeValueAsString(playerForInsert);
            return supabaseWebClient.post()
                    .uri("/players")
                    .header("Prefer", "return=representation")
                    .bodyValue(playerJson)
                    .retrieve()
                    .bodyToMono(Player.class);
        } catch (JsonProcessingException e) {
            return Mono.error(new RuntimeException("Error serializing player data", e));
        }
    }
    
    // Create a single player in Supabase
    public Mono<Player> createPlayer(Player player) {
        try {
            String playerJson = objectMapper.writeValueAsString(player);
            System.out.println("Sending single player JSON to Supabase: " + playerJson);
            
            return supabaseWebClient.post()
                    .uri("/players")
                    .header("Prefer", "return=representation")
                    .bodyValue(playerJson)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(response -> System.out.println("Supabase single player response: " + response))
                    .doOnError(error -> System.out.println("Supabase single player error: " + error.getMessage()))
                    .map(json -> {
                        try {
                            return objectMapper.readValue(json, Player.class);
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Error deserializing player", e);
                        }
                    });
        } catch (JsonProcessingException e) {
            return Mono.error(new RuntimeException("Error serializing player data", e));
        }
    }

    // Bulk create players in Supabase
    public Mono<List<Player>> createPlayers(List<Player> players) {
        try {
            String playersJson = objectMapper.writeValueAsString(players);
            System.out.println("Sending JSON to Supabase: " + playersJson);
            
            return supabaseWebClient.post()
                    .uri("/players")
                    .header("Prefer", "return=representation")
                    .bodyValue(playersJson)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(response -> System.out.println("Supabase response: " + response))
                    .doOnError(error -> System.out.println("Supabase error: " + error.getMessage()))
                    .map(json -> {
                        try {
                            return objectMapper.readValue(json, 
                                objectMapper.getTypeFactory().constructCollectionType(List.class, Player.class));
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException("Error deserializing players", e);
                        }
                    });
        } catch (JsonProcessingException e) {
            return Mono.error(new RuntimeException("Error serializing players data", e));
        }
    }
    
    // Get all players from Supabase
    public Mono<List<Player>> getAllPlayers() {
        return supabaseWebClient.get()
                .uri("/players?select=*")
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, 
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Player.class));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Error deserializing players", e);
                    }
                });
    }
    
    // Get player by ID from Supabase
    public Mono<Optional<Player>> getPlayerById(Long id) {
        return supabaseWebClient.get()
                .uri("/players?id=eq." + id + "&select=*")
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> {
                    try {
                        List<Player> players = objectMapper.readValue(json, 
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Player.class));
                        return players.isEmpty() ? Optional.empty() : Optional.of(players.get(0));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Error deserializing player", e);
                    }
                });
    }
    
    // Update player in Supabase
    public Mono<Player> updatePlayer(Long id, Player playerDetails) {
        try {
            String playerJson = objectMapper.writeValueAsString(playerDetails);
            return supabaseWebClient.patch()
                    .uri("/players?id=eq." + id)
                    .bodyValue(playerJson)
                    .retrieve()
                    .bodyToMono(String.class)
                    .then(Mono.just(playerDetails));
        } catch (JsonProcessingException e) {
            return Mono.error(new RuntimeException("Error serializing player data", e));
        }
    }
    
    // Delete player from Supabase
    public Mono<Void> deletePlayer(Long id) {
        return supabaseWebClient.delete()
                .uri("/players?id=eq." + id)
                .retrieve()
                .bodyToMono(Void.class);
    }
    
    // Get players by country from Supabase
    public Mono<List<Player>> getPlayersByCountry(String country) {
        return supabaseWebClient.get()
                .uri("/players?country=eq." + country + "&select=*")
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> {
                    try {
                        return objectMapper.readValue(json, 
                            objectMapper.getTypeFactory().constructCollectionType(List.class, Player.class));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Error deserializing players", e);
                    }
                });
    }
    
    // Test connection to Supabase
    public Mono<String> testConnection() {
        return supabaseWebClient.get()
                .uri("/players?select=count")
                .retrieve()
                .bodyToMono(String.class)
                .onErrorReturn("Connection failed");
    }
}
