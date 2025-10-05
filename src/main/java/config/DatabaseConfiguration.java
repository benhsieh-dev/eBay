package config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("never-active-disable-completely")
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
            
            // Settings optimized for AWS RDS with extended timeouts for schema creation
            dataSource.setMaximumPoolSize(5); // Small pool for cloud connection
            dataSource.setMinimumIdle(1); // Keep at least 1 connection
            dataSource.setConnectionTimeout(300000); // 5 minutes for AWS RDS
            dataSource.setIdleTimeout(600000); // 10 minutes
            dataSource.setMaxLifetime(1800000); // 30 minutes
            dataSource.setValidationTimeout(120000); // 2 minutes for AWS RDS
            dataSource.setLeakDetectionThreshold(0); // Disable for initial schema creation
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