package config;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConfig {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    
    public static String getDbUrl() {
        // Try AWS variables first, then fall back to Supabase, then localhost
        String host = dotenv.get("AWS_DB_HOST", dotenv.get("SUPABASE_DB_HOST", "localhost"));
        String port = dotenv.get("AWS_DB_PORT", dotenv.get("SUPABASE_DB_PORT", "5432"));
        String database = dotenv.get("AWS_DB_NAME", dotenv.get("SUPABASE_DB_NAME", "postgres"));
        
        // For local development, use MySQL if no cloud config
        if ("localhost".equals(host)) {
            return "jdbc:mysql://localhost:3306/eBay?useSSL=false&serverTimezone=UTC";
        }
        
        return String.format("jdbc:postgresql://%s:%s/%s?sslmode=require&ApplicationName=ebay-marketplace", host, port, database);
    }
    
    public static String getDbUsername() {
        return dotenv.get("AWS_DB_USERNAME", dotenv.get("SUPABASE_DB_USERNAME", "root"));
    }
    
    public static String getDbPassword() {
        return dotenv.get("AWS_DB_PASSWORD", dotenv.get("SUPABASE_DB_PASSWORD", ""));
    }
    
    public static String getDriverClassName() {
        String host = dotenv.get("AWS_DB_HOST", dotenv.get("SUPABASE_DB_HOST", "localhost"));
        if ("localhost".equals(host)) {
            return "com.mysql.cj.jdbc.Driver";
        }
        return "org.postgresql.Driver";
    }
    
    public static String getHibernateDialect() {
        String host = dotenv.get("AWS_DB_HOST", dotenv.get("SUPABASE_DB_HOST", "localhost"));
        if ("localhost".equals(host)) {
            return "org.hibernate.dialect.MySQL8Dialect";
        }
        return "org.hibernate.dialect.PostgreSQL10Dialect";
    }
}