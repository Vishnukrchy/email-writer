# Use official OpenJDK base image from DockerHub
FROM openjdk:11-jdk-slim as build

# Set the working directory in the container
WORKDIR /app

# Copy the source code into the container
COPY . .

# Install Maven and build the project
RUN apt-get update && apt-get install -y maven && mvn clean package

# Use a smaller base image for the final stage
FROM openjdk:11-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the build stage into the container
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot app will run on (default: 8080)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]