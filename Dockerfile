FROM openjdk:17-jdk-slim

# Install Maven and Node.js
RUN apt-get update && apt-get install -y maven curl && \
    curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs

# Set working directory
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src
COPY frontend ./frontend

# Build the application
RUN mvn clean package -DskipTests

# Expose port
EXPOSE 8080

# Run Spring Boot application
CMD ["java", "-jar", "target/ebay-0.0.1-SNAPSHOT.jar", "--server.port=8080"]