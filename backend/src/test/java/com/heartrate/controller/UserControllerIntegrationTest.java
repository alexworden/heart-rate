package com.heartrate.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.heartrate.model.User;
import com.heartrate.service.TestEmailService;
import com.heartrate.service.UserService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private TestEmailService testEmailService;

    @BeforeEach
    void setUp() {
        testEmailService.clear();
    }

    @Test
    public void testSignUpAndSignIn() {
        // Test signup
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));

        ResponseEntity<User> signUpResponse = restTemplate.postForEntity(
            "/api/users/signup",
            user,
            User.class
        );
        assertEquals(HttpStatus.OK, signUpResponse.getStatusCode());
        assertNotNull(signUpResponse.getBody());
        assertEquals("Test", signUpResponse.getBody().getFirstName());

        // Test signin
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", "test@example.com");
        credentials.put("password", "password123");

        ResponseEntity<User> signInResponse = restTemplate.postForEntity(
            "/api/users/signin",
            credentials,
            User.class
        );
        assertEquals(HttpStatus.OK, signInResponse.getStatusCode());
        assertNotNull(signInResponse.getBody());
        assertEquals("Test", signInResponse.getBody().getFirstName());
    }

    @Test
    void testPasswordResetFlow() {
        // Create a user
        User user = new User();
        user.setEmail("reset@example.com");
        user.setPassword("password123");
        user.setFirstName("Test");
        user.setLastName("User");
        user = userService.signup(user);

        // Request password reset
        Map<String, String> request = new HashMap<>();
        request.put("email", "reset@example.com");
        
        ResponseEntity<Void> response = restTemplate.postForEntity(
            "/api/users/reset-password/request",
            request,
            Void.class
        );
        
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify email was sent
        TestEmailService.SentEmail sentEmail = testEmailService.getLastEmailFor("reset@example.com");
        assertNotNull(sentEmail);
        assertEquals("Password Reset Request", sentEmail.getSubject());
        System.out.println("Email body: " + sentEmail.getBody());
        assertTrue(sentEmail.getBody().contains("http://localhost:8080/reset-password?token="));

        // Extract token from email body
        String emailBody = sentEmail.getBody();
        String token = emailBody.substring(
            emailBody.indexOf("token=") + 6,
            emailBody.indexOf("\n", emailBody.indexOf("token="))
        );

        // Reset password
        Map<String, String> resetRequest = new HashMap<>();
        resetRequest.put("token", token);
        resetRequest.put("newPassword", "newPassword123");

        response = restTemplate.postForEntity(
            "/api/users/reset-password",
            resetRequest,
            Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify new password works
        User loggedInUser = userService.signin("reset@example.com", "newPassword123");
        assertNotNull(loggedInUser);
    }
} 