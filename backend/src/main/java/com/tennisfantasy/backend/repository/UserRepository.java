package com.tennisfantasy.backend.repository;

import com.tennisfantasy.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by Supabase ID
    Optional<User> findBySupabaseId(String supabaseId);

    // Find user by email
    Optional<User> findByEmail(String email);

    // Check if email exists
    boolean existsByEmail(String email);

    // Check if Supabase ID exists
    boolean existsBySupabaseId(String supabaseId);
}
