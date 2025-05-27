package com.heartrate.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.heartrate.model.ChildOf;
import com.heartrate.model.TypeOf;
import com.heartrate.repository.ChildOfRepository;
import com.heartrate.repository.TypeOfRepository;

@Service
public class RelationshipService {

    @Autowired
    private TypeOfRepository typeOfRepository;

    @Autowired
    private ChildOfRepository childOfRepository;

    // Create a TypeOf relationship
    public TypeOf createTypeOfRelationship(UUID itemId, UUID categoryId) {
        TypeOf typeOf = new TypeOf(itemId, categoryId);
        return typeOfRepository.save(typeOf);
    }

    // Find TypeOf relationships by item ID
    public List<TypeOf> findTypeOfRelationshipsByItemId(UUID itemId) {
        return typeOfRepository.findByItemId(itemId);
    }

    // Find TypeOf relationships by category ID
    public List<TypeOf> findTypeOfRelationshipsByCategoryId(UUID categoryId) {
        return typeOfRepository.findByCategoryId(categoryId);
    }

    // Create a ChildOf relationship
    public ChildOf createChildOfRelationship(UUID childId, UUID parentId) {
        ChildOf childOf = new ChildOf(childId, parentId);
        return childOfRepository.save(childOf);
    }

    // Find ChildOf relationships by child ID
    public List<ChildOf> findChildOfRelationshipsByChildId(UUID childId) {
        return childOfRepository.findByChildId(childId);
    }

    // Find ChildOf relationships by parent ID
    public List<ChildOf> findChildOfRelationshipsByParentId(UUID parentId) {
        return childOfRepository.findByParentId(parentId);
    }
} 