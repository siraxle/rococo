#!/bin/bash

source ./docker.properties 2>/dev/null || true

export PROFILE=docker
export PREFIX="${IMAGE_PREFIX:-siraxle}"
export ALLURE_DOCKER_API=http://allure:5050/

echo '### Building Docker images for all services (excluding gateway and tests)... ###'
./gradlew clean jibDockerBuild -x test -x :rococo-gateway:jibDockerBuild -x :rococo-tests:jibDockerBuild

echo '### Building Gateway via tar (to avoid hanging) ###'
cd rococo-gateway
../gradlew jibBuildTar -x test
echo '### Loading Gateway image to Docker ###'
docker load < build/jib-image.tar
echo '### Tagging Gateway image ###'
docker tag siraxle/rococo-gateway-null:1.0.0 siraxle/rococo-gateway-docker:latest
cd ..

echo '### Building Frontend ###'
cd rococo-client
docker build -t siraxle/rococo-frontend-docker:latest .
cd ..

echo '### Pull Selenoid browser ###'
docker pull selenoid/vnc_chrome:127.0

echo '### Stop and remove old containers ###'
docker compose -f docker-compose.yml -f docker-compose-tests.yml down

echo '### Run docker-compose ###'
docker compose -f docker-compose.yml -f docker-compose-tests.yml up -d

echo '### Wait for services ###'
sleep 15

echo '### Run tests ###'
docker compose -f docker-compose-tests.yml run --rm rococo-tests

EXIT_CODE=$?

echo '### Generate Allure report ###'
docker compose -f docker-compose-tests.yml exec allure allure generate /app/allure-results -o /app/default-reports

echo '### Show containers ###'
docker ps -a

echo '### Allure report available at: http://localhost:5050 ###'

exit $EXIT_CODE