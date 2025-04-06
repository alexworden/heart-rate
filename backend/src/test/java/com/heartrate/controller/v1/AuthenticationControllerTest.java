package com.heartrate.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heartrate.config.TestConfig;
import com.heartrate.config.TestSecurityConfig;
import com.heartrate.dto.LoginRequest;
import com.heartrate.dto.UserRegistrationRequest;
import com.heartrate.entity.User;
import com.heartrate.entity.VerificationToken;
import com.heartrate.repository.LoginAttemptRepository;
import com.heartrate.repository.UserRepository;
import com.heartrate.repository.VerificationTokenRepository;
import com.heartrate.service.AuthenticationService;
import com.heartrate.service.IEmailService;
import com.heartrate.service.notification.NotificationService;
import com.heartrate.service.TestEmailService;
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
import java.util.Optional;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for AuthenticationController.
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
class AuthenticationControllerTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationService authService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private IEmailService emailService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_IP = "127.0.0.1";

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        // Clear any previous test data
        userRepository.deleteAll();
        loginAttemptRepository.deleteAll();
        verificationTokenRepository.deleteAll();
        ((TestEmailService) emailService).clearSentEmails();

        session = new MockHttpSession();
    }

    @Test
    void register_WithValidRequest_ShouldCreateUser() throws Exception {
        UserRegistrationRequest request = createValidRegistrationRequest();

        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));

        Optional<User> savedUser = userRepository.findByEmail(TEST_EMAIL);
        assertTrue(savedUser.isPresent());
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, savedUser.get().getPasswordHash()));

        TestEmailService testEmailService = (TestEmailService) emailService;
        assertTrue(testEmailService.hasEmailTo(TEST_EMAIL));
    }

    @Test
    void register_WithExistingEmail_ShouldReturn400() throws Exception {
        // Create a user first
        UserRegistrationRequest request = createValidRegistrationRequest();
        authService.registerUser(request);

        // Try to register with the same email
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void login_WithValidCredentials_ShouldReturnUser() throws Exception {
        // Create a user first
        UserRegistrationRequest registrationRequest = createValidRegistrationRequest();
        authService.registerUser(registrationRequest);

        // Login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);
        loginRequest.setRememberMe(true);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(loginRequest))
                .session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value(TEST_EMAIL));

        // Verify session contains user ID
        assertNotNull(session.getAttribute("userId"));
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturn401() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail(TEST_EMAIL);
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request))
                .session(session))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void logout_ShouldInvalidateSession() throws Exception {
        // Create a session with user ID
        session.setAttribute("userId", "test-user-id");

        mockMvc.perform(post("/api/v1/auth/logout")
                .session(session))
            .andExpect(status().isOk());

        assertTrue(session.isInvalid());
    }

    private UserRegistrationRequest createValidRegistrationRequest() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setEmail(TEST_EMAIL);
        request.setPassword(TEST_PASSWORD);
        request.setFirstName("Test");
        request.setDisplayName("Test User");
        request.setMobileNumber("1234567890");
        request.setYearOfBirth(1990);
        return request;
    }

    private String asJsonString(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
