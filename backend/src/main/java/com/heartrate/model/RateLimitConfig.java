package com.heartrate.model;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Rate limit configuration model.
 * Following user rules:
 * - No enums, using String values
 * - UUIDs instead of incremental IDs
 * - No entity relationships in ORMs
 * - Application-level validation
 * - Fail fast approach
 */
public class RateLimitConfig {
    private UUID id;
    private String key;
    private int maxAttempts;
    private int windowMinutes;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    
    public int getMaxAttempts() { return maxAttempts; }
    public void setMaxAttempts(int maxAttempts) { this.maxAttempts = maxAttempts; }
    
    public int getWindowMinutes() { return windowMinutes; }
    public void setWindowMinutes(int windowMinutes) { this.windowMinutes = windowMinutes; }
    
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
