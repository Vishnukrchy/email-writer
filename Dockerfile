# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the pom.xml and source code to the container
COPY pom.xml /app
COPY src /app/src

# Build the project with Maven
RUN ./mvnw clean package -DskipTests

# Copy the jar file into the container
COPY target/email-writer-sb.jar /app/email-writer-sb.jar

# Expose port 8080
EXPOSE 8080

# Run the jar file
CMD ["java", "-jar", "email-writer-sb.jar"]
