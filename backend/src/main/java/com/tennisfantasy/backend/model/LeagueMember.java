package com.tennisfantasy.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * Entity representing a member of a fantasy league.
 * 
 * Each LeagueMember represents a user's participation in a specific league.
 * A user can be a member of multiple leagues.
 */
@Entity
@Table(name = "league_members", uniqueConstraints = @UniqueConstraint(columnNames = { "league_id", "user_id" }))
public class LeagueMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The league this membership belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_id", nullable = false)
    private League league;

    // The user who is a member
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Team name for this league
    @NotBlank
    @Column(name = "team_name")
    private String teamName;

    // Draft position (1, 2, 3, etc.)
    @Column(name = "draft_position")
    private Integer draftPosition;

    // Total fantasy points in this league
    @Column(name = "total_points")
    private Integer totalPoints = 0;

    // Weekly points
    @Column(name = "weekly_points")
    private Integer weeklyPoints = 0;

    // League rank/standing
    @Column(name = "league_rank")
    private Integer leagueRank;

    // Wins in head-to-head matchups
    @Column(name = "wins")
    private Integer wins = 0;

    // Losses in head-to-head matchups
    @Column(name = "losses")
    private Integer losses = 0;

    // Is this member the league commissioner/owner?
    @Column(name = "is_commissioner")
    private Boolean isCommissioner = false;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    // Default constructor
    public LeagueMember() {
        this.joinedAt = LocalDateTime.now();
    }

    // Constructor with required fields
    public LeagueMember(League league, User user, String teamName) {
        this();
        this.league = league;
        this.user = user;
        this.teamName = teamName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Integer getDraftPosition() {
        return draftPosition;
    }

    public void setDraftPosition(Integer draftPosition) {
        this.draftPosition = draftPosition;
    }

    public Integer getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(Integer totalPoints) {
        this.totalPoints = totalPoints;
    }

    public Integer getWeeklyPoints() {
        return weeklyPoints;
    }

    public void setWeeklyPoints(Integer weeklyPoints) {
        this.weeklyPoints = weeklyPoints;
    }

    public Integer getLeagueRank() {
        return leagueRank;
    }

    public void setLeagueRank(Integer leagueRank) {
        this.leagueRank = leagueRank;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getLosses() {
        return losses;
    }

    public void setLosses(Integer losses) {
        this.losses = losses;
    }

    public Boolean getIsCommissioner() {
        return isCommissioner;
    }

    public void setIsCommissioner(Boolean isCommissioner) {
        this.isCommissioner = isCommissioner;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    /**
     * Add points to total
     */
    public void addPoints(int points) {
        this.totalPoints += points;
        this.weeklyPoints += points;
    }

    /**
     * Reset weekly points (called at start of new week)
     */
    public void resetWeeklyPoints() {
        this.weeklyPoints = 0;
    }

    /**
     * Record a win
     */
    public void recordWin() {
        this.wins++;
    }

    /**
     * Record a loss
     */
    public void recordLoss() {
        this.losses++;
    }

    @Override
    public String toString() {
        return "LeagueMember{" +
                "id=" + id +
                ", teamName='" + teamName + '\'' +
                ", totalPoints=" + totalPoints +
                ", leagueRank=" + leagueRank +
                ", wins=" + wins +
                ", losses=" + losses +
                '}';
    }
}
