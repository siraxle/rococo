-- V2__insert_countries.sql
-- Наполнение таблицы country тестовыми данными (без дублирования)

INSERT INTO country (id, name, code)
SELECT UUID_TO_BIN(UUID(), true), 'United Kingdom', 'GB'
    WHERE NOT EXISTS (SELECT 1 FROM country WHERE code = 'GB');

INSERT INTO country (id, name, code)
SELECT UUID_TO_BIN(UUID(), true), 'Russia', 'RU'
    WHERE NOT EXISTS (SELECT 1 FROM country WHERE code = 'RU');

INSERT INTO country (id, name, code)
SELECT UUID_TO_BIN(UUID(), true), 'France', 'FR'
    WHERE NOT EXISTS (SELECT 1 FROM country WHERE code = 'FR');

INSERT INTO country (id, name, code)
SELECT UUID_TO_BIN(UUID(), true), 'Spain', 'ES'
    WHERE NOT EXISTS (SELECT 1 FROM country WHERE code = 'ES');