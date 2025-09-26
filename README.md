# eBay
Users can auction and purchase items online

## Technologies Used

- CSS
- Docker
- HTML
- JavaScript
- JDK 17
- Maven
- PostgreSQL
- Render
- Spring Boot

## Demo User
Demo Login: username=demo_user, password=demo123

## Run Locally
mvn spring-boot:run
npm start

## Implementation in Progress: Kafka
Apache Kafka Integration

Role: Event-Driven Architecture & Real-time Communication

For an auction platform, Kafka would handle:

1. Auction Events
   - Bid placement/updates
   - Auction start/end notifications
   - Price changes
   - Time remaining updates
2. User Activity Streams
   - User login/logout events
   - Product views and searches
   - Cart modifications
   - Purchase completions
3. System Integration
   - Payment processing events
   - Inventory updates
   - Email notifications
   - Analytics data

Implementation Points:
- Replace direct database writes with event publishing for critical actions
- Enable real-time bid updates across multiple user sessions
- Decouple payment processing from order creation
- Stream data to analytics systems

## Implementation in Progress: Kubernetes

Kubernetes Integration

Role: Container Orchestration & Scalability

Kubernetes would manage:

1. Microservices Deployment
   - Spring Boot application pods
   - React frontend service
   - PostgreSQL database (or external RDS)
   - Kafka cluster
   - Redis for caching
2. Auto-scaling
   - Horizontal Pod Autoscaler for traffic spikes
   - Vertical scaling for resource optimization
   - Load balancing across multiple instances
3. Service Discovery & Configuration
   - ConfigMaps for environment variables
   - Secrets for database credentials
   - Service mesh for inter-service communication

Deployment Architecture:
┌─ Ingress Controller (nginx/traefik)
├─ Frontend Service (React)
├─ Backend Service (Spring Boot)
├─ Database Service (PostgreSQL)
├─ Message Broker (Kafka)
└─ Cache Service (Redis)

## Future Considerations

- Session management
- CRUD operations
- Security framework integration (Spring Security)
- RESTful API endpoints
- Frontend modernization (React/Angular integration)
