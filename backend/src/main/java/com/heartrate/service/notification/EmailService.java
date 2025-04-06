package com.heartrate.service.notification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

/**
 * Implementation of IEmailService that sends emails using JavaMailSender.
 * Following user rules:
 * - No enums, using String values
 * - Application-level validation
 * - Fail fast approach with RuntimeExceptions
 */
@Service
@Profile("!test")
public class EmailService implements IEmailService {
    private final JavaMailSender mailSender;
    private final String fromEmail;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String fromEmail) {
        this.mailSender = mailSender;
        this.fromEmail = fromEmail;
    }

    @Override
    public void sendEmail(String to, String subject, String content) {
        if (to == null || to.isBlank()) {
            throw new RuntimeException("Recipient email cannot be null or blank");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);

        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }
} 
