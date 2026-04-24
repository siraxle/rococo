FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY rococo-museum/build/libs/*.jar app.jar
COPY rococo-museum/src/main/resources/application-docker.yml /app/application-docker.yml

EXPOSE 8094

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-Dspring.config.location=file:/app/application-docker.yml", "-jar", "app.jar"]
