package com.tennisfantasy.backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LeagueCreateRequest {

    @NotBlank(message = "League name is required")
    private String name;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Team name is required")
    private String teamName;

    private String description;

    @Min(value = 2, message = "Max teams must be at least 2")
    @Max(value = 100, message = "Max teams cannot exceed 100")
    private Integer maxTeams = 10; // Default

    @Min(value = 1, message = "Roster size must be at least 1")
    private Integer rosterSize = 8; // Default

    private Integer starterSize = 5; // Default

    private String tourType = "ATP/WTA"; // Default

    private Boolean isPublic = true; // Default

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxTeams() {
        return maxTeams;
    }

    public void setMaxTeams(Integer maxTeams) {
        this.maxTeams = maxTeams;
    }

    public Integer getRosterSize() {
        return rosterSize;
    }

    public void setRosterSize(Integer rosterSize) {
        this.rosterSize = rosterSize;
    }

    public Integer getStarterSize() {
        return starterSize;
    }

    public void setStarterSize(Integer starterSize) {
        this.starterSize = starterSize;
    }

    public String getTourType() {
        return tourType;
    }

    public void setTourType(String tourType) {
        this.tourType = tourType;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
}
