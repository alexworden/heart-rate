package com.heartrate.controller.v1;

import com.heartrate.dto.LoginRequest;
import com.heartrate.dto.UserRegistrationRequest;
import com.heartrate.entity.User;
import com.heartrate.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints.
 * Following user rules:
 * - No enums, using String values
 * - UUIDs for all IDs
 * - No entity relationships
 * - Application-level validation instead of database constraints
 * - Fail fast approach
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authService;

    public AuthenticationController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        User user = authService.registerUser(request);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<User> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest servletRequest,
            HttpSession session) {
        String ipAddress = servletRequest.getRemoteAddr();
        User user = authService.login(request, ipAddress);
        
        // Store user ID in session
        session.setAttribute("userId", user.getId().toString());
        
        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Void> verifyEmail(@RequestParam String token) {
        authService.verifyEmailToken(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/request-password-reset")
    public ResponseEntity<Void> requestPasswordReset(@RequestParam String email) {
        authService.requestPasswordReset(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/request-passwordless-login")
    public ResponseEntity<Void> requestPasswordlessLogin(@RequestParam String email) {
        authService.requestPasswordlessLogin(email);
        return ResponseEntity.ok().build();
    }
}
