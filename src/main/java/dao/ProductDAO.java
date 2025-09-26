package dao;

import entity.Product;
import entity.Category;
import entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Repository
@Transactional
public class ProductDAO {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public Product save(Product product) {
        if (product.getProductId() == null) {
            entityManager.persist(product);
        } else {
            entityManager.merge(product);
        }
        return product;
    }
    
    public Product findById(Integer productId) {
        return entityManager.find(Product.class, productId);
    }
    
    public List<Product> findAll() {
        TypedQuery<Product> query = entityManager.createQuery(
            "FROM Product ORDER BY createdDate DESC", Product.class);
        return query.getResultList();
    }
    
    public List<Product> findActiveProducts() {
        TypedQuery<Product> query = entityManager.createQuery(
            "FROM Product WHERE status = 'ACTIVE' ORDER BY createdDate DESC", Product.class);
        return query.getResultList();
    }
    
    public List<Product> findByCategory(Integer categoryId) {
        TypedQuery<Product> query = entityManager.createQuery(
            "FROM Product WHERE category.categoryId = :categoryId AND status = 'ACTIVE' ORDER BY createdDate DESC", Product.class);
        query.setParameter("categoryId", categoryId);
        return query.getResultList();
    }
    
    public List<Product> findBySeller(Integer sellerId) {
        TypedQuery<Product> query = entityManager.createQuery(
            "FROM Product WHERE seller.userId = :sellerId ORDER BY createdDate DESC", Product.class);
        query.setParameter("sellerId", sellerId);
        return query.getResultList();
    }
    
    public List<Product> findBySellerAndStatus(Integer sellerId, Product.ProductStatus status) {
        TypedQuery<Product> query = entityManager.createQuery(
            "FROM Product WHERE seller.userId = :sellerId AND status = :status ORDER BY createdDate DESC", Product.class);
        query.setParameter("sellerId", sellerId);
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    public List<Product> findByListingType(Product.ListingType listingType) {
        TypedQuery<Product> query = entityManager.createQuery(
            "FROM Product WHERE listingType = :listingType AND status = 'ACTIVE' ORDER BY createdDate DESC", Product.class);
        query.setParameter("listingType", listingType);
        return query.getResultList();
    }
    
    public List<Product> findAuctionsEndingSoon(int hours) {
        Timestamp cutoffTime = new Timestamp(System.currentTimeMillis() + (hours * 60 * 60 * 1000));
        TypedQuery<Product> query = entityManager.createQuery(
            "FROM Product WHERE listingType IN ('AUCTION', 'BOTH') AND status = 'ACTIVE' " +
            "AND auctionEndTime <= :cutoff AND auctionEndTime > CURRENT_TIMESTAMP " +
            "ORDER BY auctionEndTime ASC", Product.class);
        query.setParameter("cutoff", cutoffTime);
        return query.getResultList();
    }
    
    public List<Product> searchProducts(String searchTerm) {
        TypedQuery<Product> query = entityManager.createQuery(
            "FROM Product WHERE (title LIKE :search OR description LIKE :search) " +
            "AND status = 'ACTIVE' ORDER BY createdDate DESC", Product.class);
        query.setParameter("search", "%" + searchTerm + "%");
        return query.getResultList();
    }
    
    public List<Product> searchProductsWithFilters(String searchTerm, Integer categoryId, 
                                                   BigDecimal minPrice, BigDecimal maxPrice, 
                                                   Product.ConditionType condition, 
                                                   Product.ListingType listingType) {
        StringBuilder hql = new StringBuilder("FROM Product WHERE status = 'ACTIVE'");
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            hql.append(" AND (title LIKE :search OR description LIKE :search)");
        }
        
        if (categoryId != null) {
            hql.append(" AND category.categoryId = :categoryId");
        }
        
        if (minPrice != null) {
            hql.append(" AND currentPrice >= :minPrice");
        }
        
        if (maxPrice != null) {
            hql.append(" AND currentPrice <= :maxPrice");
        }
        
        if (condition != null) {
            hql.append(" AND conditionType = :condition");
        }
        
        if (listingType != null) {
            hql.append(" AND listingType = :listingType");
        }
        
        hql.append(" ORDER BY createdDate DESC");
        
        TypedQuery<Product> query = entityManager.createQuery(hql.toString(), Product.class);
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            query.setParameter("search", "%" + searchTerm + "%");
        }
        if (categoryId != null) {
            query.setParameter("categoryId", categoryId);
        }
        if (minPrice != null) {
            query.setParameter("minPrice", minPrice);
        }
        if (maxPrice != null) {
            query.setParameter("maxPrice", maxPrice);
        }
        if (condition != null) {
            query.setParameter("condition", condition);
        }
        if (listingType != null) {
            query.setParameter("listingType", listingType);
        }
        
