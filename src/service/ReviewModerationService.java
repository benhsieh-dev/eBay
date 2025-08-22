package service;

import dao.ReviewDAO;
import dao.UserDAO;
import entity.Review;
import entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Service for moderating reviews and handling reports
 * Includes automated content filtering and manual moderation tools
 */
@Service
public class ReviewModerationService {
    
    @Autowired
    private ReviewDAO reviewDAO;
    
    @Autowired
    private UserDAO userDAO;
    
    // Content filtering patterns
    private static final Pattern[] PROFANITY_PATTERNS = {
        Pattern.compile("\\b(spam|scam|fake|fraud)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(terrible|awful|horrible|worst)\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\b(best|amazing|perfect|excellent)\\s+(seller|buyer)\\s+ever\\b", Pattern.CASE_INSENSITIVE)
    };
    
    private static final Pattern[] PERSONAL_INFO_PATTERNS = {
        Pattern.compile("\\b\\d{3}-\\d{3}-\\d{4}\\b"), // Phone numbers
        Pattern.compile("\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b"), // Email addresses
        Pattern.compile("\\b\\d{4}\\s?\\d{4}\\s?\\d{4}\\s?\\d{4}\\b") // Credit card patterns
    };
    
    public enum ModerationAction {
        APPROVE("Approved", "Review approved for public display"),
        HIDE("Hidden", "Review hidden from public view"),
        DELETE("Deleted", "Review permanently deleted"),
        FLAG_FOR_REVIEW("Flagged", "Review flagged for manual review"),
        REQUEST_EDIT("Edit Requested", "User asked to edit review");
        
        private final String displayName;
        private final String description;
        
        ModerationAction(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    public enum ReportReason {
        INAPPROPRIATE_CONTENT("Inappropriate Content", "Contains offensive or inappropriate language"),
        SPAM("Spam", "Spam or promotional content"),
        PERSONAL_INFORMATION("Personal Information", "Contains personal information"),
        FAKE_REVIEW("Fake Review", "Suspected fake or fraudulent review"),
        HARASSMENT("Harassment", "Harassment or personal attacks"),
        OFF_TOPIC("Off Topic", "Not related to the transaction or product"),
        OTHER("Other", "Other reason (please specify)");
        
        private final String displayName;
        private final String description;
        
        ReportReason(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }
        
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
    }
    
    /**
     * Automatically moderate a review using content filters
     */
    public ModerationResult autoModerateReview(Review review) {
        ModerationResult result = new ModerationResult();
        result.setReviewId(review.getReviewId());
        result.setAutoModerated(true);
        
        // Check for personal information
        if (containsPersonalInformation(review)) {
            result.setAction(ModerationAction.HIDE);
            result.setReason("Contains personal information");
            result.setScore(100); // High confidence
            return result;
        }
        
        // Check for spam patterns
        if (containsSpamPatterns(review)) {
            result.setAction(ModerationAction.FLAG_FOR_REVIEW);
            result.setReason("Potential spam content detected");
            result.setScore(75);
            return result;
        }
        
        // Check review length and quality
        if (isLowQualityReview(review)) {
            result.setAction(ModerationAction.FLAG_FOR_REVIEW);
            result.setReason("Low quality content");
            result.setScore(60);
            return result;
        }
        
        // Check for extreme ratings with short comments
        if (hasExtremeRatingWithPoorComment(review)) {
            result.setAction(ModerationAction.FLAG_FOR_REVIEW);
            result.setReason("Extreme rating with minimal explanation");
            result.setScore(70);
            return result;
        }
        
        // Default: approve
        result.setAction(ModerationAction.APPROVE);
        result.setReason("Passed automated checks");
        result.setScore(10);
        
        return result;
    }
    
    /**
     * Report a review for manual moderation
     */
    public void reportReview(Integer reviewId, Integer reporterId, ReportReason reason, String additionalInfo) {
        Review review = reviewDAO.findById(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("Review not found");
        }
        
        User reporter = userDAO.findById(reporterId);
        if (reporter == null) {
            throw new IllegalArgumentException("Reporter not found");
        }
        
        // Flag the review
        String reportReason = reason.getDisplayName();
        if (additionalInfo != null && !additionalInfo.trim().isEmpty()) {
            reportReason += ": " + additionalInfo;
        }
        
        review.flagForModeration(reportReason);
        reviewDAO.update(review);
        
        // Log the report
        logModerationAction(reviewId, reporterId, "REPORTED", reportReason);
        
        // Send notification to moderators
        notifyModerators(review, reason, reporter);
    }
    
    /**
     * Manually moderate a review
     */
    public void moderateReview(Integer reviewId, Integer moderatorId, ModerationAction action, String reason) {
        Review review = reviewDAO.findById(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("Review not found");
        }
        
        User moderator = userDAO.findById(moderatorId);
        if (moderator == null) {
            throw new IllegalArgumentException("Moderator not found");
        }
        
        // Apply moderation action
        switch (action) {
            case APPROVE:
                review.setStatus(Review.ReviewStatus.ACTIVE);
                review.setFlagged(false);
                break;
                
            case HIDE:
                review.setStatus(Review.ReviewStatus.HIDDEN);
                break;
                
            case DELETE:
                review.setStatus(Review.ReviewStatus.DELETED);
                break;
                
            case FLAG_FOR_REVIEW:
                review.setStatus(Review.ReviewStatus.REPORTED);
                review.setFlagged(true);
                break;
                
            case REQUEST_EDIT:
                review.setStatus(Review.ReviewStatus.PENDING);
                // Could send email to reviewer asking for edits
                break;
        }
        
        // Update moderation fields
        review.setModeratedBy(moderatorId);
        review.setModeratedAt(new Timestamp(System.currentTimeMillis()));
        review.setFlaggedReason(reason);
        
        reviewDAO.update(review);
        
        // Log the action
        logModerationAction(reviewId, moderatorId, action.name(), reason);
        
        // Notify the reviewer if appropriate
        notifyReviewer(review, action, reason);
    }
    
    /**
     * Get reviews pending moderation
     */
    public List<Review> getPendingReviews() {
        return reviewDAO.findPendingReviews();
    }
    
    /**
     * Get flagged reviews
     */
    public List<Review> getFlaggedReviews() {
        return reviewDAO.findFlaggedReviews();
    }
    
    /**
     * Get moderation statistics
     */
    public Map<String, Object> getModerationStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Review> pendingReviews = getPendingReviews();
        List<Review> flaggedReviews = getFlaggedReviews();
        
        stats.put("pendingCount", pendingReviews.size());
        stats.put("flaggedCount", flaggedReviews.size());
        stats.put("totalRequiringAttention", pendingReviews.size() + flaggedReviews.size());
        
        // Calculate approval rates (simplified)
        stats.put("approvalRate", 85.0); // Would calculate from actual data
        stats.put("automoderationRate", 70.0); // Percentage handled automatically
        
        return stats;
    }
    
