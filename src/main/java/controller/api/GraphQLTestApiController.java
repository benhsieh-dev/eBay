package controller.api;

import entity.Product;
import service.ProductService;
import service.BidService;
import service.UserService;
import service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/graphql-test")
@CrossOrigin(origins = {"http://localhost:3000", "https://ebay-u3h1.onrender.com"})
public class GraphQLTestApiController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private BidService bidService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CategoryService categoryService;
    
    // Test products query (similar to GraphQL products query)
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> testProductsQuery(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String category) {
        
        try {
            List<Product> products;
            
            if (category != null && !category.trim().isEmpty()) {
                // Find category by name and get products
                var categories = categoryService.getAllCategories();
                var categoryEntity = categories.stream()
                    .filter(c -> c.getCategoryName().equalsIgnoreCase(category.trim()))
                    .findFirst()
                    .orElse(null);
                
                if (categoryEntity != null) {
                    products = productService.getProductsByCategory(categoryEntity.getCategoryId());
                } else {
                    products = productService.getActiveProducts();
                }
            } else {
                products = productService.getActiveProducts();
            }
            
            // Apply limit if specified
            if (limit != null && limit > 0 && products.size() > limit) {
                products = products.subList(0, limit);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", Map.of("products", products));
            response.put("status", "success");
            response.put("message", "GraphQL-style products query executed successfully");
            response.put("graphql_equivalent", "{ products(limit: " + limit + ", category: \"" + category + "\") { productId title currentPrice } }");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "GraphQL products query failed: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    // Test single product query (similar to GraphQL product query)
    @GetMapping("/product/{id}")
    public ResponseEntity<Map<String, Object>> testProductQuery(@PathVariable String id) {
        try {
            Integer productId = Integer.valueOf(id);
            Product product = productService.getProductById(productId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("data", Map.of("product", product));
            response.put("status", "success");
            response.put("message", "GraphQL-style product query executed successfully");
            response.put("graphql_equivalent", "{ product(id: \"" + id + "\") { productId title currentPrice } }");
            
            return ResponseEntity.ok(response);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body(Map.of("status", "error", "message", "Invalid product ID"));
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "GraphQL product query failed: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    // Test bid mutation (similar to GraphQL placeBid mutation)
    @PostMapping("/place-bid")
    public ResponseEntity<Map<String, Object>> testPlaceBidMutation(
            @RequestBody Map<String, Object> request) {
        
        try {
            // Get current session
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession();
            
            // Get current user from session
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("status", "error");
                errorResponse.put("message", "User not logged in");
                errorResponse.put("graphql_equivalent", "mutation { placeBid(productId: \"" + request.get("productId") + "\", price: " + request.get("price") + ") { success message } }");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            // Validate inputs
            Integer productId = Integer.valueOf(request.get("productId").toString());
            BigDecimal bidAmount = BigDecimal.valueOf(Double.valueOf(request.get("price").toString()));
            
            // Place the bid using the same logic as GraphQL controller would
            entity.Bid placedBid = bidService.placeBid(productId, userId, bidAmount, entity.Bid.BidType.REGULAR, null);
            
            Map<String, Object> bidResult = new HashMap<>();
            Map<String, Object> response = new HashMap<>();
            
            if (placedBid != null) {
                bidResult.put("success", true);
                bidResult.put("message", "Bid placed successfully");
                bidResult.put("bidId", placedBid.getBidId().toString());
            } else {
                bidResult.put("success", false);
                bidResult.put("message", "Failed to place bid");
                bidResult.put("bidId", null);
            }
            
            response.put("data", Map.of("placeBid", bidResult));
            response.put("status", "success");
            response.put("message", "GraphQL-style bid mutation executed successfully");
            response.put("graphql_equivalent", "mutation { placeBid(productId: \"" + productId + "\", price: " + bidAmount + ") { success message bidId } }");
            
            return ResponseEntity.ok(response);
            
        } catch (NumberFormatException e) {
            return ResponseEntity.status(400).body(Map.of("status", "error", "message", "Invalid product ID or bid amount"));
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "GraphQL bid mutation failed: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}