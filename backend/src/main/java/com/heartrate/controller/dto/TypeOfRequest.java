package com.heartrate.controller.dto;

import java.util.UUID;

public class TypeOfRequest {
    private UUID itemId;
    private UUID categoryId;

    // Getters and setters

    public UUID getItemId() {
        return itemId;
    }

    public void setItemId(UUID itemId) {
        this.itemId = itemId;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }
} 