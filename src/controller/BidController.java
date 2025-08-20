package controller;

import entity.Bid;
import entity.Product;
import entity.User;
import service.BidService;
import service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/bid")
public class BidController {
    
    @Autowired
    private BidService bidService;
    
    @Autowired
    private ProductService productService;
    
    // Place a regular bid (AJAX)
    @PostMapping("/place")
    @ResponseBody
    public Map<String, Object> placeBid(@RequestParam Integer productId,
                                        @RequestParam BigDecimal bidAmount,
                                        @RequestParam(required = false) BigDecimal maxProxyAmount,
                                        @RequestParam(defaultValue = "REGULAR") String bidType,
                                        HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "You must be logged in to place a bid");
            return response;
        }
        
        try {
            Bid.BidType type = Bid.BidType.valueOf(bidType);
            
            Bid bid = bidService.placeBid(productId, currentUser.getUserId(), 
                                         bidAmount, type, maxProxyAmount);
            
            // Get updated product info
            Product product = productService.getProductById(productId);
            Bid highestBid = bidService.getHighestBid(productId);
            Long bidCount = bidService.getBidCount(productId);
            
            response.put("success", true);
            response.put("message", "Bid placed successfully!");
            response.put("bidId", bid.getBidId());
            response.put("currentPrice", product.getCurrentPrice());
            response.put("highestBidAmount", highestBid != null ? highestBid.getBidAmount() : product.getStartingPrice());
            response.put("bidCount", bidCount);
            response.put("minNextBid", bidService.getMinimumBidAmount(productId));
            response.put("isWinning", bid.getIsWinningBid());
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // Buy It Now (AJAX)
    @PostMapping("/buy-now")
    @ResponseBody
    public Map<String, Object> buyNow(@RequestParam Integer productId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "You must be logged in to buy");
            return response;
        }
        
        try {
            Bid bid = bidService.placeBuyNowBid(productId, currentUser.getUserId());
            
            response.put("success", true);
            response.put("message", "Congratulations! You won this item with Buy It Now!");
            response.put("bidId", bid.getBidId());
            response.put("finalPrice", bid.getBidAmount());
            response.put("redirect", "/eBay/order/checkout/" + bid.getBidId());
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // Get bid history for a product (AJAX)
    @GetMapping("/history/{productId}")
    @ResponseBody
    public Map<String, Object> getBidHistory(@PathVariable Integer productId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Bid> bidHistory = bidService.getBidHistory(productId);
            BidService.BidStatistics stats = bidService.getBidStatistics(productId);
            
            response.put("success", true);
            response.put("bids", bidHistory);
            response.put("statistics", stats);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // Get minimum bid amount (AJAX)
    @GetMapping("/min-amount/{productId}")
    @ResponseBody
    public Map<String, Object> getMinimumBidAmount(@PathVariable Integer productId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            BigDecimal minAmount = bidService.getMinimumBidAmount(productId);
            Product product = productService.getProductById(productId);
            Bid highestBid = bidService.getHighestBid(productId);
            Long bidCount = bidService.getBidCount(productId);
            
            response.put("success", true);
            response.put("minAmount", minAmount);
            response.put("currentPrice", product.getCurrentPrice());
            response.put("highestBid", highestBid != null ? highestBid.getBidAmount() : product.getStartingPrice());
            response.put("bidCount", bidCount);
            response.put("timeRemaining", product.getFormattedTimeRemaining());
            response.put("isActive", product.isAuctionActive());
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // Show user's bid history
    @GetMapping("/my-bids")
    public String showMyBids(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        List<Bid> userBids = bidService.getUserBids(currentUser.getUserId());
        List<Bid> activeBids = bidService.getUserActiveBids(currentUser.getUserId());
        
        model.addAttribute("userBids", userBids);
        model.addAttribute("activeBids", activeBids);
        
        return "bid-history";
    }
    
    // Show detailed bid history for a product
    @GetMapping("/history-page/{productId}")
    public String showBidHistoryPage(@PathVariable Integer productId, Model model) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            return "redirect:/";
        }
        
        List<Bid> bidHistory = bidService.getBidHistory(productId);
        BidService.BidStatistics stats = bidService.getBidStatistics(productId);
        Bid winningBid = bidService.getWinningBid(productId);
        
        model.addAttribute("product", product);
        model.addAttribute("bidHistory", bidHistory);
        model.addAttribute("statistics", stats);
        model.addAttribute("winningBid", winningBid);
        
        return "bid-history-detail";
    }
    
    // Check if user is winning any auctions (AJAX)
    @GetMapping("/winning-status")
    @ResponseBody
    public Map<String, Object> getWinningStatus(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not logged in");
            return response;
        }
        
        try {
            List<Bid> activeBids = bidService.getUserActiveBids(currentUser.getUserId());
            
            int winningCount = 0;
            int biddingCount = 0;
            
            for (Bid bid : activeBids) {
                if (bid.getBidStatus() == Bid.BidStatus.WINNING) {
                    winningCount++;
                } else if (bid.getBidStatus() == Bid.BidStatus.ACTIVE) {
                    biddingCount++;
                }
            }
            
            response.put("success", true);
            response.put("winningCount", winningCount);
            response.put("biddingCount", biddingCount);
            response.put("totalActiveBids", activeBids.size());
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // Auto-refresh data for auction page (AJAX)
    @GetMapping("/refresh/{productId}")
    @ResponseBody
    public Map<String, Object> refreshAuctionData(@PathVariable Integer productId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Product product = productService.getProductById(productId);
            if (product == null) {
                response.put("success", false);
                response.put("message", "Product not found");
                return response;
            }
            
            Bid highestBid = bidService.getHighestBid(productId);
            Long bidCount = bidService.getBidCount(productId);
            BigDecimal minNextBid = bidService.getMinimumBidAmount(productId);
            
            User currentUser = (User) session.getAttribute("currentUser");
            boolean userHasBid = currentUser != null && 
                               bidService.hasUserBid(currentUser.getUserId(), productId);
            boolean userIsWinning = false;
            
            if (currentUser != null && userHasBid) {
                Bid userLastBid = bidService.getUserLastBid(currentUser.getUserId(), productId);
                userIsWinning = userLastBid != null && userLastBid.getIsWinningBid();
            }
            
            response.put("success", true);
            response.put("currentPrice", product.getCurrentPrice());
            response.put("highestBid", highestBid != null ? highestBid.getBidAmount() : product.getStartingPrice());
            response.put("bidCount", bidCount);
            response.put("minNextBid", minNextBid);
            response.put("timeRemaining", product.getFormattedTimeRemaining());
            response.put("timeRemainingMillis", product.getTimeRemainingMillis());
            response.put("isActive", product.isAuctionActive());
            response.put("isEnded", product.isAuctionEnded());
            response.put("userHasBid", userHasBid);
            response.put("userIsWinning", userIsWinning);
            response.put("status", product.getStatus().toString());
            
            // Add winning bidder info if auction ended
            if (product.isAuctionEnded()) {
                Bid winningBid = bidService.getWinningBid(productId);
                if (winningBid != null) {
                    response.put("winningBidder", winningBid.getBidder().getUsername());
                    response.put("finalPrice", winningBid.getBidAmount());
                }
            }
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // Add current user to all models
    @ModelAttribute("currentUser")
    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }
}