package com.heartrate.service;

import com.heartrate.dto.LoginRequest;
import com.heartrate.dto.UserRegistrationRequest;
import com.heartrate.entity.User;
import com.heartrate.repository.LoginAttemptRepository;
import com.heartrate.repository.UserRepository;
import com.heartrate.repository.VerificationTokenRepository;
import com.heartrate.service.notification.EmailService;
import com.heartrate.service.notification.IEmailService;
import com.heartrate.service.notification.NotificationService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for AuthenticationService.
 * Following user rules:
 * - Using reusable test infrastructure instead of mocks
 * - No enums, using String values
 * - Application-level validation
 * - Fail fast approach
 */
@DataJpaTest
@Import({AuthenticationService.class, BCryptPasswordEncoder.class, TestEmailService.class, NotificationService.class})
@ActiveProfiles("test")
@Transactional
public class AuthenticationServiceTest {
    @Autowired
    private AuthenticationService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private IEmailService emailService;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final String TEST_IP = "127.0.0.1";

    @BeforeEach
    void setUp() {
        // Clear any previous test data
        userRepository.deleteAll();
        loginAttemptRepository.deleteAll();
        verificationTokenRepository.deleteAll();
        ((TestEmailService) emailService).clearSentEmails();
    }

    @Test
    void registerUser_WithValidRequest_ShouldCreateUser() {
        // Given
        UserRegistrationRequest request = createValidRegistrationRequest();

        // When
        User user = authService.registerUser(request);

        // Then
        assertNotNull(user);
        assertEquals(TEST_EMAIL, user.getEmail());
        assertFalse(user.getEmailVerified());
        assertTrue(passwordEncoder.matches(TEST_PASSWORD, user.getPasswordHash()));

        // Verify verification token was created
        Optional<User> savedUser = userRepository.findByEmail(TEST_EMAIL);
        assertTrue(savedUser.isPresent());
        assertTrue(verificationTokenRepository.findFirstByUserIdAndTypeOrderByCreatedAtDesc(savedUser.get().getId(), "EMAIL_VERIFICATION").isPresent());

        // Verify verification email was sent
        TestEmailService testEmailService = (TestEmailService) emailService;
        assertTrue(testEmailService.hasEmailTo(TEST_EMAIL));
        TestEmailService.SentEmail sentEmail = testEmailService.getEmailTo(TEST_EMAIL);
        assertEquals("Verify Your Email", sentEmail.getSubject());
        assertTrue(sentEmail.getContent().contains("verify your email address"));
    }

    @Test
    void registerUser_WithExistingEmail_ShouldThrowException() {
        // Given
        UserRegistrationRequest request = createValidRegistrationRequest();
        authService.registerUser(request);

        // Then
        assertThrows(RuntimeException.class, () -> {
            authService.registerUser(request);
        });
    }

    @Test
    void login_WithValidCredentials_ShouldReturnUser() {
        // Given
        UserRegistrationRequest registrationRequest = createValidRegistrationRequest();
        User user = authService.registerUser(registrationRequest);
        user.setEmailVerified(true);
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);
        loginRequest.setRememberMe(true);

        // When
        User loggedInUser = authService.login(loginRequest, TEST_IP);

        // Then
        assertNotNull(loggedInUser);
        assertEquals(TEST_EMAIL, loggedInUser.getEmail());
        assertNotNull(loggedInUser.getRememberMeToken());
    }

    @Test
    void login_WithInvalidCredentials_ShouldThrowException() {
        // Given
        UserRegistrationRequest registrationRequest = createValidRegistrationRequest();
        User user = authService.registerUser(registrationRequest);
        user.setEmailVerified(true);
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword("wrongpassword");

        // Then
        assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest, TEST_IP);
        });
    }

    @Test
    void login_WithUnverifiedEmail_ShouldThrowException() {
        // Given
        UserRegistrationRequest registrationRequest = createValidRegistrationRequest();
        authService.registerUser(registrationRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);

        // Then
        assertThrows(RuntimeException.class, () -> {
            authService.login(loginRequest, TEST_IP);
        });
    }

    @Test
    void verifyEmailToken_WithValidToken_ShouldVerifyEmail() {
        // Given
        UserRegistrationRequest request = createValidRegistrationRequest();
        User user = authService.registerUser(request);
        String token = verificationTokenRepository
            .findFirstByUserIdAndTypeOrderByCreatedAtDesc(user.getId(), "EMAIL_VERIFICATION")
            .orElseThrow()
            .getToken();

        // When
        authService.verifyEmailToken(token);

        // Then
        User verifiedUser = userRepository.findById(user.getId()).orElseThrow();
        assertTrue(verifiedUser.getEmailVerified());
    }

    @Test
    void requestPasswordReset_WithValidEmail_ShouldSendResetEmail() {
        // Given
        UserRegistrationRequest request = createValidRegistrationRequest();
        User user = authService.registerUser(request);
        user.setEmailVerified(true);
        userRepository.save(user);

        // When
        authService.requestPasswordReset(TEST_EMAIL);

        // Then
        // Verify reset token was created
        assertTrue(verificationTokenRepository.findFirstByUserIdAndTypeOrderByCreatedAtDesc(user.getId(), "PASSWORD_RESET").isPresent());

        // Verify reset email was sent
        TestEmailService testEmailService = (TestEmailService) emailService;
        assertTrue(testEmailService.hasEmailTo(TEST_EMAIL));
        TestEmailService.SentEmail sentEmail = testEmailService.getEmailTo(TEST_EMAIL);
        assertEquals("Reset Your Password", sentEmail.getSubject());
        assertTrue(sentEmail.getContent().contains("reset your password"));
    }

    @Test
    void resetPassword_WithValidToken_ShouldUpdatePassword() {
        // Given
        UserRegistrationRequest request = createValidRegistrationRequest();
        User user = authService.registerUser(request);
        user.setEmailVerified(true);
        userRepository.save(user);

        authService.requestPasswordReset(TEST_EMAIL);
        String token = verificationTokenRepository
            .findFirstByUserIdAndTypeOrderByCreatedAtDesc(user.getId(), "PASSWORD_RESET")
            .orElseThrow()
            .getToken();

        String newPassword = "newpassword123";

        // When
        authService.resetPassword(token, newPassword);

        // Then
        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPasswordHash()));
    }

    @Test
    void requestPasswordlessLogin_WithValidEmail_ShouldSendLoginLink() {
        // Given
        UserRegistrationRequest request = createValidRegistrationRequest();
        User user = authService.registerUser(request);
        user.setEmailVerified(true);
        userRepository.save(user);

        // When
        authService.requestPasswordlessLogin(TEST_EMAIL);

        // Then
        // Verify login token was created
        assertTrue(verificationTokenRepository.findFirstByUserIdAndTypeOrderByCreatedAtDesc(user.getId(), "PASSWORDLESS_LOGIN").isPresent());

        // Verify login email was sent
        TestEmailService testEmailService = (TestEmailService) emailService;
        assertTrue(testEmailService.hasEmailTo(TEST_EMAIL));
        TestEmailService.SentEmail sentEmail = testEmailService.getEmailTo(TEST_EMAIL);
        assertEquals("Login Link", sentEmail.getSubject());
        assertTrue(sentEmail.getContent().contains("Click the link below to log in"));
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
}
