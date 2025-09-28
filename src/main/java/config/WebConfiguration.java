package config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve React static files
        registry.addResourceHandler("/static/**")
                .addResourceLocations("/static/");
                
        registry.addResourceHandler("/**")
                .addResourceLocations("/");
                
        // Serve uploaded images and assets
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("/assets/");
                
        registry.addResourceHandler("/profile/**")
                .addResourceLocations("/profile/");
                
        // Serve uploaded product images
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}