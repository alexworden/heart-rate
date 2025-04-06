package com.heartrate.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heartrate.config.TestConfig;
import com.heartrate.config.TestSecurityConfig;
import com.heartrate.dto.PasswordChangeRequest;
import com.heartrate.dto.UserProfileUpdateRequest;
import com.heartrate.entity.User;
import com.heartrate.repository.UserRepository;
import com.heartrate.repository.VerificationTokenRepository;
import com.heartrate.service.UserProfileService;
import com.heartrate.service.notification.EmailService;
import com.heartrate.service.notification.NotificationService;
import com.heartrate.service.notification.TestEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for UserProfileController.
 * Following user rules:
 * - Using reusable test infrastructure instead of mocks
 * - No enums, using String values
 * - Application-level validation
 * - Fail fast approach
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import({TestConfig.class, TestSecurityConfig.class})
class UserProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private TestEmailService testEmailService;

    private User testUser;
    private MockHttpSession session;
    private LocalDateTime initialTimestamp;

    @BeforeEach
    void setUp() throws InterruptedException {
        // Create initial timestamp
        initialTimestamp = LocalDateTime.now();
        
        // Create a fresh test user
        testUser = createTestUser();
        testUser = userRepository.save(testUser);
        
        // Wait a bit to ensure timestamps will be different
        Thread.sleep(10);

        // Clear any previous test data
        userRepository.deleteAll();
        verificationTokenRepository.deleteAll();
        testEmailService.clearSentEmails();

        // Create session and add user ID
        session = new MockHttpSession();
        session.setAttribute("userId", testUser.getId().toString());
    }

    @Test
    void getProfile_WithValidId_ShouldReturnUser() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/profile/{userId}", testUser.getId())
                .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId().toString()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()))
                .andExpect(jsonPath("$.displayName").value(testUser.getDisplayName()));

        // Verify no emails were sent
        assertTrue(testEmailService.getSentEmails().isEmpty());
    }

    @Test
    void getProfile_WithInvalidId_ShouldReturn404() throws Exception {
        // Arrange
        UUID invalidId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(get("/api/v1/profile/{userId}", invalidId)
                .session(session))
                .andExpect(status().isNotFound());

        // Verify no emails were sent
        assertTrue(testEmailService.getSentEmails().isEmpty());
    }

    @Test
    void getProfile_WithoutSession_ShouldReturn401() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/v1/profile/{userId}", testUser.getId()))
                .andExpect(status().isUnauthorized());

        // Verify no emails were sent
        assertTrue(testEmailService.getSentEmails().isEmpty());
    }

    @Test
    void updateProfile_WithValidData_ShouldUpdateUser() throws Exception {
        // Arrange
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setFirstName("Updated First");
        request.setDisplayName("Updated Display");
        request.setMobileNumber("9876543210");
        request.setAddress("456 Update St");
        request.setYearOfBirth(1995);

        // Act & Assert
        mockMvc.perform(put("/api/v1/profile/{userId}", testUser.getId())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(request.getFirstName()))
                .andExpect(jsonPath("$.displayName").value(request.getDisplayName()))
                .andExpect(jsonPath("$.mobileNumber").value(request.getMobileNumber()))
                .andExpect(jsonPath("$.userAddress").value(request.getAddress()))
                .andExpect(jsonPath("$.yearOfBirth").value(request.getYearOfBirth()));

        // Verify database update
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertTrue(updatedUser.getUpdatedAt().isAfter(initialTimestamp));

        // Verify no emails were sent for non-email updates
        assertTrue(testEmailService.getSentEmails().isEmpty());
    }

    @Test
    void updateProfile_WithInvalidData_ShouldReturn400() throws Exception {
        // Arrange
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setFirstName("");
        request.setDisplayName("");
        request.setMobileNumber("");
        request.setAddress("");
        request.setYearOfBirth(0);

        // Act & Assert
        mockMvc.perform(put("/api/v1/profile/{userId}", testUser.getId())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Verify no emails were sent
        assertTrue(testEmailService.getSentEmails().isEmpty());
    }

    @Test
    void updateProfile_WithoutAuthentication_ShouldReturn401() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/v1/profile/{userId}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new UserProfileUpdateRequest())))
                .andExpect(status().isUnauthorized());

        // Verify no emails were sent
        assertTrue(testEmailService.getSentEmails().isEmpty());
    }

    @Test
    void updateProfile_WithNonExistentUser_ShouldReturn404() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setFirstName("New Name");

        // Act & Assert
        mockMvc.perform(put("/api/v1/profile/{userId}", nonExistentId)
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        // Verify no emails were sent
        assertTrue(testEmailService.getSentEmails().isEmpty());
    }

    @Test
    void updateProfile_WithEmailChange_ShouldCreateVerificationToken() throws Exception {
        // Arrange
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setEmail("newemail@example.com");

        // Act & Assert
        mockMvc.perform(put("/api/v1/profile/{userId}", testUser.getId())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(testUser.getEmail())) // Email should not change yet
                .andExpect(jsonPath("$.pendingEmail").value(request.getEmail()));

        // Verify verification token was created
        var token = verificationTokenRepository.findFirstByUserIdAndTypeOrderByCreatedAtDesc(testUser.getId(), "EMAIL_CHANGE");
        assertTrue(token.isPresent());
        assertEquals(testUser.getId(), token.get().getUserId());
        assertEquals("EMAIL_CHANGE", token.get().getType());

        // Verify verification email was sent
        assertTrue(testEmailService.hasEmailTo(request.getEmail()));
        var email = testEmailService.getEmailTo(request.getEmail());
        assertTrue(email.getSubject().contains("Verify Email Change"));
        assertTrue(email.getContent().contains("newemail@example.com"));
    }

    @Test
    void updateProfile_WithoutChanges_ShouldReturn400() throws Exception {
        // Arrange
        UserProfileUpdateRequest request = new UserProfileUpdateRequest();
        request.setFirstName(testUser.getFirstName());
        request.setDisplayName(testUser.getDisplayName());
        request.setMobileNumber(testUser.getMobileNumber());
        request.setAddress(testUser.getUserAddress());
        request.setYearOfBirth(testUser.getYearOfBirth());

        // Act & Assert
        mockMvc.perform(put("/api/v1/profile/{userId}", testUser.getId())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Verify no emails were sent
        assertTrue(testEmailService.getSentEmails().isEmpty());
    }

    @Test
    void changePassword_WithValidPassword_ShouldUpdatePassword() throws Exception {
        // Arrange
        String newPassword = "newPassword123!";
        String currentPassword = "testPassword123!";

        // Act & Assert
        mockMvc.perform(put("/api/v1/profile/{userId}/password", testUser.getId())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PasswordChangeRequest(currentPassword, newPassword))))
                .andExpect(status().isOk());

        // Verify password was updated
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPasswordHash()));

        // Verify no emails were sent
        assertTrue(testEmailService.getSentEmails().isEmpty());
    }

    @Test
    void changePassword_WithIncorrectCurrentPassword_ShouldReturn400() throws Exception {
        // Arrange
        String newPassword = "newPassword123!";
        String incorrectPassword = "wrongPassword123!";

        // Act & Assert
        mockMvc.perform(put("/api/v1/profile/{userId}/password", testUser.getId())
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PasswordChangeRequest(incorrectPassword, newPassword))))
                .andExpect(status().isBadRequest());

        // Verify password was not updated
        User updatedUser = userRepository.findById(testUser.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches("testPassword123!", updatedUser.getPasswordHash()));

        // Verify no emails were sent
        assertTrue(testEmailService.getSentEmails().isEmpty());
    }

    @Test
    void changePassword_WithoutSession_ShouldReturn401() throws Exception {
        // Act & Assert
        mockMvc.perform(put("/api/v1/profile/{userId}/password", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PasswordChangeRequest("old", "new"))))
                .andExpect(status().isUnauthorized());

        // Verify no emails were sent
        assertTrue(testEmailService.getSentEmails().isEmpty());
    }

    private User createTestUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash(passwordEncoder.encode("testPassword123!"));
        user.setFirstName("Test");
        user.setDisplayName("Test User");
        user.setMobileNumber("1234567890");
        user.setUserAddress("123 Test St");
        user.setYearOfBirth(1990);
        return user;
    }
}
