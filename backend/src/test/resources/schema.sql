-- Following user rules:
-- - No database constraints (using application-level validation)
-- - No incremental IDs (using UUIDs)
-- - No enums (using String values)
-- - No entity relationships in ORMs

-- Users table
create table if not exists "users" (
    "id" varchar(36),  -- Simple UUID storage, compatible with both H2 and PostgreSQL
    "email" varchar(255),
    "email_verified" boolean default false,
    "first_name" varchar(255),
    "display_name" varchar(255),
    "password_hash" varchar(255),
    "mobile_number" varchar(50),
    "user_address" varchar(1000),
    "year_of_birth" integer,
    "created_at" timestamp,
    "updated_at" timestamp,
    "remember_me_token" varchar(255)
);

-- Login attempts table for rate limiting
create table if not exists "login_attempts" (
    "id" varchar(36),
    "email" varchar(255),
    "ip_address" varchar(45),  -- IPv6 addresses can be up to 45 characters
    "attempt_time" timestamp
);

-- Verification tokens table for email verification and password reset
create table if not exists "verification_tokens" (
    "id" varchar(36),
    "user_id" varchar(36),
    "token" varchar(255),
    "type" varchar(50),
    "expires_at" timestamp,
    "created_at" timestamp
);

-- Indexes for faster lookups (no constraints, just performance)
create index if not exists "idx_users_email" on "users"("email");
create index if not exists "idx_login_attempts_email" on "login_attempts"("email", "attempt_time");
create index if not exists "idx_verification_tokens_token" on "verification_tokens"("token");
create index if not exists "idx_verification_tokens_user" on "verification_tokens"("user_id", "type");

-- Spring Session tables (required by spring.session.store-type: jdbc)
create table if not exists "SPRING_SESSION" (
    "PRIMARY_ID" char(36),
    "SESSION_ID" char(36),
    "CREATION_TIME" bigint,
    "LAST_ACCESS_TIME" bigint,
    "MAX_INACTIVE_INTERVAL" int,
    "EXPIRY_TIME" bigint,
    "PRINCIPAL_NAME" varchar(100)
);

create index if not exists "SPRING_SESSION_IX1" on "SPRING_SESSION"("SESSION_ID");
create index if not exists "SPRING_SESSION_IX2" on "SPRING_SESSION"("EXPIRY_TIME");
create index if not exists "SPRING_SESSION_IX3" on "SPRING_SESSION"("PRINCIPAL_NAME");

create table if not exists "SPRING_SESSION_ATTRIBUTES" (
    "SESSION_PRIMARY_ID" char(36),
    "ATTRIBUTE_NAME" varchar(200),
    "ATTRIBUTE_BYTES" blob
);

create index if not exists "SPRING_SESSION_ATTRIBUTES_IX1" on "SPRING_SESSION_ATTRIBUTES"("SESSION_PRIMARY_ID");
