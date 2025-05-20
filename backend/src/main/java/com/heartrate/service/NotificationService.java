package com.heartrate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.heartrate.model.User;

@Service
public class NotificationService {
    private static final String RESET_EMAIL_SUBJECT = "Password Reset Request";
    private static final String RESET_EMAIL_TEMPLATE = """
Hello %s,

We received a request to reset your password. If you did not make this request, please ignore this email.

To reset your password, please click the following link:
%s?token=%s

This link will expire in 24 hours.

Best regards,
HeartRate Team""";

    private static final String EMAIL_SIGNATURE = """
            Best regards,
            HeartRate Team""";

    private final EmailService emailService;

    @Autowired
    public NotificationService(EmailService emailService) {
        this.emailService = emailService;
    }

    public void sendPasswordResetEmail(User user, String resetUrl, String resetToken) {
        String emailBody = String.format(RESET_EMAIL_TEMPLATE,
            user.getFirstName(),
            resetUrl,
            resetToken);
            
        emailService.sendEmail(user.getEmail(), RESET_EMAIL_SUBJECT, emailBody);
    }
} 