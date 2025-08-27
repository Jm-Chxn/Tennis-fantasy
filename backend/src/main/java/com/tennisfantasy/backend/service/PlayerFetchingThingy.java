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
@Service
public class PlayerFetchingThingy {

    private final WebClient sportsradarWebClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public PlayerFetchingThingy(@Qualifier("sportsradarWebClient") WebClient sportsradarWebClient) {
        this.sportsradarWebClient = sportsradarWebClient;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setPropertyNamingStrategy(com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE);
    }

    public void fetchAndSavePlayers(String locale, String apiKey) throws Exception {
        String json = sportsradarWebClient.get()
                .uri(builder -> builder
                        .path("/{locale}/rankings")
                        .queryParam("api_key", apiKey)
                        .build(locale))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    
        List<Player> players = parseRankings(json);
    
        for (Player playa : players) {
        //jim add the superbase insert code here 
        }
    }


    public List<Player> parseRankings(String json) throws Exception {
        RankingResponse response = objectMapper.readValue(json, RankingResponse.class);

        List<Player> players = new ArrayList<>();

        for (Rankings rankingGroup : response.getRankings()) {

            for (CompetitorRanking compRank : rankingGroup.getRankings()){
                Competitor c = compRank.getCompetitor();


                Player playa = new Player();

                //#bro why didnt you just make the name field one entire field like why do we need first name and last name fields seperate bruh

                String fullName = c.getName();
                String firstName = "";
                String lastName = "";
                if (fullName != null && !fullName.isEmpty()) {
                    String[] parts = fullName.split(" ", 2); // split into max 2 parts
                    firstName = parts[0];
                    if (parts.length > 1) {
                        lastName = parts[1];
                    }
                }

                playa.setFirstName(firstName);
                playa.setLastName(lastName);
                playa.setCountry(c.getCountry());
                playa.setRank(compRank.getRank());
                //add formula for calculating cost later also why the fuck is cost type big decimal and not int??
                // int cost = 0;
                // playa.setCost(int);

                players.add(playa);

            }

        





        }

        return players;
    }
}