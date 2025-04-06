package com.heartrate.controller.v1;

import com.heartrate.dto.UserProfileUpdateRequest;
import com.heartrate.entity.User;
import com.heartrate.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for user profile management.
 * Following user rules:
 * - No enums, using String values
 * - UUIDs for all IDs
 * - No entity relationships
 * - Application-level validation instead of database constraints
 * - Fail fast approach
 */
@RestController
@RequestMapping("/api/v1/profile")
public class UserProfileController {
    private final UserProfileService profileService;

    public UserProfileController(UserProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getProfile(@PathVariable String userId) {
        User user = profileService.getUserProfile(UUID.fromString(userId));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateProfile(
            @PathVariable String userId,
            @Valid @RequestBody UserProfileUpdateRequest request) {
        User updatedUser = profileService.updateProfile(UUID.fromString(userId), request);
        return ResponseEntity.ok(updatedUser);
    }

    @PostMapping("/{userId}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable String userId,
            @RequestParam String currentPassword,
            @RequestParam String newPassword) {
        profileService.changePassword(UUID.fromString(userId), currentPassword, newPassword);
        return ResponseEntity.ok().build();
    }
}
