FROM openjdk:17-jdk-slim

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

# Force cache bust - change this comment to rebuild: 2025-10-02-v3
# Build the application with verbose output
RUN echo "Starting Maven build..." && \
    echo "Checking if npm is available:" && \
    which npm && npm --version && \
    echo "Checking frontend directory:" && \
    ls -la frontend/ && \
    echo "Starting Maven build with frontend integration..." && \
    mvn clean package -DskipTests -X && \
    echo "Maven build completed. Checking static files..." && \
    ls -la target/classes/static/ && \
    echo "Checking if React build directory exists:" && \
    ls -la frontend/build/ && \
    echo "Build process finished."

# Expose port
EXPOSE 8080

# Run Spring Boot application
CMD ["java", "-jar", "target/ebay-0.0.1-SNAPSHOT.jar", "--server.port=8080"]