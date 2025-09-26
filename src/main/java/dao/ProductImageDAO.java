package dao;

import entity.ProductImage;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ProductImageDAO {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public ProductImage save(ProductImage productImage) {
        if (productImage.getImageId() == null) {
            entityManager.persist(productImage);
        } else {
            entityManager.merge(productImage);
        }
        return productImage;
    }
    
    public ProductImage findById(Integer imageId) {
        return entityManager.find(ProductImage.class, imageId);
    }
    
    public List<ProductImage> findByProductId(Integer productId) {
        TypedQuery<ProductImage> query = entityManager.createQuery(
            "FROM ProductImage WHERE product.productId = :productId ORDER BY sortOrder, uploadedDate", ProductImage.class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }
    
    public ProductImage findPrimaryImageByProductId(Integer productId) {
        TypedQuery<ProductImage> query = entityManager.createQuery(
            "FROM ProductImage WHERE product.productId = :productId AND isPrimary = true", ProductImage.class);
        query.setParameter("productId", productId);
        query.setMaxResults(1);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public List<ProductImage> findAll() {
        TypedQuery<ProductImage> query = entityManager.createQuery(
            "FROM ProductImage ORDER BY uploadedDate DESC", ProductImage.class);
        return query.getResultList();
    }
    
    public void delete(ProductImage productImage) {
        if (entityManager.contains(productImage)) {
            entityManager.remove(productImage);
        } else {
            entityManager.remove(entityManager.merge(productImage));
        }
    }
    
    public void deleteById(Integer imageId) {
        ProductImage image = findById(imageId);
        if (image != null) {
            delete(image);
        }
    }
    
    public void deleteByProductId(Integer productId) {
        entityManager.createQuery(
            "DELETE FROM ProductImage WHERE product.productId = :productId")
            .setParameter("productId", productId)
            .executeUpdate();
    }
    
    public ProductImage update(ProductImage productImage) {
        return entityManager.merge(productImage);
    }
    
    public void clearPrimaryStatus(Integer productId) {
        entityManager.createQuery(
            "UPDATE ProductImage SET isPrimary = false WHERE product.productId = :productId")
            .setParameter("productId", productId)
            .executeUpdate();
    }
    
    public void setPrimaryImage(Integer imageId) {
        ProductImage image = findById(imageId);
        if (image != null) {
            // Clear existing primary status for this product
            clearPrimaryStatus(image.getProduct().getProductId());
            
            // Set this image as primary
            image.setIsPrimary(true);
            update(image);
        }
    }
    
    public Long getImageCountByProductId(Integer productId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(pi) FROM ProductImage pi WHERE pi.product.productId = :productId", Long.class);
        query.setParameter("productId", productId);
        return query.getSingleResult();
    }
    
    public void updateSortOrder(Integer imageId, Integer sortOrder) {
        entityManager.createQuery(
            "UPDATE ProductImage SET sortOrder = :sortOrder WHERE imageId = :imageId")
            .setParameter("sortOrder", sortOrder)
            .setParameter("imageId", imageId)
            .executeUpdate();
    }
}