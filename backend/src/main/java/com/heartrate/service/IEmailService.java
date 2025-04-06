package com.heartrate.service;

/**
 * Interface defining the contract for email services.
 * This allows for different implementations (real email sending vs test) without inheritance.
 */
public interface IEmailService {
    void sendVerificationEmail(String toEmail, String token);
    void sendPasswordResetEmail(String toEmail, String token);
    void sendPasswordlessLoginEmail(String toEmail, String token);
} 
