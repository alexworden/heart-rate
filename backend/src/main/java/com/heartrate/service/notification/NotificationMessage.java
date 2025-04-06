package com.heartrate.service.notification;

/**
 * Represents a notification message that can be sent to a user.
 * Following user rules:
 * - No enums, using String values
 * - Application-level validation
 */
public interface NotificationMessage {
    String getRecipient();
    String getSubject();
    String getContent();
    String getType();  // "EMAIL", "SMS", etc.
}
