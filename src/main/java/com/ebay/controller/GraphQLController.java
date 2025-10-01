package com.ebay.controller;

import entity.Product;
import entity.Bid;
import dto.ProductGraphQLDto;
import service.ProductService;
import service.BidService;
import service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GraphQLController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private BidService bidService;
    
    @Autowired
    private CategoryService categoryService;

    @QueryMapping
    public List<ProductGraphQLDto> products(@Argument Integer limit, @Argument String category) {
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
        
        // Convert to DTOs to avoid circular references
        return products.stream()
            .map(ProductGraphQLDto::new)
            .collect(Collectors.toList());
    }
    
    @QueryMapping
    public ProductGraphQLDto product(@Argument String id) {
        try {
            Integer productId = Integer.valueOf(id);
            Product product = productService.getProductById(productId);
            return product != null ? new ProductGraphQLDto(product) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @MutationMapping
    public BidResult placeBid(@Argument String productId, @Argument Double price) {
        try {
            // Get current session
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession();
            
            // Get current user from session
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                return new BidResult(false, "User not logged in", null);
            }
            
            // Validate inputs
            Integer prodId = Integer.valueOf(productId);
            BigDecimal bidAmount = BigDecimal.valueOf(price);
            
            // Place the bid
            Bid placedBid = bidService.placeBid(prodId, userId, bidAmount, Bid.BidType.REGULAR, null);
            
            if (placedBid != null) {
                return new BidResult(true, "Bid placed successfully", placedBid.getBidId().toString());
            } else {
                return new BidResult(false, "Failed to place bid", null);
            }
            
        } catch (NumberFormatException e) {
            return new BidResult(false, "Invalid product ID or bid amount", null);
        } catch (Exception e) {
            return new BidResult(false, "Error placing bid: " + e.getMessage(), null);
        }
    }
    
    // Inner class for BidResult
    public static class BidResult {
        private Boolean success;
        private String message;
        private String bidId;
        
        public BidResult(Boolean success, String message, String bidId) {
            this.success = success;
            this.message = message;
            this.bidId = bidId;
        }
        
        public Boolean getSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getBidId() {
            return bidId;
        }
    }
}