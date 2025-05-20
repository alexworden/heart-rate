package com.heartrate.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.heartrate.config.PasswordResetConfig;
import com.heartrate.model.User;
import com.heartrate.repository.UserRepository;
import com.heartrate.security.JwtTokenProvider;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final PasswordResetConfig passwordResetConfig;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                      NotificationService notificationService, PasswordResetConfig passwordResetConfig,
                      JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
        this.passwordResetConfig = passwordResetConfig;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public User signup(User user) {
        if (user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Map<String, Object> signin(String email, String password) {
        User user = userRepository.findByEmail(email);

        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            String token = jwtTokenProvider.createToken(user.getEmail());
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", user);
            return response;
        }
        return null;
    }

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            String token = jwtTokenProvider.createToken(user.getEmail());
            notificationService.sendPasswordResetEmail(user, passwordResetConfig.getResetUrl(), token);
        }
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void resetPassword(String resetToken, String newPassword) {
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        try {
            String email = jwtTokenProvider.getEmailFromToken(resetToken);
            if (email == null) {
                throw new IllegalArgumentException("Invalid reset token");
            }
            
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new IllegalArgumentException("Invalid reset token");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid or expired reset token");
        }
    }

    public User getCurrentUser(String token) {
        try {
            String email = jwtTokenProvider.getEmailFromToken(token);
            if (email == null) {
                return null;
            }
            return userRepository.findByEmail(email);
        } catch (Exception e) {
            return null;
        }
    }
} 