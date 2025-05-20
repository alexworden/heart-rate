# Heart Rate Backend

This is the backend service for the Heart Rate application, built with Spring Boot.

## Prerequisites

- Java 21 or later
- Maven 3.8 or later
- H2 Database (embedded)

## Building the Project

To build the project, run:

```bash
./mvnw clean install
```

## Running the Application

To run the application in development mode:

```bash
./mvnw spring-boot:run
```

The application will start on port 8080 by default.

## Running Tests

To run all tests:

```bash
./mvnw test
```

To run specific test classes:

```bash
# Run integration tests
./mvnw test -Dtest=UserControllerIntegrationTest

# Run repository tests
./mvnw test -Dtest=UserRepositoryTest
```

## Configuration

The application uses the following configuration files:

- `src/main/resources/application.properties` - Main configuration file
- `src/test/resources/application-test.properties` - Test configuration file

### Password Reset Configuration

The password reset functionality requires the following configuration:

```properties
# Main application properties
app.password-reset.reset-url=http://localhost:8080/reset-password
app.password-reset.token-expiry-hours=24

# Email configuration (required for production)
spring.mail.host=your-smtp-host
spring.mail.port=587
spring.mail.username=your-email@example.com
spring.mail.password=your-email-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

For testing purposes, a stub email service is used that logs emails to the console instead of sending them.

## API Documentation

The API documentation is available at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Database

The application uses an embedded H2 database in development mode. The H2 console is available at:
`http://localhost:8080/h2-console`

Default H2 console credentials:
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (leave empty)

## Security

The application uses JWT (JSON Web Tokens) for authentication. Password reset tokens are also JWT-based and expire after the configured time period.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request 