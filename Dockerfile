# Build Stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Package the application skipping tests for quick production build
RUN mvn clean package -DskipTests

# Run Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy executable jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Hugging Face Spaces uses port 7860 by default
ENV PORT=7860
EXPOSE 7860

ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
