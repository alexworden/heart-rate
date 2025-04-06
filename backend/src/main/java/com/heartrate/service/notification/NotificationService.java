package com.heartrate.service.notification;

import com.heartrate.entity.User;
import com.heartrate.entity.VerificationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for sending notifications.
 * Following user rules:
 * - No enums, using String values
 * - Application-level validation
 * - Fail fast approach with RuntimeExceptions
 */
@Service
public class NotificationService {
    private final IEmailService emailService;
    private final String frontendUrl;
    private final String verificationLinkExpiration;

    public NotificationService(
            IEmailService emailService,
            @Value("${app.frontend.url}") String frontendUrl,
            @Value("${app.verification.link.expiration}") String verificationLinkExpiration) {
        this.emailService = emailService;
        this.frontendUrl = frontendUrl;
        this.verificationLinkExpiration = verificationLinkExpiration;
    }

    public void sendVerificationEmail(User user, VerificationToken token) {
        String content = buildVerificationEmailContent(token);
        emailService.sendEmail(user.getEmail(), "Verify Your Email", content);
    }

    public void sendPasswordResetEmail(User user, VerificationToken token) {
        String content = buildPasswordResetEmailContent(token);
        emailService.sendEmail(user.getEmail(), "Reset Your Password", content);
    }

    public void sendEmailChangeVerification(User user, VerificationToken token) {
        String content = buildEmailChangeVerificationContent(token);
        emailService.sendEmail(user.getEmail(), "Verify Email Change", content);
    }

    public void sendPasswordlessLoginLink(User user, VerificationToken token) {
        String content = buildPasswordlessLoginEmailContent(token);
        emailService.sendEmail(user.getEmail(), "Login Link", content);
    }

    private String buildVerificationEmailContent(VerificationToken token) {
        return String.format(
            "Please verify your email address by clicking the link below:\n\n" +
            "%s/verify-email?token=%s\n\n" +
            "This link will expire in %s.\n\n" +
            "If you did not request this verification, please ignore this email.",
            frontendUrl, token.getToken(), verificationLinkExpiration
        );
    }

    private String buildPasswordResetEmailContent(VerificationToken token) {
        return String.format(
            "You have requested to reset your password. Click the link below to proceed:\n\n" +
            "%s/reset-password?token=%s\n\n" +
            "This link will expire in %s.\n\n" +
            "If you did not request this password reset, please ignore this email.",
            frontendUrl, token.getToken(), verificationLinkExpiration
        );
    }

    private String buildEmailChangeVerificationContent(VerificationToken token) {
        return String.format(
            "You have requested to change your email address. Click the link below to verify:\n\n" +
            "%s/verify-email-change?token=%s\n\n" +
            "This link will expire in %s.\n\n" +
            "If you did not request this email change, please ignore this email.",
            frontendUrl, token.getToken(), verificationLinkExpiration
        );
    }

    private String buildPasswordlessLoginEmailContent(VerificationToken token) {
        return String.format(
            "Click the link below to log in:\n\n" +
            "%s/login?token=%s\n\n" +
            "This link will expire in %s.\n\n" +
            "If you did not request this login link, please ignore this email.",
            frontendUrl, token.getToken(), verificationLinkExpiration
        );
    }
}
