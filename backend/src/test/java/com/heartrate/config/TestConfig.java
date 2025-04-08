package com.heartrate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.heartrate.service.EmailService;
import com.heartrate.service.TestEmailService;

@Configuration
public class TestConfig {
    @Bean
    @Primary
    public EmailService emailService() {
        return new TestEmailService();
    }
} 