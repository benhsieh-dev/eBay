package dao;

import entity.Review;
import entity.User;
import entity.Product;
import entity.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Repository
@Transactional
public class ReviewDAO {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Save a new review
     */
    public Review save(Review review) {
        if (review.getReviewId() == null) {
            entityManager.persist(review);
        } else {
            entityManager.merge(review);
        }
        return review;
    }
    
    /**
     * Update an existing review
     */
    public Review update(Review review) {
        review.updateTimestamp();
        return entityManager.merge(review);
    }
    
    /**
     * Find review by ID
     */
    public Review findById(Integer reviewId) {
        return entityManager.find(Review.class, reviewId);
    }
    
    /**
     * Find all reviews for a specific user (as reviewee)
     */
    public List<Review> findByReviewee(Integer revieweeId) {
        TypedQuery<Review> query = entityManager.createQuery(
            "FROM Review r WHERE r.reviewee.userId = :revieweeId AND r.status = :status ORDER BY r.createdAt DESC", 
            Review.class);
        query.setParameter("revieweeId", revieweeId);
        query.setParameter("status", Review.ReviewStatus.ACTIVE);
        return query.getResultList();
    }
    
    /**
     * Find all reviews written by a specific user (as reviewer)
     */
    public List<Review> findByReviewer(Integer reviewerId) {
        TypedQuery<Review> query = entityManager.createQuery(
            "FROM Review r WHERE r.reviewer.userId = :reviewerId ORDER BY r.createdAt DESC", 
            Review.class);
        query.setParameter("reviewerId", reviewerId);
        return query.getResultList();
    }
    
    /**
     * Find reviews for a specific product
     */
    public List<Review> findByProduct(Integer productId) {
        TypedQuery<Review> query = entityManager.createQuery(
            "FROM Review r WHERE r.product.productId = :productId AND r.status = :status ORDER BY r.createdAt DESC", 
            Review.class);
        query.setParameter("productId", productId);
        query.setParameter("status", Review.ReviewStatus.ACTIVE);
        return query.getResultList();
    }
    
    /**
     * Find reviews for a specific order
     */
    public List<Review> findByOrder(Integer orderId) {
        TypedQuery<Review> query = entityManager.createQuery(
            "FROM Review r WHERE r.order.orderId = :orderId ORDER BY r.createdAt DESC", 
            Review.class);
        query.setParameter("orderId", orderId);
        return query.getResultList();
    }
    
    /**
     * Find seller reviews (reviews of sellers by buyers)
     */
    public List<Review> findSellerReviews(Integer sellerId) {
        TypedQuery<Review> query = entityManager.createQuery(
            "FROM Review r WHERE r.reviewee.userId = :sellerId AND r.reviewType = :reviewType AND r.status = :status ORDER BY r.createdAt DESC", 
            Review.class);
        query.setParameter("sellerId", sellerId);
        query.setParameter("reviewType", Review.ReviewType.SELLER_REVIEW);
        query.setParameter("status", Review.ReviewStatus.ACTIVE);
        return query.getResultList();
    }
    
    /**
     * Find buyer reviews (reviews of buyers by sellers)
     */
    public List<Review> findBuyerReviews(Integer buyerId) {
        TypedQuery<Review> query = entityManager.createQuery(
            "FROM Review r WHERE r.reviewee.userId = :buyerId AND r.reviewType = :reviewType AND r.status = :status ORDER BY r.createdAt DESC", 
            Review.class);
        query.setParameter("buyerId", buyerId);
        query.setParameter("reviewType", Review.ReviewType.BUYER_REVIEW);
        query.setParameter("status", Review.ReviewStatus.ACTIVE);
        return query.getResultList();
    }
    
