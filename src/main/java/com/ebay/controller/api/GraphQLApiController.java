package com.ebay.controller.api;

import entity.Product;
import entity.Bid;
import dto.ProductGraphQLDto;
import service.ProductService;
import service.BidService;
import service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/graphql")
@CrossOrigin(origins = {"http://localhost:3000", "https://ebay-u3h1.onrender.com"})
public class GraphQLApiController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private BidService bidService;
    
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> graphql(@RequestBody Map<String, Object> request) {
        try {
            String query = (String) request.get("query");
            @SuppressWarnings("unchecked")
            Map<String, Object> variables = (Map<String, Object>) request.get("variables");
            
            if (query == null) {
                return ResponseEntity.badRequest().body(Map.of("errors", List.of(Map.of("message", "Query is required"))));
            }
            
            // Simple GraphQL query parser
            if (query.contains("products")) {
                return handleProductsQuery(query, variables);
            } else if (query.contains("product(")) {
                return handleProductQuery(query, variables);
            } else if (query.contains("placeBid")) {
                return handlePlaceBidMutation(query, variables);
            } else {
                return ResponseEntity.badRequest().body(Map.of("errors", List.of(Map.of("message", "Unknown query: " + query))));
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("errors", List.of(Map.of("message", "Internal server error: " + e.getMessage()))));
        }
    }
    
    private ResponseEntity<Map<String, Object>> handleProductsQuery(String query, Map<String, Object> variables) {
        // Extract arguments from query (simple parsing)
        Integer limit = null;
        String category = null;
        
        if (query.contains("limit:")) {
            String limitStr = query.substring(query.indexOf("limit:") + 6);
            limitStr = limitStr.substring(0, limitStr.indexOf(",") > 0 ? limitStr.indexOf(",") : limitStr.indexOf(")"));
            limitStr = limitStr.trim();
            try {
                limit = Integer.valueOf(limitStr);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        
        if (query.contains("category:")) {
            String categoryStr = query.substring(query.indexOf("category:") + 9);
            categoryStr = categoryStr.substring(1, categoryStr.indexOf("\"", 1)); // Extract quoted string
            category = categoryStr;
        }
        
        List<Product> products;
        
        if (category != null && !category.trim().isEmpty()) {
            final String finalCategory = category.trim();
            var categories = categoryService.getAllCategories();
            var categoryEntity = categories.stream()
                .filter(c -> c.getCategoryName().equalsIgnoreCase(finalCategory))
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
        
        if (limit != null && limit > 0 && products.size() > limit) {
            products = products.subList(0, limit);
        }
        
        List<ProductGraphQLDto> productDtos = products.stream()
            .map(product -> {
                ProductGraphQLDto dto = new ProductGraphQLDto(product);
                // Populate bidCount using BidService
                dto.setBidCount(bidService.getBidCount(product.getProductId()));
                return dto;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(Map.of("data", Map.of("products", productDtos)));
    }
    
    private ResponseEntity<Map<String, Object>> handleProductQuery(String query, Map<String, Object> variables) {
        // Extract id from query: product(id: "1")
        String idStr = query.substring(query.indexOf("id:") + 3);
        idStr = idStr.substring(idStr.indexOf("\"") + 1, idStr.indexOf("\"", idStr.indexOf("\"") + 1));
        
        try {
            Integer productId = Integer.valueOf(idStr);
            Product product = productService.getProductById(productId);
            ProductGraphQLDto productDto = null;
            if (product != null) {
                productDto = new ProductGraphQLDto(product);
                productDto.setBidCount(bidService.getBidCount(product.getProductId()));
            }
            
            return ResponseEntity.ok(Map.of("data", Map.of("product", productDto)));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of("errors", List.of(Map.of("message", "Invalid product ID: " + idStr))));
        }
    }
    
    private ResponseEntity<Map<String, Object>> handlePlaceBidMutation(String query, Map<String, Object> variables) {
        try {
            // Get current session
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession();
            
            // Get current user from session
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                Map<String, Object> bidResult = Map.of(
                    "success", false,
                    "message", "User not logged in",
                    "bidId", null
                );
                return ResponseEntity.ok(Map.of("data", Map.of("placeBid", bidResult)));
            }
            
            // Extract arguments from mutation
            String productIdStr = query.substring(query.indexOf("productId:") + 10);
            productIdStr = productIdStr.substring(productIdStr.indexOf("\"") + 1, productIdStr.indexOf("\"", productIdStr.indexOf("\"") + 1));
            
            String priceStr = query.substring(query.indexOf("price:") + 6);
            priceStr = priceStr.substring(0, priceStr.indexOf(",") > 0 ? priceStr.indexOf(",") : priceStr.indexOf(")"));
            priceStr = priceStr.trim();
            
            Integer productId = Integer.valueOf(productIdStr);
            BigDecimal bidAmount = BigDecimal.valueOf(Double.valueOf(priceStr));
            
            // Place the bid
            Bid placedBid = bidService.placeBid(productId, userId, bidAmount, Bid.BidType.REGULAR, null);
            
            Map<String, Object> bidResult;
            if (placedBid != null) {
                bidResult = Map.of(
                    "success", true,
                    "message", "Bid placed successfully",
                    "bidId", placedBid.getBidId().toString()
                );
            } else {
                bidResult = Map.of(
                    "success", false,
                    "message", "Failed to place bid",
                    "bidId", null
                );
            }
            
            return ResponseEntity.ok(Map.of("data", Map.of("placeBid", bidResult)));
            
        } catch (Exception e) {
            Map<String, Object> bidResult = Map.of(
                "success", false,
                "message", "Error placing bid: " + e.getMessage(),
                "bidId", null
            );
            return ResponseEntity.ok(Map.of("data", Map.of("placeBid", bidResult)));
        }
    }
}