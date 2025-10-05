package dao;

import entity.ReviewImage;
import entity.Review;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ReviewImageDAO {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Save a review image
     */
    public ReviewImage save(ReviewImage reviewImage) {
        if (reviewImage.getImageId() == null) {
            entityManager.persist(reviewImage);
        } else {
            entityManager.merge(reviewImage);
        }
        return reviewImage;
    }
    
    /**
     * Update an existing review image
     */
    public ReviewImage update(ReviewImage reviewImage) {
        return entityManager.merge(reviewImage);
    }
    
    /**
     * Find review image by ID
     */
    public ReviewImage findById(Integer imageId) {
        return entityManager.find(ReviewImage.class, imageId);
    }
    
    /**
     * Find all images for a review
     */
    public List<ReviewImage> findByReview(Integer reviewId) {
        TypedQuery<ReviewImage> query = entityManager.createQuery(
            "FROM ReviewImage ri WHERE ri.review.reviewId = :reviewId ORDER BY ri.displayOrder ASC, ri.uploadedAt ASC", 
            ReviewImage.class);
        query.setParameter("reviewId", reviewId);
        return query.getResultList();
    }
    
    /**
     * Find primary image for a review
     */
    public ReviewImage findPrimaryByReview(Integer reviewId) {
        TypedQuery<ReviewImage> query = entityManager.createQuery(
            "FROM ReviewImage ri WHERE ri.review.reviewId = :reviewId AND ri.isPrimary = true", 
            ReviewImage.class);
        query.setParameter("reviewId", reviewId);
        query.setMaxResults(1);
        List<ReviewImage> results = query.getResultList();
        return results.isEmpty() ? null : results.getFirst();
    }
    
    /**
     * Count images for a review
     */
    public Long countByReview(Integer reviewId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(ri) FROM ReviewImage ri WHERE ri.review.reviewId = :reviewId", 
            Long.class);
        query.setParameter("reviewId", reviewId);
        return query.getSingleResult();
    }
    
    /**
     * Set primary image for a review (clears other primary flags)
     */
    public void setPrimaryImage(Integer reviewId, Integer imageId) {
        // Clear all primary flags for this review
        entityManager.createQuery(
            "UPDATE ReviewImage ri SET ri.isPrimary = false WHERE ri.review.reviewId = :reviewId")
            .setParameter("reviewId", reviewId)
            .executeUpdate();
        
        // Set the specified image as primary
        if (imageId != null) {
            ReviewImage image = entityManager.find(ReviewImage.class, imageId);
            if (image != null && image.getReview().getReviewId().equals(reviewId)) {
                image.setIsPrimary(true);
                entityManager.merge(image);
            }
        }
    }
    
    /**
     * Update display order for images
     */
    public void updateDisplayOrder(Integer reviewId, List<Integer> imageIds) {
        for (int i = 0; i < imageIds.size(); i++) {
            Integer imageId = imageIds.get(i);
            entityManager.createQuery(
                "UPDATE ReviewImage ri SET ri.displayOrder = :order WHERE ri.imageId = :imageId AND ri.review.reviewId = :reviewId")
                .setParameter("order", i)
                .setParameter("imageId", imageId)
                .setParameter("reviewId", reviewId)
                .executeUpdate();
        }
    }
    
    /**
     * Find images by original filename (for duplicate detection)
     */
    public List<ReviewImage> findByOriginalFilename(String originalFilename) {
        TypedQuery<ReviewImage> query = entityManager.createQuery(
            "FROM ReviewImage ri WHERE ri.originalFilename = :filename ORDER BY ri.uploadedAt DESC", 
            ReviewImage.class);
        query.setParameter("filename", originalFilename);
        return query.getResultList();
    }
    
    /**
     * Find images by file size range (for storage management)
     */
    public List<ReviewImage> findByFileSizeRange(Long minSize, Long maxSize) {
        TypedQuery<ReviewImage> query = entityManager.createQuery(
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
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT SUM(ri.fileSize) FROM ReviewImage ri WHERE ri.fileSize IS NOT NULL", 
            Long.class);
        Long result = query.getSingleResult();
        return result != null ? result : 0L;
    }
    
    /**
     * Delete review image
     */
    public void delete(Integer imageId) {
        ReviewImage image = entityManager.find(ReviewImage.class, imageId);
        if (image != null) {
            if (entityManager.contains(image)) {
                entityManager.remove(image);
            } else {
                entityManager.remove(entityManager.merge(image));
            }
        }
    }
    
    /**
     * Delete all images for a review
     */
    public void deleteByReview(Integer reviewId) {
        entityManager.createQuery(
            "DELETE FROM ReviewImage ri WHERE ri.review.reviewId = :reviewId")
            .setParameter("reviewId", reviewId)
            .executeUpdate();
    }
}