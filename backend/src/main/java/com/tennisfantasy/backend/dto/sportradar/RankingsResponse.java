package com.tennisfantasy.backend.dto.sportradar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO for SportsRadar Rankings API response.
 * 
 * Example response structure:
 * {
 * "rankings": [
 * {
 * "type": "ATP",
 * "name": "ATP Singles",
 * "competitor_rankings": [...]
 * }
 * ]
 * }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RankingsResponse {

    private List<RankingType> rankings;

    public List<RankingType> getRankings() {
        return rankings;
    }

    public void setRankings(List<RankingType> rankings) {
        this.rankings = rankings;
    }

    /**
     * Represents a ranking type (e.g., "ATP Singles", "WTA Singles")
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RankingType {
        private String type;
        private String name;

        @JsonProperty("competitor_rankings")
        private List<CompetitorRanking> competitorRankings;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<CompetitorRanking> getCompetitorRankings() {
            return competitorRankings;
        }

        public void setCompetitorRankings(List<CompetitorRanking> competitorRankings) {
            this.competitorRankings = competitorRankings;
        }
    }

    /**
     * Represents a single competitor's ranking entry
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CompetitorRanking {
        private Integer rank;
        private Integer points;

        @JsonProperty("competitions_played")
        private Integer competitionsPlayed;

        private CompetitorInfo competitor;

        public Integer getRank() {
            return rank;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }

        public Integer getPoints() {
            return points;
        }

        public void setPoints(Integer points) {
            this.points = points;
        }

        public Integer getCompetitionsPlayed() {
            return competitionsPlayed;
        }

        public void setCompetitionsPlayed(Integer competitionsPlayed) {
            this.competitionsPlayed = competitionsPlayed;
        }

        public CompetitorInfo getCompetitor() {
            return competitor;
        }

        public void setCompetitor(CompetitorInfo competitor) {
            this.competitor = competitor;
        }
    }

    /**
     * Basic competitor information within a ranking
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CompetitorInfo {
        private String id;
        private String name;
        private String country;

        @JsonProperty("country_code")
        private String countryCode;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }
    }
}
