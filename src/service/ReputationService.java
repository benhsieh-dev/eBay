package service;

import dao.ReviewDAO;
import dao.OrderDAO;
import dao.UserDAO;
import entity.Review;
import entity.Order;
import entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for calculating and managing user reputation scores
 * Reputation is based on reviews, transaction history, and behavioral metrics
 */
@Service
public class ReputationService {
    
    @Autowired
    private ReviewDAO reviewDAO;
    
    @Autowired
    private OrderDAO orderDAO;
    
    @Autowired
    private UserDAO userDAO;
    
    // Reputation calculation weights
    private static final double RATING_WEIGHT = 0.4;          // 40% from average rating
    private static final double VOLUME_WEIGHT = 0.3;          // 30% from transaction volume
    private static final double RECENCY_WEIGHT = 0.2;         // 20% from recent activity
    private static final double QUALITY_WEIGHT = 0.1;         // 10% from review quality metrics
    
    // Rating thresholds for reputation levels
    private static final BigDecimal EXCELLENT_THRESHOLD = new BigDecimal("4.5");
    private static final BigDecimal GOOD_THRESHOLD = new BigDecimal("4.0");
    private static final BigDecimal FAIR_THRESHOLD = new BigDecimal("3.0");
    
    // Volume thresholds
    private static final int HIGH_VOLUME_THRESHOLD = 100;
    private static final int MEDIUM_VOLUME_THRESHOLD = 25;
    
    public enum ReputationLevel {
        TOP_RATED("Top Rated", "⭐⭐⭐⭐⭐", "#FFD700"),
        EXCELLENT("Excellent", "⭐⭐⭐⭐", "#32CD32"),
        GOOD("Good", "⭐⭐⭐", "#FFA500"),
        FAIR("Fair", "⭐⭐", "#FF6347"),
        POOR("Poor", "⭐", "#DC143C"),
        NEW_USER("New User", "👤", "#808080");
        
        private final String displayName;
        private final String badge;
        private final String color;
        
        ReputationLevel(String displayName, String badge, String color) {
            this.displayName = displayName;
            this.badge = badge;
            this.color = color;
        }
        
        public String getDisplayName() { return displayName; }
        public String getBadge() { return badge; }
        public String getColor() { return color; }
    }
    
    /**
     * Calculate comprehensive reputation score for a user
     */
    public ReputationScore calculateReputationScore(Integer userId) {
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        // Get seller reviews and stats
        List<Review> sellerReviews = reviewDAO.findSellerReviews(userId);
        BigDecimal sellerRating = reviewDAO.getAverageSellerRating(userId);
        Long sellerReviewCount = reviewDAO.countSellerReviews(userId);
        
        // Get recent activity
        List<Review> recentReviews = reviewDAO.findRecentReviews(userId, 90); // Last 90 days
        
        // Calculate component scores
        double ratingScore = calculateRatingScore(sellerRating, sellerReviewCount);
        double volumeScore = calculateVolumeScore(sellerReviewCount, user.getTotalSalesCount());
        double recencyScore = calculateRecencyScore(recentReviews, sellerReviewCount);
        double qualityScore = calculateQualityScore(sellerReviews);
        
        // Calculate weighted overall score
        double overallScore = (ratingScore * RATING_WEIGHT) +
                            (volumeScore * VOLUME_WEIGHT) +
                            (recencyScore * RECENCY_WEIGHT) +
                            (qualityScore * QUALITY_WEIGHT);
        
        // Determine reputation level
        ReputationLevel level = determineReputationLevel(sellerRating, sellerReviewCount, overallScore);
        
        return new ReputationScore(
            userId,
            BigDecimal.valueOf(overallScore).setScale(2, RoundingMode.HALF_UP),
            level,
            sellerRating,
            sellerReviewCount,
            ratingScore,
            volumeScore,
            recencyScore,
            qualityScore
        );
    }
    
    /**
     * Calculate rating component score (0-100)
     */
    private double calculateRatingScore(BigDecimal avgRating, Long reviewCount) {
        if (reviewCount == 0) return 0.0;
        
        // Base score from rating (0-5 -> 0-100)
        double baseScore = avgRating.doubleValue() * 20;
        
        // Apply confidence factor based on review count
        double confidenceFactor = Math.min(1.0, reviewCount.doubleValue() / 50.0); // Full confidence at 50+ reviews
        
        return baseScore * confidenceFactor;
    }
    
