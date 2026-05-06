package com.hostelpay.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    private SecretKey signingKey;
 
    @jakarta.annotation.PostConstruct
    public void init() {
        try {
            this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            log.info("JWT Signing Key initialized successfully.");
        } catch (Exception e) {
            log.error("CRITICAL: JWT Secret is too weak or invalid! Error: {}", e.getMessage());
            // Fail fast with a clear message
            throw new RuntimeException("JWT Configuration Failure: " + e.getMessage());
        }
    }
 
    private SecretKey getSigningKey() {
        return this.signingKey;
    }

    /**
     * Generate JWT token with hostelId and role as custom claims
     */
    public String generateToken(UUID userId, String email, UUID hostelId, String role) {
        JwtBuilder builder = Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("role", role)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(getSigningKey());

        if (hostelId != null) {
            builder.claim("hostelId", hostelId.toString());
        }

        return builder.compact();
    }

    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            log.error("JWT validation failed: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public UUID extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return UUID.fromString(claims.getSubject());
    }

    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("email", String.class);
    }

    public UUID extractHostelId(String token) {
        Claims claims = extractAllClaims(token);
        String hostelIdStr = claims.get("hostelId", String.class);
        return hostelIdStr != null ? UUID.fromString(hostelIdStr) : null;
    }

    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
