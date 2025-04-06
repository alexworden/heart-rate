package com.heartrate.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Rate limiting configuration using bucket4j.
 * Following user rules:
 * - No enums, using String values
 * - Application-level validation instead of database constraints
 * - Fail fast approach
 */
@Configuration
public class RateLimitConfig {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Value("${app.security.rate-limiting.login.max-attempts:5}")
    private int loginMaxAttempts;

    @Value("${app.security.rate-limiting.login.window-minutes:15}")
    private int loginWindowMinutes;

    @Value("${app.security.rate-limiting.email-verification.max-attempts:3}")
    private int emailVerificationMaxAttempts;

    @Value("${app.security.rate-limiting.email-verification.window-minutes:60}")
    private int emailVerificationWindowMinutes;

    @Value("${app.security.rate-limiting.password-reset.max-attempts:3}")
    private int passwordResetMaxAttempts;

    @Value("${app.security.rate-limiting.password-reset.window-minutes:60}")
    private int passwordResetWindowMinutes;

    public Bucket resolveBucket(String key, String type) {
        return buckets.computeIfAbsent(key + ":" + type, k -> createNewBucket(type));
    }

    private Bucket createNewBucket(String type) {
        Bandwidth limit = switch (type) {
            case "LOGIN" -> Bandwidth.classic(loginMaxAttempts, 
                Refill.intervally(loginMaxAttempts, Duration.ofMinutes(loginWindowMinutes)));
            case "EMAIL_VERIFICATION" -> Bandwidth.classic(emailVerificationMaxAttempts,
                Refill.intervally(emailVerificationMaxAttempts, Duration.ofMinutes(emailVerificationWindowMinutes)));
            case "PASSWORD_RESET" -> Bandwidth.classic(passwordResetMaxAttempts,
                Refill.intervally(passwordResetMaxAttempts, Duration.ofMinutes(passwordResetWindowMinutes)));
            default -> throw new RuntimeException("Unknown rate limit type: " + type);
        };

        return Bucket.builder()
            .addLimit(limit)
            .build();
    }

    public void clearBucket(String key, String type) {
        buckets.remove(key + ":" + type);
    }
}
