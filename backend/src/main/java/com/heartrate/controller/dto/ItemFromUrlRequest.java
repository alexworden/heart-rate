package com.heartrate.controller.dto;

import jakarta.validation.constraints.NotBlank;

public class ItemFromUrlRequest {
    @NotBlank(message = "Name is mandatory")
    private String name;

    private String description;

    private String imageUrl;

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
} 