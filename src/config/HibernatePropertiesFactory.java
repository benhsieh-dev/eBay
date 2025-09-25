package config;

import java.util.Properties;

public class HibernatePropertiesFactory {
    
    public static Properties getProperties() {
        Properties props = new Properties();
        
        props.setProperty("hibernate.dialect", DatabaseConfig.getHibernateDialect());
        // Use none to prevent schema operations during startup
        String env = System.getProperty("hibernate.mode", "none");
        props.setProperty("hibernate.hbm2ddl.auto", env);
        props.setProperty("hibernate.show_sql", "true");
        props.setProperty("hibernate.format_sql", "true");
        props.setProperty("hibernate.current_session_context_class", 
                         "org.springframework.orm.hibernate5.SpringSessionContext");
        
        // Allow app to start even with connection issues
        props.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");
        
        return props;
    }
}