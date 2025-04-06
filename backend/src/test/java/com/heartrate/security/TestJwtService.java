package com.heartrate.security;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Test JWT service following user rules:
 * - Using reusable test infrastructure instead of mocks
 * - Fail fast approach
 */
public class TestJwtService {
    private final String secretKey;
    private final long jwtExpiration;

    public TestJwtService(String secretKey, long jwtExpiration) {
        this.secretKey = secretKey;
        this.jwtExpiration = jwtExpiration;
    }

    public String generateToken(UserDetails userDetails) {
        return "test-token-" + userDetails.getUsername();
    }

    public String extractUsername(String token) {
        if (token == null || !token.startsWith("test-token-")) {
            throw new RuntimeException("Invalid test token");
        }
        return token.substring("test-token-".length());
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        return token != null && 
               token.startsWith("test-token-") && 
               token.substring("test-token-".length()).equals(userDetails.getUsername());
    }
}
