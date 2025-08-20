package dao;

import entity.ProductImage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ProductImageDAO {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    public ProductImage save(ProductImage productImage) {
        getCurrentSession().saveOrUpdate(productImage);
        return productImage;
    }
    
    public ProductImage findById(Integer imageId) {
        return getCurrentSession().get(ProductImage.class, imageId);
    }
    
    public List<ProductImage> findByProductId(Integer productId) {
        Query<ProductImage> query = getCurrentSession().createQuery(
            "FROM ProductImage WHERE product.productId = :productId ORDER BY sortOrder, uploadedDate", ProductImage.class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }
    
    public ProductImage findPrimaryImageByProductId(Integer productId) {
        Query<ProductImage> query = getCurrentSession().createQuery(
            "FROM ProductImage WHERE product.productId = :productId AND isPrimary = true", ProductImage.class);
        query.setParameter("productId", productId);
        query.setMaxResults(1);
        return query.uniqueResult();
    }
    
    public List<ProductImage> findAll() {
        Query<ProductImage> query = getCurrentSession().createQuery(
            "FROM ProductImage ORDER BY uploadedDate DESC", ProductImage.class);
        return query.getResultList();
    }
    
    public void delete(ProductImage productImage) {
        getCurrentSession().delete(productImage);
    }
    
    public void deleteById(Integer imageId) {
        ProductImage image = findById(imageId);
        if (image != null) {
            delete(image);
        }
    }
    
    public void deleteByProductId(Integer productId) {
        Query query = getCurrentSession().createQuery(
            "DELETE FROM ProductImage WHERE product.productId = :productId");
        query.setParameter("productId", productId);
        query.executeUpdate();
    }
    
    public ProductImage update(ProductImage productImage) {
        return (ProductImage) getCurrentSession().merge(productImage);
    }
    
    public void clearPrimaryStatus(Integer productId) {
        Query query = getCurrentSession().createQuery(
            "UPDATE ProductImage SET isPrimary = false WHERE product.productId = :productId");
        query.setParameter("productId", productId);
        query.executeUpdate();
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
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(pi) FROM ProductImage pi WHERE pi.product.productId = :productId", Long.class);
        query.setParameter("productId", productId);
        return query.uniqueResult();
    }
    
    public void updateSortOrder(Integer imageId, Integer sortOrder) {
        Query query = getCurrentSession().createQuery(
            "UPDATE ProductImage SET sortOrder = :sortOrder WHERE imageId = :imageId");
        query.setParameter("sortOrder", sortOrder);
        query.setParameter("imageId", imageId);
        query.executeUpdate();
    }
}