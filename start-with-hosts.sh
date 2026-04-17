#!/bin/bash

echo "🚀 Starting all services without extra_hosts first..."
docker compose up -d

echo "⏳ Waiting for containers to get IPs..."
sleep 10

# Получаем IP
DB_IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' rococo-all-db)
ARTIST_IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' artist)
MUSEUM_IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' museum)
PAINTING_IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' painting)
GEO_IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' geo)
USERDATA_IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' userdata)
AUTH_IP=$(docker inspect -f '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' auth)

echo "📦 Container IPs:"
echo "  rococo-all-db: $DB_IP"
echo "  artist: $ARTIST_IP"
echo "  museum: $MUSEUM_IP"
echo "  painting: $PAINTING_IP"
echo "  geo: $GEO_IP"
echo "  userdata: $USERDATA_IP"
echo "  auth: $AUTH_IP"

# Генерируем docker-compose.override.yml с extra_hosts
cat > docker-compose.override.yml <<EOF
services:
  artist:
    extra_hosts:
      - "rococo-all-db:$DB_IP"
  museum:
    extra_hosts:
      - "rococo-all-db:$DB_IP"
  painting:
    extra_hosts:
      - "rococo-all-db:$DB_IP"
  geo:
    extra_hosts:
      - "rococo-all-db:$DB_IP"
  userdata:
    extra_hosts:
      - "rococo-all-db:$DB_IP"
  auth:
    extra_hosts:
      - "rococo-all-db:$DB_IP"
  gateway:
    extra_hosts:
      - "artist:$ARTIST_IP"
      - "museum:$MUSEUM_IP"
      - "painting:$PAINTING_IP"
      - "geo:$GEO_IP"
      - "userdata:$USERDATA_IP"
      - "auth:$AUTH_IP"
EOF

echo "✅ Generated docker-compose.override.yml"

# Перезапускаем всё с override
docker compose down
docker compose up -d

echo "⏳ Waiting for services to stabilize..."
sleep 15

echo "🔍 Testing Gateway..."
curl http://localhost:8081/api/artist

echo ""
echo "✅ Done."