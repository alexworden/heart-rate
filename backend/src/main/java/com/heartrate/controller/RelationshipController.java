package com.heartrate.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heartrate.controller.dto.ChildOfRequest;
import com.heartrate.controller.dto.TypeOfRequest;
import com.heartrate.model.ChildOf;
import com.heartrate.model.TypeOf;
import com.heartrate.service.RelationshipService;

@RestController
@RequestMapping("/api/relationships")
public class RelationshipController {

    @Autowired
    private RelationshipService relationshipService;

    // Endpoint to create a TypeOf relationship
    @PostMapping("/typeof")
    public ResponseEntity<TypeOf> createTypeOfRelationship(@RequestBody TypeOfRequest request) {
        TypeOf typeOf = relationshipService.createTypeOfRelationship(request.getItemId(), request.getCategoryId());
        return new ResponseEntity<>(typeOf, HttpStatus.CREATED);
    }

    // Endpoint to find TypeOf relationships by item ID
    @GetMapping("/typeof/item/{itemId}")
    public ResponseEntity<List<TypeOf>> findTypeOfRelationshipsByItemId(@PathVariable UUID itemId) {
        List<TypeOf> relationships = relationshipService.findTypeOfRelationshipsByItemId(itemId);
        return ResponseEntity.ok(relationships);
    }

    // Endpoint to find TypeOf relationships by category ID
    @GetMapping("/typeof/category/{categoryId}")
    public ResponseEntity<List<TypeOf>> findTypeOfRelationshipsByCategoryId(@PathVariable UUID categoryId) {
        List<TypeOf> relationships = relationshipService.findTypeOfRelationshipsByCategoryId(categoryId);
        return ResponseEntity.ok(relationships);
    }

    // Endpoint to create a ChildOf relationship
    @PostMapping("/childof")
    public ResponseEntity<ChildOf> createChildOfRelationship(@RequestBody ChildOfRequest request) {
        ChildOf childOf = relationshipService.createChildOfRelationship(request.getChildId(), request.getParentId());
        return new ResponseEntity<>(childOf, HttpStatus.CREATED);
    }

    // Endpoint to find ChildOf relationships by child ID
    @GetMapping("/childof/child/{childId}")
    public ResponseEntity<List<ChildOf>> findChildOfRelationshipsByChildId(@PathVariable UUID childId) {
        List<ChildOf> relationships = relationshipService.findChildOfRelationshipsByChildId(childId);
        return ResponseEntity.ok(relationships);
    }

    // Endpoint to find ChildOf relationships by parent ID
    @GetMapping("/childof/parent/{parentId}")
    public ResponseEntity<List<ChildOf>> findChildOfRelationshipsByParentId(@PathVariable UUID parentId) {
        List<ChildOf> relationships = relationshipService.findChildOfRelationshipsByParentId(parentId);
        return ResponseEntity.ok(relationships);
    }
} 