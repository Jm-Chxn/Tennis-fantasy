package com.tennisfantasy.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a tennis match.
 * 
 * Stores match results synced from SportsRadar API.
 * Used to calculate fantasy points when matches complete.
 */
@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // SportsRadar unique match identifier
    @Column(name = "sportradar_id", unique = true)
    private String sportradarId;

    // Player 1 (home)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player1_id")
    private Player player1;

    // Player 2 (away)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player2_id")
    private Player player2;

    // Winner of the match
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private Player winner;

    // Score in format like "6-4, 3-6, 7-5"
    @Column(name = "score")
    private String score;

    // Player 1 sets won
    @Column(name = "player1_sets")
    private Integer player1Sets;

    // Player 2 sets won
    @Column(name = "player2_sets")
    private Integer player2Sets;

    // Tournament name (e.g., "Australian Open")
    @Column(name = "tournament_name")
    private String tournamentName;

    // SportsRadar tournament ID
    @Column(name = "tournament_id")
    private String tournamentId;

    // Round (e.g., "Final", "Semi-Final", "Round of 16")
    @Column(name = "round_name")
    private String roundName;

    // Match date and time
    @Column(name = "match_date")
    private LocalDateTime matchDate;

    // Status: "scheduled", "live", "completed", "cancelled"
    @Column(name = "status")
    private String status;

    // Is this a Grand Slam match?
    @Column(name = "is_grand_slam")
    private Boolean isGrandSlam = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Default constructor
    public Match() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = "scheduled";
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSportradarId() {
        return sportradarId;
    }

    public void setSportradarId(String sportradarId) {
        this.sportradarId = sportradarId;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Integer getPlayer1Sets() {
        return player1Sets;
    }

    public void setPlayer1Sets(Integer player1Sets) {
        this.player1Sets = player1Sets;
    }

    public Integer getPlayer2Sets() {
        return player2Sets;
    }

    public void setPlayer2Sets(Integer player2Sets) {
        this.player2Sets = player2Sets;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public void setTournamentName(String tournamentName) {
        this.tournamentName = tournamentName;
    }

    public String getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(String tournamentId) {
        this.tournamentId = tournamentId;
    }

    public String getRoundName() {
        return roundName;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }

    public LocalDateTime getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDateTime matchDate) {
        this.matchDate = matchDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsGrandSlam() {
        return isGrandSlam;
    }

    public void setIsGrandSlam(Boolean isGrandSlam) {
        this.isGrandSlam = isGrandSlam;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Check if this match has been completed
     */
    public boolean isCompleted() {
        return "completed".equalsIgnoreCase(status);
    }

    /**
     * Check if this match is currently live
     */
    public boolean isLive() {
        return "live".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "Match{" +
                "id=" + id +
                ", sportradarId='" + sportradarId + '\'' +
                ", tournamentName='" + tournamentName + '\'' +
                ", roundName='" + roundName + '\'' +
                ", status='" + status + '\'' +
                ", matchDate=" + matchDate +
                '}';
    }
}
