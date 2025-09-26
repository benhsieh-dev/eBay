package dao;

import entity.Bid;
import entity.Product;
import entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Repository
@Transactional
public class BidDAO {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public Bid save(Bid bid) {
        if (bid.getBidId() == null) {
            entityManager.persist(bid);
        } else {
            entityManager.merge(bid);
        }
        return bid;
    }
    
    public Bid findById(Integer bidId) {
        return entityManager.find(Bid.class, bidId);
    }
    
    public List<Bid> findAll() {
        TypedQuery<Bid> query = entityManager.createQuery(
            "FROM Bid ORDER BY bidTime DESC", Bid.class);
        return query.getResultList();
    }
    
    public List<Bid> findByProductId(Integer productId) {
        TypedQuery<Bid> query = entityManager.createQuery(
            "FROM Bid WHERE product.productId = :productId ORDER BY bidAmount DESC, bidTime ASC", Bid.class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }
    
    public List<Bid> findByBidderId(Integer bidderId) {
        TypedQuery<Bid> query = entityManager.createQuery(
            "FROM Bid WHERE bidder.userId = :bidderId ORDER BY bidTime DESC", Bid.class);
        query.setParameter("bidderId", bidderId);
        return query.getResultList();
    }
    
    public List<Bid> findActiveBidsByBidderId(Integer bidderId) {
        TypedQuery<Bid> query = entityManager.createQuery(
            "FROM Bid WHERE bidder.userId = :bidderId AND bidStatus IN ('ACTIVE', 'WINNING') ORDER BY bidTime DESC", Bid.class);
        query.setParameter("bidderId", bidderId);
        return query.getResultList();
    }
    
    public Bid findHighestBidForProduct(Integer productId) {
        TypedQuery<Bid> query = entityManager.createQuery(
            "FROM Bid WHERE product.productId = :productId ORDER BY bidAmount DESC, bidTime ASC", Bid.class);
        query.setParameter("productId", productId);
        query.setMaxResults(1);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public Bid findWinningBidForProduct(Integer productId) {
        TypedQuery<Bid> query = entityManager.createQuery(
            "FROM Bid WHERE product.productId = :productId AND isWinningBid = true", Bid.class);
        query.setParameter("productId", productId);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public List<Bid> findBidHistoryForProduct(Integer productId) {
        TypedQuery<Bid> query = entityManager.createQuery(
            "FROM Bid WHERE product.productId = :productId ORDER BY bidTime DESC", Bid.class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }
    
    public BigDecimal getMinimumBidAmount(Integer productId) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT COALESCE(MAX(b.bidAmount), p.startingPrice) FROM Product p " +
            "LEFT JOIN Bid b ON b.product.productId = p.productId " +
            "WHERE p.productId = :productId", BigDecimal.class);
        query.setParameter("productId", productId);
        BigDecimal currentHigh = query.getSingleResult();
        
        // Add minimum increment (could be configurable)
        return currentHigh.add(new BigDecimal("1.00"));
    }
    
    public Long getBidCountForProduct(Integer productId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(b) FROM Bid b WHERE b.product.productId = :productId", Long.class);
        query.setParameter("productId", productId);
        return query.getSingleResult();
    }
    
    public Long getBidCountForUser(Integer userId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(b) FROM Bid b WHERE b.bidder.userId = :userId", Long.class);
        query.setParameter("userId", userId);
        return query.getSingleResult();
    }
    
    public boolean hasUserBidOnProduct(Integer userId, Integer productId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(b) FROM Bid b WHERE b.bidder.userId = :userId AND b.product.productId = :productId", Long.class);
        query.setParameter("userId", userId);
        query.setParameter("productId", productId);
        return query.getSingleResult() > 0;
    }
    
    public Bid findUserLastBidOnProduct(Integer userId, Integer productId) {
        TypedQuery<Bid> query = entityManager.createQuery(
            "FROM Bid WHERE bidder.userId = :userId AND product.productId = :productId ORDER BY bidTime DESC", Bid.class);
        query.setParameter("userId", userId);
        query.setParameter("productId", productId);
        query.setMaxResults(1);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public List<Bid> findRecentBids(int limit) {
        TypedQuery<Bid> query = entityManager.createQuery(
            "FROM Bid ORDER BY bidTime DESC", Bid.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public List<Bid> findBidsInTimeRange(Timestamp startTime, Timestamp endTime) {
        TypedQuery<Bid> query = entityManager.createQuery(
            "FROM Bid WHERE bidTime BETWEEN :startTime AND :endTime ORDER BY bidTime DESC", Bid.class);
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);
        return query.getResultList();
    }
    
    public void markPreviousBidsAsOutbid(Integer productId, Integer excludeBidId) {
        entityManager.createQuery(
            "UPDATE Bid SET bidStatus = 'OUTBID', isWinningBid = false " +
            "WHERE product.productId = :productId AND bidId != :excludeBidId AND bidStatus != 'CANCELLED'")
            .setParameter("productId", productId)
            .setParameter("excludeBidId", excludeBidId)
            .executeUpdate();
    }
    
    public void markBidAsWinning(Integer bidId) {
        entityManager.createQuery(
            "UPDATE Bid SET bidStatus = 'WINNING', isWinningBid = true WHERE bidId = :bidId")
            .setParameter("bidId", bidId)
            .executeUpdate();
    }
    
    public void markBidAsWon(Integer bidId) {
        entityManager.createQuery(
            "UPDATE Bid SET bidStatus = 'WON' WHERE bidId = :bidId")
            .setParameter("bidId", bidId)
            .executeUpdate();
    }
    
    public void cancelBid(Integer bidId) {
        entityManager.createQuery(
            "UPDATE Bid SET bidStatus = 'CANCELLED' WHERE bidId = :bidId")
            .setParameter("bidId", bidId)
            .executeUpdate();
    }
    
    public void delete(Bid bid) {
        if (entityManager.contains(bid)) {
            entityManager.remove(bid);
        } else {
            entityManager.remove(entityManager.merge(bid));
        }
    }
    
    public void deleteById(Integer bidId) {
        Bid bid = findById(bidId);
        if (bid != null) {
            delete(bid);
        }
    }
    
    public Bid update(Bid bid) {
        return entityManager.merge(bid);
    }
    
    public List<Object[]> getBidStatistics(Integer productId) {
        TypedQuery<Object[]> query = entityManager.createQuery(
            "SELECT COUNT(b), MIN(b.bidAmount), MAX(b.bidAmount), AVG(b.bidAmount) " +
            "FROM Bid b WHERE b.product.productId = :productId", Object[].class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }
    
    public List<Bid> findExpiredWinningBids() {
        TypedQuery<Bid> query = entityManager.createQuery(
            "FROM Bid b WHERE b.isWinningBid = true AND b.bidStatus = 'WINNING' " +
            "AND b.product.status = 'ENDED'", Bid.class);
        return query.getResultList();
    }
    
    public List<Bid> findProxyBidsToExecute(Integer productId, BigDecimal currentHighBid) {
        TypedQuery<Bid> query = entityManager.createQuery(
            "FROM Bid b WHERE b.product.productId = :productId AND b.bidType = 'PROXY' " +
            "AND b.maxProxyAmount > :currentHighBid AND b.bidStatus = 'ACTIVE' " +
            "ORDER BY b.bidTime ASC", Bid.class);
        query.setParameter("productId", productId);
        query.setParameter("currentHighBid", currentHighBid);
        return query.getResultList();
    }
}