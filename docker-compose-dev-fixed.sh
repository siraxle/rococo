#!/bin/bash

source ./docker.properties 2>/dev/null || true

export PROFILE=docker
export PREFIX="${IMAGE_PREFIX:-siraxle}"

echo '### Building Docker images for all services (excluding gateway and artist)... ###'
./gradlew clean jibDockerBuild -x test -x :rococo-gateway:jibDockerBuild -x :rococo-artist:jibDockerBuild

echo '### Building Gateway via Dockerfile ###'
cd rococo-gateway
../gradlew bootJar -x test
docker build -t siraxle/rococo-gateway-docker:latest .
cd ..

echo '### Building Artist via Dockerfile ###'
cd rococo-artist
../gradlew bootJar -x test
docker build -t siraxle/rococo-artist-docker:latest .
cd ..

echo '### Building Frontend ###'
cd rococo-client
docker build -t siraxle/rococo-frontend-docker:latest .
cd ..

echo '### Stop old containers (except DB) ###'
docker stop auth.rococo.dc userdata.rococo.dc artist.rococo.dc museum.rococo.dc painting.rococo.dc geo.rococo.dc gateway.rococo.dc frontend.rococo.dc 2>/dev/null
docker rm auth.rococo.dc userdata.rococo.dc artist.rococo.dc museum.rococo.dc painting.rococo.dc geo.rococo.dc gateway.rococo.dc frontend.rococo.dc 2>/dev/null

echo '### Run docker-compose with existing DB ###'
docker compose -f docker-compose-fixed.yml up -d auth userdata artist museum painting geo gateway frontend

echo '### Show running containers ###'
docker ps -a

echo '### Gateway URL ###'
echo 'http://localhost:8081/api/artist'