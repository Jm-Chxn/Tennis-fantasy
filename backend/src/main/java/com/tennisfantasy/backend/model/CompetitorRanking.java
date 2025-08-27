package com.tennisfantasy.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CompetitorRanking {
    private Competitor competitor;

    private int movement;
    private int points;
    private int rank;

    public Competitor getCompetitor() { return competitor; }
    public void setCompetitor(Competitor competitor) { this.competitor = competitor; }
    public int getMovement() { return movement; }
    public void setMovement(int movement) { this.movement = movement; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
}
