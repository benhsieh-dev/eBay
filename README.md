# eBay
Users can auction and purchase items online

## Technologies Used

- CSS
- Docker
- GraphQL
- HTML
- JavaScript
- JDK 17
- Kafka
- Maven
- Node JS
- PostgreSQL
- React JS
- Render
- Spring Boot

## Demo Users
- **Alice Demo**: username=demo_user_1, password=demo123
- **Bob Demo**: username=demo_user_2, password=demo123  
- **Charlie Demo**: username=demo_user, password=demo123

## Run Locally

**With Docker Compose:**
```bash
# Terminal 1: Start Kafka & Redis
docker-compose up

# Terminal 2: Start Spring Boot
mvn spring-boot:run
./start.sh

## GraphQL Playground
http://localhost:8080/graphql

# Micro service statup
cd notification-service
source ../.env && mvn spring-boot:run

# Terminal 3: Start React frontend
npm start
```

**Stop services:**
```bash
docker-compose down
```

Port summary:

| Port | Service              | Status         |
|------|----------------------|----------------|
| 3000 | React Dev            | Optional       |
| 5432 | PostgreSQL           | Production DB  |
| 6379 | Redis                | Cache          |
| 6543 | Supabase Pooler      | Alternative DB |
| 8080 | Main Spring Boot     | Core App       |
| 8081 | Kafka UI             | Monitoring     |
| 8082 | Notification Service | Microservice   |
| 9092 | Kafka Broker         | Message Queue  |

## Docker Maintenance
- docker system prune -af

## Future Considerations

- Security framework integration (Spring Security)


