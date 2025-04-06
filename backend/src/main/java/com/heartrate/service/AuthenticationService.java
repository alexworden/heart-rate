package com.heartrate.service;

import com.heartrate.dto.LoginRequest;
import com.heartrate.dto.UserRegistrationRequest;
import com.heartrate.entity.User;
import com.heartrate.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Authentication service implementing application-level validation and business logic.
 * Following user rules:
 * - No enums, using String values
 * - UUIDs for all IDs
 * - No ORM relationships
 * - Application-level validation instead of database constraints
 * - Fail fast approach
 */
@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final RateLimitService rateLimitService;
    private final JwtService jwtService;

    public AuthenticationService(UserRepository userRepository,
                               PasswordEncoder passwordEncoder,
                               EmailService emailService,
                               RateLimitService rateLimitService,
                               JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.rateLimitService = rateLimitService;
        this.jwtService = jwtService;
    }

    @Transactional
    public User registerUser(UserRegistrationRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Create new user
        User user = new User();  // This will generate a new UUID
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setDisplayName(request.getDisplayName());
        user.setMobileNumber(request.getMobileNumber());
        user.setUserAddress(request.getAddress());
        user.setYearOfBirth(request.getYearOfBirth());

        user = userRepository.save(user);

        // Generate email verification token (valid for 24 hours)
        String verificationToken = jwtService.generateToken(
            user.getId(),
            request.getEmail(),
            "EMAIL_VERIFICATION",
            Duration.ofHours(24)
        );

        // Send verification email
        emailService.sendVerificationEmail(request.getEmail(), verificationToken);

        return user;
    }

    @Transactional
    public User login(LoginRequest request, String ipAddress) {
        // Check rate limiting
        if (!rateLimitService.tryLogin(request.getEmail(), ipAddress)) {
            throw new RuntimeException("Too many login attempts. Please try again later.");
        }

        // Get user by email
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> {
                rateLimitService.recordFailedLogin(request.getEmail(), ipAddress);
                return new RuntimeException("Invalid credentials");
            });

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            rateLimitService.recordFailedLogin(request.getEmail(), ipAddress);
            throw new RuntimeException("Invalid credentials");
        }

        // Generate remember me token if requested
        if (request.isRememberMe()) {
            user.setRememberMeToken(UUID.randomUUID().toString());
            user.setUpdatedAt(LocalDateTime.now());
            user = userRepository.save(user);
        }

        return user;
    }

    @Transactional
    public void verifyEmailToken(String token) {
        // Verify token
        Jws<Claims> claims = jwtService.verifyToken(token, "EMAIL_VERIFICATION");
        UUID userId = UUID.fromString(claims.getPayload().getSubject());

        // Get user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Update user's email verified status
        user.setEmailVerified(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void requestPasswordReset(String email) {
        // Get user by email
        userRepository.findByEmail(email).ifPresent(user -> {
            // Generate password reset token (valid for 1 hour)
            String resetToken = jwtService.generateToken(
                user.getId(),
                email,
                "PASSWORD_RESET",
                Duration.ofHours(1)
            );

            // Send password reset email
            emailService.sendPasswordResetEmail(email, resetToken);
        });
        // Don't reveal that email doesn't exist by throwing or returning anything
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        // Verify token
        Jws<Claims> claims = jwtService.verifyToken(token, "PASSWORD_RESET");
        UUID userId = UUID.fromString(claims.getPayload().getSubject());

        // Get user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void requestPasswordlessLogin(String email) {
        // Get user by email
        userRepository.findByEmail(email).ifPresent(user -> {
            // Generate passwordless login token (valid for 15 minutes)
            String loginToken = jwtService.generateToken(
                user.getId(),
                email,
                "PASSWORDLESS_LOGIN",
                Duration.ofMinutes(15)
            );

            // Send passwordless login email
            emailService.sendPasswordlessLoginEmail(email, loginToken);
        });
        // Don't reveal that email doesn't exist by throwing or returning anything
    }
}
