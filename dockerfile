# syntax=docker/dockerfile:1
FROM openjdk:17-alpine

WORKDIR /recipe_app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
COPY src ./src

CMD ["./mvnw", "spring-boot:run"]