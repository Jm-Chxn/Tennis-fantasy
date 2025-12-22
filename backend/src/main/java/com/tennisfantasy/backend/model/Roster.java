package com.tennisfantasy.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a player on a fantasy roster.
 * 
 * Links a Player to a LeagueMember's team.
 * Tracks whether the player is a starter or on the bench.
 */
@Entity
@Table(name = "rosters", uniqueConstraints = @UniqueConstraint(columnNames = { "league_member_id", "player_id" }))
public class Roster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The team this roster entry belongs to
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "league_member_id", nullable = false)
    private LeagueMember leagueMember;

    // The player on the roster
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    // Is this player in the starting lineup?
    @Column(name = "is_starter")
    private Boolean isStarter = true;

    // Roster slot (1, 2, 3, etc.)
    @Column(name = "slot_number")
    private Integer slotNumber;

    // Draft round when player was picked
    @Column(name = "draft_round")
    private Integer draftRound;

    // Draft pick number overall
    @Column(name = "draft_pick")
    private Integer draftPick;

    // Total points this player has earned for this team
    @Column(name = "points_earned")
    private Integer pointsEarned = 0;

    // Was this player acquired via waiver/trade?
    @Column(name = "acquisition_type")
    private String acquisitionType = "DRAFT"; // "DRAFT", "WAIVER", "TRADE"

    @Column(name = "added_at")
    private LocalDateTime addedAt;

    // Default constructor
    public Roster() {
        this.addedAt = LocalDateTime.now();
    }

    // Constructor with required fields
    public Roster(LeagueMember leagueMember, Player player) {
        this();
        this.leagueMember = leagueMember;
        this.player = player;
    }

    // Constructor for draft pick
    public Roster(LeagueMember leagueMember, Player player, int draftRound, int draftPick) {
        this(leagueMember, player);
        this.draftRound = draftRound;
        this.draftPick = draftPick;
        this.acquisitionType = "DRAFT";
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

    public Boolean getIsStarter() {
        return isStarter;
    }

    public void setIsStarter(Boolean isStarter) {
        this.isStarter = isStarter;
    }

    public Integer getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(Integer slotNumber) {
        this.slotNumber = slotNumber;
    }

    public Integer getDraftRound() {
        return draftRound;
    }

    public void setDraftRound(Integer draftRound) {
        this.draftRound = draftRound;
    }

    public Integer getDraftPick() {
        return draftPick;
    }

    public void setDraftPick(Integer draftPick) {
        this.draftPick = draftPick;
    }

    public Integer getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(Integer pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public String getAcquisitionType() {
        return acquisitionType;
    }

    public void setAcquisitionType(String acquisitionType) {
        this.acquisitionType = acquisitionType;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    /**
     * Add points to this roster entry
     */
    public void addPoints(int points) {
        this.pointsEarned += points;
    }

    @Override
    public String toString() {
        return "Roster{" +
                "id=" + id +
                ", isStarter=" + isStarter +
                ", slotNumber=" + slotNumber +
                ", pointsEarned=" + pointsEarned +
                '}';
    }
}
