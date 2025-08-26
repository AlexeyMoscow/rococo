CREATE TABLE IF NOT EXISTS users (
    id BINARY(16) PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS authorities (
    id BINARY(16) PRIMARY KEY,
    user_id BINARY(16) NOT NULL,
    authority VARCHAR(255) NOT NULL,
    CONSTRAINT fk_auth_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_auth_user ON authorities(user_id);
