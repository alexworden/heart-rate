package com.heartrate.repository;

import com.heartrate.entity.LoginAttempt;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for LoginAttemptRepository using H2 database.
 * Following user rules:
 * - No mocks, using real H2 database
 * - Application-level validation
 * - Fail fast approach
 */
@DataJpaTest
@ActiveProfiles("test")
class LoginAttemptRepositoryTest {

    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    @Test
    void countByEmailAndAttemptTimeAfter_WithRecentAttempts_ShouldReturnCount() {
        // Arrange
        String email = "test@example.com";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveMinutesAgo = now.minusMinutes(5);

        LoginAttempt attempt1 = new LoginAttempt();
        attempt1.setEmail(email);
        attempt1.setIpAddress("127.0.0.1");
        attempt1.setAttemptTime(now.minusMinutes(1));
        loginAttemptRepository.save(attempt1);

        LoginAttempt attempt2 = new LoginAttempt();
        attempt2.setEmail(email);
        attempt2.setIpAddress("127.0.0.1");
        attempt2.setAttemptTime(now.minusMinutes(3));
        loginAttemptRepository.save(attempt2);

        LoginAttempt oldAttempt = new LoginAttempt();
        oldAttempt.setEmail(email);
        oldAttempt.setIpAddress("127.0.0.1");
        oldAttempt.setAttemptTime(now.minusMinutes(10));
        loginAttemptRepository.save(oldAttempt);

        // Act
        long count = loginAttemptRepository.countByEmailAndAttemptTimeAfter(email, fiveMinutesAgo);

        // Assert
        assertEquals(2, count);
    }

    @Test
    void countByIpAddressAndAttemptTimeAfter_WithRecentAttempts_ShouldReturnCount() {
        // Arrange
        String ipAddress = "127.0.0.1";
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveMinutesAgo = now.minusMinutes(5);

        LoginAttempt attempt1 = new LoginAttempt();
        attempt1.setEmail("test1@example.com");
        attempt1.setIpAddress(ipAddress);
        attempt1.setAttemptTime(now.minusMinutes(1));
        loginAttemptRepository.save(attempt1);

        LoginAttempt attempt2 = new LoginAttempt();
        attempt2.setEmail("test2@example.com");
        attempt2.setIpAddress(ipAddress);
        attempt2.setAttemptTime(now.minusMinutes(3));
        loginAttemptRepository.save(attempt2);

        LoginAttempt oldAttempt = new LoginAttempt();
        oldAttempt.setEmail("test3@example.com");
        oldAttempt.setIpAddress(ipAddress);
        oldAttempt.setAttemptTime(now.minusMinutes(10));
        loginAttemptRepository.save(oldAttempt);

        // Act
        long count = loginAttemptRepository.countByIpAddressAndAttemptTimeAfter(ipAddress, fiveMinutesAgo);

        // Assert
        assertEquals(2, count);
    }

    @Test
    void save_WithValidAttempt_ShouldPersistAttempt() {
        // Arrange
        LoginAttempt attempt = new LoginAttempt();
        attempt.setEmail("save@example.com");
        attempt.setIpAddress("127.0.0.1");
        attempt.setAttemptTime(LocalDateTime.now());

        // Act
        LoginAttempt saved = loginAttemptRepository.save(attempt);

        // Assert
        assertNotNull(saved.getId());
        Optional<LoginAttempt> found = loginAttemptRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("save@example.com", found.get().getEmail());
        assertEquals("127.0.0.1", found.get().getIpAddress());
    }

    @Test
    void delete_WithExistingAttempt_ShouldRemoveAttempt() {
        // Arrange
        LoginAttempt attempt = new LoginAttempt();
        attempt.setEmail("delete@example.com");
        attempt.setIpAddress("127.0.0.1");
        attempt.setAttemptTime(LocalDateTime.now());
        LoginAttempt saved = loginAttemptRepository.save(attempt);

        // Act
        loginAttemptRepository.delete(saved);

        // Assert
        Optional<LoginAttempt> found = loginAttemptRepository.findById(saved.getId());
        assertTrue(found.isEmpty());
    }
}
