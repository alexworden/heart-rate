package com.heartrate.config;

import com.heartrate.service.JwtService;
import com.heartrate.service.notification.IEmailService;
import com.heartrate.service.notification.TestEmailService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Test configuration for the application.
 * Following user rules:
 * - No mocks in tests, use real implementations
 * - Create reusable test infrastructure
 * - Fail fast approach with RuntimeExceptions
 */
@Configuration
@EnableAutoConfiguration
@Profile("test")
public class TestConfig {
    @Bean
    public IEmailService emailService() {
        return new TestEmailService();
    }

    @Bean
    public JwtService jwtService() {
        // Use a fixed secret key for testing
        String secret = "test-secret-key-that-is-at-least-32-bytes-long-for-testing";
        return new JwtService(secret);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JavaMailSender javaMailSender() {
        // Return a mock JavaMailSender since we're using TestEmailService
        return null;
    }

    @Bean
    public String frontendUrl() {
        return "http://localhost:3000";
    }

    @Bean
    public String fromEmail() {
        return "test@example.com";
    }

    @Bean
    public String verificationLinkExpiration() {
        return "2h";
    }
}
