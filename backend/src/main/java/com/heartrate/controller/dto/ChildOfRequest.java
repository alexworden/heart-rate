package com.heartrate.controller.dto;

import java.util.UUID;

public class ChildOfRequest {
    private UUID childId;
    private UUID parentId;

    // Getters and setters

    public UUID getChildId() {
        return childId;
    }

    public void setChildId(UUID childId) {
        this.childId = childId;
    }

    public UUID getParentId() {
        return parentId;
    }

    public void setParentId(UUID parentId) {
        this.parentId = parentId;
    }
} 