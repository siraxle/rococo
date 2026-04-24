FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY build/libs/*.jar app.jar
COPY src/main/resources/application-docker.yml /app/application-docker.yml   # ← эта строка должна быть
EXPOSE 8081
CMD ["java", "-Dspring.profiles.active=docker", "-jar", "app.jar", "--spring.config.additional-location=file:/app/application-docker.yml"]