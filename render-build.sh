#!/bin/bash
set -e

echo "Starting Render build process..."

# Install Node.js dependencies and build frontend
echo "Building React frontend..."
cd frontend
npm install
npm run build
cd ..

# Build Spring Boot application with Maven
echo "Building Spring Boot application..."
mvn clean package -DskipTests

echo "Build completed successfully!"