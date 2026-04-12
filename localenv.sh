#!/bin/bash

CONTAINER_NAME="rococo-all-db"
DB_PASSWORD="secret"

echo '### Stop and remove existing containers (optional) ###'
# Останавливаем и удаляем только если нужно (раскомментировать при необходимости)
# docker stop $(docker ps -a -q) 2>/dev/null
# docker rm $(docker ps -a -q) 2>/dev/null

echo '### Check if database container already exists ###'
if [ "$(docker ps -a -q -f name=$CONTAINER_NAME)" ]; then
    echo "Container $CONTAINER_NAME already exists."

    if [ "$(docker ps -q -f name=$CONTAINER_NAME)" ]; then
        echo "Container $CONTAINER_NAME is running."
    else
        echo "Starting existing container $CONTAINER_NAME..."
        docker start $CONTAINER_NAME
    fi
else
    echo "Creating and starting new container $CONTAINER_NAME..."
    docker run --name $CONTAINER_NAME \
        -p 3306:3306 \
        -e MYSQL_ROOT_PASSWORD=$DB_PASSWORD \
        -v rococo-data:/var/lib/mysql \
        -d mysql:8.4.7 \
        --sql-mode=""
fi

echo '### Waiting for MySQL to be ready... ###'
sleep 10

echo '✅ Database container is ready!'
echo "   Connection: localhost:3306"
echo "   Username: root"
echo "   Password: $DB_PASSWORD"