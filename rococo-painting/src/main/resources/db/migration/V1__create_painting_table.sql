CREATE TABLE IF NOT EXISTS painting
(
    id          BINARY(16) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID(), true)),
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    artist_id   BINARY(16) NOT NULL,
    museum_id   BINARY(16),
    photo       LONGTEXT,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_painting_title (title),
    INDEX idx_painting_artist (artist_id),
    INDEX idx_painting_museum (museum_id)
    );