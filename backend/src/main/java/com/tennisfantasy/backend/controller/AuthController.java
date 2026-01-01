package com.tennisfantasy.backend.controller;

import com.tennisfantasy.backend.model.User;
import com.tennisfantasy.backend.model.LeagueMember;
import com.tennisfantasy.backend.repository.UserRepository;
import com.tennisfantasy.backend.repository.LeagueMemberRepository;
import com.tennisfantasy.backend.repository.RosterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for Authentication endpoints.
 * 
 * Syncs Supabase Auth users with our local database.
 * Frontend authenticates with Supabase directly, then calls
 * these endpoints to create/sync the local user record.
 */
@RestController
@RequestMapping("/auth")

public class AuthController {

    private final UserRepository userRepository;
    private final LeagueMemberRepository leagueMemberRepository;
    private final RosterRepository rosterRepository;

    public AuthController(UserRepository userRepository, LeagueMemberRepository leagueMemberRepository, RosterRepository rosterRepository) {
        this.userRepository = userRepository;
        this.leagueMemberRepository = leagueMemberRepository;
        this.rosterRepository = rosterRepository;
    }

    /**
     * Register/sync a user after Supabase authentication.
     * 
     * POST /api/auth/register
     * Body: { "supabaseId": "uuid", "email": "user@example.com", "displayName":
     * "User" }
     * 
     * This creates a local user record if one doesn't exist,
     * or returns the existing user if they've already registered.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            String supabaseId = request.get("supabaseId");
            String email = request.get("email");
            String displayName = request.getOrDefault("displayName", email.split("@")[0]);

            // Check if user already exists
            Optional<User> existingUser = userRepository.findBySupabaseId(supabaseId);
            if (existingUser.isPresent()) {
                // Update last login and return existing user
                User user = existingUser.get();
                user.setLastLoginAt(LocalDateTime.now());
                userRepository.save(user);
                return ResponseEntity.ok(user);
            }

            // Create new user
            User newUser = new User(supabaseId, email, displayName);
            User savedUser = userRepository.save(newUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get user profile by Supabase ID.
     * 
     * GET /api/auth/profile?supabaseId=uuid
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestParam String supabaseId) {
        return userRepository.findBySupabaseId(supabaseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update user profile.
     * 
     * PUT /api/auth/profile
     * Body: { "supabaseId": "uuid", "displayName": "New Name", "avatarUrl": "..." }
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> request) {
        try {
            String supabaseId = request.get("supabaseId");

            User user = userRepository.findBySupabaseId(supabaseId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (request.containsKey("displayName")) {
                user.setDisplayName(request.get("displayName"));
            }
            if (request.containsKey("avatarUrl")) {
                user.setAvatarUrl(request.get("avatarUrl"));
            }

            User savedUser = userRepository.save(user);
            return ResponseEntity.ok(savedUser);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * Get user by ID (for internal use).
     * 
     * GET /api/auth/user/{id}
     */
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Health check for auth service.
     * 
     * GET /api/auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "ok");
        response.put("service", "auth");
        return ResponseEntity.ok(response);
    }

    /**
     * Get user stats for dashboard.
     * 
     * GET /api/auth/stats/{userId}
     * Returns: totalPoints, activeLeagues, bestRank, playersDrafted
     */
    @GetMapping("/stats/{userId}")
    public ResponseEntity<?> getUserStats(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<LeagueMember> memberships = leagueMemberRepository.findByUserId(userId);
            
            // Calculate total points across all leagues
            int totalPoints = memberships.stream()
                    .mapToInt(m -> m.getTotalPoints() != null ? m.getTotalPoints() : 0)
                    .sum();
            
            // Count active leagues
            int activeLeagues = memberships.size();
            
            // Find best rank (lowest number is best)
            Integer bestRank = memberships.stream()
                    .map(LeagueMember::getLeagueRank)
                    .filter(r -> r != null && r > 0)
                    .min(Integer::compareTo)
                    .orElse(0);
            
            // Count total players drafted across all leagues
            int playersDrafted = 0;
            for (LeagueMember member : memberships) {
                playersDrafted += rosterRepository.countByLeagueMemberId(member.getId());
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalPoints", totalPoints);
            stats.put("activeLeagues", activeLeagues);
            stats.put("bestRank", bestRank);
            stats.put("playersDrafted", playersDrafted);

            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
