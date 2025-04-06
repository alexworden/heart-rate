# Web App Base

A modern web application base with user management capabilities, built with Spring Boot, PostgreSQL, and Next.js.

## Project Structure

```
web-app-base/
├── backend/           # Spring Boot backend
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       └── resources/
│   └── build.gradle
├── frontend/         # Next.js frontend
│   ├── pages/
│   ├── components/
│   └── package.json
└── README.md
```

## Prerequisites

- Java 17 or higher
- Node.js 18 or higher
- PostgreSQL 15 or higher

## Development Setup

### Database Setup

1. Create a PostgreSQL database:
```sql
CREATE DATABASE web_app_base;
```

### Backend Setup

1. Navigate to the backend directory:
```bash
cd backend
```

2. Run the Spring Boot application:
```bash
./gradlew bootRun
```

The backend will be available at `http://localhost:8080`

### Frontend Setup

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm run dev
```

The frontend will be available at `http://localhost:3000`

## Environment Variables

### Backend (application.yml)
```yaml
JDBC_DATABASE_URL: jdbc:postgresql://localhost:5432/web_app_base
JDBC_DATABASE_USERNAME: your_username
JDBC_DATABASE_PASSWORD: your_password
SMTP_HOST: smtp.gmail.com
SMTP_PORT: 587
SMTP_USERNAME: your-email@gmail.com
SMTP_PASSWORD: your-app-password
CORS_ALLOWED_ORIGINS: http://localhost:3000
```

## API Documentation

The API is versioned with the prefix `/api/v1`. Available endpoints:

### Authentication
- POST `/api/v1/auth/signup`
- POST `/api/v1/auth/login`
- POST `/api/v1/auth/login-link`
- POST `/api/v1/auth/forgot-password`
- POST `/api/v1/auth/logout`

### User Profile
- GET `/api/v1/users/profile`
- PUT `/api/v1/users/profile`

## Security Features

- Session-based authentication with cookies
- Rate limiting for login and email verification
- CSRF protection
- Prepared for future 2FA implementation

## Development Guidelines

1. Follow the established layer architecture:
   - Controllers
   - DTOs
   - Services
   - Models
   - Repositories

2. Database:
   - Use UUIDs for all IDs
   - No entity relationships in ORM
   - No database constraints (handled in application layer)

3. Testing:
   - Write unit tests
   - Use H2 in-memory database for testing
