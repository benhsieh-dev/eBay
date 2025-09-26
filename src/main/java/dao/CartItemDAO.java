package dao;

import entity.CartItem;
import entity.Product;
import entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Repository
@Transactional
public class CartItemDAO {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public CartItem save(CartItem cartItem) {
        if (cartItem.getCartId() == null) {
            entityManager.persist(cartItem);
        } else {
            entityManager.merge(cartItem);
        }
        return cartItem;
    }
    
    public CartItem findById(Integer cartId) {
        return entityManager.find(CartItem.class, cartId);
    }
    
    public List<CartItem> findByUserId(Integer userId) {
        TypedQuery<CartItem> query = entityManager.createQuery(
            "FROM CartItem WHERE user.userId = :userId ORDER BY addedDate DESC", CartItem.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    public CartItem findByUserIdAndProductId(Integer userId, Integer productId) {
        TypedQuery<CartItem> query = entityManager.createQuery(
            "FROM CartItem WHERE user.userId = :userId AND product.productId = :productId", CartItem.class);
        query.setParameter("userId", userId);
        query.setParameter("productId", productId);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public List<CartItem> findAll() {
        TypedQuery<CartItem> query = entityManager.createQuery(
            "FROM CartItem ORDER BY addedDate DESC", CartItem.class);
        return query.getResultList();
    }
    
    public Long getCartItemCount(Integer userId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM CartItem c WHERE c.user.userId = :userId", Long.class);
        query.setParameter("userId", userId);
        return query.getSingleResult();
    }
    
    public BigDecimal getCartTotal(Integer userId) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT COALESCE(SUM(c.quantity * p.currentPrice + p.shippingCost), 0) " +
            "FROM CartItem c JOIN c.product p WHERE c.user.userId = :userId " +
            "AND p.status = 'ACTIVE' AND (p.listingType = 'BUY_NOW' OR p.listingType = 'BOTH')", BigDecimal.class);
        query.setParameter("userId", userId);
        BigDecimal total = query.getSingleResult();
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public List<CartItem> findActiveCartItems(Integer userId) {
        TypedQuery<CartItem> query = entityManager.createQuery(
            "FROM CartItem c WHERE c.user.userId = :userId " +
            "AND c.product.status = 'ACTIVE' " +
            "AND (c.product.listingType = 'BUY_NOW' OR c.product.listingType = 'BOTH') " +
            "ORDER BY c.addedDate DESC", CartItem.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    public List<CartItem> findInactiveCartItems(Integer userId) {
        TypedQuery<CartItem> query = entityManager.createQuery(
            "FROM CartItem c WHERE c.user.userId = :userId " +
            "AND (c.product.status != 'ACTIVE' " +
            "OR c.product.listingType = 'AUCTION') " +
            "ORDER BY c.addedDate DESC", CartItem.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    public void updateQuantity(Integer cartId, Integer quantity) {
        entityManager.createQuery(
            "UPDATE CartItem SET quantity = :quantity WHERE cartId = :cartId")
            .setParameter("quantity", quantity)
            .setParameter("cartId", cartId)
            .executeUpdate();
    }
    
    public void delete(CartItem cartItem) {
        if (entityManager.contains(cartItem)) {
            entityManager.remove(cartItem);
        } else {
            entityManager.remove(entityManager.merge(cartItem));
        }
    }
    
    public void deleteById(Integer cartId) {
        CartItem cartItem = findById(cartId);
        if (cartItem != null) {
            delete(cartItem);
        }
    }
    
    public void deleteByUserIdAndProductId(Integer userId, Integer productId) {
        entityManager.createQuery(
            "DELETE FROM CartItem WHERE user.userId = :userId AND product.productId = :productId")
            .setParameter("userId", userId)
            .setParameter("productId", productId)
            .executeUpdate();
    }
    
    public void clearCart(Integer userId) {
        entityManager.createQuery(
            "DELETE FROM CartItem WHERE user.userId = :userId")
            .setParameter("userId", userId)
            .executeUpdate();
    }
    
    public void clearInactiveItems(Integer userId) {
        entityManager.createQuery(
            "DELETE FROM CartItem c WHERE c.user.userId = :userId " +
            "AND (c.product.status != 'ACTIVE' OR c.product.listingType = 'AUCTION')")
            .setParameter("userId", userId)
            .executeUpdate();
    }
    
    public CartItem update(CartItem cartItem) {
        return entityManager.merge(cartItem);
    }
    
    public boolean existsByUserIdAndProductId(Integer userId, Integer productId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(c) FROM CartItem c WHERE c.user.userId = :userId AND c.product.productId = :productId", Long.class);
        query.setParameter("userId", userId);
        query.setParameter("productId", productId);
        return query.getSingleResult() > 0;
    }
    
    public List<Object[]> getCartSummary(Integer userId) {
        TypedQuery<Object[]> query = entityManager.createQuery(
            "SELECT COUNT(c), SUM(c.quantity), SUM(c.quantity * p.currentPrice), SUM(p.shippingCost) " +
            "FROM CartItem c JOIN c.product p WHERE c.user.userId = :userId " +
            "AND p.status = 'ACTIVE' AND (p.listingType = 'BUY_NOW' OR p.listingType = 'BOTH')", Object[].class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    public List<CartItem> findExpiredCartItems(Integer daysBefore) {
        TypedQuery<CartItem> query = entityManager.createQuery(
            "FROM CartItem WHERE addedDate < :cutoffDate", CartItem.class);
        query.setParameter("cutoffDate", java.sql.Timestamp.valueOf(
            java.time.LocalDateTime.now().minusDays(daysBefore)));
        return query.getResultList();
    }
    
    public void removeExpiredCartItems(Integer daysBefore) {
        entityManager.createQuery(
            "DELETE FROM CartItem WHERE addedDate < :cutoffDate")
            .setParameter("cutoffDate", java.sql.Timestamp.valueOf(
                java.time.LocalDateTime.now().minusDays(daysBefore)))
            .executeUpdate();
    }
}