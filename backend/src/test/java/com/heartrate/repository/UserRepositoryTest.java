package com.heartrate.repository;

import com.heartrate.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindUser() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setDateOfBirth(LocalDate.of(1990, 1, 1));
        user.setZipCode("12345");
        user.setGender("Male");

        User savedUser = userRepository.save(user);
        assertNotNull(savedUser.getId());

        User foundUser = userRepository.findByEmail("john@example.com");
        assertNotNull(foundUser);
        assertEquals("John", foundUser.getFirstName());
        assertEquals("Doe", foundUser.getLastName());
        assertEquals("12345", foundUser.getZipCode());
    }

    @Test
    public void testFindByResetToken() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setResetToken("test-token");

        userRepository.save(user);

        User foundUser = userRepository.findByResetToken("test-token");
        assertNotNull(foundUser);
        assertEquals("test@example.com", foundUser.getEmail());
    }
} 