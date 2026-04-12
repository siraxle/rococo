-- V2__insert_countries.sql
-- Наполнение таблицы country тестовыми данными

INSERT INTO country (id, name, code) VALUES
     (
         UUID_TO_BIN(UUID(), true),
         'United Kingdom',
         'GB'
     ),
     (
         UUID_TO_BIN(UUID(), true),
         'Russia',
         'RU'
     ),
     (
         UUID_TO_BIN(UUID(), true),
         'France',
         'FR'
     ),
     (
         UUID_TO_BIN(UUID(), true),
         'Spain',
         'ES'
     );