# eBay Marketplace - Credentials & Setup

## Default Admin Account

**⚠️ IMPORTANT: Change these credentials after first login!**

- **Username:** `admin`
- **Password:** `admin123`
- **Access:** Full admin privileges

## Security Best Practices

### 1. Environment Variables
- Copy `.env.example` to `.env` for local development
- `.env` file is gitignored - never commit it
- Use environment variables for all sensitive data

### 2. Password Security
- Change default admin password immediately after first login
- Use strong passwords (12+ characters, mixed case, numbers, symbols)
- Consider implementing password complexity requirements

### 3. Database Security
- Change MySQL root password from default
- Create dedicated database user with limited privileges
- Use connection pooling (already configured with HikariCP)

### 4. Production Deployment
- Use proper secrets management (AWS Secrets Manager, HashiCorp Vault, etc.)
- Enable HTTPS/SSL
- Implement proper session management
- Add rate limiting and input validation

## Initial Setup Steps

1. **Setup Database:**
   ```bash
   ./setup-database.sh
   ```

2. **Start Application:**
   ```bash
   mvn jetty:run
   ```

3. **Access Application:**
   - URL: `http://localhost:8081/eBay`
   - Login with admin credentials above

4. **First Login Actions:**
   - Change admin password
   - Create your user account
   - Test functionality

## Environment Configuration

Check `.env.example` for all configurable variables including:
- Database connection settings
- Security secrets
- Application ports
- Feature flags

Never commit the actual `.env` file - it contains sensitive information!