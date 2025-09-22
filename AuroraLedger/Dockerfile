# Aurora Ledger Santander - Banking Grade Dockerfile
# Multi-stage build for production-ready banking application

# Stage 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /build

# Copy pom.xml and download dependencies (for better cache utilization)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Create the runtime image
FROM eclipse-temurin:17-jre-jammy

# Create application user (security best practice)
RUN groupadd -r aurora && useradd --no-log-init -r -g aurora aurora

# Install curl for health checks
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /build/target/aurora-ledger-santander-0.1.0-SNAPSHOT.jar app.jar

# Change ownership to aurora user
RUN chown -R aurora:aurora /app

# Switch to non-root user
USER aurora

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]