package dao;

import entity.ReviewImage;
import entity.Review;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReviewImageDAO {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    /**
     * Save a review image
     */
    public ReviewImage save(ReviewImage reviewImage) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.save(reviewImage);
            transaction.commit();
            return reviewImage;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    /**
     * Update an existing review image
     */
    public ReviewImage update(ReviewImage reviewImage) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(reviewImage);
            transaction.commit();
            return reviewImage;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    /**
     * Find review image by ID
     */
    public ReviewImage findById(Integer imageId) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(ReviewImage.class, imageId);
    }
    
    /**
     * Find all images for a review
     */
    public List<ReviewImage> findByReview(Integer reviewId) {
        Session session = sessionFactory.getCurrentSession();
        Query<ReviewImage> query = session.createQuery(
            "FROM ReviewImage ri WHERE ri.review.reviewId = :reviewId ORDER BY ri.displayOrder ASC, ri.uploadedAt ASC", 
            ReviewImage.class);
        query.setParameter("reviewId", reviewId);
        return query.getResultList();
    }
    
    /**
     * Find primary image for a review
     */
    public ReviewImage findPrimaryByReview(Integer reviewId) {
        Session session = sessionFactory.getCurrentSession();
        Query<ReviewImage> query = session.createQuery(
            "FROM ReviewImage ri WHERE ri.review.reviewId = :reviewId AND ri.isPrimary = true", 
            ReviewImage.class);
        query.setParameter("reviewId", reviewId);
        query.setMaxResults(1);
        List<ReviewImage> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    /**
     * Count images for a review
     */
    public Long countByReview(Integer reviewId) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(
            "SELECT COUNT(ri) FROM ReviewImage ri WHERE ri.review.reviewId = :reviewId", 
            Long.class);
        query.setParameter("reviewId", reviewId);
        return query.uniqueResult();
    }
    
    /**
     * Set primary image for a review (clears other primary flags)
     */
    public void setPrimaryImage(Integer reviewId, Integer imageId) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            // Clear all primary flags for this review
            Query updateQuery = session.createQuery(
                "UPDATE ReviewImage ri SET ri.isPrimary = false WHERE ri.review.reviewId = :reviewId");
            updateQuery.setParameter("reviewId", reviewId);
            updateQuery.executeUpdate();
            
            // Set the specified image as primary
            if (imageId != null) {
                ReviewImage image = session.get(ReviewImage.class, imageId);
                if (image != null && image.getReview().getReviewId().equals(reviewId)) {
                    image.setIsPrimary(true);
                    session.update(image);
                }
            }
            
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    /**
     * Update display order for images
     */
    public void updateDisplayOrder(Integer reviewId, List<Integer> imageIds) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            for (int i = 0; i < imageIds.size(); i++) {
                Integer imageId = imageIds.get(i);
                Query updateQuery = session.createQuery(
                    "UPDATE ReviewImage ri SET ri.displayOrder = :order WHERE ri.imageId = :imageId AND ri.review.reviewId = :reviewId");
                updateQuery.setParameter("order", i);
                updateQuery.setParameter("imageId", imageId);
                updateQuery.setParameter("reviewId", reviewId);
                updateQuery.executeUpdate();
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    /**
     * Find images by original filename (for duplicate detection)
     */
    public List<ReviewImage> findByOriginalFilename(String originalFilename) {
        Session session = sessionFactory.getCurrentSession();
        Query<ReviewImage> query = session.createQuery(
            "FROM ReviewImage ri WHERE ri.originalFilename = :filename ORDER BY ri.uploadedAt DESC", 
            ReviewImage.class);
        query.setParameter("filename", originalFilename);
        return query.getResultList();
    }
    
    /**
     * Find images by file size range (for storage management)
     */
    public List<ReviewImage> findByFileSizeRange(Long minSize, Long maxSize) {
        Session session = sessionFactory.getCurrentSession();
        Query<ReviewImage> query = session.createQuery(
            "FROM ReviewImage ri WHERE ri.fileSize BETWEEN :minSize AND :maxSize ORDER BY ri.fileSize DESC", 
            ReviewImage.class);
        query.setParameter("minSize", minSize);
        query.setParameter("maxSize", maxSize);
        return query.getResultList();
    }
    
    /**
     * Get total storage used by review images
     */
    public Long getTotalStorageUsed() {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(
            "SELECT SUM(ri.fileSize) FROM ReviewImage ri WHERE ri.fileSize IS NOT NULL", 
            Long.class);
        Long result = query.uniqueResult();
        return result != null ? result : 0L;
    }
    
    /**
     * Delete review image
     */
    public void delete(Integer imageId) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            ReviewImage image = session.get(ReviewImage.class, imageId);
            if (image != null) {
                session.delete(image);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
    
    /**
     * Delete all images for a review
     */
    public void deleteByReview(Integer reviewId) {
        Session session = sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try {
            Query query = session.createQuery(
                "DELETE FROM ReviewImage ri WHERE ri.review.reviewId = :reviewId");
            query.setParameter("reviewId", reviewId);
            query.executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
}