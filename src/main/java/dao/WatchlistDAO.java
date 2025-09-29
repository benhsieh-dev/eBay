package dao;

import entity.Watchlist;
import entity.User;
import entity.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class WatchlistDAO {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public Watchlist save(Watchlist watchlist) {
        if (watchlist.getWatchlistId() == null) {
            entityManager.persist(watchlist);
        } else {
            entityManager.merge(watchlist);
        }
        return watchlist;
    }
    
    public Watchlist findById(Integer watchlistId) {
        return entityManager.find(Watchlist.class, watchlistId);
    }
    
    public List<Watchlist> findAll() {
        TypedQuery<Watchlist> query = entityManager.createQuery(
            "FROM Watchlist ORDER BY addedDate DESC", Watchlist.class);
        return query.getResultList();
    }
    
    public List<Watchlist> findByUserId(Integer userId) {
        TypedQuery<Watchlist> query = entityManager.createQuery(
            "FROM Watchlist WHERE user.userId = :userId ORDER BY addedDate DESC", Watchlist.class);
        query.setParameter("userId", userId);
        return query.getResultList();
    }
    
    public List<Watchlist> findByProductId(Integer productId) {
        TypedQuery<Watchlist> query = entityManager.createQuery(
            "FROM Watchlist WHERE product.productId = :productId ORDER BY addedDate DESC", Watchlist.class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }
    
    public Watchlist findByUserIdAndProductId(Integer userId, Integer productId) {
        TypedQuery<Watchlist> query = entityManager.createQuery(
            "FROM Watchlist WHERE user.userId = :userId AND product.productId = :productId", Watchlist.class);
        query.setParameter("userId", userId);
        query.setParameter("productId", productId);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public boolean isProductInUserWatchlist(Integer userId, Integer productId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(w) FROM Watchlist w WHERE w.user.userId = :userId AND w.product.productId = :productId", Long.class);
        query.setParameter("userId", userId);
        query.setParameter("productId", productId);
        return query.getSingleResult() > 0;
    }
    
    public Long getWatchlistCountForUser(Integer userId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(w) FROM Watchlist w WHERE w.user.userId = :userId", Long.class);
        query.setParameter("userId", userId);
        return query.getSingleResult();
    }
    
    public Long getWatchlistCountForProduct(Integer productId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(w) FROM Watchlist w WHERE w.product.productId = :productId", Long.class);
        query.setParameter("productId", productId);
        return query.getSingleResult();
    }
    
    public List<Watchlist> findRecentWatchlistItems(int limit) {
        TypedQuery<Watchlist> query = entityManager.createQuery(
            "FROM Watchlist ORDER BY addedDate DESC", Watchlist.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public void delete(Watchlist watchlist) {
        if (entityManager.contains(watchlist)) {
            entityManager.remove(watchlist);
        } else {
            entityManager.remove(entityManager.merge(watchlist));
        }
    }
    
    public void deleteById(Integer watchlistId) {
        Watchlist watchlist = findById(watchlistId);
        if (watchlist != null) {
            delete(watchlist);
        }
    }
    
    public void deleteByUserIdAndProductId(Integer userId, Integer productId) {
        entityManager.createQuery(
            "DELETE FROM Watchlist WHERE user.userId = :userId AND product.productId = :productId")
            .setParameter("userId", userId)
            .setParameter("productId", productId)
            .executeUpdate();
    }
    
    public void deleteAllForUser(Integer userId) {
        entityManager.createQuery(
            "DELETE FROM Watchlist WHERE user.userId = :userId")
            .setParameter("userId", userId)
            .executeUpdate();
    }
    
    public void deleteAllForProduct(Integer productId) {
        entityManager.createQuery(
            "DELETE FROM Watchlist WHERE product.productId = :productId")
            .setParameter("productId", productId)
            .executeUpdate();
    }
    
    public Watchlist update(Watchlist watchlist) {
        return entityManager.merge(watchlist);
    }
}