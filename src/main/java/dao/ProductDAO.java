package dao;

import entity.Product;
import entity.Category;
import entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Repository
@Transactional
public class ProductDAO {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    private Session getCurrentSession() {
        try {
            return sessionFactory.getCurrentSession();
        } catch (Exception e) {
            return sessionFactory.openSession();
        }
    }
    
    public Product save(Product product) {
        getCurrentSession().saveOrUpdate(product);
        return product;
    }
    
    public Product findById(Integer productId) {
        return getCurrentSession().get(Product.class, productId);
    }
    
    public List<Product> findAll() {
        Query<Product> query = getCurrentSession().createQuery(
            "FROM Product ORDER BY createdDate DESC", Product.class);
        return query.getResultList();
    }
    
    public List<Product> findActiveProducts() {
        Query<Product> query = getCurrentSession().createQuery(
            "FROM Product WHERE status = 'ACTIVE' ORDER BY createdDate DESC", Product.class);
        return query.getResultList();
    }
    
    public List<Product> findByCategory(Integer categoryId) {
        Query<Product> query = getCurrentSession().createQuery(
            "FROM Product WHERE category.categoryId = :categoryId AND status = 'ACTIVE' ORDER BY createdDate DESC", Product.class);
        query.setParameter("categoryId", categoryId);
        return query.getResultList();
    }
    
    public List<Product> findBySeller(Integer sellerId) {
        Query<Product> query = getCurrentSession().createQuery(
            "FROM Product WHERE seller.userId = :sellerId ORDER BY createdDate DESC", Product.class);
        query.setParameter("sellerId", sellerId);
        return query.getResultList();
    }
    
    public List<Product> findBySellerAndStatus(Integer sellerId, Product.ProductStatus status) {
        Query<Product> query = getCurrentSession().createQuery(
            "FROM Product WHERE seller.userId = :sellerId AND status = :status ORDER BY createdDate DESC", Product.class);
        query.setParameter("sellerId", sellerId);
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    public List<Product> findByListingType(Product.ListingType listingType) {
        Query<Product> query = getCurrentSession().createQuery(
            "FROM Product WHERE listingType = :listingType AND status = 'ACTIVE' ORDER BY createdDate DESC", Product.class);
        query.setParameter("listingType", listingType);
        return query.getResultList();
    }
    
    public List<Product> findAuctionsEndingSoon(int hours) {
        Timestamp cutoffTime = new Timestamp(System.currentTimeMillis() + (hours * 60 * 60 * 1000));
        Query<Product> query = getCurrentSession().createQuery(
            "FROM Product WHERE listingType IN ('AUCTION', 'BOTH') AND status = 'ACTIVE' " +
            "AND auctionEndTime <= :cutoff AND auctionEndTime > CURRENT_TIMESTAMP " +
            "ORDER BY auctionEndTime ASC", Product.class);
        query.setParameter("cutoff", cutoffTime);
        return query.getResultList();
    }
    
    public List<Product> searchProducts(String searchTerm) {
        Query<Product> query = getCurrentSession().createQuery(
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
        
        Query<Product> query = getCurrentSession().createQuery(hql.toString(), Product.class);
        
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
        Query<Product> query = getCurrentSession().createQuery(
            "FROM Product WHERE status = 'ACTIVE' ORDER BY viewCount DESC, createdDate DESC", Product.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public List<Product> findRecentProducts(int limit) {
        Query<Product> query = getCurrentSession().createQuery(
            "FROM Product WHERE status = 'ACTIVE' ORDER BY createdDate DESC", Product.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public List<Product> findProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        Query<Product> query = getCurrentSession().createQuery(
            "FROM Product WHERE status = 'ACTIVE' AND currentPrice BETWEEN :minPrice AND :maxPrice " +
            "ORDER BY currentPrice ASC", Product.class);
        query.setParameter("minPrice", minPrice);
        query.setParameter("maxPrice", maxPrice);
        return query.getResultList();
    }
    
    public void incrementViewCount(Integer productId) {
        Query query = getCurrentSession().createQuery(
            "UPDATE Product SET viewCount = viewCount + 1 WHERE productId = :productId");
        query.setParameter("productId", productId);
        query.executeUpdate();
    }
    
    public void incrementWatchCount(Integer productId) {
        Query query = getCurrentSession().createQuery(
            "UPDATE Product SET watchCount = watchCount + 1 WHERE productId = :productId");
        query.setParameter("productId", productId);
        query.executeUpdate();
    }
    
    public void decrementWatchCount(Integer productId) {
        Query query = getCurrentSession().createQuery(
            "UPDATE Product SET watchCount = GREATEST(0, watchCount - 1) WHERE productId = :productId");
        query.setParameter("productId", productId);
        query.executeUpdate();
    }
    
    public void delete(Product product) {
        getCurrentSession().delete(product);
    }
    
    public void deleteById(Integer productId) {
        Product product = findById(productId);
        if (product != null) {
            delete(product);
        }
    }
    
    public Product update(Product product) {
        product.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        return (Product) getCurrentSession().merge(product);
    }
    
    public Long getTotalProductCount() {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(p) FROM Product p WHERE p.status = 'ACTIVE'", Long.class);
        return query.uniqueResult();
    }
    
    public Long getProductCountByCategory(Integer categoryId) {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(p) FROM Product p WHERE p.category.categoryId = :categoryId AND p.status = 'ACTIVE'", Long.class);
        query.setParameter("categoryId", categoryId);
        return query.uniqueResult();
    }
    
    public List<Product> findExpiredAuctions() {
        Session session = sessionFactory.openSession();
        try {
            Query<Product> query = session.createQuery(
                "FROM Product WHERE listingType IN ('AUCTION', 'BOTH') AND status = 'ACTIVE' " +
                "AND auctionEndTime <= CURRENT_TIMESTAMP", Product.class);
            return query.getResultList();
        } finally {
            session.close();
        }
    }
    
    public void markAuctionAsEnded(Integer productId) {
        Query query = getCurrentSession().createQuery(
            "UPDATE Product SET status = 'ENDED' WHERE productId = :productId");
        query.setParameter("productId", productId);
        query.executeUpdate();
    }
}