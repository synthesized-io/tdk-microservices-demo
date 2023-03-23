# Use the official Gradle image as the base image for building the project
FROM openjdk:17-jdk-slim AS build

# Set the working directory
WORKDIR /app

COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN ./gradlew build || return 0

# Copy the project files
COPY . .

# Build the project
RUN ./gradlew clean build -x test

# Use the official OpenJDK image for running the application
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/payments-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the application's port
EXPOSE 8080

# Set the entry point for the built JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
