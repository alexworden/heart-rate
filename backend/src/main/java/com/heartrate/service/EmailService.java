package com.heartrate.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

/**
 * Service for handling email communications.
 * Following user rules:
 * - Fail fast approach
 * - No enums, using String values
 */
@Service
public class EmailService implements IEmailService {
    private final JavaMailSender mailSender;
    
    @Value("${app.security.verification-link.expiration:2h}")
    private String verificationLinkExpiration;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${server.cors.allowed-origins}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String token) {
        try {
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(toEmail);
                message.setSubject("Verify Your Email");
                message.setText(buildVerificationEmailContent(token));
                mailSender.send(message);
            } else {
                System.out.println("EmailService: mailSender is null, likely in a test environment. Skipping real email send.");
            }
        } catch (Exception e) {
            // Following fail-fast principle
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(toEmail);
                message.setSubject("Reset Your Password");
                message.setText(buildPasswordResetEmailContent(token));
                mailSender.send(message);
            } else {
                System.out.println("EmailService: mailSender is null, likely in a test environment. Skipping real email send.");
            }
        } catch (Exception e) {
            // Following fail-fast principle
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    public void sendPasswordlessLoginEmail(String toEmail, String token) {
        try {
            if (mailSender != null) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(toEmail);
                message.setSubject("Login Link");
                message.setText(buildPasswordlessLoginEmailContent(token));
                mailSender.send(message);
            } else {
                System.out.println("EmailService: mailSender is null, likely in a test environment. Skipping real email send.");
            }
        } catch (Exception e) {
            // Following fail-fast principle
            throw new RuntimeException("Failed to send login link email", e);
        }
    }

    private String buildVerificationEmailContent(String token) {
        return String.format(
            "Please verify your email address by clicking the link below:\n\n" +
            "%s/verify-email?token=%s\n\n" +
            "This link will expire in %s.\n\n" +
            "If you did not request this verification, please ignore this email.",
            frontendUrl, token, verificationLinkExpiration
        );
    }

    private String buildPasswordResetEmailContent(String token) {
        return String.format(
            "You have requested to reset your password. Click the link below to proceed:\n\n" +
            "%s/reset-password?token=%s\n\n" +
            "This link will expire in %s.\n\n" +
            "If you did not request this password reset, please ignore this email.",
            frontendUrl, token, verificationLinkExpiration
        );
    }

    private String buildPasswordlessLoginEmailContent(String token) {
        return String.format(
            "Click the link below to log in:\n\n" +
            "%s/login?token=%s\n\n" +
            "This link will expire in %s.\n\n" +
            "If you did not request this login link, please ignore this email.",
            frontendUrl, token, verificationLinkExpiration
        );
    }
}
