# Стадия 1: сборка
FROM node:22-alpine AS build
WORKDIR /app

ARG VITE_AUTH_URL=http://localhost:9000
ARG VITE_API_URL=http://localhost:8081
ARG VITE_FRONT_HOST=localhost
ARG VITE_FRONT_URL=http://localhost:3000
ARG VITE_CLIENT_ID=client

ENV VITE_AUTH_URL=$VITE_AUTH_URL
ENV VITE_API_URL=$VITE_API_URL
ENV VITE_FRONT_HOST=$VITE_FRONT_HOST
ENV VITE_FRONT_URL=$VITE_FRONT_URL
ENV VITE_CLIENT_ID=$VITE_CLIENT_ID

# Копируем package.json и устанавливаем зависимости
COPY package*.json ./
RUN npm ci

# Копируем исходники и собираем
COPY . .
RUN npm run build

# Стадия 2: релизный образ
FROM nginx:alpine

# Копируем существующий конфиг nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Копируем собранные файлы из папки build
COPY --from=build /app/build /usr/share/nginx/html

EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]