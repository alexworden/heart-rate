package com.heartrate.controller;

import com.heartrate.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

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
    public void testPasswordReset() {
        // First signup a user
        User user = new User();
        user.setEmail("reset@example.com");
        user.setPassword("password123");
        restTemplate.postForEntity("/api/users/signup", user, User.class);

        // Request password reset
        Map<String, String> request = new HashMap<>();
        request.put("email", "reset@example.com");
        ResponseEntity<String> resetRequestResponse = restTemplate.postForEntity(
            "/api/users/reset-password/request",
            request,
            String.class
        );
        assertEquals(HttpStatus.OK, resetRequestResponse.getStatusCode());
        String resetToken = resetRequestResponse.getBody();

        // Reset password
        Map<String, String> resetRequest = new HashMap<>();
        resetRequest.put("token", resetToken);
        resetRequest.put("newPassword", "newpassword123");
        ResponseEntity<Boolean> resetResponse = restTemplate.postForEntity(
            "/api/users/reset-password",
            resetRequest,
            Boolean.class
        );
        assertEquals(HttpStatus.OK, resetResponse.getStatusCode());
        assertTrue(resetResponse.getBody());
    }
} 