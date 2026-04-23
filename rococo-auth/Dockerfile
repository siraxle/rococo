# syntax=docker/dockerfile:1
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY gradle.properties .

RUN echo "rootProject.name = 'rococo'" > settings.gradle && \
    echo "include 'rococo-grpc-common'" >> settings.gradle && \
    echo "include 'rococo-auth'" >> settings.gradle

COPY rococo-grpc-common rococo-grpc-common
COPY rococo-auth rococo-auth

RUN chmod +x gradlew
RUN --mount=type=cache,target=/tmp/gradle-home \
    GRADLE_USER_HOME=/tmp/gradle-home ./gradlew :rococo-auth:bootJar -x test --no-daemon

FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=build /app/rococo-auth/build/libs/*.jar app.jar
COPY rococo-auth/src/main/resources/application-docker.yml /app/application-docker.yml

EXPOSE 9000

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-Dspring.config.location=file:/app/application-docker.yml", "-jar", "app.jar"]
