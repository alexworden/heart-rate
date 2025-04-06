package com.heartrate.repository;

import com.heartrate.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for UserRepository using H2 database.
 * Following user rules:
 * - No mocks, using real H2 database
 * - Application-level validation
 * - Fail fast approach
 */
@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByEmail_WithExistingEmail_ShouldReturnUser() {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedPassword");
        user.setFirstName("Test");
        user.setDisplayName("Test User");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // Act
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("test@example.com", found.get().getEmail());
        assertEquals("Test", found.get().getFirstName());
    }

    @Test
    void findByEmail_WithNonExistentEmail_ShouldReturnEmpty() {
        // Act
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertTrue(found.isEmpty());
    }

    @Test
    void save_WithValidUser_ShouldPersistUser() {
        // Arrange
        User user = new User();
        user.setEmail("save@example.com");
        user.setPasswordHash("hashedPassword");
        user.setFirstName("Save");
        user.setDisplayName("Save Test");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Act
        User saved = userRepository.save(user);

        // Assert
        assertNotNull(saved.getId());
        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("save@example.com", found.get().getEmail());
    }

    @Test
    void delete_WithExistingUser_ShouldRemoveUser() {
        // Arrange
        User user = new User();
        user.setEmail("delete@example.com");
        user.setPasswordHash("hashedPassword");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User saved = userRepository.save(user);

        // Act
        userRepository.delete(saved);

        // Assert
        Optional<User> found = userRepository.findById(saved.getId());
        assertTrue(found.isEmpty());
    }
}
