package controller.api;

import entity.Bid;
import entity.Product;
import entity.User;
import service.BidService;
import service.ProductService;
import service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bids")
@CrossOrigin(origins = {"http://localhost:3000", "https://ebay-u3h1.onrender.com"})
public class BidApiController {
    
    @Autowired
    private BidService bidService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/place")
    public ResponseEntity<Map<String, Object>> placeBid(
            @RequestBody Map<String, Object> bidRequest,
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
            
            // Extract request parameters
            Integer productId = Integer.valueOf(bidRequest.get("productId").toString());
            BigDecimal bidAmount = new BigDecimal(bidRequest.get("bidAmount").toString());
            String bidTypeStr = (String) bidRequest.getOrDefault("bidType", "REGULAR");
            Bid.BidType bidType = Bid.BidType.valueOf(bidTypeStr);
            
            BigDecimal maxProxyAmount = null;
            if (bidRequest.get("maxProxyAmount") != null) {
                maxProxyAmount = new BigDecimal(bidRequest.get("maxProxyAmount").toString());
            }
            
            // Place the bid
            Bid bid = bidService.placeBid(productId, userId, bidAmount, bidType, maxProxyAmount);
            
            // Get updated product info
            Product product = productService.getProductById(productId);
            Bid highestBid = bidService.getHighestBid(productId);
            Long bidCount = bidService.getBidCount(productId);
            
            // Create safe response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Bid placed successfully!");
            response.put("bidId", bid.getBidId());
            response.put("currentPrice", product.getCurrentPrice());
            response.put("highestBidAmount", highestBid != null ? highestBid.getBidAmount() : product.getStartingPrice());
            response.put("bidCount", bidCount);
            response.put("minNextBid", bidService.getMinimumBidAmount(productId));
            response.put("isWinning", bid.getIsWinningBid());
            response.put("timeRemaining", product.getFormattedTimeRemaining());
            response.put("timeRemainingMillis", product.getTimeRemainingMillis());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to place bid: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @PostMapping("/buy-now")
    public ResponseEntity<Map<String, Object>> buyNow(
            @RequestBody Map<String, Object> buyRequest,
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
            
            Integer productId = Integer.valueOf(buyRequest.get("productId").toString());
            
            // Execute Buy It Now
            Bid bid = bidService.placeBuyNowBid(productId, userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Congratulations! You won this item with Buy It Now!");
            response.put("bidId", bid.getBidId());
            response.put("finalPrice", bid.getBidAmount());
            response.put("redirect", "/order/checkout/" + bid.getBidId());
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to execute Buy It Now: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @GetMapping("/history/{productId}")
    public ResponseEntity<Map<String, Object>> getBidHistory(@PathVariable Integer productId) {
        try {
            List<Bid> bidHistory = bidService.getBidHistory(productId);
            BidService.BidStatistics stats = bidService.getBidStatistics(productId);
            
            // Convert bids to safe DTOs
            List<Map<String, Object>> bidDTOs = bidHistory.stream()
                .map(this::convertBidToSafeDTO)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("bids", bidDTOs);
            response.put("statistics", Map.of(
                "bidCount", stats.getBidCount(),
                "minBid", stats.getMinBid(),
                "maxBid", stats.getMaxBid(),
                "avgBid", stats.getAvgBid()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to fetch bid history: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @GetMapping("/product/{productId}/info")
    public ResponseEntity<Map<String, Object>> getProductBidInfo(@PathVariable Integer productId, HttpSession session) {
        try {
            Product product = productService.getProductById(productId);
            if (product == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Product not found");
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            Bid highestBid = bidService.getHighestBid(productId);
            Long bidCount = bidService.getBidCount(productId);
            BigDecimal minNextBid = bidService.getMinimumBidAmount(productId);
            
            // Check user's bid status if logged in
            Integer userId = (Integer) session.getAttribute("userId");
            boolean userHasBid = userId != null && bidService.hasUserBid(userId, productId);
            boolean userIsWinning = false;
            Bid userLastBid = null;
            
            if (userId != null && userHasBid) {
                userLastBid = bidService.getUserLastBid(userId, productId);
                userIsWinning = userLastBid != null && userLastBid.getIsWinningBid();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("currentPrice", product.getCurrentPrice());
            response.put("startingPrice", product.getStartingPrice());
            response.put("buyNowPrice", product.getBuyNowPrice());
            response.put("reservePrice", product.getReservePrice());
            response.put("highestBidAmount", highestBid != null ? highestBid.getBidAmount() : product.getStartingPrice());
            response.put("bidCount", bidCount);
            response.put("minNextBid", minNextBid);
            response.put("timeRemaining", product.getFormattedTimeRemaining());
            response.put("timeRemainingMillis", product.getTimeRemainingMillis());
            response.put("isAuctionActive", product.isAuctionActive());
            response.put("isAuctionEnded", product.isAuctionEnded());
            response.put("isBuyNowAvailable", product.isBuyNowAvailable());
            response.put("isAuction", product.isAuction());
            response.put("listingType", product.getListingType().toString());
            response.put("status", product.getStatus().toString());
            
            // User-specific information
            response.put("userHasBid", userHasBid);
            response.put("userIsWinning", userIsWinning);
            if (userLastBid != null) {
                response.put("userLastBid", convertBidToSafeDTO(userLastBid));
            }
            
            // Add winning bidder info if auction ended
            if (product.isAuctionEnded()) {
                Bid winningBid = bidService.getWinningBid(productId);
                if (winningBid != null) {
                    response.put("winningBidder", winningBid.getBidder().getUsername());
                    response.put("finalPrice", winningBid.getBidAmount());
                }
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to fetch bid info: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @GetMapping("/my-bids")
    public ResponseEntity<Map<String, Object>> getMyBids(HttpSession session) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "User not logged in");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            List<Bid> userBids = bidService.getUserBids(userId);
            List<Bid> activeBids = bidService.getUserActiveBids(userId);
            
            // Convert to safe DTOs
            List<Map<String, Object>> userBidDTOs = userBids.stream()
                .map(this::convertBidToSafeDTO)
                .collect(Collectors.toList());
            
            List<Map<String, Object>> activeBidDTOs = activeBids.stream()
                .map(this::convertBidToSafeDTO)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userBids", userBidDTOs);
            response.put("activeBids", activeBidDTOs);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to fetch user bids: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    @GetMapping("/winning-status")
    public ResponseEntity<Map<String, Object>> getWinningStatus(HttpSession session) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "User not logged in");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            List<Bid> activeBids = bidService.getUserActiveBids(userId);
            
            int winningCount = 0;
            int biddingCount = 0;
            
            for (Bid bid : activeBids) {
                if (bid.getBidStatus() == Bid.BidStatus.WINNING) {
                    winningCount++;
                } else if (bid.getBidStatus() == Bid.BidStatus.ACTIVE) {
                    biddingCount++;
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("winningCount", winningCount);
            response.put("biddingCount", biddingCount);
            response.put("totalActiveBids", activeBids.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to fetch winning status: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    private Map<String, Object> convertBidToSafeDTO(Bid bid) {
        Map<String, Object> bidDTO = new HashMap<>();
        bidDTO.put("bidId", bid.getBidId());
        bidDTO.put("bidAmount", bid.getBidAmount());
        bidDTO.put("bidTime", bid.getBidTime().toString());
        bidDTO.put("bidType", bid.getBidType().toString());
        bidDTO.put("bidStatus", bid.getBidStatus().toString());
        bidDTO.put("isWinningBid", bid.getIsWinningBid());
        bidDTO.put("maxProxyAmount", bid.getMaxProxyAmount());
        
        // Bidder info (minimal for privacy)
        if (bid.getBidder() != null) {
            Map<String, Object> bidderInfo = new HashMap<>();
            bidderInfo.put("userId", bid.getBidder().getUserId());
            bidderInfo.put("username", bid.getBidder().getUsername());
            bidDTO.put("bidder", bidderInfo);
        }
        
        // Product info (minimal)
        if (bid.getProduct() != null) {
            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("productId", bid.getProduct().getProductId());
            productInfo.put("title", bid.getProduct().getTitle());
            productInfo.put("currentPrice", bid.getProduct().getCurrentPrice());
            productInfo.put("status", bid.getProduct().getStatus().toString());
            productInfo.put("isAuctionActive", bid.getProduct().isAuctionActive());
            productInfo.put("timeRemaining", bid.getProduct().getFormattedTimeRemaining());
            bidDTO.put("product", productInfo);
        }
        
        return bidDTO;
    }
}