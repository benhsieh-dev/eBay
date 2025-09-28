# eBay
Users can auction and purchase items online

## Technologies Used

- CSS
- Docker
- HTML
- JavaScript
- JDK 17
- Kafka
- Maven
- PostgreSQL
- React JS
- Render
- Spring Boot

## Demo Users
- **Alice Demo**: username=_1, password=demo123
- **Bob Demo**: username=demo_user_2, password=demo123  
- **Charlie Demo**: username=demo_user, password=demo123

## Run Locally

**With Docker Compose:**
```bash
# Terminal 1: Start Kafka & Redis
docker-compose up

# Terminal 2: Start Spring Boot
mvn spring-boot:run

# Terminal 3: Start React frontend
npm start
```

**Stop services:**
```bash
docker-compose down
```

Port summary:
- Kafka: 9092
- Kafka web dashboard: 8081
- Redis: 6379
- Spring Boot: 8080 
- React: 3000 


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
