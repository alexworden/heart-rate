package com.heartrate.service;

import com.heartrate.entity.LoginAttempt;
import com.heartrate.repository.LoginAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Service for handling rate limiting.
 * Following user rules:
 * - No enums, using String values
 * - UUIDs for all IDs
 * - No ORM relationships
 * - Application-level validation instead of database constraints
 * - Fail fast approach
 */
@Service
public class RateLimitService {
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final Duration LOGIN_WINDOW = Duration.ofMinutes(15);
    private static final int MAX_EMAIL_VERIFY_ATTEMPTS = 3;
    private static final Duration EMAIL_VERIFY_WINDOW = Duration.ofMinutes(60);
    private static final int MAX_PASSWORD_RESET_ATTEMPTS = 3;
    private static final Duration PASSWORD_RESET_WINDOW = Duration.ofMinutes(60);

    private final LoginAttemptRepository loginAttemptRepository;

    public RateLimitService(LoginAttemptRepository loginAttemptRepository) {
        this.loginAttemptRepository = loginAttemptRepository;
    }

    @Transactional(readOnly = true)
    public boolean tryLogin(String email, String ipAddress) {
        LocalDateTime windowStart = LocalDateTime.now().minus(LOGIN_WINDOW);
        long emailAttempts = loginAttemptRepository.countByEmailAndAttemptTimeAfter(email, windowStart);
        long ipAttempts = loginAttemptRepository.countByIpAddressAndAttemptTimeAfter(ipAddress, windowStart);

        return emailAttempts < MAX_LOGIN_ATTEMPTS && ipAttempts < MAX_LOGIN_ATTEMPTS;
    }

    @Transactional
    public void recordFailedLogin(String email, String ipAddress) {
        LoginAttempt attempt = new LoginAttempt();
        attempt.setEmail(email);
        attempt.setIpAddress(ipAddress);
        loginAttemptRepository.save(attempt);
    }

    @Transactional(readOnly = true)
    public boolean tryEmailVerification(String email) {
        LocalDateTime windowStart = LocalDateTime.now().minus(EMAIL_VERIFY_WINDOW);
        long attempts = loginAttemptRepository.countByEmailAndAttemptTimeAfter(email, windowStart);
        return attempts < MAX_EMAIL_VERIFY_ATTEMPTS;
    }

    @Transactional(readOnly = true)
    public boolean tryPasswordReset(String email) {
        LocalDateTime windowStart = LocalDateTime.now().minus(PASSWORD_RESET_WINDOW);
        long attempts = loginAttemptRepository.countByEmailAndAttemptTimeAfter(email, windowStart);
        return attempts < MAX_PASSWORD_RESET_ATTEMPTS;
    }
}
