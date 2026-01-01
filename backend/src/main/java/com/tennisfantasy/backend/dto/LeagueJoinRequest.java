package com.tennisfantasy.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LeagueJoinRequest {

    @NotBlank(message = "Join code is required")
    private String joinCode;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Team name is required")
    private String teamName;

    // Getters and Setters

    public String getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
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
}
