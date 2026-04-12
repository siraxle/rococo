-- V2__insert_artists.sql
-- Наполнение таблицы artist тестовыми данными

INSERT INTO artist (id, name, biography, photo, created_at, updated_at) VALUES
        (
        UUID_TO_BIN(UUID(), true),
        'Leonardo da Vinci',
        'Великий итальянский художник, учёный, изобретатель эпохи Возрождения. Автор таких шедевров, как "Мона Лиза" и "Тайная вечеря".',
        '/images/artists/leonardo.jpg',
        NOW(),
        NOW()
        ),
        (
        UUID_TO_BIN(UUID(), true),
        'Vincent van Gogh',
        'Нидерландский художник-постимпрессионист, оказавший огромное влияние на искусство XX века. Известен картинами "Звёздная ночь" и "Подсолнухи".',
        '/images/artists/vangogh.jpg',
        NOW(),
        NOW()
        ),
        (
        UUID_TO_BIN(UUID(), true),
        'Claude Monet',
        'Французский живописец, один из основателей импрессионизма. Мастер пленэрной живописи.',
        '/images/artists/monet.jpg',
        NOW(),
        NOW()
        ),
        (
        UUID_TO_BIN(UUID(), true),
        'Pablo Picasso',
        'Испанский художник, скульптор, график, керамист. Один из самых влиятельных художников XX века, основоположник кубизма.',
        '/images/artists/picasso.jpg',
        NOW(),
        NOW()
        );