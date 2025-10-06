#!/bin/bash

# Start eBay Microservices
echo "Starting eBay Microservices..."

# Start Docker services (Kafka, Redis)
echo "Starting Docker services..."
docker-compose up -d

# Wait for Docker services to be ready
echo "Waiting for Docker services to start..."
sleep 10

# Start Payment Service (Port 8081)
echo "Starting Payment Service on port 8081..."
cd payment-service
mvn spring-boot:run &
PAYMENT_PID=$!
echo "Payment Service PID: $PAYMENT_PID"

# Wait for Payment Service to start
echo "Waiting for Payment Service to start..."
sleep 15

# Start Main Application (Port 8080)
echo "Starting Main Application on port 8080..."
cd ..
mvn spring-boot:run &
MAIN_PID=$!
echo "Main Application PID: $MAIN_PID"

echo ""
echo "🚀 eBay Microservices started successfully!"
echo ""
echo "📊 Services:"
echo "   Main App:        http://localhost:8080"
echo "   Payment Service: http://localhost:8081"
echo "   Kafka UI:        http://localhost:8081"
echo ""
echo "📝 To stop all services, run:"
echo "   kill $MAIN_PID $PAYMENT_PID"
echo "   docker-compose down"
echo ""
echo "Press Ctrl+C to stop all services..."

# Function to cleanup on exit
cleanup() {
    echo ""
    echo "Stopping services..."
    kill $MAIN_PID $PAYMENT_PID 2>/dev/null
    docker-compose down
    echo "All services stopped."
    exit 0
}

# Set trap to cleanup on script exit
trap cleanup SIGINT SIGTERM

# Wait for services to run
wait