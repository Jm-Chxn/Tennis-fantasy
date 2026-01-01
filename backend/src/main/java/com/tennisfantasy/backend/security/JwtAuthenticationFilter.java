package com.tennisfantasy.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

/**
 * JWT Authentication Filter for validating Supabase tokens.
 * 
 * Extracts the JWT from the Authorization header and validates it.
 * If valid, sets the authentication in the SecurityContext.
 * 
 * Supabase JWT tokens contain:
 * - sub: The user's Supabase ID (UUID)
 * - email: The user's email
 * - exp: Expiration time
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Value("${supabase.jwt.secret}")
    private String jwtSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String bearerToken = request.getHeader("Authorization");
            String uri = request.getRequestURI();
            String method = request.getMethod();

            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
                String jwtToken = bearerToken.substring(7);
                try {
                    Claims claims = parseToken(jwtToken);
                    if (claims != null) {
                        String supabaseId = claims.getSubject();
                        logger.info(">>> Authenticated User: {} for {} {}", supabaseId, method, uri);

                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                supabaseId, null, new ArrayList<>());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        logger.warn(">>> Auth failed (null claims) for {} {}", method, uri);
                    }
                } catch (Exception e) {
                    logger.error(">>> Internal Auth Error for {} {}: {}", method, uri, e.getMessage());
                }
            } else {
                // Public endpoints won't have the token, which is fine
                // But we should know if a protected one is missing it
                logger.debug(">>> No Bearer token for {} {}", method, uri);
            }
        } catch (Exception e) {
            logger.error(">>> Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Parse the JWT token and extract claims.
     */
    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKeyResolver(new io.jsonwebtoken.SigningKeyResolverAdapter() {
                    @Override
                    public java.security.Key resolveSigningKey(io.jsonwebtoken.JwsHeader header, Claims claims) {
                        String alg = header.getAlgorithm();
                        String kid = (String) header.get("kid");
                        logger.info(">>> Incoming Token - Alg: {}, Key ID (kid): {}", alg, kid);

                        if ("HS256".equals(alg)) {
                            // Standard Supabase implementation uses the UTF-8 bytes of the symmetric secret
                            // string.
                            byte[] keyBytes = jwtSecret.trim().getBytes(StandardCharsets.UTF_8);
                            return Keys.hmacShaKeyFor(keyBytes);
                        }

                        logger.error("Algorithm {} is not supported. Please rotate your Supabase keys to use HS256.",
                                alg);
                        return null;
                    }
                })
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Skip filter for certain paths (for performance).
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // Skip for public paths
        return path.startsWith("/auth/")
                || path.startsWith("/h2-console");
    }
}
