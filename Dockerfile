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

# Force cache bust - change this comment to rebuild: 2025-10-18-v5-fixed
# Build the application - skip frontend build for now to isolate Java issues  
RUN echo "Starting Maven build (Java only)..." && \
    mvn clean package -DskipTests -Dexec.skip=true && \
    echo "Maven build completed. Checking JAR file..." && \
    ls -la target/*.jar && \
    echo "Build process finished."

# Expose port
EXPOSE 8080

# Run Spring Boot application
CMD ["java", "-jar", "target/ebay-0.0.1-SNAPSHOT.jar", "--server.port=8080"]