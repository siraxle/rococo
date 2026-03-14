CREATE TABLE IF NOT EXISTS country (
                                       id BINARY(16) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(3) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Вставляем каждую страну отдельным запросом для уникальных ID
INSERT INTO country (id, name, code) VALUES (UUID_TO_BIN(UUID()), 'Франция', 'FR');
INSERT INTO country (id, name, code) VALUES (UUID_TO_BIN(UUID()), 'Россия', 'RU');
INSERT INTO country (id, name, code) VALUES (UUID_TO_BIN(UUID()), 'Италия', 'IT');
INSERT INTO country (id, name, code) VALUES (UUID_TO_BIN(UUID()), 'Германия', 'DE');
INSERT INTO country (id, name, code) VALUES (UUID_TO_BIN(UUID()), 'Испания', 'ES');
INSERT INTO country (id, name, code) VALUES (UUID_TO_BIN(UUID()), 'Великобритания', 'GB');
INSERT INTO country (id, name, code) VALUES (UUID_TO_BIN(UUID()), 'США', 'US');
INSERT INTO country (id, name, code) VALUES (UUID_TO_BIN(UUID()), 'Нидерланды', 'NL');
INSERT INTO country (id, name, code) VALUES (UUID_TO_BIN(UUID()), 'Бельгия', 'BE');
INSERT INTO country (id, name, code) VALUES (UUID_TO_BIN(UUID()), 'Греция', 'GR');