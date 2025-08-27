package com.tennisfantasy.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rankings {
    private List<CompetitorRanking> competitorRankings;
    private String gender;
    private String name;
    private Integer typeId;
    private Integer week;
    private Integer year;

    public List<CompetitorRanking> getCompetitorRankings() { return competitorRankings; }
    public void setCompetitorRankings(List<CompetitorRanking> competitorRankings) { this.competitorRankings = competitorRankings; }

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
