package com.heartrate.service;

import com.heartrate.entity.User;
import com.heartrate.entity.VerificationToken;
import com.heartrate.service.notification.NotificationService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Test implementation of IEmailService that records emails instead of sending them.
 * Used for testing email-related functionality without actually sending emails.
 */
@Service
public class TestEmailService implements IEmailService {
    private final List<EmailRecord> sentEmails = new ArrayList<>();
    private final NotificationService notificationService;

    public TestEmailService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    public void sendVerificationEmail(String toEmail, String token) {
        sentEmails.add(new EmailRecord("Verify Your Email", toEmail, token));
        // Create a temporary user and token for the notification service
        User user = new User();
        user.setEmail(toEmail);
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        notificationService.sendVerificationEmail(user, verificationToken);
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String token) {
        sentEmails.add(new EmailRecord("Reset Your Password", toEmail, token));
        // Create a temporary user and token for the notification service
        User user = new User();
        user.setEmail(toEmail);
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        notificationService.sendPasswordResetEmail(user, verificationToken);
    }

    @Override
    public void sendPasswordlessLoginEmail(String toEmail, String token) {
        sentEmails.add(new EmailRecord("Login Link", toEmail, token));
        // Create a temporary user and token for the notification service
        User user = new User();
        user.setEmail(toEmail);
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        notificationService.sendPasswordlessLoginLink(user, verificationToken);
    }

    public List<EmailRecord> getSentEmails() {
        return new ArrayList<>(sentEmails);
    }

    public void clearSentEmails() {
        sentEmails.clear();
    }

    public boolean hasEmailTo(String email) {
        return sentEmails.stream().anyMatch(record -> record.toEmail().equals(email));
    }

    /**
     * Record class to store email details for verification in tests
     */
    public static class EmailRecord {
        private final String subject;
        private final String toEmail;
        private final String content;

        public EmailRecord(String subject, String toEmail, String content) {
            this.subject = subject;
            this.toEmail = toEmail;
            this.content = content;
        }

        public String subject() {
            return subject;
        }

        public String toEmail() {
            return toEmail;
        }

        public String content() {
            return content;
        }
    }
}
