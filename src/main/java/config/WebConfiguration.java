package config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve React static files (JS, CSS, images)
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/static/");
                
        // Serve React root files (index.html, favicon.ico, etc.)
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
                
        // Serve uploaded product images from static resources
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("classpath:/static/uploads/");
    }
}