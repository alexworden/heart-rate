package com.heartrate.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * User model class.
 * Following user rules:
 * - No enums, using String values
 * - UUIDs instead of incremental IDs
 * - No entity relationships in ORMs
 * - Application-level validation
 * - Fail fast approach
 */
public class User {
    private UUID id;
    private String email;
    private String firstName;
    private String displayName;
    private String passwordHash;
    private String mobileNumber;
    private String address;
    private Integer yearOfBirth;
    private Boolean emailVerified;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String rememberMeToken;

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    
    public String getMobileNumber() { return mobileNumber; }
    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public Integer getYearOfBirth() { return yearOfBirth; }
    public void setYearOfBirth(Integer yearOfBirth) { this.yearOfBirth = yearOfBirth; }
    
    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }
    
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public String getRememberMeToken() { return rememberMeToken; }
    public void setRememberMeToken(String rememberMeToken) { this.rememberMeToken = rememberMeToken; }
}
