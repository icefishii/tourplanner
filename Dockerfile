# Use OpenJDK 21 on Alpine for a lightweight image
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

# Optimized with Docker AI

# Set working directory
WORKDIR /app

# Copy POM files first for better caching
COPY pom.xml /app/
COPY models/pom.xml /app/models/
COPY server/pom.xml /app/server/
COPY client/pom.xml /app/client/

# Copy source code
COPY models/src /app/models/src
COPY server/src /app/server/src
COPY client/src /app/client/src
# Caching dependencies to speed up builds
RUN mvn dependency:go-offline

# Build the project with models first, then server
# Use -pl to specify which modules to build
RUN mvn clean package -pl models,server -am -DskipTests

# --- Runtime Stage ---
FROM eclipse-temurin:21-jre-alpine

# Create a non-root user and group
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/server/target/*.jar server.jar

# Change ownership to the new user
RUN chown appuser:appgroup server.jar

# Switch to the non-root user
USER appuser

# Remove JAVA_OPTS initially, add back only if needed based on runtime errors
# ENV JAVA_OPTS="..."

# Run the application (use exec form if no JAVA_OPTS needed, otherwise sh -c)
 ENTRYPOINT ["java", "-jar", "/app/server.jar"]
# If no JAVA_OPTS
#ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/server.jar"]
# If using JAVA_OPTS