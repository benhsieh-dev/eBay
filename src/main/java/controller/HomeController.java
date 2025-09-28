package controller;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Order(1)
public class HomeController {
    
    // Health check endpoint
    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "OK";
    }
    
    // Serve React app for root path
    @GetMapping("/")
    public String home() {
        return "forward:/index.html";
    }
    
    // Serve React app for all other non-API routes (React Router handles client-side routing)
    @RequestMapping(value = {"/products", "/products/**", "/login", "/register", "/cart", "/sell", "/profile", "/profile/**", "/about", "/contact"}, 
                   method = {RequestMethod.GET})
    public String serveReactApp() {
        return "forward:/index.html";
    }
    
    // Catch-all for React routes - this ensures any route not handled by API controllers serves the React app
    @RequestMapping(value = "/{path:[^\\.]*}", method = RequestMethod.GET)
    public String redirect() {
        return "forward:/index.html";
    }
}