FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY build/libs/*.jar app.jar
COPY src/main/resources/application-docker.yml /app/application-docker.yml
EXPOSE 8081
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-Dspring.config.location=file:/app/application-docker.yml", "-jar", "app.jar"]