#!/bin/bash

# Остановить и удалить все контейнеры, кроме БД
docker stop auth.rococo.dc userdata.rococo.dc artist.rococo.dc museum.rococo.dc painting.rococo.dc geo.rococo.dc gateway.rococo.dc frontend.rococo.dc 2>/dev/null
docker rm auth.rococo.dc userdata.rococo.dc artist.rococo.dc museum.rococo.dc painting.rococo.dc geo.rococo.dc gateway.rococo.dc frontend.rococo.dc 2>/dev/null

# Запустить сервисы
docker compose -f docker-compose-services.yml up -d

# Показать статус
docker ps