-- Following user rules:
-- - No database constraints (using application-level validation)
-- - No incremental IDs (using UUIDs)
-- - No enums (using String values)
-- - No entity relationships (using foreign keys)

create table spring_session (
    primary_id varchar(36),  -- Simple UUID storage, compatible with both H2 and PostgreSQL
    session_id varchar(36),  -- Simple UUID storage, compatible with both H2 and PostgreSQL
    creation_time bigint not null,
    last_access_time bigint not null,
    max_inactive_interval int not null,
    expiry_time bigint not null,
    principal_name varchar(100)
);

create index spring_session_ix1 on spring_session (last_access_time);
create index spring_session_ix2 on spring_session (expiry_time);
create index spring_session_ix3 on spring_session (session_id);

create table spring_session_attributes (
    session_primary_id varchar(36),  -- Simple UUID storage, compatible with both H2 and PostgreSQL
    attribute_name varchar(200),
    attribute_bytes binary
);

create index spring_session_attributes_ix1 on spring_session_attributes (session_primary_id);
