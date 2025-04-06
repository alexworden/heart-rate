package com.heartrate.dto;

/**
 * DTO for password change requests.
 * Following user rules:
 * - No enums, using String values
 * - Application-level validation
 * - Fail fast approach
 */
public class PasswordChangeRequest {
    private String currentPassword;
    private String newPassword;

    public PasswordChangeRequest() {
    }

    public PasswordChangeRequest(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
} 
