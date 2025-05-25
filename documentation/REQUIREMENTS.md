# Application Foundation Requirements

## Overview
A foundational application template with user management capabilities, designed for extensibility and following industry best practices.

## Technical Stack
- Backend: Java Spring Boot
- Database: PostgreSQL (without entity relationships or DB constraints)
- ORM: Spring Data JPA (without entity relationships, using foreign keys instead)
- Frontend: Next.js with TypeScript (Pages Router)
- Hosting: Railway-compatible
- Session Store: JDBC (with easy migration path to Redis)

## Architecture Decisions
- Session-based authentication using cookies with "Remember Me" functionality
- REST API with versioning (v1)
- UUID for all database identifiers
- Rate limiting for login attempts and email verification (configurable with defaults)
- No password complexity requirements
- Email verification links expire after 2 hours
- Frontend routing supports browser back button and bookmarks

## Core Features

### Authentication & User Management
- User signup with email verification (2-hour expiration)
- Password-less login via email link (2-hour expiration)
- Traditional email/password login with "Remember Me" option
- Password recovery flow
- Session-based authentication
- Rate limiting protection with configurable thresholds:
  - Default: 5 login attempts per 15 minutes
  - Default: 3 email verifications per hour
- Password reset via email link (expires in 2 hours)
- Remember Me functionality (30-day duration)

### User Profile
- Required fields at signup:
  - First name
  - Email address
  - Password (single entry)
- Additional profile fields:
  - Display name
  - Mobile number
  - Address
  - Year of birth
- Users can update their profile information
- Email verification required for email changes

### Application Structure
- Backend layers:
  - Controllers (API endpoints)
  - DTOs (Data Transfer Objects)
  - Services (Business logic)
  - Models (Database entities)
  - Repositories (Data access)

### Technical Requirements
- Hot-reload support for development
- CORS configuration
- Mobile-responsive design
- No database-level constraints (handled in application layer)
- No ORM relationships (using foreign keys instead)
- Prepared for future 2FA implementation
- Configurable rate limiting with sensible defaults
- Browser history support with bookmarkable routes
- Java Spring Boot
- PostgreSQL database
- Spring Data JPA for database interactions
- No entity relationships in JPA entities
- UUID for all IDs (no incremental IDs)
- No database constraints
- No enums (use String values)
- Next.js with TypeScript
- Pages Router
- React Query for data fetching
- React Hook Form with Zod validation
- Tailwind CSS for styling
- Responsive design

### User Interface
- Signup screen
- Login screen with "Remember Me" option
- Password recovery screen
- Profile management screen
- Home screen with personalized greeting

## Security Considerations
- Secure session management with cookies
- Configurable rate limiting for authentication endpoints
- Email verification with 2-hour expiration
- API versioning
- CSRF protection
- Secure password hashing
- No password complexity requirements
- Password reset links expire in 2 hours
- Rate limiting on password reset attempts
- CSRF protection
- Secure headers
- Input validation
- Rate limiting

## Development Environment
- Local development setup
- Hot-reload capability
- Development tools configuration

Note: This is a living document and will be updated based on additional requirements and decisions.
