# eBay
Users can auction and purchase items online

## Technologies Used

- AWS Certificate Manager
- AWS CloudFront
- AWS EC2
- AWS RDS
- AWS Route 53
- CSS
- Docker
- GraphQL
- HTML
- JavaScript
- JDK 21
- Jenkins CI/CD
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

**Prerequisites:**
- PostgreSQL running locally (Postgres.app recommended)
- Local database `ebay_marketplace` with user `ebayuser`/`ebaypassword123`

**Quick Start:**
```bash
# Start application (includes React frontend build)
./start.sh
```
Application runs at http://localhost:8080

**With Docker Services (Optional):**
```bash
# Terminal 1: Start Kafka & Redis (optional for full features)
docker-compose up -d kafka redis kafka-ui

# Terminal 2: Start Spring Boot
./start.sh
```

**Configuration Profiles:**
- **Local Development**: Uses `LOCAL_DB_*` variables (default profile)
- **Render Production**: Uses `SUPABASE_DB_*` variables (`render` profile)  
- **AWS EB Production**: Uses `AWS_DB_*` variables (`production` profile)

**GraphQL Playground:** http://localhost:8080/graphql

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
| 8083 | Jenkins CI/CD        | CI/CD          |
| 9092 | Kafka Broker         | Message Queue  |

## Docker Commands
- docker ps
- docker ps -a
- docker images

## Docker Maintenance
- docker system prune -af

## IAM
-  aws iam list-users

## Microservices
### Payments Service
1. Start all services:
   ./start-microservices.sh
2. Access points:
   - Main App: http://localhost:8080
   - Payment Service: http://localhost:8081
   - Payment Health: http://localhost:8081/api/payments/health
3. Test payment flow:

## AWS Commands
### Get EC2 Instance IDs
- aws ec2 describe-instances --query 'Reservations[*].Instances[*].[InstanceId,State.Name,Tags[?Key==`Name`].Value|[0]]' --output table

- aws events list-targets-by-rule --rule ebay-start-instances
- aws events list-targets-by-rule --rule ebay-stop-instances
- aws events list-targets-by-rule --rule ebay-start-instances
- aws events list-targets-by-rule --rule ebay-stop-instances

## CloudWatch logs
- https://us-east-2.console.aws.amazon.com/cloudwatch/home?region=us-east-2#logsV2:log-groups/log-group/$252Faws$252Flambda$252Febay-scheduler

## SSH to EC2 Instance
ssh -i ~/.ssh/ebay-debug-key.pem ec2-user@3.24.98.160

eBay application is now running at:
https://aws-cloud-app.com/
http://3.145.148.232/
http://ec2-3-145-148-232.us-east-2.compute.amazonaws.com

## EC2 List Instances
aws ec2 describe-instances --query "Reservations[*].Instances[*].InstanceId" --output text

## EC2 List Security Groups
aws ec2 describe-security-groups --query 'SecurityGroups[*].[GroupId,GroupName,Description]' --region us-east-2 --output table

## EC2 Deployment Script
aws ec2 run-instances --cli-input-json file://ec2-config.json --region us-east-2

## EC2 Public IP
aws ec2 describe-instances --instance-ids i-021d853b40fb0faae --region us-east-2 --query "Reservations[0].Instances[0].State.Name" --output text

## Jenkins
http://3.145.148.232:8083/

## Future Considerations

- Security framework integration (Spring Security)


