-- liquibase formatted sql

-- changeset viti:1
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ROLE_DEVELOPER',
    created_at TIMESTAMP NOT NULL
);

-- changeset viti:2
CREATE TABLE projects (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    owner_id UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL
);

-- changeset viti:3
CREATE TABLE project_members (
    project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    PRIMARY KEY (project_id, user_id)
);

-- changeset viti:4
CREATE TABLE applications (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL UNIQUE,
    git_url VARCHAR(512) NOT NULL,
    git_branch VARCHAR(100) DEFAULT 'main',
    build_path VARCHAR(255) DEFAULT './',
    status VARCHAR(50) NOT NULL,
    last_image_tag VARCHAR(255),
    replicas INTEGER DEFAULT 1,
    cpu_limit DOUBLE PRECISION DEFAULT 0.5,
    mem_limit_mb INTEGER DEFAULT 512,
    exposed_port INTEGER DEFAULT 8080,
    storage_path VARCHAR(255) DEFAULT '/data',
    created_at TIMESTAMP NOT NULL
);

-- changeset viti:5
CREATE TABLE app_variables (
    id UUID PRIMARY KEY,
    app_id UUID NOT NULL,
    env_key VARCHAR(255) NOT NULL,
    env_value TEXT NOT NULL,
    CONSTRAINT fk_app FOREIGN KEY (app_id) REFERENCES applications(id) ON DELETE CASCADE
);