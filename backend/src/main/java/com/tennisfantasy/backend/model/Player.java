
package com.tennisfantasy.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "players")
public class Player {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    
    @NotBlank
    @Column(name = "first_name")
    @JsonProperty("first_name")
    private String firstName;
    
    @NotBlank
    @Column(name = "last_name")
    @JsonProperty("last_name")
    private String lastName;
    
    @NotBlank
    @Column(name = "country")
    @JsonProperty("country")
    private String country;
    
    @NotNull
    @Column(name = "rank")
    @JsonProperty("rank")
    private Integer rank;
    
    @Column(name = "cost", precision = 10, scale = 2)
    @JsonProperty("cost")
    private BigDecimal cost;
    
    // Default constructor
    public Player() {}
    
    // Constructor with fields
    public Player(String firstName, String lastName, String country, Integer rank, BigDecimal cost) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.country = country;
        this.rank = rank;
        this.cost = cost;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public Integer getRank() {
        return rank;
    }
    
    public void setRank(Integer rank) {
        this.rank = rank;
    }
    
    public BigDecimal getCost() {
        return cost;
    }
    
    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }
    
    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", country='" + country + '\'' +
                ", rank=" + rank +
                ", cost=" + cost +
                '}';
    }
}
