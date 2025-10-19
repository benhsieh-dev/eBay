FROM openjdk:21-jdk-slim

# Install Maven and Node.js
RUN apt-get update && apt-get install -y maven curl && \
    curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs

# Set working directory
WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Copy source code and frontend
COPY src ./src
COPY frontend ./frontend

# Force cache bust - change this comment to rebuild: 2025-10-18-v5-fixed
# Build the application with frontend integration
RUN echo "Starting Maven build with frontend..." && \
    mvn clean package -DskipTests && \
    echo "Maven build completed. Checking JAR file..." && \
    ls -la target/*.jar && \
    echo "Checking frontend integration..." && \
    ls -la target/classes/static/ || echo "No static files found" && \
    echo "Build process finished."

# Expose port
EXPOSE 8080

# Run Spring Boot application (use PORT env var for Render)
CMD ["java", "-jar", "target/ebay-0.0.1-SNAPSHOT.jar"]