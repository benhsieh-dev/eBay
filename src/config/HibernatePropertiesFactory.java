package config;

import java.util.Properties;

public class HibernatePropertiesFactory {
    
    public static Properties getProperties() {
        Properties props = new Properties();
        
        props.setProperty("hibernate.dialect", DatabaseConfig.getHibernateDialect());
        props.setProperty("hibernate.hbm2ddl.auto", "update");
        props.setProperty("hibernate.show_sql", "true");
        props.setProperty("hibernate.format_sql", "true");
        props.setProperty("hibernate.current_session_context_class", 
                         "org.springframework.orm.hibernate5.SpringSessionContext");
        
        return props;
    }
}