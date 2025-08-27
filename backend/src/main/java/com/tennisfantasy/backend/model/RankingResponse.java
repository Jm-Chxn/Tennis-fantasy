package com.tennisfantasy.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RankingResponse {
    private String generatedAt;
    private List<Rankings> rankings;

    public String getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }
    public List<Rankings> getRankings() { return rankings; }
    public void setRankings(List<Rankings> rankings) { this.rankings = rankings; }
}
