package com.tennisfantasy.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entity representing a fantasy tennis league.
 * 
 * Users can create leagues and invite others to join.
 * Each league has its own draft and scoring.
 */
@Entity
@Table(name = "leagues")
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    // League owner/creator
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    // League join code for invites
    @Column(name = "join_code", unique = true)
    private String joinCode;

    // Maximum number of teams allowed
    @NotNull
    @Column(name = "max_teams")
    private Integer maxTeams = 8;

    // Current number of teams
    @Column(name = "current_teams")
    private Integer currentTeams = 0;

    // Roster size per team
    @Column(name = "roster_size")
    private Integer rosterSize = 6;

    // Draft type: "SNAKE", "LINEAR", "AUCTION"
    @Column(name = "draft_type")
    private String draftType = "SNAKE";

    // Draft status: "NOT_STARTED", "IN_PROGRESS", "COMPLETED"
    @Column(name = "draft_status")
    private String draftStatus = "NOT_STARTED";

    // Scoring type: "STANDARD", "CUSTOM"
    @Column(name = "scoring_type")
    private String scoringType = "STANDARD";

    // Tour type: "ATP", "WTA", "BOTH"
    @Column(name = "tour_type")
    private String tourType = "BOTH";

    // League status: "ACTIVE", "COMPLETED", "ARCHIVED"
    @Column(name = "status")
    private String status = "ACTIVE";

    // Season year
    @Column(name = "season")
    private Integer season;

    @Column(name = "is_public")
    private Boolean isPublic = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "draft_date")
    private LocalDateTime draftDate;

    // Default constructor
    public League() {
        this.createdAt = LocalDateTime.now();
        this.joinCode = generateJoinCode();
    }

    // Constructor with basic fields
    public League(String name, User owner) {
        this();
        this.name = name;
        this.owner = owner;
    }

    // Generate a random 6-character join code
    private String generateJoinCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return code.toString();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    public Integer getMaxTeams() {
        return maxTeams;
    }

    public void setMaxTeams(Integer maxTeams) {
        this.maxTeams = maxTeams;
    }

    public Integer getCurrentTeams() {
        return currentTeams;
    }

    public void setCurrentTeams(Integer currentTeams) {
        this.currentTeams = currentTeams;
    }

    public Integer getRosterSize() {
        return rosterSize;
    }

    public void setRosterSize(Integer rosterSize) {
        this.rosterSize = rosterSize;
    }

    public String getDraftType() {
        return draftType;
    }

    public void setDraftType(String draftType) {
        this.draftType = draftType;
    }

    public String getDraftStatus() {
        return draftStatus;
    }

    public void setDraftStatus(String draftStatus) {
        this.draftStatus = draftStatus;
    }

    public String getScoringType() {
        return scoringType;
    }

    public void setScoringType(String scoringType) {
        this.scoringType = scoringType;
    }

    public String getTourType() {
        return tourType;
    }

    public void setTourType(String tourType) {
        this.tourType = tourType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSeason() {
        return season;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getDraftDate() {
        return draftDate;
    }

    public void setDraftDate(LocalDateTime draftDate) {
        this.draftDate = draftDate;
    }

    /**
     * Check if the league has room for more teams
     */
    public boolean hasRoom() {
        return currentTeams < maxTeams;
    }

    /**
     * Check if the draft can start
     */
    public boolean canStartDraft() {
        return currentTeams >= 2 && "NOT_STARTED".equals(draftStatus);
    }

    @Override
    public String toString() {
        return "League{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", maxTeams=" + maxTeams +
                ", currentTeams=" + currentTeams +
                ", draftStatus='" + draftStatus + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
