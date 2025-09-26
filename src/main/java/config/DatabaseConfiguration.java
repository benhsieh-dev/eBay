package config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfiguration {

    @Bean
    @Primary
    @Profile("!test")
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        
        try {
            dataSource.setDriverClassName(DatabaseConfig.getDriverClassName());
            dataSource.setJdbcUrl(DatabaseConfig.getDbUrl());
            dataSource.setUsername(DatabaseConfig.getDbUsername());
            dataSource.setPassword(DatabaseConfig.getDbPassword());
            
            // Settings optimized for Supabase direct connection
            dataSource.setMaximumPoolSize(5); // Small pool for direct connection
            dataSource.setMinimumIdle(0); // Allow 0 connections initially
            dataSource.setConnectionTimeout(30000); // 30 seconds
            dataSource.setIdleTimeout(300000); // 5 minutes
            dataSource.setMaxLifetime(1800000); // 30 minutes
            dataSource.setValidationTimeout(5000); // 5 seconds
            dataSource.setLeakDetectionThreshold(60000); // 1 minute
            dataSource.setInitializationFailTimeout(-1); // Don't fail on startup
            
            // Test the configuration
            System.out.println("Database Configuration:");
            System.out.println("URL: " + DatabaseConfig.getDbUrl());
            System.out.println("Username: " + DatabaseConfig.getDbUsername());
            System.out.println("Driver: " + DatabaseConfig.getDriverClassName());
            
            return dataSource;
            
        } catch (Exception e) {
            System.err.println("Failed to create DataSource: " + e.getMessage());
            e.printStackTrace();
            // Don't throw exception - let app start anyway
            return dataSource;
        }
    }
}