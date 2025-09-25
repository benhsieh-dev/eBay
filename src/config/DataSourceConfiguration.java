package config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    @Bean
    @Primary
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        
        dataSource.setDriverClassName(DatabaseConfig.getDriverClassName());
        dataSource.setJdbcUrl(DatabaseConfig.getDbUrl());
        dataSource.setUsername(DatabaseConfig.getDbUsername());
        dataSource.setPassword(DatabaseConfig.getDbPassword());
        
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);
        
        return dataSource;
    }
}