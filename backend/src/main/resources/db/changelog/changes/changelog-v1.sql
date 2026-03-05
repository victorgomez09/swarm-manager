-- liquibase formatted sql
-- changeset author:viti id:1
CREATE TABLE applications (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    git_url VARCHAR(512) NOT NULL,
    status VARCHAR(50) NOT NULL,
    last_image_tag VARCHAR(255),
    created_at TIMESTAMP NOT NULL
);

-- liquibase formatted sql
-- changeset author:viti id:2
ALTER TABLE applications ADD COLUMN git_branch VARCHAR(100) DEFAULT 'main';
ALTER TABLE applications ADD COLUMN build_path VARCHAR(255) DEFAULT './';

-- liquibase formatted sql
-- changeset author:viti id:3
ALTER TABLE applications ADD COLUMN replicas INTEGER DEFAULT 1;
ALTER TABLE applications ADD COLUMN cpu_limit DOUBLE PRECISION DEFAULT 0.5;
ALTER TABLE applications ADD COLUMN mem_limit_mb INTEGER DEFAULT 512;
ALTER TABLE applications ADD COLUMN exposed_port INTEGER DEFAULT 8080;
ALTER TABLE applications ADD COLUMN storage_path VARCHAR(255) DEFAULT '/data';

-- liquibase formatted sql
-- changeset author:viti id:4
CREATE TABLE app_variables (
    id UUID PRIMARY KEY,
    app_id UUID NOT NULL,
    env_key VARCHAR(255) NOT NULL,
    env_value TEXT NOT NULL,
    CONSTRAINT fk_app FOREIGN KEY (app_id) REFERENCES applications(id) ON DELETE CASCADE
);