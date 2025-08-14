# syntax=docker/dockerfile:1

# ---- Build stage ----
FROM maven:3.9.8-eclipse-temurin-17 AS builder
WORKDIR /build

# Cache dependencies first
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Copy sources and build
COPY src ./src
RUN mvn -q -DskipTests package

# ---- Runtime stage ----
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built jar (version-agnostic)
COPY --from=builder /build/target/*.jar /app/app.jar

# Application runtime configuration (override at run-time as needed)
ENV JAVA_OPTS=""
ENV MYSQL_PASSWORD=""
ENV JWT_SECRET="change_me"
ENV JWT_EXPIRATION="86400000"

EXPOSE 8080

# Use shell form so JAVA_OPTS is expanded
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