    /**
     * Get average rating for a user as seller
     */
    public BigDecimal getAverageSellerRating(Integer sellerId) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT AVG(r.rating) FROM Review r WHERE r.reviewee.userId = :sellerId AND r.reviewType = :reviewType AND r.status = :status", 
            BigDecimal.class);
        query.setParameter("sellerId", sellerId);
        query.setParameter("reviewType", Review.ReviewType.SELLER_REVIEW);
        query.setParameter("status", Review.ReviewStatus.ACTIVE);
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
    
    /**
     * Get average rating for a user as buyer
     */
    public BigDecimal getAverageBuyerRating(Integer buyerId) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT AVG(r.rating) FROM Review r WHERE r.reviewee.userId = :buyerId AND r.reviewType = :reviewType AND r.status = :status", 
            BigDecimal.class);
        query.setParameter("buyerId", buyerId);
        query.setParameter("reviewType", Review.ReviewType.BUYER_REVIEW);
        query.setParameter("status", Review.ReviewStatus.ACTIVE);
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
    
    /**
     * Get average rating for a product
     */
    public BigDecimal getAverageProductRating(Integer productId) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT AVG(r.rating) FROM Review r WHERE r.product.productId = :productId AND r.status = :status", 
            BigDecimal.class);
        query.setParameter("productId", productId);
        query.setParameter("status", Review.ReviewStatus.ACTIVE);
        BigDecimal result = query.getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }
    
    /**
     * Count reviews for a user as seller
     */
    public Long countSellerReviews(Integer sellerId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM Review r WHERE r.reviewee.userId = :sellerId AND r.reviewType = :reviewType AND r.status = :status", 
            Long.class);
        query.setParameter("sellerId", sellerId);
        query.setParameter("reviewType", Review.ReviewType.SELLER_REVIEW);
        query.setParameter("status", Review.ReviewStatus.ACTIVE);
        return query.getSingleResult();
    }
    
    /**
     * Count reviews for a product
     */
    public Long countProductReviews(Integer productId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(r) FROM Review r WHERE r.product.productId = :productId AND r.status = :status", 
            Long.class);
        query.setParameter("productId", productId);
        query.setParameter("status", Review.ReviewStatus.ACTIVE);
        return query.getSingleResult();
    }
    
    /**
     * Find reviews by rating range
     */
    public List<Review> findByRatingRange(Integer userId, BigDecimal minRating, BigDecimal maxRating) {
        TypedQuery<Review> query = entityManager.createQuery(
            "FROM Review r WHERE r.reviewee.userId = :userId AND r.rating BETWEEN :minRating AND :maxRating AND r.status = :status ORDER BY r.createdAt DESC", 
            Review.class);
        query.setParameter("userId", userId);
        query.setParameter("minRating", minRating);
        query.setParameter("maxRating", maxRating);
        query.setParameter("status", Review.ReviewStatus.ACTIVE);
        return query.getResultList();
    }
    
    /**
     * Find recent reviews (within specified days)
     */
    public List<Review> findRecentReviews(Integer userId, int days) {
        Timestamp cutoffDate = new Timestamp(System.currentTimeMillis() - (days * 24L * 60L * 60L * 1000L));
        
        TypedQuery<Review> query = entityManager.createQuery(
            "FROM Review r WHERE r.reviewee.userId = :userId AND r.createdAt >= :cutoffDate AND r.status = :status ORDER BY r.createdAt DESC", 
            Review.class);
        query.setParameter("userId", userId);
        query.setParameter("cutoffDate", cutoffDate);
        query.setParameter("status", Review.ReviewStatus.ACTIVE);
        return query.getResultList();
    }
    
    /**
     * Find flagged reviews for moderation
     */
    public List<Review> findFlaggedReviews() {
        TypedQuery<Review> query = entityManager.createQuery(
            "FROM Review r WHERE r.flagged = true OR r.status = :reportedStatus ORDER BY r.createdAt DESC", 
            Review.class);
        query.setParameter("reportedStatus", Review.ReviewStatus.REPORTED);
        return query.getResultList();
    }
    
    /**
     * Find pending reviews for moderation
     */
    public List<Review> findPendingReviews() {
        TypedQuery<Review> query = entityManager.createQuery(
            "FROM Review r WHERE r.status = :pendingStatus ORDER BY r.createdAt ASC", 
            Review.class);
        query.setParameter("pendingStatus", Review.ReviewStatus.PENDING);
        return query.getResultList();
    }
    
    /**
     * Check if user has already reviewed a specific order
     */
    public Review findExistingReview(Integer reviewerId, Integer orderId, Review.ReviewType reviewType) {
        TypedQuery<Review> query = entityManager.createQuery(
            "FROM Review r WHERE r.reviewer.userId = :reviewerId AND r.order.orderId = :orderId AND r.reviewType = :reviewType", 
            Review.class);
        query.setParameter("reviewerId", reviewerId);
        query.setParameter("orderId", orderId);
        query.setParameter("reviewType", reviewType);
        query.setMaxResults(1);
        List<Review> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    /**
     * Search reviews by text content
     */
    public List<Review> searchReviews(String searchText, Integer userId) {
        TypedQuery<Review> query = entityManager.createQuery(
            "FROM Review r WHERE (r.title LIKE :searchText OR r.comment LIKE :searchText) " +
            "AND (:userId IS NULL OR r.reviewee.userId = :userId) " +
            "AND r.status = :status ORDER BY r.createdAt DESC", 
            Review.class);
        query.setParameter("searchText", "%" + searchText + "%");
        query.setParameter("userId", userId);
        query.setParameter("status", Review.ReviewStatus.ACTIVE);
        return query.getResultList();
    }
    
    /**
     * Get paginated reviews
     */
    public List<Review> findPaginated(Integer userId, Review.ReviewType reviewType, int page, int pageSize) {
        TypedQuery<Review> query = entityManager.createQuery(
            "FROM Review r WHERE r.reviewee.userId = :userId " +
            "AND (:reviewType IS NULL OR r.reviewType = :reviewType) " +
            "AND r.status = :status ORDER BY r.createdAt DESC", 
            Review.class);
        query.setParameter("userId", userId);
        query.setParameter("reviewType", reviewType);
        query.setParameter("status", Review.ReviewStatus.ACTIVE);
        query.setFirstResult(page * pageSize);
        query.setMaxResults(pageSize);
        return query.getResultList();
    }
    
    /**
     * Delete review (soft delete by changing status)
     */
    public void softDelete(Integer reviewId) {
        Review review = entityManager.find(Review.class, reviewId);
        if (review != null) {
            review.setStatus(Review.ReviewStatus.DELETED);
            review.updateTimestamp();
            entityManager.merge(review);
        }
    }
}