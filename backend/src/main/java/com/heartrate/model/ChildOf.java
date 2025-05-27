package com.heartrate.model;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "child_of")
public class ChildOf {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // The item that is the child
    private UUID childId;

    // The item that is the parent
    private UUID parentId;

    // Constructors, Getters, and Setters

    public ChildOf() {
    }

    public ChildOf(UUID childId, UUID parentId) {
        this.childId = childId;
        this.parentId = parentId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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