package com.heartrate.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * JWT service for handling verification tokens.
 * Following user rules:
 * - No enums (using String values)
 * - Fail fast (throws RuntimeException)
 * - No database storage (stateless tokens)
 */
@Service
public class JwtService {
    private final SecretKey key;

    public JwtService(@Value("${jwt.secret}") String secret) {
        // Convert string secret to SecretKey
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Generate a verification token for email verification, password reset, or passwordless login.
     * @param userId User ID to include in the token
     * @param email Email to include in the token
     * @param type Type of token (EMAIL_VERIFICATION, PASSWORD_RESET, PASSWORDLESS_LOGIN)
     * @param duration How long the token should be valid for
     * @return JWT token string
     */
    public String generateToken(UUID userId, String email, String type, Duration duration) {
        return Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("type", type)
            .issuedAt(new Date())
            .expiration(Date.from(Instant.now().plus(duration)))
            .signWith(key)
            .compact();
    }

    /**
     * Verify and parse a token.
     * @param token JWT token to verify
     * @param expectedType Expected token type
     * @return Claims from the token if valid
     * @throws RuntimeException if token is invalid or expired
     */
    public Jws<Claims> verifyToken(String token, String expectedType) {
        try {
            Jws<Claims> claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);

            String tokenType = claims.getPayload().get("type", String.class);
            if (!expectedType.equals(tokenType)) {
                throw new RuntimeException("Invalid token type");
            }

            return claims;
        } catch (JwtException e) {
            throw new RuntimeException("Invalid or expired token", e);
        }
    }
}
