FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY gradle.properties .

# Не копируем оригинальный settings.gradle, создаём свой
RUN echo "rootProject.name = 'rococo'" > settings.gradle && \
    echo "include 'rococo-grpc-common'" >> settings.gradle && \
    echo "include 'rococo-tests'" >> settings.gradle

COPY rococo-grpc-common rococo-grpc-common
COPY rococo-tests rococo-tests

RUN chmod +x gradlew

RUN ./gradlew :rococo-tests:compileTestJava -x test --no-daemon

RUN rm -rf /app/rococo-tests/build/reports

CMD ["./gradlew", ":rococo-tests:test", "-Dtest.env=docker", "--no-daemon"]