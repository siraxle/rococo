FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

COPY rococo-grpc-common rococo-grpc-common
COPY rococo-tests rococo-tests

RUN chmod +x gradlew

# Временно меняем settings.gradle, чтобы исключить ненужные модули
RUN cp settings.gradle settings.gradle.bak && \
    echo "rootProject.name = 'rococo'" > settings.gradle && \
    echo "include 'rococo-grpc-common'" >> settings.gradle && \
    echo "include 'rococo-tests'" >> settings.gradle

RUN ./gradlew :rococo-tests:compileTestJava -x test --no-daemon

# Восстанавливаем original settings.gradle
RUN mv settings.gradle.bak settings.gradle

CMD ["./gradlew", ":rococo-tests:test", "-Dtest.env=docker", "--no-daemon"]