    /**
     * Calculate volume component score (0-100)
     */
    private double calculateVolumeScore(Long reviewCount, Integer salesCount) {
        if (reviewCount == 0) return 0.0;
        
        // Score based on review count (logarithmic scale)
        double reviewScore = Math.min(100, 20 * Math.log10(reviewCount + 1));
        
        // Bonus for high sales volume
        double salesBonus = 0;
        if (salesCount != null && salesCount > 0) {
            salesBonus = Math.min(20, 5 * Math.log10(salesCount + 1));
        }
        
        return Math.min(100, reviewScore + salesBonus);
    }
    
    /**
     * Calculate recency component score (0-100)
     */
    private double calculateRecencyScore(List<Review> recentReviews, Long totalReviews) {
        if (totalReviews == 0) return 0.0;
        
        // Score based on percentage of recent activity
        double recentPercentage = recentReviews.size() / totalReviews.doubleValue();
        
        // Base score (50-100 based on recent activity)
        double baseScore = 50 + (recentPercentage * 50);
        
        // Bonus for consistent recent activity
        if (recentReviews.size() >= 5) {
            baseScore += 10; // Activity bonus
        }
        
        return Math.min(100, baseScore);
    }
    
    /**
     * Calculate quality component score based on review characteristics (0-100)
     */
    private double calculateQualityScore(List<Review> reviews) {
        if (reviews.isEmpty()) return 0.0;
        
        double score = 50.0; // Base score
        
        // Analyze review quality metrics
        int detailedReviews = 0;
        int responseCount = 0;
        int helpfulReviews = 0;
        
        for (Review review : reviews) {
            // Detailed reviews bonus
            if (review.getCommunicationRating() != null || 
                review.getShippingRating() != null ||
                review.getItemDescriptionRating() != null ||
                review.getResponseTimeRating() != null) {
                detailedReviews++;
            }
            
            // Response engagement bonus
            if (review.hasResponse()) {
                responseCount++;
            }
            
            // Helpfulness bonus
            if (review.getHelpfulnessPercentage() > 70) {
                helpfulReviews++;
            }
        }
        
        // Calculate bonuses
        double detailedBonus = (detailedReviews / (double) reviews.size()) * 20; // Up to 20 points
        double responseBonus = (responseCount / (double) reviews.size()) * 15;   // Up to 15 points
        double helpfulBonus = (helpfulReviews / (double) reviews.size()) * 15;   // Up to 15 points
        
        return Math.min(100, score + detailedBonus + responseBonus + helpfulBonus);
    }
    
    /**
     * Determine reputation level based on metrics
     */
    private ReputationLevel determineReputationLevel(BigDecimal avgRating, Long reviewCount, double overallScore) {
        // New users with few reviews
        if (reviewCount < 5) {
            return ReputationLevel.NEW_USER;
        }
        
        // High-volume, high-rating sellers
        if (avgRating.compareTo(EXCELLENT_THRESHOLD) >= 0 && reviewCount >= HIGH_VOLUME_THRESHOLD && overallScore >= 85) {
            return ReputationLevel.TOP_RATED;
        }
        
        // Excellent rating with good volume
        if (avgRating.compareTo(EXCELLENT_THRESHOLD) >= 0 && overallScore >= 75) {
            return ReputationLevel.EXCELLENT;
        }
        
        // Good rating
        if (avgRating.compareTo(GOOD_THRESHOLD) >= 0 && overallScore >= 60) {
            return ReputationLevel.GOOD;
        }
        
        // Fair rating
        if (avgRating.compareTo(FAIR_THRESHOLD) >= 0 && overallScore >= 40) {
            return ReputationLevel.FAIR;
        }
        
        // Poor rating
        return ReputationLevel.POOR;
    }
    
    /**
     * Get reputation comparison with peer users
     */
    public Map<String, Object> getReputationComparison(Integer userId) {
        ReputationScore userScore = calculateReputationScore(userId);
        
        Map<String, Object> comparison = new HashMap<>();
        comparison.put("userScore", userScore);
        
        // Calculate percentile ranking (simplified - in production would query database)
        double percentile = calculatePercentileRanking(userScore.getOverallScore().doubleValue());
        comparison.put("percentile", percentile);
        
        // Performance insights
        comparison.put("insights", generateInsights(userScore));
        
        return comparison;
    }
    
