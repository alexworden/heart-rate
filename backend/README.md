# Heart Rate Backend

## Development Setup

### Prerequisites
- Java 21
- PostgreSQL 15+
- Gradle 8.13+

### Database Setup
The application uses two different database configurations:
1. **Development/Production**: PostgreSQL database
2. **Testing**: H2 in-memory database (no setup required)

#### PostgreSQL Setup
```bash
# Create the database
psql -U postgres -c "CREATE DATABASE heart_rate;"
```

### Running the Application
```bash
# Run the application
./gradlew bootRun
```

### Testing
The project uses H2 in-memory database for testing to avoid mocking and provide a real database for integration tests.

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.heartrate.service.AuthenticationServiceTest"
```

#### Test Configuration
- H2 in-memory database is used for testing
- Database is automatically created for each test
- Test configuration is in `src/test/resources/application.yml`
- Spring Boot auto-configuration handles component scanning
- Following user rules:
  - No database constraints (using application-level validation)
  - No incremental IDs (using UUIDs)
  - No enums (using String values)
  - No entity relationships (using foreign keys)
  - Using reusable test infrastructure instead of mocks

### Debugging
1. **IDE Debugging**: Run the application with debug configuration
2. **Test Debugging**: Run tests with debug configuration
3. **Database Logs**: Enable SQL logging in `application.yml`

### Project Structure
```
src/
├── main/
│   ├── java/
│   │   └── com/heartrate/
│   │       ├── config/       # Configuration classes
│   │       ├── controller/   # REST controllers (v1)
│   │       ├── dto/          # Data transfer objects
│   │       ├── entity/       # Database entities
│   │       └── service/      # Business logic
│   └── resources/
│       └── application.yml   # Application configuration
└── test/
    ├── java/
    │   └── com/heartrate/
    │       ├── config/       # Test configuration
    │       ├── controller/   # Controller tests
    │       └── service/      # Service tests
    └── resources/
        └── application.yml   # Test configuration
