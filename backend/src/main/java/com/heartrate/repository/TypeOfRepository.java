package com.heartrate.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.heartrate.model.TypeOf;

@Repository
public interface TypeOfRepository extends JpaRepository<TypeOf, UUID> {
    // Find all TypeOf relationships for a given item (where the item is the 'is a type of' item)
    List<TypeOf> findByItemId(UUID itemId);

    // Find all TypeOf relationships for a given category item
    List<TypeOf> findByCategoryId(UUID categoryId);
} 