    // Private helper methods
    
    private boolean containsPersonalInformation(Review review) {
        String content = getReviewContent(review);
        
        for (Pattern pattern : PERSONAL_INFO_PATTERNS) {
            if (pattern.matcher(content).find()) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean containsSpamPatterns(Review review) {
        String content = getReviewContent(review);
        
        // Check for repeated characters
        if (content.matches(".*([a-zA-Z])\\1{4,}.*")) {
            return true;
        }
        
        // Check for excessive capitalization
        long upperCaseCount = content.chars().filter(Character::isUpperCase).count();
        if (content.length() > 20 && upperCaseCount > content.length() * 0.7) {
            return true;
        }
        
        // Check profanity patterns
        for (Pattern pattern : PROFANITY_PATTERNS) {
            if (pattern.matcher(content).find()) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isLowQualityReview(Review review) {
        String comment = review.getComment();
        
        // Very short reviews
        if (comment != null && comment.trim().length() < 10) {
            return true;
        }
        
        // All caps
        if (comment != null && comment.equals(comment.toUpperCase()) && comment.length() > 5) {
            return true;
        }
        
        // No meaningful content
        if (comment != null && comment.trim().matches("^[.!?\\s]*$")) {
            return true;
        }
        
        return false;
    }
    
    private boolean hasExtremeRatingWithPoorComment(Review review) {
        if (review.getRating() == null) return false;
        
        double rating = review.getRating().doubleValue();
        String comment = review.getComment();
        
        // 1-star or 5-star ratings with very short comments
        if ((rating <= 1.0 || rating >= 5.0) && 
            (comment == null || comment.trim().length() < 20)) {
            return true;
        }
        
        return false;
    }
    
    private String getReviewContent(Review review) {
        StringBuilder content = new StringBuilder();
        
        if (review.getTitle() != null) {
            content.append(review.getTitle()).append(" ");
        }
        
        if (review.getComment() != null) {
            content.append(review.getComment());
        }
        
        return content.toString();
    }
    
    private void logModerationAction(Integer reviewId, Integer userId, String action, String reason) {
        // In production, this would log to a moderation audit table
        System.out.printf("[MODERATION] Review %d - Action: %s by User %d - Reason: %s%n", 
                         reviewId, action, userId, reason);
    }
    
    private void notifyModerators(Review review, ReportReason reason, User reporter) {
        // In production, this would send notifications to moderators
        System.out.printf("[MODERATION ALERT] Review %d reported by %s for: %s%n", 
                         review.getReviewId(), reporter.getUsername(), reason.getDisplayName());
    }
    
    private void notifyReviewer(Review review, ModerationAction action, String reason) {
        // In production, this would send notification to the reviewer
        if (action != ModerationAction.APPROVE) {
            System.out.printf("[REVIEWER NOTIFICATION] Review %d %s - Reason: %s%n", 
                             review.getReviewId(), action.getDisplayName(), reason);
        }
    }
    
    /**
     * Moderation result data class
     */
    public static class ModerationResult {
        private Integer reviewId;
        private ModerationAction action;
        private String reason;
        private int score; // Confidence score 0-100
        private boolean autoModerated;
        
        // Getters and setters
        public Integer getReviewId() { return reviewId; }
        public void setReviewId(Integer reviewId) { this.reviewId = reviewId; }
        
        public ModerationAction getAction() { return action; }
        public void setAction(ModerationAction action) { this.action = action; }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        
        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
        
        public boolean isAutoModerated() { return autoModerated; }
        public void setAutoModerated(boolean autoModerated) { this.autoModerated = autoModerated; }
    }
}