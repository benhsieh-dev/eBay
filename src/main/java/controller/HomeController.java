package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {
    
    // Serve React app for root path
    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }
    
    // Serve React app for all other non-API routes (React Router handles client-side routing)
    @RequestMapping(value = {"/products", "/products/**", "/login", "/register", "/cart", "/sell", "/profile", "/profile/**"}, 
                   method = {RequestMethod.GET})
    public String serveReactApp() {
        return "forward:/index.html";
    }
    
    // Health check endpoint
    @GetMapping("/health")
    public String health() {
        return "forward:/health.html";
    }
}