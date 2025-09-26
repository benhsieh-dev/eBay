package dao;

import entity.ReviewHelpfulness;
import entity.Review;
import entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReviewHelpfulnessDAO {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    /**
     * Save a helpfulness vote
     */
    public ReviewHelpfulness save(ReviewHelpfulness helpfulness) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.save(helpfulness);
            transaction.commit();
            return helpfulness;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    /**
     * Update an existing helpfulness vote
     */
    public ReviewHelpfulness update(ReviewHelpfulness helpfulness) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(helpfulness);
            transaction.commit();
            return helpfulness;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    /**
     * Find helpfulness vote by ID
     */
    public ReviewHelpfulness findById(Integer helpfulnessId) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(ReviewHelpfulness.class, helpfulnessId);
    }
    
    /**
     * Find existing vote by user and review
     */
    public ReviewHelpfulness findByUserAndReview(Integer userId, Integer reviewId) {
        Session session = sessionFactory.getCurrentSession();
        Query<ReviewHelpfulness> query = session.createQuery(
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
        Session session = sessionFactory.getCurrentSession();
        Query<ReviewHelpfulness> query = session.createQuery(
            "FROM ReviewHelpfulness rh WHERE rh.review.reviewId = :reviewId ORDER BY rh.createdAt DESC", 
            ReviewHelpfulness.class);
        query.setParameter("reviewId", reviewId);
        return query.getResultList();
    }
    
    /**
     * Count helpful votes for a review
     */
    public Long countHelpfulVotes(Integer reviewId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(
            "SELECT COUNT(rh) FROM ReviewHelpfulness rh WHERE rh.review.reviewId = :reviewId AND rh.isHelpful = true", 
            Long.class);
        query.setParameter("reviewId", reviewId);
        return query.uniqueResult();
    }
    
    /**
     * Count not helpful votes for a review
     */
    public Long countNotHelpfulVotes(Integer reviewId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(
            "SELECT COUNT(rh) FROM ReviewHelpfulness rh WHERE rh.review.reviewId = :reviewId AND rh.isHelpful = false", 
            Long.class);
        query.setParameter("reviewId", reviewId);
        return query.uniqueResult();
    }
    
    /**
     * Get total votes for a review
     */
    public Long countTotalVotes(Integer reviewId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(
            "SELECT COUNT(rh) FROM ReviewHelpfulness rh WHERE rh.review.reviewId = :reviewId", 
            Long.class);
        query.setParameter("reviewId", reviewId);
        return query.uniqueResult();
    }
    
    /**
     * Delete helpfulness vote
     */
    public void delete(Integer helpfulnessId) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            ReviewHelpfulness helpfulness = session.get(ReviewHelpfulness.class, helpfulnessId);
            if (helpfulness != null) {
                session.delete(helpfulness);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    /**
     * Delete all votes for a review
     */
    public void deleteByReview(Integer reviewId) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            Query query = session.createQuery(
                "DELETE FROM ReviewHelpfulness rh WHERE rh.review.reviewId = :reviewId");
            query.setParameter("reviewId", reviewId);
            query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
}