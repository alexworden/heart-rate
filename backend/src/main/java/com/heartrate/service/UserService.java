package com.heartrate.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.heartrate.config.PasswordResetConfig;
import com.heartrate.model.User;
import com.heartrate.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final PasswordResetConfig passwordResetConfig;
    private final byte[] jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, NotificationService notificationService, PasswordResetConfig passwordResetConfig) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
        this.passwordResetConfig = passwordResetConfig;
    }

    public User signup(User user) {
        if (user.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User signin(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            String token = Jwts.builder()
                .setSubject(user.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(Keys.hmacShaKeyFor(jwtSecret))
                .compact();
            notificationService.sendPasswordResetEmail(user, passwordResetConfig.getResetUrl(), token);
        }
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @SuppressWarnings("squid:S1181")
    public void resetPassword(String resetToken, String newPassword) {
        if (newPassword.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long");
        }
        try {
            String email = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret))
                .build()
                .parseClaimsJws(resetToken)
                .getBody()
                .getSubject();
            
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

    public byte[] getJwtSecret() {
        return jwtSecret;
    }
} 