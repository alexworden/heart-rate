package com.heartrate.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "type_of")
public class TypeOf {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // The item that is a type of a category item
    private UUID itemId;

    // The item that represents the category
    private UUID categoryId;

    // Constructors, Getters, and Setters

    public TypeOf() {
    }

    public TypeOf(UUID itemId, UUID categoryId) {
        this.itemId = itemId;
        this.categoryId = categoryId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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