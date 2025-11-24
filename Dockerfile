# Multi-Stage Build für optimale Image-Größe

# Build Stage
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Kopiere pom.xml und lade Dependencies (für besseres Caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Kopiere Source Code und baue die Anwendung
COPY src ./src
COPY src/main/resources ./src/main/resources
RUN mvn clean package -DskipTests

# Runtime Stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Erstelle non-root User für Sicherheit
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Kopiere das gebaute JAR aus der Build Stage
COPY --from=build /app/target/*.jar app.jar

COPY --from=build /app/src/main/resources/system-prompt.st ./system-prompt.st

# Exponiere den Standard Spring Boot Port
EXPOSE 8080

# Healthcheck (optional, aber empfohlen)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Setze Ollama Umgebungsvariablen
ENV OLLAMA_BASE_URL=http://host.docker.internal:11434
ENV OLLAMA_MODEL_NAME=codellama:7b
ENV OLLAMA_TEMPERATURE=0.9
ENV OLLAMA_MAX_MESSAGES=10

# Starte die Anwendung
ENTRYPOINT ["java", "-jar", "app.jar"]