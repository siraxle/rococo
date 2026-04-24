FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY rococo-gateway/build/libs/*.jar app.jar
COPY rococo-gateway/src/main/resources/application-docker.yml /app/application-docker.yml

EXPOSE 8081

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-Dspring.config.location=file:/app/application-docker.yml", "-jar", "app.jar"]
