package com.tennisfantasy.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rankings {
    @JsonProperty("competitor_rankings")
    private List<CompetitorRanking> rankings;

    private String gender;
    private String name;
    private Integer typeId;
    private Integer week;
    private Integer year;

    public List<CompetitorRanking> getRankings() { return rankings; }
    public void setRankings(List<CompetitorRanking> rankings) { this.rankings = rankings; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getTypeId() { return typeId; }
    public void setTypeId(Integer typeId) { this.typeId = typeId; }
    public Integer getWeek() { return week; }
    public void setWeek(Integer week) { this.week = week; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
}
