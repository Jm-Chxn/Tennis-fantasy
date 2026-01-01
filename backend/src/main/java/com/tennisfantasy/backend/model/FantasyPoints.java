package com.tennisfantasy.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for logging fantasy points.
 * 
 * Records each point-earning event for audit and history.
 * Shows why points were awarded (e.g., "Match Win", "Upset Bonus").
 */
@Entity
@Table(name = "fantasy_points")
public class FantasyPoints {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The team that earned the points
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_member_id", nullable = false)
    private LeagueMember leagueMember;

    // The player who earned the points
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    // The match that triggered the points (optional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    // Points earned
    @Column(name = "points", nullable = false)
    private Integer points;

    // Reason for points: "MATCH_WIN", "SET_WIN", "UPSET_BONUS", etc.
    @Column(name = "reason")
    private String reason;

    // Detailed description
    @Column(name = "description")
    private String description;

    // Tournament name for context
    @Column(name = "tournament_name")
    private String tournamentName;

    // Week number for weekly scoring
    @Column(name = "week_number")
    private Integer weekNumber;

    @Column(name = "awarded_at")
    private LocalDateTime awardedAt;

    // Default constructor
    public FantasyPoints() {
        this.awardedAt = LocalDateTime.now();
    }

    // Constructor with required fields
    public FantasyPoints(LeagueMember leagueMember, Player player, int points, String reason) {
        this();
        this.leagueMember = leagueMember;
        this.player = player;
        this.points = points;
        this.reason = reason;
    }

    // Constructor with match
    public FantasyPoints(LeagueMember leagueMember, Player player, Match match, int points, String reason) {
        this(leagueMember, player, points, reason);
        this.match = match;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LeagueMember getLeagueMember() {
        return leagueMember;
    }

    public void setLeagueMember(LeagueMember leagueMember) {
        this.leagueMember = leagueMember;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(Integer weekNumber) {
        this.weekNumber = weekNumber;
    }

    public LocalDateTime getAwardedAt() {
        return awardedAt;
    }

    public void setAwardedAt(LocalDateTime awardedAt) {
        this.awardedAt = awardedAt;
    }

    @Override
    public String toString() {
        return "FantasyPoints{" +
                "id=" + id +
                ", points=" + points +
                ", reason='" + reason + '\'' +
                ", awardedAt=" + awardedAt +
                '}';
    }
}
