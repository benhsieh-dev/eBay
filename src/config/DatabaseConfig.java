package config;

import io.github.cdimascio.dotenv.Dotenv;

public class DatabaseConfig {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    
    public static String getDbUrl() {
        String host = dotenv.get("SUPABASE_DB_HOST", "localhost");
        String port = dotenv.get("SUPABASE_DB_PORT", "5432");
        String database = dotenv.get("SUPABASE_DB_NAME", "postgres");
        
        // For local development, use MySQL if no Supabase config
        if ("localhost".equals(host)) {
            return "jdbc:mysql://localhost:3306/eBay?useSSL=false&serverTimezone=UTC";
        }
        
        return String.format("jdbc:postgresql://%s:%s/%s?sslmode=require", host, port, database);
    }
    
    public static String getDbUsername() {
        return dotenv.get("SUPABASE_DB_USERNAME", "root");
    }
    
    public static String getDbPassword() {
        return dotenv.get("SUPABASE_DB_PASSWORD", "");
    }
    
    public static String getDriverClassName() {
        String host = dotenv.get("SUPABASE_DB_HOST", "localhost");
        if ("localhost".equals(host)) {
            return "com.mysql.cj.jdbc.Driver";
        }
        return "org.postgresql.Driver";
    }
    
    public static String getHibernateDialect() {
        String host = dotenv.get("SUPABASE_DB_HOST", "localhost");
        if ("localhost".equals(host)) {
            return "org.hibernate.dialect.MySQL8Dialect";
        }
        return "org.hibernate.dialect.PostgreSQL10Dialect";
    }
}