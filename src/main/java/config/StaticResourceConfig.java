package config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Order(1)
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve Angular assets from root path
        registry
            .addResourceHandler("/*.js", "/*.css", "/*.png", "/*.ico", "/*.txt")
            .addResourceLocations("classpath:/static/browser/")
            .setCachePeriod(3600);
            
        // Serve images from /images/* 
        registry
            .addResourceHandler("/images/**")
            .addResourceLocations("classpath:/static/browser/images/")
            .setCachePeriod(3600);
    }
}