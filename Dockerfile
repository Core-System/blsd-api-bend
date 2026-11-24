FROM maven:3.9.11-amazoncorretto-21 AS dependencies

WORKDIR /build

COPY pom.xml .

RUN mvn dependency:go-offline -B

FROM dependencies AS build

COPY src ./src

RUN mvn package -Dmaven.test.skip=true -B

FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-jar", "app.jar"]
