package com.heartrate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO for login requests.
 * Following user rules:
 * - Application-level validation instead of database constraints
 * - No enums, using String values
 */
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private boolean rememberMe;

    public LoginRequest() {
        // Default constructor for JSON deserialization
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
        this.rememberMe = false;
    }
    
    // Getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isRememberMe() { return rememberMe; }
    public void setRememberMe(boolean rememberMe) { this.rememberMe = rememberMe; }
}
