CREATE TABLE IF NOT EXISTS museum
(
    id          BINARY(16)   UNIQUE NOT NULL DEFAULT (UUID_TO_BIN(UUID(), true)),
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    city        VARCHAR(255) NOT NULL,
    address     VARCHAR(255) NOT NULL,
    photo       LONGTEXT,
    country_id  BINARY(16)   NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

CREATE INDEX idx_museum_title ON museum (title);
CREATE INDEX idx_museum_city ON museum (city);
CREATE INDEX idx_museum_country_id ON museum (country_id);