        return query.getResultList();
    }
    
    public List<Product> findFeaturedProducts(int limit) {
        TypedQuery<Product> query = entityManager.createQuery(
            "FROM Product WHERE status = 'ACTIVE' ORDER BY viewCount DESC, createdDate DESC", Product.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public List<Product> findRecentProducts(int limit) {
        TypedQuery<Product> query = entityManager.createQuery(
            "FROM Product WHERE status = 'ACTIVE' ORDER BY createdDate DESC", Product.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public List<Product> findProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        TypedQuery<Product> query = entityManager.createQuery(
            "FROM Product WHERE status = 'ACTIVE' AND currentPrice BETWEEN :minPrice AND :maxPrice " +
            "ORDER BY currentPrice ASC", Product.class);
        query.setParameter("minPrice", minPrice);
        query.setParameter("maxPrice", maxPrice);
        return query.getResultList();
    }
    
    public void incrementViewCount(Integer productId) {
        entityManager.createQuery(
            "UPDATE Product SET viewCount = viewCount + 1 WHERE productId = :productId")
            .setParameter("productId", productId)
            .executeUpdate();
    }
    
    public void incrementWatchCount(Integer productId) {
        entityManager.createQuery(
            "UPDATE Product SET watchCount = watchCount + 1 WHERE productId = :productId")
            .setParameter("productId", productId)
            .executeUpdate();
    }
    
    public void decrementWatchCount(Integer productId) {
        entityManager.createQuery(
            "UPDATE Product SET watchCount = GREATEST(0, watchCount - 1) WHERE productId = :productId")
            .setParameter("productId", productId)
            .executeUpdate();
    }
    
    public void delete(Product product) {
        if (entityManager.contains(product)) {
            entityManager.remove(product);
        } else {
            entityManager.remove(entityManager.merge(product));
        }
    }
    
    public void deleteById(Integer productId) {
        Product product = findById(productId);
        if (product != null) {
            delete(product);
        }
    }
    
    public Product update(Product product) {
        product.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        return entityManager.merge(product);
    }
    
    public Long getTotalProductCount() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(p) FROM Product p WHERE p.status = 'ACTIVE'", Long.class);
        return query.getSingleResult();
    }
    
    public Long getProductCountByCategory(Integer categoryId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(p) FROM Product p WHERE p.category.categoryId = :categoryId AND p.status = 'ACTIVE'", Long.class);
        query.setParameter("categoryId", categoryId);
        return query.getSingleResult();
    }
    
    public List<Product> findExpiredAuctions() {
        TypedQuery<Product> query = entityManager.createQuery(
            "FROM Product WHERE listingType IN ('AUCTION', 'BOTH') AND status = 'ACTIVE' " +
            "AND auctionEndTime <= CURRENT_TIMESTAMP", Product.class);
        return query.getResultList();
    }
    
    public void markAuctionAsEnded(Integer productId) {
        entityManager.createQuery(
            "UPDATE Product SET status = 'ENDED' WHERE productId = :productId")
            .setParameter("productId", productId)
            .executeUpdate();
    }
}