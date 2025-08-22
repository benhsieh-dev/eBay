package service;

import dao.ReviewDAO;
import dao.ReviewHelpfulnessDAO;
import dao.ReviewImageDAO;
import dao.UserDAO;
import dao.OrderDAO;
import dao.ProductDAO;
import entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReviewService {
    
    @Autowired
    private ReviewDAO reviewDAO;
    
    @Autowired
    private ReviewHelpfulnessDAO helpfulnessDAO;
    
    @Autowired
    private ReviewImageDAO reviewImageDAO;
    
    @Autowired
    private UserDAO userDAO;
    
    @Autowired
    private OrderDAO orderDAO;
    
    @Autowired
    private ProductDAO productDAO;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Create a new review
     */
    public Review createReview(Integer reviewerId, Integer revieweeId, Integer orderId, 
                              Integer productId, Review.ReviewType reviewType, 
                              BigDecimal rating, String title, String comment) throws Exception {
        
        // Validate users exist
        User reviewer = userDAO.findById(reviewerId);
        User reviewee = userDAO.findById(revieweeId);
        if (reviewer == null || reviewee == null) {
            throw new IllegalArgumentException("Invalid reviewer or reviewee");
        }
        
        // Validate order exists
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Invalid order");
        }
        
        // Check if review already exists for this order and reviewer
        Review existingReview = reviewDAO.findExistingReview(reviewerId, orderId, reviewType);
        if (existingReview != null) {
            throw new IllegalArgumentException("Review already exists for this order");
        }
        
        // Validate business rules
        validateReviewPermissions(reviewer, reviewee, order, reviewType);
        
        // Create review
        Review review = new Review(reviewer, reviewee, order, reviewType, rating);
        review.setTitle(title);
        review.setComment(comment);
        
        if (productId != null) {
            Product product = productDAO.findById(productId);
            if (product != null) {
                review.setProduct(product);
            }
        }
        
        // Set as verified purchase if applicable
        if (isVerifiedPurchase(reviewer, order)) {
            review.setVerifiedPurchase(true);
        }
        
        review = reviewDAO.save(review);
        
        // Update user ratings
        updateUserRatings(revieweeId, reviewType);
        
        // Send notification email
        sendReviewNotification(review);
        
        return review;
    }
    
    /**
     * Create detailed review with breakdown ratings
     */
    public Review createDetailedReview(Integer reviewerId, Integer revieweeId, Integer orderId,
                                     Integer productId, Review.ReviewType reviewType,
                                     BigDecimal overallRating, String title, String comment,
                                     BigDecimal communicationRating, BigDecimal shippingRating,
                                     BigDecimal itemDescriptionRating, BigDecimal responseTimeRating) throws Exception {
        
        Review review = createReview(reviewerId, revieweeId, orderId, productId, reviewType, 
                                   overallRating, title, comment);
        
        // Add detailed ratings
        review.setCommunicationRating(communicationRating);
        review.setShippingRating(shippingRating);
        review.setItemDescriptionRating(itemDescriptionRating);
        review.setResponseTimeRating(responseTimeRating);
        
        return reviewDAO.update(review);
    }
    
    /**
     * Add response to a review
     */
    public Review addResponse(Integer reviewId, Integer userId, String response) throws Exception {
        Review review = reviewDAO.findById(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("Review not found");
        }
        
        // Check if user is the reviewee (person being reviewed)
        if (!review.getReviewee().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Only the reviewee can respond to this review");
        }
        
        review.addResponse(response);
        return reviewDAO.update(review);
    }
    
    /**
     * Edit an existing review
     */
    public Review editReview(Integer reviewId, Integer userId, String title, String comment,
                           BigDecimal rating) throws Exception {
        Review review = reviewDAO.findById(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("Review not found");
        }
        
        // Check if user is the reviewer
        if (!review.getReviewer().getUserId().equals(userId)) {
            throw new IllegalArgumentException("Only the reviewer can edit this review");
        }
        
        // Check if review can still be modified (within time limit)
        if (!review.canBeModified()) {
            throw new IllegalArgumentException("Review can no longer be modified");
        }
        
        review.setTitle(title);
        review.setComment(comment);
        review.setRating(rating);
        
        review = reviewDAO.update(review);
        
        // Update user ratings since rating may have changed
        updateUserRatings(review.getReviewee().getUserId(), review.getReviewType());
        
        return review;
    }
    
    /**
     * Vote on review helpfulness
     */
    public ReviewHelpfulness voteHelpfulness(Integer reviewId, Integer userId, boolean isHelpful) throws Exception {
        Review review = reviewDAO.findById(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("Review not found");
        }
        
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        // Check if user has already voted
        ReviewHelpfulness existingVote = helpfulnessDAO.findByUserAndReview(userId, reviewId);
        if (existingVote != null) {
            // Update existing vote if different
            if (!existingVote.getIsHelpful().equals(isHelpful)) {
                // Update counters in review
                if (existingVote.getIsHelpful()) {
                    review.setHelpfulCount(review.getHelpfulCount() - 1);
                    review.setNotHelpfulCount(review.getNotHelpfulCount() + 1);
                } else {
                    review.setNotHelpfulCount(review.getNotHelpfulCount() - 1);
                    review.setHelpfulCount(review.getHelpfulCount() + 1);
                }
                
                existingVote.setIsHelpful(isHelpful);
                reviewDAO.update(review);
                return helpfulnessDAO.update(existingVote);
            }
            return existingVote; // No change needed
        }
        
        // Create new vote
        ReviewHelpfulness vote = new ReviewHelpfulness(review, user, isHelpful);
        vote = helpfulnessDAO.save(vote);
        
        // Update counters in review
        if (isHelpful) {
            review.incrementHelpfulCount();
        } else {
            review.incrementNotHelpfulCount();
        }
        reviewDAO.update(review);
        
        return vote;
    }
    
    /**
     * Flag review for moderation
     */
    public void flagReview(Integer reviewId, Integer userId, String reason) throws Exception {
        Review review = reviewDAO.findById(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("Review not found");
        }
        
        review.flagForModeration(reason);
        reviewDAO.update(review);
        
        // Notify moderators (this would be implemented based on your notification system)
        notifyModerators(review, reason);
    }
    
    /**
     * Get reviews for a user (as reviewee)
     */
    public List<Review> getUserReviews(Integer userId, Review.ReviewType reviewType) {
        if (reviewType != null) {
            if (reviewType == Review.ReviewType.SELLER_REVIEW) {
                return reviewDAO.findSellerReviews(userId);
            } else if (reviewType == Review.ReviewType.BUYER_REVIEW) {
                return reviewDAO.findBuyerReviews(userId);
            }
        }
        return reviewDAO.findByReviewee(userId);
    }
    
    /**
     * Get reviews written by a user (as reviewer)
     */
    public List<Review> getReviewsByUser(Integer userId) {
        return reviewDAO.findByReviewer(userId);
    }
    
    /**
     * Get reviews for a product
     */
    public List<Review> getProductReviews(Integer productId) {
        return reviewDAO.findByProduct(productId);
    }
    
    /**
     * Get review statistics for a user
     */
    public Map<String, Object> getUserReviewStats(Integer userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Seller statistics
        BigDecimal sellerRating = reviewDAO.getAverageSellerRating(userId);
        Long sellerReviewCount = reviewDAO.countSellerReviews(userId);
        
        // Buyer statistics  
        BigDecimal buyerRating = reviewDAO.getAverageBuyerRating(userId);
        
        // Recent reviews
        List<Review> recentReviews = reviewDAO.findRecentReviews(userId, 30);
        
        stats.put("sellerRating", sellerRating);
        stats.put("sellerReviewCount", sellerReviewCount);
        stats.put("buyerRating", buyerRating);
        stats.put("recentReviewCount", recentReviews.size());
        stats.put("averageRating", sellerRating); // Default to seller rating
        
        return stats;
    }
    
    /**
     * Get product rating statistics
     */
    public Map<String, Object> getProductReviewStats(Integer productId) {
        Map<String, Object> stats = new HashMap<>();
        
        BigDecimal averageRating = reviewDAO.getAverageProductRating(productId);
        Long reviewCount = reviewDAO.countProductReviews(productId);
        
        stats.put("averageRating", averageRating);
        stats.put("reviewCount", reviewCount);
        stats.put("productId", productId);
        
        return stats;
    }
    
    /**
     * Search reviews
     */
    public List<Review> searchReviews(String searchText, Integer userId) {
        return reviewDAO.searchReviews(searchText, userId);
    }
    
    /**
     * Get paginated reviews
     */
    public List<Review> getPaginatedReviews(Integer userId, Review.ReviewType reviewType, 
                                          int page, int pageSize) {
        return reviewDAO.findPaginated(userId, reviewType, page, pageSize);
    }
    
    // Private helper methods
    
    private void validateReviewPermissions(User reviewer, User reviewee, Order order, 
                                         Review.ReviewType reviewType) throws Exception {
        switch (reviewType) {
            case SELLER_REVIEW:
                // Buyer reviewing seller - reviewer must be the buyer
                if (!order.getBuyer().getUserId().equals(reviewer.getUserId())) {
                    throw new IllegalArgumentException("Only the buyer can review the seller");
                }
                break;
                
            case BUYER_REVIEW:
                // Seller reviewing buyer - reviewer must be the seller
                if (!order.getSeller().getUserId().equals(reviewer.getUserId())) {
                    throw new IllegalArgumentException("Only the seller can review the buyer");
                }
                break;
                
            case PRODUCT_REVIEW:
            case TRANSACTION_REVIEW:
                // Either party can leave these types of reviews
                if (!order.getBuyer().getUserId().equals(reviewer.getUserId()) && 
                    !order.getSeller().getUserId().equals(reviewer.getUserId())) {
                    throw new IllegalArgumentException("Only order participants can leave reviews");
                }
                break;
        }
    }
    
    private boolean isVerifiedPurchase(User reviewer, Order order) {
        return order.getBuyer().getUserId().equals(reviewer.getUserId()) && 
               order.getStatus() == Order.OrderStatus.COMPLETED;
    }
    
    private void updateUserRatings(Integer userId, Review.ReviewType reviewType) {
        User user = userDAO.findById(userId);
        if (user == null) return;
        
        if (reviewType == Review.ReviewType.SELLER_REVIEW) {
            BigDecimal newRating = reviewDAO.getAverageSellerRating(userId);
            user.setSellerRating(newRating);
            Long salesCount = reviewDAO.countSellerReviews(userId);
            user.setTotalSalesCount(salesCount.intValue());
        } else if (reviewType == Review.ReviewType.BUYER_REVIEW) {
            BigDecimal newRating = reviewDAO.getAverageBuyerRating(userId);
            user.setBuyerRating(newRating);
        }
        
        userDAO.update(user);
    }
    
    private void sendReviewNotification(Review review) {
        try {
            // Since EmailService doesn't have a generic sendEmail method,
            // we'll simulate the notification for now
            System.out.println("=== REVIEW NOTIFICATION ===");
            System.out.println("To: " + review.getReviewee().getEmail());
            System.out.println("Subject: New Review Received");
            System.out.printf("You have received a new %s review from %s. Rating: %.1f/5.0%n",
                review.getReviewType().toString().toLowerCase().replace("_", " "),
                review.getReviewer().getFullName(),
                review.getRating().doubleValue());
            System.out.println("==============================");
        } catch (Exception e) {
            // Log error but don't fail the review creation
            System.err.println("Failed to send review notification: " + e.getMessage());
        }
    }
    
    private void notifyModerators(Review review, String reason) {
        // This would integrate with your moderation system
        // For now, just log the flagged review
        System.out.println("Review flagged for moderation: " + review.getReviewId() + " - " + reason);
    }
}