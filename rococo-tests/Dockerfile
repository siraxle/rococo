FROM eclipse-temurin:21-jdk

WORKDIR /app
ENV TZ=Europe/Moscow

# Копируем Gradle файлы
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

# Копируем общий модуль и тесты
COPY rococo-grpc-common rococo-grpc-common
COPY rococo-tests rococo-tests

# Даём права на выполнение
RUN chmod +x gradlew

# Запуск тестов
CMD ./gradlew :rococo-tests:test -Dtest.env=docker --no-daemon