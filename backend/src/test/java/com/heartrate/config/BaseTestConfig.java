package com.heartrate.config;

import com.heartrate.BackendApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base test configuration that sets up H2 in-memory database for testing.
 * Following user rules:
 * - Using H2 in-memory database for testing infrastructure instead of mocks
 * - Tests run in transactions to ensure test isolation
 * - Application-level validation instead of database constraints
 */
@SpringBootTest(classes = BackendApplication.class)
@ActiveProfiles("test")
@Transactional
public abstract class BaseTestConfig {
}
