package com.heartrate.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heartrate.controller.dto.ItemFromUrlRequest;
import com.heartrate.model.Item;
import com.heartrate.model.Rating;
import com.heartrate.model.User;
import com.heartrate.service.ItemService;
import com.heartrate.service.RatingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private RatingService ratingService;

    @PostMapping("/from-url")
    public ResponseEntity<Item> createItemFromUrl(
            @Valid @RequestBody ItemFromUrlRequest itemRequest
    ) {
        try {
            Item savedItem = itemService.saveItemFromUrl(itemRequest);
            return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.err.println("Error creating item from URL: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @PostMapping("/{itemId}/rate")
    public ResponseEntity<Rating> rateItem(
            @PathVariable UUID itemId,
            @RequestBody Integer ratingValue,
            @AuthenticationPrincipal User user
    ) {
        try {
            Rating savedRating = ratingService.saveRating(user.getId(), itemId, ratingValue, "RATED");
            return ResponseEntity.ok(savedRating);
        } catch (RuntimeException e) {
            // Log the error for debugging
            System.err.println("Error rating item: " + e.getMessage());
            // Return a NOT_FOUND status if the exception indicates item not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Or return an error response body
        }
    }

    @PostMapping("/{itemId}/dont-know")
    public ResponseEntity<Rating> dontKnowItem(
            @PathVariable UUID itemId,
            @AuthenticationPrincipal User user
    ) {
        try {
            Rating savedRating = ratingService.saveRating(user.getId(), itemId, null, "DONT_KNOW");
            return ResponseEntity.ok(savedRating);
        } catch (RuntimeException e) {
            System.err.println("Error marking item as don't know: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/{itemId}/dont-care")
    public ResponseEntity<Rating> dontCareItem(
            @PathVariable UUID itemId,
            @AuthenticationPrincipal User user
    ) {
        try {
            Rating savedRating = ratingService.saveRating(user.getId(), itemId, null, "DONT_CARE");
            return ResponseEntity.ok(savedRating);
        } catch (RuntimeException e) {
            System.err.println("Error marking item as don't care: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Item> getItemById(@PathVariable UUID itemId) {
        System.out.println("Attempting to retrieve item with ID: " + itemId);
        Optional<Item> item = itemService.findById(itemId);
        if (item.isPresent()) {
            System.out.println("Item found: " + item.get().getName());
            return ResponseEntity.ok(item.get());
        } else {
            System.out.println("Item with ID " + itemId + " not found.");
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/next-for-user")
    public ResponseEntity<?> getNextItemForUser(@RequestHeader("Authorization") String authHeader) {
        // TODO: Implement logic to find an unrated item for the user
        // For now, return a placeholder response
        return ResponseEntity.ok(java.util.Map.of("status", "NO_ITEMS"));
    }
} 