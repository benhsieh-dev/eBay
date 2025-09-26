package controller;

import entity.Review;
import entity.ReviewHelpfulness;
import entity.User;
import service.ReviewService;
import service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @Controller  // Temporarily disabled
@RequestMapping("/reviews")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private FileUploadService fileUploadService;
    
    // View Controllers
    
    /**
     * Show review form for an order
     */
    @GetMapping("/write/{orderId}")
    public ModelAndView showReviewForm(@PathVariable Integer orderId,
                                     @RequestParam Review.ReviewType reviewType,
                                     @RequestParam(required = false) Integer productId,
                                     HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        mv.setViewName("review-form");
        mv.addObject("orderId", orderId);
        mv.addObject("reviewType", reviewType);
        mv.addObject("productId", productId);
        mv.addObject("currentUser", currentUser);
        
        return mv;
    }
    
    /**
     * Show user's reviews (received)
     */
    @GetMapping("/user/{userId}")
    public ModelAndView showUserReviews(@PathVariable Integer userId,
                                      @RequestParam(required = false) String type,
                                      @RequestParam(defaultValue = "0") int page,
                                      HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        Review.ReviewType reviewType = null;
        if (type != null) {
            try {
                reviewType = Review.ReviewType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Invalid type, will show all reviews
            }
        }
        
        List<Review> reviews = reviewService.getPaginatedReviews(userId, reviewType, page, 20);
        Map<String, Object> stats = reviewService.getUserReviewStats(userId);
        
        mv.setViewName("user-reviews");
        mv.addObject("reviews", reviews);
        mv.addObject("stats", stats);
        mv.addObject("reviewType", reviewType);
        mv.addObject("page", page);
        mv.addObject("userId", userId);
        mv.addObject("currentUser", currentUser);
        
        return mv;
    }
    
    /**
     * Show user's written reviews
     */
    @GetMapping("/my-reviews")
    public ModelAndView showMyReviews(HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        List<Review> reviews = reviewService.getReviewsByUser(currentUser.getUserId());
        
        mv.setViewName("my-reviews");
        mv.addObject("reviews", reviews);
        mv.addObject("currentUser", currentUser);
        
        return mv;
    }
    
    /**
     * Show product reviews
     */
    @GetMapping("/product/{productId}")
    public ModelAndView showProductReviews(@PathVariable Integer productId,
                                         @RequestParam(defaultValue = "0") int page,
                                         HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        List<Review> reviews = reviewService.getProductReviews(productId);
        Map<String, Object> stats = reviewService.getProductReviewStats(productId);
        
        mv.setViewName("product-reviews");
        mv.addObject("reviews", reviews);
        mv.addObject("stats", stats);
        mv.addObject("productId", productId);
        mv.addObject("page", page);
        
        User currentUser = (User) session.getAttribute("currentUser");
        mv.addObject("currentUser", currentUser);
        
        return mv;
    }
    
    // REST API Endpoints
    
    /**
     * Create a new review
     */
    @PostMapping("/api/create")
    @ResponseBody
    public Map<String, Object> createReview(@RequestParam Integer revieweeId,
                                          @RequestParam Integer orderId,
                                          @RequestParam(required = false) Integer productId,
                                          @RequestParam String reviewType,
                                          @RequestParam BigDecimal rating,
                                          @RequestParam(required = false) String title,
                                          @RequestParam(required = false) String comment,
                                          HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            Review.ReviewType type = Review.ReviewType.valueOf(reviewType.toUpperCase());
            
            Review review = reviewService.createReview(
                currentUser.getUserId(), revieweeId, orderId, productId,
                type, rating, title, comment
            );
            
            response.put("success", true);
            response.put("review", review);
            response.put("message", "Review created successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Create detailed review with breakdown ratings
     */
    @PostMapping("/api/create-detailed")
    @ResponseBody
    public Map<String, Object> createDetailedReview(@RequestParam Integer revieweeId,
                                                   @RequestParam Integer orderId,
                                                   @RequestParam(required = false) Integer productId,
                                                   @RequestParam String reviewType,
                                                   @RequestParam BigDecimal overallRating,
                                                   @RequestParam(required = false) String title,
                                                   @RequestParam(required = false) String comment,
                                                   @RequestParam(required = false) BigDecimal communicationRating,
                                                   @RequestParam(required = false) BigDecimal shippingRating,
                                                   @RequestParam(required = false) BigDecimal itemDescriptionRating,
                                                   @RequestParam(required = false) BigDecimal responseTimeRating,
                                                   HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            Review.ReviewType type = Review.ReviewType.valueOf(reviewType.toUpperCase());
            
            Review review = reviewService.createDetailedReview(
                currentUser.getUserId(), revieweeId, orderId, productId, type,
                overallRating, title, comment, communicationRating,
                shippingRating, itemDescriptionRating, responseTimeRating
            );
            
            response.put("success", true);
            response.put("review", review);
            response.put("message", "Detailed review created successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Edit an existing review
     */
    @PutMapping("/api/{reviewId}")
    @ResponseBody
    public Map<String, Object> editReview(@PathVariable Integer reviewId,
                                        @RequestParam String title,
                                        @RequestParam String comment,
                                        @RequestParam BigDecimal rating,
                                        HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            Review review = reviewService.editReview(reviewId, currentUser.getUserId(), 
                                                   title, comment, rating);
            
            response.put("success", true);
            response.put("review", review);
            response.put("message", "Review updated successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Add response to a review
     */
    @PostMapping("/api/{reviewId}/respond")
    @ResponseBody
    public Map<String, Object> addResponse(@PathVariable Integer reviewId,
                                         @RequestParam String response,
                                         HttpSession session) {
        Map<String, Object> responseMap = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            responseMap.put("success", false);
            responseMap.put("message", "Not authenticated");
            return responseMap;
        }
        
        try {
            Review review = reviewService.addResponse(reviewId, currentUser.getUserId(), response);
            
            responseMap.put("success", true);
            responseMap.put("review", review);
            responseMap.put("message", "Response added successfully");
            
        } catch (Exception e) {
            responseMap.put("success", false);
            responseMap.put("message", e.getMessage());
        }
        
        return responseMap;
    }
    
    /**
     * Vote on review helpfulness
     */
    @PostMapping("/api/{reviewId}/vote")
    @ResponseBody
    public Map<String, Object> voteHelpfulness(@PathVariable Integer reviewId,
                                             @RequestParam boolean helpful,
                                             HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            ReviewHelpfulness vote = reviewService.voteHelpfulness(
                reviewId, currentUser.getUserId(), helpful);
            
            response.put("success", true);
            response.put("vote", vote);
            response.put("message", helpful ? "Marked as helpful" : "Marked as not helpful");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Flag review for moderation
     */
    @PostMapping("/api/{reviewId}/flag")
    @ResponseBody
    public Map<String, Object> flagReview(@PathVariable Integer reviewId,
                                        @RequestParam String reason,
                                        HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            reviewService.flagReview(reviewId, currentUser.getUserId(), reason);
            
            response.put("success", true);
            response.put("message", "Review flagged for moderation");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Get user reviews
     */
    @GetMapping("/api/user/{userId}")
    @ResponseBody
    public Map<String, Object> getUserReviews(@PathVariable Integer userId,
                                            @RequestParam(required = false) String type,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int pageSize) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Review.ReviewType reviewType = null;
            if (type != null) {
                reviewType = Review.ReviewType.valueOf(type.toUpperCase());
            }
            
            List<Review> reviews = reviewService.getPaginatedReviews(userId, reviewType, page, pageSize);
            Map<String, Object> stats = reviewService.getUserReviewStats(userId);
            
            response.put("success", true);
            response.put("reviews", reviews);
            response.put("stats", stats);
            response.put("page", page);
            response.put("pageSize", pageSize);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Get product reviews
     */
    @GetMapping("/api/product/{productId}")
    @ResponseBody
    public Map<String, Object> getProductReviews(@PathVariable Integer productId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "20") int pageSize) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Review> reviews = reviewService.getProductReviews(productId);
            Map<String, Object> stats = reviewService.getProductReviewStats(productId);
            
            // Simple pagination for product reviews
            int start = page * pageSize;
            int end = Math.min(start + pageSize, reviews.size());
            List<Review> paginatedReviews = reviews.subList(start, end);
            
            response.put("success", true);
            response.put("reviews", paginatedReviews);
            response.put("stats", stats);
            response.put("page", page);
            response.put("pageSize", pageSize);
            response.put("totalReviews", reviews.size());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Search reviews
     */
    @GetMapping("/api/search")
    @ResponseBody
    public Map<String, Object> searchReviews(@RequestParam String query,
                                           @RequestParam(required = false) Integer userId,
                                           HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Review> reviews = reviewService.searchReviews(query, userId);
            
            response.put("success", true);
            response.put("reviews", reviews);
            response.put("query", query);
            response.put("resultCount", reviews.size());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Get review statistics
     */
    @GetMapping("/api/stats/user/{userId}")
    @ResponseBody
    public Map<String, Object> getUserStats(@PathVariable Integer userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> stats = reviewService.getUserReviewStats(userId);
            
            response.put("success", true);
            response.put("stats", stats);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Get product rating statistics
     */
    @GetMapping("/api/stats/product/{productId}")
    @ResponseBody
    public Map<String, Object> getProductStats(@PathVariable Integer productId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> stats = reviewService.getProductReviewStats(productId);
            
            response.put("success", true);
            response.put("stats", stats);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // Helper method for session management
    @ModelAttribute("currentUser")
    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }
}