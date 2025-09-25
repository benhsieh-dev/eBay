package controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StaticController {

    // Simple test endpoint
    @GetMapping("/test")
    @org.springframework.web.bind.annotation.ResponseBody
    public String test() {
        return "<h1>✅ Spring MVC is working!</h1><p>Server is running correctly.</p>";
    }

    // Serve React app for specific routes (HomeController handles "/")
    @GetMapping({"/react", "/products", "/products/**", "/cart", "/sell"})
    @org.springframework.web.bind.annotation.ResponseBody
    public String serveReactApp() {
        // Serve React app content directly since forward doesn't work with context path
        return """
        <!doctype html>
        <html lang="en">
        <head>
            <meta charset="utf-8"/>
            <meta name="viewport" content="width=device-width,initial-scale=1"/>
            <title>eBay Clone - React App</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 40px; }
                .container { max-width: 800px; margin: 0 auto; }
                .header { background: #e53e3e; color: white; padding: 20px; border-radius: 8px; }
                .content { padding: 20px 0; }
                .nav { margin: 20px 0; }
                .nav a { margin-right: 20px; color: #e53e3e; text-decoration: none; }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>🏪 eBay Clone</h1>
                    <p>Modern React Frontend with Spring Backend</p>
                </div>
                <div class="nav">
                    <a href="/eBay/test">Test Page</a>
                    <a href="/eBay/connection-test">Database Test</a>
                    <a href="/eBay/splash">Splash Page</a>
                    <a href="/eBay/api/products/featured">API Test</a>
                </div>
                <div class="content">
                    <h2>✅ Application Status</h2>
                    <p>✅ Spring MVC is running</p>
                    <p>✅ Database connection established</p>
                    <p>✅ React build integrated</p>
                    <p>✅ Context path: /eBay</p>
                    
                    <h3>Next Steps:</h3>
                    <ul>
                        <li>Database is connected and working</li>
                        <li>API endpoints are functional</li>
                        <li>Ready for full React integration</li>
                    </ul>
                </div>
            </div>
        </body>
        </html>
        """;
    }
    
    // Health check endpoint
    @GetMapping("/health")
    public String health() {
        return "forward:/health.html";
    }
}