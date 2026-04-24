FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY rococo-userdata/build/libs/*.jar app.jar
COPY rococo-userdata/src/main/resources/application-docker.yml /app/application-docker.yml

EXPOSE 8098

ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-Dspring.config.location=file:/app/application-docker.yml", "-jar", "app.jar"]
