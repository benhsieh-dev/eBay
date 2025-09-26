package dao;

import entity.ReviewHelpfulness;
import entity.Review;
import entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ReviewHelpfulnessDAO {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Save a helpfulness vote
     */
    public ReviewHelpfulness save(ReviewHelpfulness helpfulness) {
        if (helpfulness.getHelpfulnessId() == null) {
            entityManager.persist(helpfulness);
        } else {
            entityManager.merge(helpfulness);
        }
        return helpfulness;
    }
    
    /**
     * Update an existing helpfulness vote
     */
    public ReviewHelpfulness update(ReviewHelpfulness helpfulness) {
        return entityManager.merge(helpfulness);
    }
    
    /**
     * Find helpfulness vote by ID
     */
    public ReviewHelpfulness findById(Integer helpfulnessId) {
        return entityManager.find(ReviewHelpfulness.class, helpfulnessId);
    }
    
    /**
     * Find existing vote by user and review
     */
    public ReviewHelpfulness findByUserAndReview(Integer userId, Integer reviewId) {
        TypedQuery<ReviewHelpfulness> query = entityManager.createQuery(
            "FROM ReviewHelpfulness rh WHERE rh.user.userId = :userId AND rh.review.reviewId = :reviewId", 
            ReviewHelpfulness.class);
        query.setParameter("userId", userId);
        query.setParameter("reviewId", reviewId);
        query.setMaxResults(1);
        List<ReviewHelpfulness> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    /**
     * Find all helpfulness votes for a review
     */
    public List<ReviewHelpfulness> findByReview(Integer reviewId) {
        TypedQuery<ReviewHelpfulness> query = entityManager.createQuery(
            "FROM ReviewHelpfulness rh WHERE rh.review.reviewId = :reviewId ORDER BY rh.createdAt DESC", 
            ReviewHelpfulness.class);
        query.setParameter("reviewId", reviewId);
        return query.getResultList();
    }
    
    /**
     * Count helpful votes for a review
     */
    public Long countHelpfulVotes(Integer reviewId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(rh) FROM ReviewHelpfulness rh WHERE rh.review.reviewId = :reviewId AND rh.isHelpful = true", 
            Long.class);
        query.setParameter("reviewId", reviewId);
        return query.getSingleResult();
    }
    
    /**
     * Count not helpful votes for a review
     */
    public Long countNotHelpfulVotes(Integer reviewId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(rh) FROM ReviewHelpfulness rh WHERE rh.review.reviewId = :reviewId AND rh.isHelpful = false", 
            Long.class);
        query.setParameter("reviewId", reviewId);
        return query.getSingleResult();
    }
    
    /**
     * Get total votes for a review
     */
    public Long countTotalVotes(Integer reviewId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(rh) FROM ReviewHelpfulness rh WHERE rh.review.reviewId = :reviewId", 
            Long.class);
        query.setParameter("reviewId", reviewId);
        return query.getSingleResult();
    }
    
    /**
     * Delete helpfulness vote
     */
    public void delete(Integer helpfulnessId) {
        ReviewHelpfulness helpfulness = entityManager.find(ReviewHelpfulness.class, helpfulnessId);
        if (helpfulness != null) {
            if (entityManager.contains(helpfulness)) {
                entityManager.remove(helpfulness);
            } else {
                entityManager.remove(entityManager.merge(helpfulness));
            }
        }
    }
    
    /**
     * Delete all votes for a review
     */
    public void deleteByReview(Integer reviewId) {
        entityManager.createQuery(
            "DELETE FROM ReviewHelpfulness rh WHERE rh.review.reviewId = :reviewId")
            .setParameter("reviewId", reviewId)
            .executeUpdate();
    }
}