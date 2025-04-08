package com.heartrate.service;

import java.util.HashMap;
import java.util.Map;

public class TestEmailService implements EmailService {
    private final Map<String, SentEmail> sentEmails = new HashMap<>();

    @Override
    public void sendEmail(String to, String subject, String body) {
        sentEmails.put(to, new SentEmail(to, subject, body));
    }

    public SentEmail getLastEmailFor(String email) {
        return sentEmails.get(email);
    }

    public void clear() {
        sentEmails.clear();
    }

    public static class SentEmail {
        private final String to;
        private final String subject;
        private final String body;

        public SentEmail(String to, String subject, String body) {
            this.to = to;
            this.subject = subject;
            this.body = body;
        }

        public String getTo() {
            return to;
        }

        public String getSubject() {
            return subject;
        }

        public String getBody() {
            return body;
        }
    }
} 