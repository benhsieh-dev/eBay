package config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve React static assets specifically
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/static/")
                .setCachePeriod(31536000); // 1 year cache for static assets
        
        // Serve specific static files (exclude /graphql and /api paths)
        registry.addResourceHandler("/favicon.ico", "/logo*.png", "/manifest.json", "/robots.txt", "/*.html")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Handle React routes - redirect non-API, non-GraphQL requests to index.html
        // IMPORTANT: Do not add catch-all patterns that would intercept /api or /graphql
        registry.addViewController("/").setViewName("forward:/index.html");
        registry.addViewController("/products").setViewName("forward:/index.html");
        registry.addViewController("/product/{id}").setViewName("forward:/index.html");
        registry.addViewController("/login").setViewName("forward:/index.html");
        registry.addViewController("/register").setViewName("forward:/index.html");
        registry.addViewController("/my-ebay").setViewName("forward:/index.html");
        registry.addViewController("/sell").setViewName("forward:/index.html");
    }

}