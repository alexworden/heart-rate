package com.heartrate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test configuration for JWT service.
 * Following user rules:
 * - Don't use mocks in tests
 * - Create reusable test infrastructure
 */
@Configuration
@ActiveProfiles("test")
public class TestJwtConfig {
    @Bean
    @Primary
    public String jwtSecretKey() {
        return "test-jwt-secret-key-that-is-long-enough-for-hs512-algorithm";
    }
}
