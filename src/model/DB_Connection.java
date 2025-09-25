package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import io.github.cdimascio.dotenv.Dotenv;

public class DB_Connection {
	
	public static void main(String[] args) {
		DB_Connection ob_DB_Connection = new DB_Connection();
		System.out.println(ob_DB_Connection.getConnection());
	}
	
	public Connection getConnection() {
		Connection connection = null;
		System.out.println("Connection called");
		try {
			// Load .env file for local development
			Dotenv dotenv = null;
			try {
				dotenv = Dotenv.load();
			} catch (Exception e) {
				// .env file not found, continue with system environment variables
			}
			
			// Try to get database configuration
			String dbUrl = System.getenv("DB_URL");
			String dbUsername = System.getenv("DB_USERNAME");
			String dbPassword = System.getenv("DB_PASSWORD");
			
			// If not in system env, try .env file
			if (dotenv != null && (dbUrl == null || dbUsername == null || dbPassword == null)) {
				dbUrl = dotenv.get("DB_URL");
				dbUsername = dotenv.get("DB_USERNAME");
				dbPassword = dotenv.get("DB_PASSWORD");
			}
			
			// Fallback to local MySQL for development
			if (dbUrl == null || dbUsername == null || dbPassword == null) {
				System.out.println("Using local MySQL fallback");
				Class.forName("com.mysql.cj.jdbc.Driver");
				dbUrl = "jdbc:mysql://localhost:3306/eBay";
				dbUsername = "root";
				dbPassword = "";
			} else {
				// Using PostgreSQL (Supabase)
				System.out.println("Using PostgreSQL database");
				Class.forName("org.postgresql.Driver");
			}
			
			connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		
		return connection; 
	}

}
