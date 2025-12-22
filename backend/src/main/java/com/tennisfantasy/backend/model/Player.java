package com.tennisfantasy.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a tennis player.
 * 
 * Players can be synced from SportsRadar API using the sportradarId field.
 * The tour field indicates whether they play on ATP or WTA tour.
 */
@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // SportsRadar unique identifier (e.g., "sr:competitor:12345")
    @Column(name = "sportradar_id", unique = true)
    private String sportradarId;

    @NotBlank
    @Column(name = "first_name")
    private String firstName;

    @NotBlank
    @Column(name = "last_name")
    private String lastName;

    @NotBlank
    @Column(name = "country")
    private String country;

    // Country code (e.g., "USA", "SRB", "ESP")
    @Column(name = "country_code", length = 3)
    private String countryCode;

    @NotNull
    @Column(name = "ranking")
    private Integer ranking;

    // ATP/WTA ranking points
    @Column(name = "points")
    private Integer points;

    // Fantasy price for drafts
    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    // Tour: "ATP" or "WTA"
    @Column(name = "tour", length = 10)
    private String tour;

    @Column(name = "position")
    private String position; // e.g., "Singles", "Doubles"

    @Column(name = "is_active")
    private Boolean isActive = true;

    // When was this player's data last synced from SportsRadar
    @Column(name = "last_synced_at")
    private LocalDateTime lastSyncedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Default constructor
    public Player() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor with fields
    public Player(String firstName, String lastName, String country, Integer ranking) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.ranking = ranking;
    }

    // Constructor for SportsRadar sync
    public Player(String sportradarId, String firstName, String lastName, String country,
            String countryCode, Integer ranking, Integer points, String tour) {
        this();
        this.sportradarId = sportradarId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.countryCode = countryCode;
        this.ranking = ranking;
        this.points = points;
        this.tour = tour;
        this.position = "Singles";
        this.lastSyncedAt = LocalDateTime.now();
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getTour() {
        return tour;
    }

    public void setTour(String tour) {
        this.tour = tour;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getLastSyncedAt() {
        return lastSyncedAt;
    }

    public void setLastSyncedAt(LocalDateTime lastSyncedAt) {
        this.lastSyncedAt = lastSyncedAt;
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
     * Get the player's full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", sportradarId='" + sportradarId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", country='" + country + '\'' +
                ", ranking=" + ranking +
                ", points=" + points +
                ", tour='" + tour + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
