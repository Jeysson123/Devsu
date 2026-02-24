FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

RUN apk add --no-cache mysql-client

COPY --from=build /build/target/backend-0.0.1-SNAPSHOT.jar app.jar

COPY wait-for-db.sh /app/wait-for-db.sh
RUN chmod +x /app/wait-for-db.sh

ENV SPRING_DATASOURCE_URL="jdbc:mysql://db:3306/devsu_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
ENV SPRING_DATASOURCE_USERNAME="devsu_user"
ENV SPRING_DATASOURCE_PASSWORD="devsu_pass"

EXPOSE 8080

ENTRYPOINT ["/bin/sh", "-c", "/app/wait-for-db.sh db 3306 60 && exec java -jar /app/app.jar"]