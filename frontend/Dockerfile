# Etapa 1: Build
FROM node:22-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Etapa 2: Servir con un servidor ligero de Node
FROM node:22-alpine
RUN npm install -g http-server
# Copia solo lo necesario del build
COPY --from=build /app/dist/frontend/browser /app/public

EXPOSE 8080
# Sirve los archivos estáticos desde la carpeta public
CMD ["http-server", "/app/public", "-p", "8080"]