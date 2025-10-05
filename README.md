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
| 9092 | Kafka Broker         | Message Queue  |

## Docker Commands
- docker ps
- docker ps -a
- docker images

## Docker Maintenance
- docker system prune -af

## IAM
-  aws iam list-users

## Elastic Beanstalk
- eb create ebay-medium --instance_type t3.medium
- eb deploy
- eb events
- eb events --follow
- eb health
- eb health --refresh
- eb logs
- eb logs --all
- eb printenv | grep AWS_DB
- eb setenv
- eb ssh --setup
- eb status

## GitLab CI/CD
- https://gitlab.com/benhsieh-dev/eBay/-/pipelines

### Your new workflow:
### Development (GitHub)
git push 

### Deploy to AWS (GitLab CI/CD)
git push gitlab 

- https://eb-ebay-demo.us-east-1.elasticbeanstalk.com/

## Upgrading to Java 21

- JAVA_HOME=/usr/local/Cellar/openjdk@21/21.0.8/libexec/openjdk.jdk/Contents/Home mvn rewrite:discover
- JAVA_HOME=/usr/local/Cellar/openjdk@21/21.0.8/libexec/openjdk.jdk/Contents/Home mvn rewrite:dryRun -Drewrite.activeRecipes=RECIPE_NAME
- JAVA_HOME=/usr/local/Cellar/openjdk@21/21.0.8/libexec/openjdk.jdk/Contents/Home mvn rewrite:run -Drewrite.activeRecipes=RECIPE_NAME
- git status
- git diff
- JAVA_HOME=/usr/local/Cellar/openjdk@21/21.0.8/libexec/openjdk.jdk/Contents/Home mvn clean compile
- git add .
- git commit -m "Apply RECIPE_NAME - description of changes"
- Test Application
- JAVA_HOME=/usr/local/Cellar/openjdk@21/21.0.8/libexec/openjdk.jdk/Contents/Home mvn spring-boot:run

### atomic approach
1. Apply all changes: mvn rewrite:run
2. Review changes: git diff
3. Test compilation: mvn clean compile
4. Test application: mvn spring-boot:run
5. Commit everything: git add . && git commit -m "Migrate to Java 21 with OpenRewrite"


## Future Considerations

- Security framework integration (Spring Security)


