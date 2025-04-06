package com.heartrate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Test configuration properties.
 * Following user rules:
 * - Don't use mocks in tests
 * - Create reusable test infrastructure
 * - Avoid excessive validation
 * - Avoid code generation libraries
 */
@Configuration
@Profile("test")
public class TestProperties {
    
    @Bean
    public String frontendUrl() {
        return "http://localhost:3000";
    }

    @Bean
    public String emailFrom() {
        return "test@example.com";
    }

    @Bean
    public String rememberMeKey() {
        return "test-remember-me-key";
    }

    @Bean
    public long verificationLinkExpiration() {
        return 7200; // 2 hours in seconds
    }
}
