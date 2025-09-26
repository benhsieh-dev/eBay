package controller;

import config.DatabaseConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.sql.Connection;

// @Controller  // Temporarily disabled
public class DatabaseTestController {

    @GetMapping("/db-test")
    @ResponseBody
    public String testDatabaseConnection() {
        try {
            // Test the configuration
            String dbUrl = DatabaseConfig.getDbUrl();
            String username = DatabaseConfig.getDbUsername();
            String driver = DatabaseConfig.getDriverClassName();
            String dialect = DatabaseConfig.getHibernateDialect();
            
            return String.format("""
                <h1>Database Configuration Test</h1>
                <p><strong>URL:</strong> %s</p>
                <p><strong>Username:</strong> %s</p>
                <p><strong>Driver:</strong> %s</p>
                <p><strong>Dialect:</strong> %s</p>
                <p><strong>Status:</strong> Configuration loaded successfully</p>
                <p><em>Note: This doesn't test the actual connection, just the config.</em></p>
                """, dbUrl, username, driver, dialect);
                
        } catch (Exception e) {
            return String.format("""
                <h1>Database Configuration Error</h1>
                <p><strong>Error:</strong> %s</p>
                <p><strong>Message:</strong> %s</p>
                """, e.getClass().getSimpleName(), e.getMessage());
        }
    }
}