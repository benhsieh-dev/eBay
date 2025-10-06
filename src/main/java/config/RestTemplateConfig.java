package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        
        // Configure timeouts for microservice communication
        factory.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());
        factory.setConnectionRequestTimeout((int) Duration.ofSeconds(5).toMillis());
        
        RestTemplate restTemplate = new RestTemplate(factory);
        
        // Add error handling and logging interceptors if needed
        return restTemplate;
    }
}