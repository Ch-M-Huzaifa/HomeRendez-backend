# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17 AS build

WORKDIR /app

# Copy the pom.xml to download dependencies
COPY pom.xml .

# Download dependencies to be used offline
RUN mvn dependency:go-offline -B

# Copy the application source code
COPY src ./src

# Package the application
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/homeRendez-0.0.1-SNAPSHOT.jar .

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/homeRendez-0.0.1-SNAPSHOT.jar"]
