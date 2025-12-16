# Multi-stage build for legacy monolith
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests package -q

FROM eclipse-temurin:17-jre-jammy
COPY --from=build /app/target/*.jar /app/app.jar
EXPOSE 3000
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
