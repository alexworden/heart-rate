package com.heartrate.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heartrate.model.ChildOf;

@Repository
public interface ChildOfRepository extends JpaRepository<ChildOf, UUID> {
    // Find all ChildOf relationships for a given child item
    List<ChildOf> findByChildId(UUID childId);

    // Find all ChildOf relationships for a given parent item
    List<ChildOf> findByParentId(UUID parentId);
} 