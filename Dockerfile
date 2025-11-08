# Multi-stage Dockerfile for building and running the Spring Boot app
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy everything and build with the included maven wrapper
COPY . .
RUN chmod +x mvnw || true
RUN ./mvnw -DskipTests package -DskipTests --batch-mode

# Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the fat JAR built by Maven
COPY --from=build /workspace/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
