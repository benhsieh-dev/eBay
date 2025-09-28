package controller;

import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
    @ResponseBody
    public String home(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        ClassPathResource resource = new ClassPathResource("static/index.html");
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }
    
    // Serve React app for all other non-API routes (React Router handles client-side routing)
    @RequestMapping(value = {"/products", "/products/**", "/login", "/register", "/cart", "/sell", "/profile", "/profile/**", "/about", "/contact"}, 
                   method = {RequestMethod.GET})
    @ResponseBody
    public String serveReactApp(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        ClassPathResource resource = new ClassPathResource("static/index.html");
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }
    
    // Catch-all for React routes - this ensures any route not handled by API controllers serves the React app
    @RequestMapping(value = "/{path:[^\\.]*}", method = RequestMethod.GET)
    @ResponseBody
    public String redirect(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        ClassPathResource resource = new ClassPathResource("static/index.html");
        return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
    }
}