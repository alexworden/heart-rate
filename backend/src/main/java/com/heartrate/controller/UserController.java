package com.heartrate.controller;

import com.heartrate.model.User;
import com.heartrate.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<User> signUp(@RequestBody User user) {
        return ResponseEntity.ok(userService.signUp(user));
    }

    @PostMapping("/signin")
    public ResponseEntity<User> signIn(@RequestBody Map<String, String> credentials) {
        User user = userService.signIn(
            credentials.get("email"),
            credentials.get("password")
        );
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/reset-password/request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody Map<String, String> request) {
        String token = userService.generateResetToken(request.get("email"));
        if (token != null) {
            return ResponseEntity.ok(token);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Boolean> resetPassword(@RequestBody Map<String, String> request) {
        boolean success = userService.resetPassword(
            request.get("token"),
            request.get("newPassword")
        );
        if (success) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.notFound().build();
    }
} 