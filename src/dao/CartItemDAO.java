package dao;

import entity.CartItem;
import entity.Product;
import entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
@Transactional
public class CartItemDAO {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    public CartItem save(CartItem cartItem) {
        getCurrentSession().saveOrUpdate(cartItem);
        return cartItem;
    }
    
    public CartItem findById(Integer cartId) {
        return getCurrentSession().get(CartItem.class, cartId);
    }
    
    public List<CartItem> findByUserId(Integer userId) {
        Query<CartItem> query = getCurrentSession().createQuery(
            "FROM CartItem WHERE user.userId = :userId ORDER BY addedDate DESC", CartItem.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    public CartItem findByUserIdAndProductId(Integer userId, Integer productId) {
        Query<CartItem> query = getCurrentSession().createQuery(
            "FROM CartItem WHERE user.userId = :userId AND product.productId = :productId", CartItem.class);
        query.setParameter("userId", userId);
        query.setParameter("productId", productId);
        return query.uniqueResult();
    }
    
    public List<CartItem> findAll() {
        Query<CartItem> query = getCurrentSession().createQuery(
            "FROM CartItem ORDER BY addedDate DESC", CartItem.class);
        return query.getResultList();
    }
    
    public Long getCartItemCount(Integer userId) {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(c) FROM CartItem c WHERE c.user.userId = :userId", Long.class);
        query.setParameter("userId", userId);
        return query.uniqueResult();
    }
    
    public BigDecimal getCartTotal(Integer userId) {
        Query<BigDecimal> query = getCurrentSession().createQuery(
            "SELECT COALESCE(SUM(c.quantity * p.currentPrice + p.shippingCost), 0) " +
            "FROM CartItem c JOIN c.product p WHERE c.user.userId = :userId " +
            "AND p.status = 'ACTIVE' AND (p.listingType = 'BUY_NOW' OR p.listingType = 'BOTH')", BigDecimal.class);
        query.setParameter("userId", userId);
        BigDecimal total = query.uniqueResult();
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public List<CartItem> findActiveCartItems(Integer userId) {
        Query<CartItem> query = getCurrentSession().createQuery(
            "FROM CartItem c WHERE c.user.userId = :userId " +
            "AND c.product.status = 'ACTIVE' " +
            "AND (c.product.listingType = 'BUY_NOW' OR c.product.listingType = 'BOTH') " +
            "ORDER BY c.addedDate DESC", CartItem.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    public List<CartItem> findInactiveCartItems(Integer userId) {
        Query<CartItem> query = getCurrentSession().createQuery(
            "FROM CartItem c WHERE c.user.userId = :userId " +
            "AND (c.product.status != 'ACTIVE' " +
            "OR c.product.listingType = 'AUCTION') " +
            "ORDER BY c.addedDate DESC", CartItem.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    public void updateQuantity(Integer cartId, Integer quantity) {
        Query query = getCurrentSession().createQuery(
            "UPDATE CartItem SET quantity = :quantity WHERE cartId = :cartId");
        query.setParameter("quantity", quantity);
        query.setParameter("cartId", cartId);
        query.executeUpdate();
    }
    
    public void delete(CartItem cartItem) {
        getCurrentSession().delete(cartItem);
    }
    
    public void deleteById(Integer cartId) {
        CartItem cartItem = findById(cartId);
        if (cartItem != null) {
            delete(cartItem);
        }
    }
    
    public void deleteByUserIdAndProductId(Integer userId, Integer productId) {
        Query query = getCurrentSession().createQuery(
            "DELETE FROM CartItem WHERE user.userId = :userId AND product.productId = :productId");
        query.setParameter("userId", userId);
        query.setParameter("productId", productId);
        query.executeUpdate();
    }
    
    public void clearCart(Integer userId) {
        Query query = getCurrentSession().createQuery(
            "DELETE FROM CartItem WHERE user.userId = :userId");
        query.setParameter("userId", userId);
        query.executeUpdate();
    }
    
    public void clearInactiveItems(Integer userId) {
        Query query = getCurrentSession().createQuery(
            "DELETE FROM CartItem c WHERE c.user.userId = :userId " +
            "AND (c.product.status != 'ACTIVE' OR c.product.listingType = 'AUCTION')");
        query.setParameter("userId", userId);
        query.executeUpdate();
    }
    
    public CartItem update(CartItem cartItem) {
        return (CartItem) getCurrentSession().merge(cartItem);
    }
    
    public boolean existsByUserIdAndProductId(Integer userId, Integer productId) {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(c) FROM CartItem c WHERE c.user.userId = :userId AND c.product.productId = :productId", Long.class);
        query.setParameter("userId", userId);
        query.setParameter("productId", productId);
        return query.uniqueResult() > 0;
    }
    
    public List<Object[]> getCartSummary(Integer userId) {
        Query<Object[]> query = getCurrentSession().createQuery(
            "SELECT COUNT(c), SUM(c.quantity), SUM(c.quantity * p.currentPrice), SUM(p.shippingCost) " +
            "FROM CartItem c JOIN c.product p WHERE c.user.userId = :userId " +
            "AND p.status = 'ACTIVE' AND (p.listingType = 'BUY_NOW' OR p.listingType = 'BOTH')", Object[].class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    public List<CartItem> findExpiredCartItems(Integer daysBefore) {
        Query<CartItem> query = getCurrentSession().createQuery(
            "FROM CartItem WHERE addedDate < :cutoffDate", CartItem.class);
        query.setParameter("cutoffDate", java.sql.Timestamp.valueOf(
            java.time.LocalDateTime.now().minusDays(daysBefore)));
        return query.getResultList();
    }
    
    public void removeExpiredCartItems(Integer daysBefore) {
        Query query = getCurrentSession().createQuery(
            "DELETE FROM CartItem WHERE addedDate < :cutoffDate");
        query.setParameter("cutoffDate", java.sql.Timestamp.valueOf(
            java.time.LocalDateTime.now().minusDays(daysBefore)));
        query.executeUpdate();
    }
}