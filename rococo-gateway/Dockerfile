FROM eclipse-temurin:21-jre-alpine
COPY build/libs/rococo-gateway-*.jar /app.jar
COPY src/main/resources/application.yml /app/application.yml
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.config.location=/app/application.yml"]