# Use OpenJDK 17 as base image
FROM eclipse-temurin:17-jdk
# Set working directory inside container
WORKDIR /app
# Copy the JAR file built by Maven
COPY target/*.jar app.jar
# Expose the application port
EXPOSE 8089
# Start the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
