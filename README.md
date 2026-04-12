# 🚀 Руководство по запуску проекта Rococo

## 📁 Структура проекта

```
rococo/
├── rococo-grpc-common/          # 🔧 Общий модуль с proto-файлами (gRPC-контракты)
├── rococo-gateway/              # 🚪 API Gateway — единая точка входа для фронта
│                                #    Проксирует REST → gRPC, НЕ имеет своей БД
├── rococo-auth/                 # 🔐 Сервис аутентификации (OAuth2 / JWT)
│                                #    Регистрация, выдача токенов | БД: rococo-auth
├── rococo-userdata/             # 👤 Сервис профилей пользователей
│                                #    Хранение информации о пользователях | БД: rococo-userdata
├── rococo-artist/               # 🎨 Сервис художников (CRUD) | БД: rococo-artist
├── rococo-museum/               # 🏛️ Сервис музеев (CRUD) | БД: rococo-museum
├── rococo-painting/             # 🖼️ Сервис картин (CRUD) | БД: rococo-painting
├── rococo-geo/                  # 🌍 Сервис геоданных (список стран) | БД: rococo-geo
├── rococo-client/               # 💻 Фронтенд (UI-приложение)
└── rococo-tests/                # 🧪 Модуль автотестов (API, интеграционные, UI)
```

---

## ⚙️ Запуск проекта

### 1️⃣ Клонирование репозитория

```bash
git clone https://github.com/siraxle/rococo.git
cd rococo
```

### 2️⃣ Запуск окружения (Windows)

> **Важно:** Используйте **Git Bash**

Перед запуском убедитесь, что **Docker Desktop** запущен

```bash
bash localenv.sh
```

### 3️⃣ Запуск фронтенда

```bash
cd rococo-client
npm i
npm run dev
```

✅ Фронтенд будет доступен по адресу:  
👉 [http://localhost:3000/](http://localhost:3000/)

### 4️⃣ Запуск микросервисов

Сервисы запускаются **вручную** из соответствующих модулей.  
Каждый сервис содержит класс с аннотацией `@SpringBootApplication`:
> 💡 ⚠️ Важно: При первом запуске соблюдайте указанный порядок.
При повторных запусках (миграции уже применены) порядок не важен.

| № | Сервис             | Класс для запуска           |
|---|--------------------|-----------------------------|
| 1 | `rococo-geo`       | `RococoGeoApplication`      |
| 2 | `rococo-artist`    | `RococoArtistApplication`   |
| 3 | `rococo-museum`    | `RococoMuseumApplication`   |
| 4 | `rococo-painting`  | `RococoPaintingApplication` |
| 5 | `rococo-userdata`  | `RococoUserdataApplication` |
| 6 | `rococo-auth`      | `RococoAuthApplication`     |
| 7 | `rococo-gateway`   | `RococoGatewayApplication`  |

---

## 🧪 Запуск тестов

### Выполнение всех тестов

```bash
gradle clean test
```

### Просмотр Allure-отчёта

После выполнения тестов откройте отчёт:

```bash
allure serve build/allure-results
```

---

## 📌 Примечания

- 🔁 Все микросервисы общаются между собой через **gRPC**
- 🌐 Фронтенд общается только с **API Gateway** (порт `8081`)
- 🗄️ Каждый сервис, кроме Gateway, имеет **собственную базу данных MySQL**
- 🧪 Тесты используют **JUnit 5**, **Retrofit**, **gRPC**, **Selenide** и **Allure**

---

## ❗ Возможные проблемы

| Проблема | Решение |
|----------|---------|
| Порты заняты | Проверьте, что предыдущие экземпляры сервисов остановлены |
| Базы данных не созданы | Сервисы создают БД автоматически через Flyway |
| Фронт не запускается | Убедитесь, что Node.js установлен, выполните `npm i` заново |

---

✅ **Готово! Проект запущен и готов к работе.**