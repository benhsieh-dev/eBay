package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Connection;
import java.sql.DriverManager;

@Controller
public class ConnectionTestController {

    @GetMapping("/connection-test")
    @ResponseBody
    public String testConnection() {
        String host = System.getenv("SUPABASE_DB_HOST");
        String port = System.getenv("SUPABASE_DB_PORT");
        String database = System.getenv("SUPABASE_DB_NAME");
        String username = System.getenv("SUPABASE_DB_USERNAME");
        String password = System.getenv("SUPABASE_DB_PASSWORD");

        // If env vars not set, try dotenv values
        if (host == null) {
            try {
                io.github.cdimascio.dotenv.Dotenv dotenv = io.github.cdimascio.dotenv.Dotenv.configure().load();
                host = dotenv.get("SUPABASE_DB_HOST");
                port = dotenv.get("SUPABASE_DB_PORT");
                database = dotenv.get("SUPABASE_DB_NAME");
                username = dotenv.get("SUPABASE_DB_USERNAME");
                password = dotenv.get("SUPABASE_DB_PASSWORD");
            } catch (Exception e) {
                return "Error loading .env file: " + e.getMessage();
            }
        }

        String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s?sslmode=require", host, port, database);

        try {
            // Test the connection with a short timeout
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            connection.close();
            
            return String.format("""
                <h1>✅ Database Connection Successful!</h1>
                <p><strong>Host:</strong> %s</p>
                <p><strong>Port:</strong> %s</p>
                <p><strong>Database:</strong> %s</p>
                <p><strong>Username:</strong> %s</p>
                <p><strong>URL:</strong> %s</p>
                <p><em>Connection tested successfully!</em></p>
                """, host, port, database, username, jdbcUrl);
                
        } catch (Exception e) {
            return String.format("""
                <h1>❌ Database Connection Failed</h1>
                <p><strong>Host:</strong> %s</p>
                <p><strong>Port:</strong> %s</p>
                <p><strong>Database:</strong> %s</p>
                <p><strong>Username:</strong> %s</p>
                <p><strong>URL:</strong> %s</p>
                <p><strong>Error:</strong> %s</p>
                <p><strong>Message:</strong> %s</p>
                <hr>
                <h3>Common Issues:</h3>
                <ul>
                    <li>Check if the password is complete</li>
                    <li>Verify the host/port are correct</li>
                    <li>Make sure your IP is allowed in Supabase</li>
                    <li>Try the Direct Connection instead of Transaction Pooler</li>
                </ul>
                """, host, port, database, username, jdbcUrl, e.getClass().getSimpleName(), e.getMessage());
        }
    }
}