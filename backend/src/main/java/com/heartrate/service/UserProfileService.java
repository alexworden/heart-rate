package com.heartrate.service;

import com.heartrate.dto.UserProfileUpdateRequest;
import com.heartrate.entity.User;
import com.heartrate.entity.VerificationToken;
import com.heartrate.repository.UserRepository;
import com.heartrate.repository.VerificationTokenRepository;
import com.heartrate.service.notification.EmailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Service for user profile management.
 * Following user rules:
 * - No enums, using String values
 * - UUIDs for all IDs
 * - No ORM relationships
 * - Application-level validation instead of database constraints
 * - Fail fast approach
 */
@Service
public class UserProfileService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserProfileService(UserRepository userRepository,
                            VerificationTokenRepository verificationTokenRepository,
                            PasswordEncoder passwordEncoder,
                            EmailService emailService) {
        this.userRepository = userRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public User getUserProfile(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public User updateProfile(UUID userId, UserProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Update basic profile fields
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName());
        }
        if (request.getMobileNumber() != null) {
            user.setMobileNumber(request.getMobileNumber());
        }
        if (request.getYearOfBirth() != null) {
            user.setYearOfBirth(request.getYearOfBirth());
        }

        // Handle email change separately
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            // Create verification token
            VerificationToken token = new VerificationToken();
            token.setUserId(userId);
            token.setType("EMAIL_CHANGE");
            token.setExpiresAt(LocalDateTime.now().plusHours(24));
            verificationTokenRepository.save(token);

            // Send verification email
            emailService.sendEmail(
                request.getEmail(),
                "Verify Email Change",
                String.format(
                    "Please verify your new email address by clicking the link below:\n\n" +
                    "http://localhost:3000/verify-email-change?token=%s\n\n" +
                    "This link will expire in 24 hours.\n\n" +
                    "If you did not request this email change, please ignore this email.",
                    token.getToken()
                )
            );
        }

        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new RuntimeException("Incorrect current password");
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
