package com.heartrate.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Verification token model.
 * Following user rules:
 * - No enums, using String values
 * - UUIDs instead of incremental IDs
 * - No entity relationships in ORMs
 * - Application-level validation
 * - Fail fast approach
 */
public class VerificationToken {
    private UUID id;
    private UUID userId;  // Simple foreign key reference, no ORM relationship
    private String token;
    private String type;  // Using String instead of enum per user rules
    private OffsetDateTime expiresAt;
    private OffsetDateTime createdAt;

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public OffsetDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
