# Стадия 1: сборка
FROM node:22-alpine AS build
WORKDIR /app

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

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]