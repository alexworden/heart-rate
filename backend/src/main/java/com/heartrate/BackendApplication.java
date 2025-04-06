package com.heartrate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * Main application class for the backend service.
 * Following user rules:
 * - Fail fast with minimal validation
 * - Application-level validation instead of database constraints
 * - No enums, using String values
 * - UUIDs instead of incremental IDs
 * - No entity relationships in ORMs
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}) // We'll configure security manually for better control
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
}
