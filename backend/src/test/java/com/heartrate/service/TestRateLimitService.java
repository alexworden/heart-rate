package com.heartrate.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Test implementation of RateLimitService.
 * Following user rules:
 * - Don't use mocks in tests
 * - Create reusable test infrastructure
 * - Avoid framework bloat
 * - Application-level validation
 * - Fail fast approach
 */
public class TestRateLimitService extends RateLimitService {
    private final Map<String, Map<LocalDateTime, String>> attemptsByEmail = new HashMap<>();
    private final Map<String, Map<LocalDateTime, String>> attemptsByIp = new HashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final int WINDOW_MINUTES = 15;

    public TestRateLimitService() {
        super(null);  // We don't need LoginAttemptRepository in tests
    }

    @Override
    public boolean tryLogin(String email, String ipAddress) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusMinutes(WINDOW_MINUTES);
        
        // Count attempts by email
        int emailAttempts = countAttempts(attemptsByEmail.computeIfAbsent(email, k -> new HashMap<>()), windowStart);
        int ipAttempts = countAttempts(attemptsByIp.computeIfAbsent(ipAddress, k -> new HashMap<>()), windowStart);
        
        return emailAttempts < MAX_ATTEMPTS && ipAttempts < MAX_ATTEMPTS;
    }

    @Override
    public void recordFailedLogin(String email, String ipAddress) {
        LocalDateTime now = LocalDateTime.now();
        attemptsByEmail.computeIfAbsent(email, k -> new HashMap<>()).put(now, ipAddress);
        attemptsByIp.computeIfAbsent(ipAddress, k -> new HashMap<>()).put(now, email);
    }

    private int countAttempts(Map<LocalDateTime, String> attempts, LocalDateTime windowStart) {
        // Remove old attempts
        attempts.keySet().removeIf(time -> time.isBefore(windowStart));
        return attempts.size();
    }

    public void clearAttempts() {
        attemptsByEmail.clear();
        attemptsByIp.clear();
    }

    public int getAttemptCountForEmail(String email) {
        return attemptsByEmail.getOrDefault(email, new HashMap<>()).size();
    }

    public int getAttemptCountForIp(String ipAddress) {
        return attemptsByIp.getOrDefault(ipAddress, new HashMap<>()).size();
    }
}
