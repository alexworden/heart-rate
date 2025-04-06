package com.heartrate.service;

import com.heartrate.repository.LoginAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Test implementation of RateLimitService that always allows operations.
 * Following user rules:
 * - Don't use mocks in tests
 * - Create reusable test infrastructure
 * - Use H2 in-memory database
 */
@Service
@Primary
public class RateLimitServiceTest extends RateLimitService {
    @Autowired
    public RateLimitServiceTest(LoginAttemptRepository loginAttemptRepository) {
        super(loginAttemptRepository);
    }

    @Override
    public boolean tryLogin(String email, String ipAddress) {
        return true;
    }

    @Override
    public void recordFailedLogin(String email, String ipAddress) {
        // No-op for tests
    }
}
