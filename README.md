# eBay
Users can auction and purchase items online

## Technologies Used

- Spring MVC
- JDK 11
- JavaScript
- HTML 
- CSS
- MySQL Database
- Maven

## Prerequisites

1. **MySQL Database**: Create database "eBay" on localhost:3306 with root/empty password
2. **Java 11+**: JDK 11 or higher installed
3. **Maven**: Apache Maven for building the project
4. **Application Server**: Apache Tomcat 9+ for deployment

## How to Run

### Method 1: IntelliJ IDEA (Recommended - GUI Based) 🖱️

#### Initial Setup:
1. **Open Project**: File → Open → Select the eBay project folder
2. **Configure Tomcat**:
   - Run → Edit Configurations → Click "+" → Tomcat Server → Local
   - **Application Server**: Browse and select your Tomcat installation
     - Tomcat 9: `/usr/local/Cellar/tomcat@9/9.0.108/libexec` (if using Homebrew)
   - **Deployment Tab**: Click "+" → Artifact → Select `eBay:war exploded`
   - **Application Context**: Set to `/eBay`
3. **Save Configuration**

#### Running the Application:
1. **Click the Green Play Button** ▶️ next to your Tomcat configuration
2. **Or**: Run → Run 'Tomcat' (your configuration name)
3. **IntelliJ will**:
   - Build the project automatically
   - Start Tomcat server
   - Deploy the application
   - Open browser to http://localhost:8080/eBay/

#### Stopping the Application:
- **Click the Red Stop Button** ⏹️ in the Run window
- **Or**: Run → Stop 'Tomcat'

### Method 2: Eclipse IDE (GUI Alternative) 🖱️

#### Setup:
1. **Import Project**: File → Import → Existing Maven Projects
2. **Add Tomcat Server**:
   - Window → Preferences → Server → Runtime Environments
   - Add → Apache Tomcat v9.0 → Browse to Tomcat installation
3. **Create Server**:
   - Servers Tab → Right-click → New → Server
   - Select Tomcat v9.0 → Add eBay project

#### Running:
1. **Right-click on Server** → Start
2. **Or drag eBay project** to the server and start

### Method 3: Command Line (Advanced Users) 💻

#### Quick Start:
```bash
# Build and deploy in one go
mvn clean package
cp target/eBay-0.0.1-SNAPSHOT.war /usr/local/Cellar/tomcat@9/9.0.108/libexec/webapps/eBay.war
brew services start tomcat@9
```

#### Individual Commands:
```bash
# 1. Build the project
mvn clean compile

# 2. Package as WAR
mvn package

# 3. Deploy to Tomcat webapps directory
cp target/eBay-0.0.1-SNAPSHOT.war [TOMCAT_HOME]/webapps/eBay.war

# 4. Start Tomcat
# On macOS with Homebrew:
brew services start tomcat@9
# Or manually:
[TOMCAT_HOME]/bin/catalina.sh run
```

### Method 4: IDE with External Tomcat 🖱️
1. **Start Tomcat externally** (using command line or Tomcat GUI)
2. **Use IDE's "Deploy to External Server"** feature
3. **Build project in IDE** and manually copy WAR file

## Access URLs
- Home: `http://localhost:8080/eBay/`
- Registration: `http://localhost:8080/eBay/registration`
- Profile: `http://localhost:8080/eBay/profile`
- Products: `http://localhost:8080/eBay/products`
- Splash: `http://localhost:8080/eBay/splash`

## Build Commands
- **Compile**: `mvn compile`
- **Package**: `mvn package`
- **Clean**: `mvn clean`
- **Test**: `mvn test`

## Troubleshooting

### Common Issues:

#### 1. Tomcat Version Compatibility
- **Problem**: `ClassCastException` with Jakarta vs Java Servlet API
- **Solution**: Use Tomcat 9.x (not Tomcat 10+) with Spring 5.x
- **Install Tomcat 9**: `brew install tomcat@9`

#### 2. Spring Version Conflicts
- **Problem**: `NoSuchMethodError` or `ClassNotFoundException`
- **Solution**: Ensure all Spring dependencies use the same version (5.1.7.RELEASE)

#### 3. MySQL Connection Issues
- **Problem**: Application won't start, database connection errors
- **Solution**: 
  - Start MySQL: `brew services start mysql`
  - Create database: `CREATE DATABASE eBay;`
  - Check credentials in `WebContent/WEB-INF/database.properties`

#### 4. Port Already in Use
- **Problem**: `Address already in use: 8080`
- **Solution**: 
  - Stop existing Tomcat: `brew services stop tomcat@9`
  - Or change port in Tomcat configuration

#### 5. IDE Configuration Issues
- **IntelliJ**: Make sure Project SDK is set to Java 11+
- **Eclipse**: Verify Maven integration and Tomcat server configuration

### Deployment Verification:
1. **Check Tomcat logs**: `[TOMCAT_HOME]/logs/catalina.out`
2. **Verify deployment**: Look for `eBay.war` in `[TOMCAT_HOME]/webapps/`
3. **Test connection**: Visit `http://localhost:8080/eBay/`

### IDE-Specific Tips:

#### IntelliJ IDEA:
- Use "Exploded WAR" artifact for faster redeployment during development
- Enable "Update classes and resources" for hot reload
- Check "Application Server" tab in Run Configuration

#### Eclipse:
- Use "Servers" view for easy start/stop
- Right-click project → "Properties" → "Project Facets" to verify Java/Maven settings
- Enable "Automatic Publishing" for seamless deployment

## Architecture Notes

### Spring Configuration
- **XML-based configuration** (traditional approach)
- **Component scanning** enabled for controllers
- **Hibernate integration** for database operations
- **JSP view resolution** for frontend

### Database Integration
- **Hibernate auto-DDL**: Tables created automatically on first run
- **Connection pooling**: Basic JDBC connections (consider upgrading for production)
- **Schema**: MySQL 8.0+ compatible

## Development Workflow

### Recommended IDE Setup:
1. **Import as Maven project**
2. **Configure Tomcat server integration**
3. **Set up run configuration with hot reload**
4. **Use exploded WAR deployment for development**

### Typical Development Cycle:
1. 🔄 Make code changes
2. ▶️ **Click Run** (IDE handles build & deploy)
3. 🌐 **Test in browser**
4. ⏹️ **Click Stop** when done
5. 🔁 Repeat

## Future Considerations

- Session management
- CRUD operations
- Security framework integration (Spring Security)
- RESTful API endpoints
- Frontend modernization (React/Angular integration)
