package com.heartrate.service.notification;

/**
 * Represents a notification that was sent.
 */
public class Notification {
    private final String type;
    private final String recipient;
    private final String content;

    public Notification(String type, String recipient, String content) {
        this.type = type;
        this.recipient = recipient;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getContent() {
        return content;
    }
} 
