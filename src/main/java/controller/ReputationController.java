package controller;

import entity.User;
import service.ReputationService;
import service.ReputationService.ReputationScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/reputation")
public class ReputationController {
    
    @Autowired
    private ReputationService reputationService;
    
    // View Controllers
    
    /**
     * Show user reputation dashboard
     */
    @GetMapping("/dashboard")
    public ModelAndView showReputationDashboard(HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        try {
            ReputationScore score = reputationService.calculateReputationScore(currentUser.getUserId());
            Map<String, Object> comparison = reputationService.getReputationComparison(currentUser.getUserId());
            List<String> suggestions = reputationService.generateImprovementSuggestions(currentUser.getUserId());
            
            mv.setViewName("reputation-dashboard");
            mv.addObject("reputationScore", score);
            mv.addObject("comparison", comparison);
            mv.addObject("suggestions", suggestions);
            mv.addObject("currentUser", currentUser);
            
        } catch (Exception e) {
            mv.setViewName("error");
            mv.addObject("error", "Unable to load reputation data: " + e.getMessage());
        }
        
        return mv;
    }
    
    /**
     * Show public reputation profile for a user
     */
    @GetMapping("/profile/{userId}")
    public ModelAndView showPublicReputation(@PathVariable Integer userId, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        try {
            ReputationScore score = reputationService.calculateReputationScore(userId);
            
            mv.setViewName("reputation-profile");
            mv.addObject("reputationScore", score);
            mv.addObject("userId", userId);
            
            User currentUser = (User) session.getAttribute("currentUser");
            mv.addObject("currentUser", currentUser);
            
        } catch (Exception e) {
            mv.setViewName("error");
            mv.addObject("error", "Unable to load reputation profile: " + e.getMessage());
        }
        
        return mv;
    }
    
    // REST API Endpoints
    
    /**
     * Get reputation score for a user
     */
    @GetMapping("/api/score/{userId}")
    @ResponseBody
    public Map<String, Object> getReputationScore(@PathVariable Integer userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            ReputationScore score = reputationService.calculateReputationScore(userId);
            
            response.put("success", true);
            response.put("reputationScore", score);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Get current user's reputation score
     */
    @GetMapping("/api/my-score")
    @ResponseBody
    public Map<String, Object> getMyReputationScore(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            ReputationScore score = reputationService.calculateReputationScore(currentUser.getUserId());
            
            response.put("success", true);
            response.put("reputationScore", score);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Get reputation comparison and analytics
     */
    @GetMapping("/api/comparison/{userId}")
    @ResponseBody
    public Map<String, Object> getReputationComparison(@PathVariable Integer userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> comparison = reputationService.getReputationComparison(userId);
            
            response.put("success", true);
            response.putAll(comparison);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Get improvement suggestions for current user
     */
    @GetMapping("/api/suggestions")
    @ResponseBody
    public Map<String, Object> getImprovementSuggestions(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        try {
            List<String> suggestions = reputationService.generateImprovementSuggestions(currentUser.getUserId());
            
            response.put("success", true);
            response.put("suggestions", suggestions);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Update user reputation (called after transactions/reviews)
     */
    @PostMapping("/api/update")
    @ResponseBody
    public Map<String, Object> updateReputation(@RequestParam Integer userId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not authenticated");
            return response;
        }
        
        // Only allow users to update their own reputation or admin users
        if (!currentUser.getUserId().equals(userId)) {
            response.put("success", false);
            response.put("message", "Unauthorized");
            return response;
        }
        
        try {
            reputationService.updateUserReputation(userId);
            
            response.put("success", true);
            response.put("message", "Reputation updated successfully");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * Get reputation badge data (for embedding in other pages)
     */
    @GetMapping("/api/badge/{userId}")
    @ResponseBody
    public Map<String, Object> getReputationBadge(@PathVariable Integer userId) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            ReputationScore score = reputationService.calculateReputationScore(userId);
            
            Map<String, Object> badge = new HashMap<>();
            badge.put("level", score.getLevel().getDisplayName());
            badge.put("badge", score.getLevel().getBadge());
            badge.put("color", score.getLevel().getColor());
            badge.put("rating", score.getAverageRating());
            badge.put("reviewCount", score.getReviewCount());
            badge.put("overallScore", score.getOverallScore());
            
            response.put("success", true);
            response.put("badge", badge);
            
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