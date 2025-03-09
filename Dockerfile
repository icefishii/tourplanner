# Use an official OpenJDK runtime as a parent image
FROM maven:3.9.9-eclipse-temurin-21-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the current directory contents into the container at /app
COPY . /app

# Build the server application
RUN mvn clean package -f server/pom.xml

# Run the server application
CMD ["java", "-jar", "server/target/server-1.0.jar"]