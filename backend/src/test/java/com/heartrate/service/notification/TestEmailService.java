package com.heartrate.service.notification;

import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Test implementation of IEmailService that records sent emails for verification.
 * Following user rules:
 * - No enums, using String values
 * - Application-level validation
 * - Fail fast approach with RuntimeExceptions
 */
@Service
@Profile("test")
public class TestEmailService implements IEmailService {
    private final List<SentEmail> sentEmails = new ArrayList<>();
    private boolean failFast = true;

    @Override
    public void sendEmail(String to, String subject, String content) {
        if (to == null || to.isBlank()) {
            throw new RuntimeException("Recipient email cannot be null or blank");
        }
        sentEmails.add(new SentEmail(to, subject, content));
    }

    public void setFailFast(boolean failFast) {
        this.failFast = failFast;
    }

    public List<SentEmail> getSentEmails() {
        return new ArrayList<>(sentEmails);
    }

    public void clearSentEmails() {
        sentEmails.clear();
    }

    public SentEmail getLastSentEmail() {
        return sentEmails.isEmpty() ? null : sentEmails.get(sentEmails.size() - 1);
    }

    public boolean hasEmailTo(String email) {
        return sentEmails.stream().anyMatch(e -> e.getTo().equals(email));
    }

    public SentEmail getEmailTo(String email) {
        return sentEmails.stream()
                .filter(e -> e.getTo().equals(email))
                .findFirst()
                .orElse(null);
    }

    public static class SentEmail {
        private final String to;
        private final String subject;
        private final String content;
        private final long timestamp;

        public SentEmail(String to, String subject, String content) {
            this.to = to;
            this.subject = subject;
            this.content = content;
            this.timestamp = System.currentTimeMillis();
        }

        public String getTo() {
            return to;
        }

        public String getSubject() {
            return subject;
        }

        public String getContent() {
            return content;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
} 
