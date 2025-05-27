package com.heartrate.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.heartrate.model.User;
import com.heartrate.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signup(@RequestBody User user) {
        return ResponseEntity.ok(userService.signup(user));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        logger.info("Login attempt received - Email: {}", email);
        logger.debug("Login attempt - Password length: {}", password != null ? password.length() : 0);
        
        try {
            Map<String, Object> response = userService.signin(email, password);
            
            if (response != null) {
                logger.info("Login successful for user: {}", email);
                return ResponseEntity.ok(response);
            }
            
            logger.warn("Login failed for user: {} - Invalid credentials", email);
            return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
        } catch (Exception e) {
            logger.error("Error during login attempt for user: {} - {}", email, e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("message", "An error occurred during login"));
        }
    }

    @PostMapping("/reset-password/request")
    public ResponseEntity<Void> requestPasswordReset(@RequestBody Map<String, String> request) {
        userService.requestPasswordReset(request.get("email"));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody Map<String, String> request) {
        try {
            userService.resetPassword(
                request.get("token"),
                request.get("newPassword")
            );
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix
        User user = userService.getCurrentUser(token);
        
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        return ResponseEntity.ok(user);
    }
} 