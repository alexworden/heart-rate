package com.heartrate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StubEmailService implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(StubEmailService.class);

    @Override
    public void sendEmail(String to, String subject, String body) {
        logger.info("Stub Email Service - Would send email:");
        logger.info("To: {}", to);
        logger.info("Subject: {}", subject);
        logger.info("Body: {}", body);
    }
} 