    /**
     * Generate improvement insights for user
     */
    public List<String> generateImprovementSuggestions(Integer userId) {
        ReputationScore score = calculateReputationScore(userId);
        List<String> suggestions = new java.util.ArrayList<>();
        
        // Rating improvement suggestions
        if (score.getRatingScore() < 70) {
            suggestions.add("Focus on improving customer satisfaction to increase your average rating");
            suggestions.add("Consider offering faster shipping or better communication");
        }
        
        // Volume suggestions
        if (score.getVolumeScore() < 50) {
            suggestions.add("Increase your sales volume to build reputation faster");
            suggestions.add("List more items to attract more buyers");
        }
        
        // Recency suggestions
        if (score.getRecencyScore() < 60) {
            suggestions.add("Stay active - recent activity boosts your reputation");
            suggestions.add("Respond promptly to customer inquiries");
        }
        
        // Quality suggestions
        if (score.getQualityScore() < 70) {
            suggestions.add("Encourage customers to leave detailed reviews");
            suggestions.add("Respond to customer reviews to show engagement");
        }
        
        return suggestions;
    }
    
    /**
     * Update user reputation (called after new reviews)
     */
    public void updateUserReputation(Integer userId) {
        ReputationScore score = calculateReputationScore(userId);
        
        User user = userDAO.findById(userId);
        if (user != null) {
            // Update user's seller rating with the calculated score
            user.setSellerRating(score.getAverageRating());
            userDAO.update(user);
            
            // In production, you might want to store the full reputation score in a separate table
            System.out.println("Updated reputation for user " + userId + ": " + score.getLevel().getDisplayName());
        }
    }
    
    // Helper methods
    
    private double calculatePercentileRanking(double score) {
        // Simplified percentile calculation
        // In production, this would query the database for actual percentiles
        if (score >= 85) return 95.0;
        if (score >= 75) return 80.0;
        if (score >= 60) return 65.0;
        if (score >= 40) return 40.0;
        return 20.0;
    }
    
    private List<String> generateInsights(ReputationScore score) {
        List<String> insights = new java.util.ArrayList<>();
        
        // Strength analysis
        double maxComponent = Math.max(Math.max(score.getRatingScore(), score.getVolumeScore()),
                                     Math.max(score.getRecencyScore(), score.getQualityScore()));
        
        if (maxComponent == score.getRatingScore()) {
            insights.add("Your strongest area is customer satisfaction with excellent ratings");
        } else if (maxComponent == score.getVolumeScore()) {
            insights.add("Your sales volume demonstrates strong business activity");
        } else if (maxComponent == score.getRecencyScore()) {
            insights.add("Your recent activity shows consistent engagement");
        } else {
            insights.add("You excel at providing quality customer experiences");
        }
        
        // Improvement area
        double minComponent = Math.min(Math.min(score.getRatingScore(), score.getVolumeScore()),
                                     Math.min(score.getRecencyScore(), score.getQualityScore()));
        
        if (minComponent == score.getRatingScore()) {
            insights.add("Focus on improving customer satisfaction to boost your reputation");
        } else if (minComponent == score.getVolumeScore()) {
            insights.add("Increasing your sales volume could significantly improve your reputation");
        } else if (minComponent == score.getRecencyScore()) {
            insights.add("Staying more active recently would help maintain your reputation");
        } else {
            insights.add("Encouraging more detailed customer feedback could enhance your reputation");
        }
        
        return insights;
    }
    
    /**
     * Reputation Score data class
     */
    public static class ReputationScore {
        private final Integer userId;
        private final BigDecimal overallScore;
        private final ReputationLevel level;
        private final BigDecimal averageRating;
        private final Long reviewCount;
        private final double ratingScore;
        private final double volumeScore;
        private final double recencyScore;
        private final double qualityScore;
        
        public ReputationScore(Integer userId, BigDecimal overallScore, ReputationLevel level,
                             BigDecimal averageRating, Long reviewCount, double ratingScore,
                             double volumeScore, double recencyScore, double qualityScore) {
            this.userId = userId;
            this.overallScore = overallScore;
            this.level = level;
            this.averageRating = averageRating;
            this.reviewCount = reviewCount;
            this.ratingScore = ratingScore;
            this.volumeScore = volumeScore;
            this.recencyScore = recencyScore;
            this.qualityScore = qualityScore;
        }
        
        // Getters
        public Integer getUserId() { return userId; }
        public BigDecimal getOverallScore() { return overallScore; }
        public ReputationLevel getLevel() { return level; }
        public BigDecimal getAverageRating() { return averageRating; }
        public Long getReviewCount() { return reviewCount; }
        public double getRatingScore() { return ratingScore; }
        public double getVolumeScore() { return volumeScore; }
        public double getRecencyScore() { return recencyScore; }
        public double getQualityScore() { return qualityScore; }
    }
}