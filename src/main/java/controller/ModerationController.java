package controller;

import entity.Review;
import entity.User;
import service.ReviewModerationService;
import service.ReviewModerationService.ModerationAction;
import service.ReviewModerationService.ReportReason;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @Controller  // Temporarily disabled
@RequestMapping("/moderation")
public class ModerationController {
    
    @Autowired
    private ReviewModerationService moderationService;
    
    // View Controllers
    
    /**
     * Show moderation dashboard (for moderators/admins)
     */
    @GetMapping("/dashboard")
    public ModelAndView showModerationDashboard(HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        // In production, you'd check if user has moderator permissions
        // For now, allow any logged-in user to access
        
        try {
            List<Review> pendingReviews = moderationService.getPendingReviews();
            List<Review> flaggedReviews = moderationService.getFlaggedReviews();
            Map<String, Object> stats = moderationService.getModerationStats();
            
            mv.setViewName("moderation-dashboard");
            mv.addObject("pendingReviews", pendingReviews);
            mv.addObject("flaggedReviews", flaggedReviews);
            mv.addObject("stats", stats);
            mv.addObject("currentUser", currentUser);
            mv.addObject("moderationActions", ModerationAction.values());
            mv.addObject("reportReasons", ReportReason.values());
            
        } catch (Exception e) {
            mv.setViewName("error");
            mv.addObject("error", "Unable to load moderation dashboard: " + e.getMessage());
        }
        
        return mv;
    }
    
    /**
     * Show review report form
     */
    @GetMapping("/report/{reviewId}")
    public ModelAndView showReportForm(@PathVariable Integer reviewId, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        mv.setViewName("report-review");
        mv.addObject("reviewId", reviewId);
        mv.addObject("reportReasons", ReportReason.values());
        mv.addObject("currentUser", currentUser);
        
        return mv;
    }
    
    // REST API Endpoints
    
    /**
     * Report a review
     */
    @PostMapping("/api/report")
    @ResponseBody
    public Map<String, Object> reportReview(@RequestParam Integer reviewId,
                                          @RequestParam String reason,
                                          @RequestParam(required = false) String additionalInfo,
                                          HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            ReportReason reportReason = ReportReason.valueOf(reason.toUpperCase());
            
            moderationService.reportReview(reviewId, currentUser.getUserId(), reportReason, additionalInfo);
            
            response.put("success", true);
            response.put("message", "Review reported successfully");
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Invalid report reason");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Moderate a review (for moderators)
     */
    @PostMapping("/api/moderate")
    @ResponseBody
    public Map<String, Object> moderateReview(@RequestParam Integer reviewId,
                                            @RequestParam String action,
                                            @RequestParam(required = false) String reason,
                                            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        // In production, check if user has moderator permissions
        // For now, allow any logged-in user
        
        try {
            ModerationAction moderationAction = ModerationAction.valueOf(action.toUpperCase());
            String moderationReason = reason != null ? reason : "No reason provided";
            
            moderationService.moderateReview(reviewId, currentUser.getUserId(), moderationAction, moderationReason);
            
            response.put("success", true);
            response.put("message", "Review moderated successfully");
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Invalid moderation action");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Get pending reviews for moderation
     */
    @GetMapping("/api/pending")
    @ResponseBody
    public Map<String, Object> getPendingReviews(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            List<Review> pendingReviews = moderationService.getPendingReviews();
            
            response.put("success", true);
            response.put("reviews", pendingReviews);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Get flagged reviews
     */
    @GetMapping("/api/flagged")
    @ResponseBody
    public Map<String, Object> getFlaggedReviews(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            List<Review> flaggedReviews = moderationService.getFlaggedReviews();
            
            response.put("success", true);
            response.put("reviews", flaggedReviews);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Get moderation statistics
     */
    @GetMapping("/api/stats")
    @ResponseBody
    public Map<String, Object> getModerationStats(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            Map<String, Object> stats = moderationService.getModerationStats();
            
            response.put("success", true);
            response.putAll(stats);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Auto-moderate a review (for testing)
     */
    @PostMapping("/api/auto-moderate")
    @ResponseBody
    public Map<String, Object> autoModerateReview(@RequestParam Integer reviewId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            // This would typically be called automatically when a review is created
            // Exposing it as an endpoint for testing/demonstration purposes
            response.put("success", true);
            response.put("message", "Auto-moderation feature is integrated into review creation");
            
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