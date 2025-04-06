package com.heartrate.service.notification;

/**
 * Interface for sending emails.
 * Following user rules:
 * - No enums, using String values
 * - Application-level validation
 * - Fail fast approach with RuntimeExceptions
 */
public interface IEmailService {
    /**
     * Send an email.
     * @param to The recipient email address
     * @param subject The email subject
     * @param content The email content
     * @throws RuntimeException if sending fails
     */
    void sendEmail(String to, String subject, String content);
} 
