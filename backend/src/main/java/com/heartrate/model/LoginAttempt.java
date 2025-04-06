package com.heartrate.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Login attempt tracking model.
 * Following user rules:
 * - No enums, using String values
 * - UUIDs instead of incremental IDs
 * - No entity relationships in ORMs
 * - Application-level validation
 * - Fail fast approach
 */
public class LoginAttempt {
    private UUID id;
    private String email;
    private String ipAddress;
    private int attemptCount;
    private OffsetDateTime windowStart;
    private OffsetDateTime lastAttempt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public int getAttemptCount() { return attemptCount; }
    public void setAttemptCount(int attemptCount) { this.attemptCount = attemptCount; }
    
    public OffsetDateTime getWindowStart() { return windowStart; }
    public void setWindowStart(OffsetDateTime windowStart) { this.windowStart = windowStart; }
    
    public OffsetDateTime getLastAttempt() { return lastAttempt; }
    public void setLastAttempt(OffsetDateTime lastAttempt) { this.lastAttempt = lastAttempt; }
    
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
