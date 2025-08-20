package dao;

import entity.Bid;
import entity.Product;
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
public class BidDAO {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    public Bid save(Bid bid) {
        getCurrentSession().saveOrUpdate(bid);
        return bid;
    }
    
    public Bid findById(Integer bidId) {
        return getCurrentSession().get(Bid.class, bidId);
    }
    
    public List<Bid> findAll() {
        Query<Bid> query = getCurrentSession().createQuery(
            "FROM Bid ORDER BY bidTime DESC", Bid.class);
        return query.getResultList();
    }
    
    public List<Bid> findByProductId(Integer productId) {
        Query<Bid> query = getCurrentSession().createQuery(
            "FROM Bid WHERE product.productId = :productId ORDER BY bidAmount DESC, bidTime ASC", Bid.class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }
    
    public List<Bid> findByBidderId(Integer bidderId) {
        Query<Bid> query = getCurrentSession().createQuery(
            "FROM Bid WHERE bidder.userId = :bidderId ORDER BY bidTime DESC", Bid.class);
        query.setParameter("bidderId", bidderId);
        return query.getResultList();
    }
    
    public List<Bid> findActiveBidsByBidderId(Integer bidderId) {
        Query<Bid> query = getCurrentSession().createQuery(
            "FROM Bid WHERE bidder.userId = :bidderId AND bidStatus IN ('ACTIVE', 'WINNING') ORDER BY bidTime DESC", Bid.class);
        query.setParameter("bidderId", bidderId);
        return query.getResultList();
    }
    
    public Bid findHighestBidForProduct(Integer productId) {
        Query<Bid> query = getCurrentSession().createQuery(
            "FROM Bid WHERE product.productId = :productId ORDER BY bidAmount DESC, bidTime ASC", Bid.class);
        query.setParameter("productId", productId);
        query.setMaxResults(1);
        return query.uniqueResult();
    }
    
    public Bid findWinningBidForProduct(Integer productId) {
        Query<Bid> query = getCurrentSession().createQuery(
            "FROM Bid WHERE product.productId = :productId AND isWinningBid = true", Bid.class);
        query.setParameter("productId", productId);
        return query.uniqueResult();
    }
    
    public List<Bid> findBidHistoryForProduct(Integer productId) {
        Query<Bid> query = getCurrentSession().createQuery(
            "FROM Bid WHERE product.productId = :productId ORDER BY bidTime DESC", Bid.class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }
    
    public BigDecimal getMinimumBidAmount(Integer productId) {
        Query<BigDecimal> query = getCurrentSession().createQuery(
            "SELECT COALESCE(MAX(b.bidAmount), p.startingPrice) FROM Product p " +
            "LEFT JOIN Bid b ON b.product.productId = p.productId " +
            "WHERE p.productId = :productId", BigDecimal.class);
        query.setParameter("productId", productId);
        BigDecimal currentHigh = query.uniqueResult();
        
        // Add minimum increment (could be configurable)
        return currentHigh.add(new BigDecimal("1.00"));
    }
    
    public Long getBidCountForProduct(Integer productId) {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(b) FROM Bid b WHERE b.product.productId = :productId", Long.class);
        query.setParameter("productId", productId);
        return query.uniqueResult();
    }
    
    public Long getBidCountForUser(Integer userId) {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(b) FROM Bid b WHERE b.bidder.userId = :userId", Long.class);
        query.setParameter("userId", userId);
        return query.uniqueResult();
    }
    
    public boolean hasUserBidOnProduct(Integer userId, Integer productId) {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(b) FROM Bid b WHERE b.bidder.userId = :userId AND b.product.productId = :productId", Long.class);
        query.setParameter("userId", userId);
        query.setParameter("productId", productId);
        return query.uniqueResult() > 0;
    }
    
    public Bid findUserLastBidOnProduct(Integer userId, Integer productId) {
        Query<Bid> query = getCurrentSession().createQuery(
            "FROM Bid WHERE bidder.userId = :userId AND product.productId = :productId ORDER BY bidTime DESC", Bid.class);
        query.setParameter("userId", userId);
        query.setParameter("productId", productId);
        query.setMaxResults(1);
        return query.uniqueResult();
    }
    
    public List<Bid> findRecentBids(int limit) {
        Query<Bid> query = getCurrentSession().createQuery(
            "FROM Bid ORDER BY bidTime DESC", Bid.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public List<Bid> findBidsInTimeRange(Timestamp startTime, Timestamp endTime) {
        Query<Bid> query = getCurrentSession().createQuery(
            "FROM Bid WHERE bidTime BETWEEN :startTime AND :endTime ORDER BY bidTime DESC", Bid.class);
        query.setParameter("startTime", startTime);
        query.setParameter("endTime", endTime);
        return query.getResultList();
    }
    
    public void markPreviousBidsAsOutbid(Integer productId, Integer excludeBidId) {
        Query query = getCurrentSession().createQuery(
            "UPDATE Bid SET bidStatus = 'OUTBID', isWinningBid = false " +
            "WHERE product.productId = :productId AND bidId != :excludeBidId AND bidStatus != 'CANCELLED'");
        query.setParameter("productId", productId);
        query.setParameter("excludeBidId", excludeBidId);
        query.executeUpdate();
    }
    
    public void markBidAsWinning(Integer bidId) {
        Query query = getCurrentSession().createQuery(
            "UPDATE Bid SET bidStatus = 'WINNING', isWinningBid = true WHERE bidId = :bidId");
        query.setParameter("bidId", bidId);
        query.executeUpdate();
    }
    
    public void markBidAsWon(Integer bidId) {
        Query query = getCurrentSession().createQuery(
            "UPDATE Bid SET bidStatus = 'WON' WHERE bidId = :bidId");
        query.setParameter("bidId", bidId);
        query.executeUpdate();
    }
    
    public void cancelBid(Integer bidId) {
        Query query = getCurrentSession().createQuery(
            "UPDATE Bid SET bidStatus = 'CANCELLED' WHERE bidId = :bidId");
        query.setParameter("bidId", bidId);
        query.executeUpdate();
    }
    
    public void delete(Bid bid) {
        getCurrentSession().delete(bid);
    }
    
    public void deleteById(Integer bidId) {
        Bid bid = findById(bidId);
        if (bid != null) {
            delete(bid);
        }
    }
    
    public Bid update(Bid bid) {
        return (Bid) getCurrentSession().merge(bid);
    }
    
    public List<Object[]> getBidStatistics(Integer productId) {
        Query<Object[]> query = getCurrentSession().createQuery(
            "SELECT COUNT(b), MIN(b.bidAmount), MAX(b.bidAmount), AVG(b.bidAmount) " +
            "FROM Bid b WHERE b.product.productId = :productId", Object[].class);
        query.setParameter("productId", productId);
        return query.getResultList();
    }
    
    public List<Bid> findExpiredWinningBids() {
        Query<Bid> query = getCurrentSession().createQuery(
            "FROM Bid b WHERE b.isWinningBid = true AND b.bidStatus = 'WINNING' " +
            "AND b.product.status = 'ENDED'", Bid.class);
        return query.getResultList();
    }
    
    public List<Bid> findProxyBidsToExecute(Integer productId, BigDecimal currentHighBid) {
        Query<Bid> query = getCurrentSession().createQuery(
            "FROM Bid b WHERE b.product.productId = :productId AND b.bidType = 'PROXY' " +
            "AND b.maxProxyAmount > :currentHighBid AND b.bidStatus = 'ACTIVE' " +
            "ORDER BY b.bidTime ASC", Bid.class);
        query.setParameter("productId", productId);
        query.setParameter("currentHighBid", currentHighBid);
        return query.getResultList();
    }
}