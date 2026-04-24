FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY rococo-painting/build/libs/*.jar app.jar
COPY rococo-painting/src/main/resources/application-docker.yml /app/application-docker.yml

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-Dspring.config.location=file:/app/application-docker.yml", "-jar", "app.jar"]
