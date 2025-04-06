package com.heartrate.repository;

import com.heartrate.entity.User;
import com.heartrate.entity.VerificationToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for VerificationTokenRepository using H2 database.
 * Following user rules:
 * - No mocks, using real H2 database
 * - Application-level validation
 * - Fail fast approach
 */
@DataJpaTest
@ActiveProfiles("test")
class VerificationTokenRepositoryTest {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByTokenAndType_WithExistingToken_ShouldReturnToken() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        VerificationToken token = new VerificationToken();
        token.setToken("test-token");
        token.setType("email_verification");
        token.setUserId(user.getId());
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        token.setCreatedAt(LocalDateTime.now());
        verificationTokenRepository.save(token);

        // Act
        Optional<VerificationToken> found = verificationTokenRepository.findByTokenAndType("test-token", "email_verification");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("test-token", found.get().getToken());
        assertEquals("email_verification", found.get().getType());
        assertEquals(user.getId(), found.get().getUserId());
    }

    @Test
    void findByTokenAndType_WithNonExistentToken_ShouldReturnEmpty() {
        // Act
        Optional<VerificationToken> found = verificationTokenRepository.findByTokenAndType("nonexistent-token", "email_verification");

        // Assert
        assertTrue(found.isEmpty());
    }

    @Test
    void findFirstByUserIdAndTypeOrderByCreatedAtDesc_WithMultipleTokens_ShouldReturnLatest() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        VerificationToken oldToken = new VerificationToken();
        oldToken.setToken("old-token");
        oldToken.setType("password_reset");
        oldToken.setUserId(user.getId());
        oldToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        oldToken.setCreatedAt(LocalDateTime.now().minusHours(1));
        verificationTokenRepository.save(oldToken);

        VerificationToken newToken = new VerificationToken();
        newToken.setToken("new-token");
        newToken.setType("password_reset");
        newToken.setUserId(user.getId());
        newToken.setExpiresAt(LocalDateTime.now().plusHours(24));
        newToken.setCreatedAt(LocalDateTime.now());
        verificationTokenRepository.save(newToken);

        // Act
        Optional<VerificationToken> found = verificationTokenRepository.findFirstByUserIdAndTypeOrderByCreatedAtDesc(user.getId(), "password_reset");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("new-token", found.get().getToken());
    }

    @Test
    void save_WithValidToken_ShouldPersistToken() {
        // Arrange
        User user = new User();
        user.setEmail("save@example.com");
        user.setPasswordHash("hashedPassword");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        VerificationToken token = new VerificationToken();
        token.setToken("save-token");
        token.setType("password_reset");
        token.setUserId(user.getId());
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        token.setCreatedAt(LocalDateTime.now());

        // Act
        VerificationToken saved = verificationTokenRepository.save(token);

        // Assert
        assertNotNull(saved.getId());
        Optional<VerificationToken> found = verificationTokenRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("save-token", found.get().getToken());
        assertEquals("password_reset", found.get().getType());
    }

    @Test
    void delete_WithExistingToken_ShouldRemoveToken() {
        // Arrange
        User user = new User();
        user.setEmail("delete@example.com");
        user.setPasswordHash("hashedPassword");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        VerificationToken token = new VerificationToken();
        token.setToken("delete-token");
        token.setType("email_verification");
        token.setUserId(user.getId());
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        token.setCreatedAt(LocalDateTime.now());
        VerificationToken saved = verificationTokenRepository.save(token);

        // Act
        verificationTokenRepository.delete(saved);

        // Assert
        Optional<VerificationToken> found = verificationTokenRepository.findById(saved.getId());
        assertTrue(found.isEmpty());
    }
}
