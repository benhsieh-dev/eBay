package config;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConfig {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    
    public static String getDbUrl() {
        // Check for AWS EB environment variables first
        String host = System.getenv("AWS_DB_HOST");
        String port = System.getenv("AWS_DB_PORT");
        String database = System.getenv("AWS_DB_NAME");
        
        // Fall back to Supabase for local development
        if (host == null) {
            host = dotenv.get("SUPABASE_DB_HOST", "localhost");
            port = dotenv.get("SUPABASE_DB_PORT", "5432");
            database = dotenv.get("SUPABASE_DB_NAME", "postgres");
        }
        
        // For local development, use MySQL if no cloud config
        if ("localhost".equals(host)) {
            return "jdbc:mysql://localhost:3306/eBay?useSSL=false&serverTimezone=UTC";
        }
        
        return String.format("jdbc:postgresql://%s:%s/%s?sslmode=require", host, port, database);
    }
    
    public static String getDbUsername() {
        // Check for AWS EB environment variables first
        String username = System.getenv("AWS_DB_USERNAME");
        if (username != null) {
            return username;
        }
        return dotenv.get("SUPABASE_DB_USERNAME", "root");
    }
    
    public static String getDbPassword() {
        // Check for AWS EB environment variables first
        String password = System.getenv("AWS_DB_PASSWORD");
        if (password != null) {
            return password;
        }
        return dotenv.get("SUPABASE_DB_PASSWORD", "");
    }
    
    public static String getDriverClassName() {
        String host = System.getenv("AWS_DB_HOST");
        if (host == null) {
            host = dotenv.get("SUPABASE_DB_HOST", "localhost");
        }
        
        if ("localhost".equals(host)) {
            return "com.mysql.cj.jdbc.Driver";
        }
        return "org.postgresql.Driver";
    }
    
    public static String getHibernateDialect() {
        String host = System.getenv("AWS_DB_HOST");
        if (host == null) {
            host = dotenv.get("SUPABASE_DB_HOST", "localhost");
        }
        
        if ("localhost".equals(host)) {
            return "org.hibernate.dialect.MySQL8Dialect";
        }
        return "org.hibernate.dialect.PostgreSQL10Dialect";
    }
}