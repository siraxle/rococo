FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY gradle.properties .

RUN echo "rootProject.name = 'rococo'" > settings.gradle && \
    echo "include 'rococo-grpc-common'" >> settings.gradle && \
    echo "include 'rococo-tests'" >> settings.gradle

COPY rococo-grpc-common rococo-grpc-common
COPY rococo-tests rococo-tests

RUN chmod +x gradlew

RUN --mount=type=cache,target=/tmp/gradle-home \
    bash -c 'GRADLE_USER_HOME=/tmp/gradle-home ./gradlew :rococo-tests:test --no-daemon; exit 0' && \
    mkdir -p /root/.gradle && \
    cp -r /tmp/gradle-home/wrapper /root/.gradle/wrapper && \
    cp -r /tmp/gradle-home/caches /root/.gradle/caches

RUN rm -rf /app/rococo-tests/build/reports

CMD ["./gradlew", ":rococo-tests:test", "-Dtest.env=docker", "--no-daemon"]
