package controller.api;

import entity.Watchlist;
import entity.Product;
import entity.User;
import service.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/watchlist")
@CrossOrigin(origins = {"http://localhost:3000", "https://ebay-u3h1.onrender.com"})
public class WatchlistApiController {
    
    @Autowired
    private WatchlistService watchlistService;
    
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToWatchlist(
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        
        try {
            // Get current user from session
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "User not logged in");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            // Extract product ID
            Integer productId = Integer.valueOf(request.get("productId").toString());
            
            // Add to watchlist
            Watchlist watchlist = watchlistService.addToWatchlist(userId, productId);
            
            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Item added to watchlist");
            response.put("watchlistId", watchlist.getWatchlistId());
            response.put("addedDate", watchlist.getAddedDate().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeFromWatchlist(
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        
        try {
            // Get current user from session
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "User not logged in");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            // Extract product ID
            Integer productId = Integer.valueOf(request.get("productId").toString());
            
            // Remove from watchlist
            watchlistService.removeFromWatchlist(userId, productId);
            
            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Item removed from watchlist");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Object>> toggleWatchlist(
            @RequestBody Map<String, Object> request,
            HttpSession session) {
        
        try {
            // Get current user from session
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "User not logged in");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            // Extract product ID
            Integer productId = Integer.valueOf(request.get("productId").toString());
            
            // Toggle watchlist status
            boolean added = watchlistService.toggleWatchlist(userId, productId);
            
            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("inWatchlist", added);
            response.put("message", added ? "Item added to watchlist" : "Item removed from watchlist");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Object>> checkWatchlistStatus(
            @PathVariable Integer productId,
            HttpSession session) {
        
        try {
            // Get current user from session
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("inWatchlist", false);
                response.put("loggedIn", false);
                return ResponseEntity.ok(response);
            }
            
            // Check if product is in watchlist
            boolean inWatchlist = watchlistService.isProductInWatchlist(userId, productId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("inWatchlist", inWatchlist);
            response.put("loggedIn", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserWatchlist(HttpSession session) {
        try {
            // Get current user from session
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "User not logged in");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            // Get user's watchlist
            List<Watchlist> watchlistItems = watchlistService.getUserWatchlist(userId);
            
            // Convert to response format
            List<Map<String, Object>> watchlistData = watchlistItems.stream()
                .map(watchlist -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("watchlistId", watchlist.getWatchlistId());
                    item.put("addedDate", watchlist.getAddedDate().toString());
                    
                    // Product information
                    Product product = watchlist.getProduct();
                    Map<String, Object> productInfo = new HashMap<>();
                    productInfo.put("productId", product.getProductId());
                    productInfo.put("name", product.getTitle());
                    productInfo.put("description", product.getDescription());
                    productInfo.put("currentPrice", product.getCurrentPrice());
                    productInfo.put("buyItNowPrice", product.getBuyNowPrice());
                    productInfo.put("status", product.getStatus());
                    productInfo.put("endTime", product.getAuctionEndTime() != null ? product.getAuctionEndTime().toString() : null);
                    
                    // Seller information
                    User seller = product.getSeller();
                    Map<String, Object> sellerInfo = new HashMap<>();
                    sellerInfo.put("userId", seller.getUserId());
                    sellerInfo.put("username", seller.getUsername());
                    sellerInfo.put("firstName", seller.getFirstName());
                    sellerInfo.put("lastName", seller.getLastName());
                    productInfo.put("seller", sellerInfo);
                    
                    item.put("product", productInfo);
                    return item;
                })
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("watchlist", watchlistData);
            response.put("count", watchlistData.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getWatchlistCount(HttpSession session) {
        try {
            // Get current user from session
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("count", 0);
                response.put("loggedIn", false);
                return ResponseEntity.ok(response);
            }
            
            Long count = watchlistService.getWatchlistCount(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("count", count);
            response.put("loggedIn", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @GetMapping("/watchers/{productId}")
    public ResponseEntity<Map<String, Object>> getProductWatchers(
            @PathVariable Integer productId,
            HttpSession session) {
        
        try {
            // Get current user from session (optional for this endpoint)
            Integer userId = (Integer) session.getAttribute("userId");
            
            // Get watchers count (public information)
            Long watchersCount = watchlistService.getWatchersCount(productId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("productId", productId);
            response.put("watchersCount", watchersCount);
            response.put("success", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearWatchlist(HttpSession session) {
        try {
            // Get current user from session
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "User not logged in");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            // Clear user's entire watchlist
            watchlistService.clearUserWatchlist(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Watchlist cleared successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}