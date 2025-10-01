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
- Kafka: 9092
- Kafka web dashboard: 8081
- Redis: 6379
- Spring Boot: 8080 
- React: 3000 

## Docker Maintenance
- docker system prune -af
- 
## Future Considerations

- Security framework integration (Spring Security)

## Notifications micro service
Implementation plan:
1. Create new Spring Boot project: notification-service
2. Move Kafka consumers: Copy EventConsumerService
3. Add notification logic: Email templates, SMS integration
4. Separate database: Small notification tables
5. Test independently: Run alongside main app
