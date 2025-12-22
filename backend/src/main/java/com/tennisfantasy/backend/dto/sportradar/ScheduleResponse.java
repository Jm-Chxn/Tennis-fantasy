package com.tennisfantasy.backend.dto.sportradar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * DTO for SportsRadar Daily Schedule API response.
 * 
 * This contains all matches scheduled for a specific date.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScheduleResponse {

    @JsonProperty("sport_events")
    private List<SportEvent> sportEvents;

    public List<SportEvent> getSportEvents() {
        return sportEvents;
    }

    public void setSportEvents(List<SportEvent> sportEvents) {
        this.sportEvents = sportEvents;
    }

    /**
     * Represents a single match/sport event
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SportEvent {
        private String id;

        @JsonProperty("scheduled")
        private String scheduledTime;

        @JsonProperty("tournament")
        private Tournament tournament;

        @JsonProperty("competitors")
        private List<Competitor> competitors;

        @JsonProperty("sport_event_status")
        private SportEventStatus status;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getScheduledTime() {
            return scheduledTime;
        }

        public void setScheduledTime(String scheduledTime) {
            this.scheduledTime = scheduledTime;
        }

        public Tournament getTournament() {
            return tournament;
        }

        public void setTournament(Tournament tournament) {
            this.tournament = tournament;
        }

        public List<Competitor> getCompetitors() {
            return competitors;
        }

        public void setCompetitors(List<Competitor> competitors) {
            this.competitors = competitors;
        }

        public SportEventStatus getStatus() {
            return status;
        }

        public void setStatus(SportEventStatus status) {
            this.status = status;
        }
    }

    /**
     * Tournament information
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Tournament {
        private String id;
        private String name;

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
    }

    /**
     * Competitor (player) in a match
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Competitor {
        private String id;
        private String name;
        private String country;

        @JsonProperty("country_code")
        private String countryCode;

        private String qualifier; // "home" or "away"

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

        public String getQualifier() {
            return qualifier;
        }

        public void setQualifier(String qualifier) {
            this.qualifier = qualifier;
        }
    }

    /**
     * Status of a sport event (match)
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SportEventStatus {
        private String status; // "not_started", "live", "ended"

        @JsonProperty("match_status")
        private String matchStatus;

        @JsonProperty("winner_id")
        private String winnerId;

        @JsonProperty("home_score")
        private Integer homeScore;

        @JsonProperty("away_score")
        private Integer awayScore;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMatchStatus() {
            return matchStatus;
        }

        public void setMatchStatus(String matchStatus) {
            this.matchStatus = matchStatus;
        }

        public String getWinnerId() {
            return winnerId;
        }

        public void setWinnerId(String winnerId) {
            this.winnerId = winnerId;
        }

        public Integer getHomeScore() {
            return homeScore;
        }

        public void setHomeScore(Integer homeScore) {
            this.homeScore = homeScore;
        }

        public Integer getAwayScore() {
            return awayScore;
        }

        public void setAwayScore(Integer awayScore) {
            this.awayScore = awayScore;
        }
    }
}
