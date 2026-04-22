FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY rococo-auth/build/libs/*.jar app.jar
COPY rococo-auth/src/main/resources/application-docker.yml /app/application-docker.yml

EXPOSE 9000

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-Dspring.config.location=file:/app/application-docker.yml", "-jar", "app.jar"]