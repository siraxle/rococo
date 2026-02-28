CREATE TABLE IF NOT EXISTS museum
(
    id          CHAR(36) PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    city        VARCHAR(255) NOT NULL,
    address     VARCHAR(255) NOT NULL,
    photo       TEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

CREATE INDEX idx_museum_title ON museum (title);
CREATE INDEX idx_museum_city ON museum (city);