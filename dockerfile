# syntax=docker/dockerfile:1
FROM openjdk:17-alpine

WORKDIR /recipe_app

COPY src .
COPY .mvn .
COPY mvnw pom.xml ./
CMD ["./mvnw", "spring-boot:run"]

EXPOSE 8080