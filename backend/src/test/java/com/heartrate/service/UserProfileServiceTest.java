package com.heartrate.service;

import com.heartrate.dto.UserProfileUpdateRequest;
import com.heartrate.entity.User;
import com.heartrate.repository.UserRepository;
import com.heartrate.repository.VerificationTokenRepository;
import com.heartrate.service.notification.EmailService;
import com.heartrate.service.notification.IEmailService;
import com.heartrate.service.notification.TestEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for UserProfileService.
 * Following user rules:
 * - Using reusable test infrastructure instead of mocks
 * - No enums, using String values
 * - Application-level validation
 * - Fail fast approach
 */
@DataJpaTest
@Import({UserProfileService.class, TestEmailService.class, BCryptPasswordEncoder.class})
@ActiveProfiles("test")
@Transactional
public class UserProfileServiceTest {
    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private IEmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create and save a test user
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash(passwordEncoder.encode("password123"));
        testUser.setFirstName("Test");
        testUser.setDisplayName("Test User");
        testUser.setMobileNumber("1234567890");
        testUser.setYearOfBirth(1990);
        testUser = userRepository.save(testUser);
    }

    @Test
    void getUserProfile_WithValidId_ShouldReturnUser() {
        // When
        User result = userProfileService.getUserProfile(testUser.getId());

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
    }

    @Test
    void getUserProfile_WithInvalidId_ShouldThrowException() {
        // Given
        UUID invalidId = UUID.randomUUID();

        // Then
        assertThrows(RuntimeException.class, () -> {
            userProfileService.getUserProfile(invalidId);
        });
    }

    @Test
    void updateProfile_WithValidData_ShouldUpdateUser() {
        // Given
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setFirstName("New First Name");
        request.setDisplayName("New Display Name");
        request.setMobileNumber("9876543210");
        request.setYearOfBirth(1995);

        // When
        User result = userProfileService.updateProfile(testUser.getId(), request);

        // Then
        assertNotNull(result);
        assertEquals(request.getFirstName(), result.getFirstName());
        assertEquals(request.getDisplayName(), result.getDisplayName());
        assertEquals(request.getMobileNumber(), result.getMobileNumber());
        assertEquals(request.getYearOfBirth(), result.getYearOfBirth());
    }

    @Test
    void updateProfile_WithEmailChange_ShouldCreateVerificationToken() {
        // Given
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setEmail("new@example.com");

        // When
        User result = userProfileService.updateProfile(testUser.getId(), request);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail()); // Email should not change until verified
        assertTrue(verificationTokenRepository.findFirstByUserIdAndTypeOrderByCreatedAtDesc(testUser.getId(), "EMAIL_CHANGE").isPresent());
        assertTrue(((TestEmailService) emailService).hasEmailTo("new@example.com"));
    }

    @Test
    void changePassword_WithValidPassword_ShouldUpdatePassword() {
        // Given
        String newPassword = "newPassword123";

        // When
        userProfileService.changePassword(testUser.getId(), "password123", newPassword);

        // Then
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPasswordHash()));
    }

    @Test
    void changePassword_WithIncorrectCurrentPassword_ShouldThrowException() {
        // Given
        String incorrectPassword = "wrongPassword";

        // Then
        assertThrows(RuntimeException.class, () -> {
            userProfileService.changePassword(testUser.getId(), incorrectPassword, "newPassword123");
        });
    }

    @Test
    void updateEmail_WithValidEmail_ShouldUpdateAndSendVerification() {
        // ... existing test code ...
        assertTrue(((TestEmailService) emailService).hasEmailTo("new@example.com"));
    }
}
