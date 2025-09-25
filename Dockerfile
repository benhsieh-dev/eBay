FROM openjdk:11-jdk-slim

# Install Maven
RUN apt-get update && apt-get install -y maven

# Set working directory
WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src
COPY WebContent ./WebContent

# Build the application
RUN mvn clean package -DskipTests

# Expose port
EXPOSE 8080

# Run the application with environment variables
CMD ["java", "-jar", "target/dependency/jetty-runner.jar", "--port", "8080", "target/eBay-0.0.1-SNAPSHOT.war"]