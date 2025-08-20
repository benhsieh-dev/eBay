package dao;

import entity.Order;
import entity.OrderItem;
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
public class OrderDAO {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    public Order save(Order order) {
        getCurrentSession().saveOrUpdate(order);
        return order;
    }
    
    public Order findById(Integer orderId) {
        return getCurrentSession().get(Order.class, orderId);
    }
    
    public List<Order> findAll() {
        Query<Order> query = getCurrentSession().createQuery(
            "FROM Order ORDER BY orderDate DESC", Order.class);
        return query.getResultList();
    }
    
    public List<Order> findByBuyerId(Integer buyerId) {
        Query<Order> query = getCurrentSession().createQuery(
            "FROM Order WHERE buyer.userId = :buyerId ORDER BY orderDate DESC", Order.class);
        query.setParameter("buyerId", buyerId);
        return query.getResultList();
    }
    
    public List<Order> findBySellerId(Integer sellerId) {
        Query<Order> query = getCurrentSession().createQuery(
            "FROM Order WHERE seller.userId = :sellerId ORDER BY orderDate DESC", Order.class);
        query.setParameter("sellerId", sellerId);
        return query.getResultList();
    }
    
    public List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus) {
        Query<Order> query = getCurrentSession().createQuery(
            "FROM Order WHERE paymentStatus = :status ORDER BY orderDate DESC", Order.class);
        query.setParameter("status", paymentStatus);
        return query.getResultList();
    }
    
    public List<Order> findByShippingStatus(Order.ShippingStatus shippingStatus) {
        Query<Order> query = getCurrentSession().createQuery(
            "FROM Order WHERE shippingStatus = :status ORDER BY orderDate DESC", Order.class);
        query.setParameter("status", shippingStatus);
        return query.getResultList();
    }
    
    public List<Order> findOrdersInDateRange(Timestamp startDate, Timestamp endDate) {
        Query<Order> query = getCurrentSession().createQuery(
            "FROM Order WHERE orderDate BETWEEN :startDate AND :endDate ORDER BY orderDate DESC", Order.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    public List<Order> findPendingOrders() {
        Query<Order> query = getCurrentSession().createQuery(
            "FROM Order WHERE paymentStatus = 'PENDING' ORDER BY orderDate ASC", Order.class);
        return query.getResultList();
    }
    
    public List<Order> findOrdersToShip() {
        Query<Order> query = getCurrentSession().createQuery(
            "FROM Order WHERE paymentStatus = 'PAID' AND shippingStatus = 'PENDING' ORDER BY orderDate ASC", Order.class);
        return query.getResultList();
    }
    
    public Long getOrderCount() {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(o) FROM Order o", Long.class);
        return query.uniqueResult();
    }
    
    public Long getOrderCountByBuyer(Integer buyerId) {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(o) FROM Order o WHERE o.buyer.userId = :buyerId", Long.class);
        query.setParameter("buyerId", buyerId);
        return query.uniqueResult();
    }
    
    public Long getOrderCountBySeller(Integer sellerId) {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(o) FROM Order o WHERE o.seller.userId = :sellerId", Long.class);
        query.setParameter("sellerId", sellerId);
        return query.uniqueResult();
    }
    
    public BigDecimal getTotalSalesBySeller(Integer sellerId) {
        Query<BigDecimal> query = getCurrentSession().createQuery(
            "SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
            "WHERE o.seller.userId = :sellerId AND o.paymentStatus = 'PAID'", BigDecimal.class);
        query.setParameter("sellerId", sellerId);
        BigDecimal total = query.uniqueResult();
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalPurchasesByBuyer(Integer buyerId) {
        Query<BigDecimal> query = getCurrentSession().createQuery(
            "SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
            "WHERE o.buyer.userId = :buyerId AND o.paymentStatus = 'PAID'", BigDecimal.class);
        query.setParameter("buyerId", buyerId);
        BigDecimal total = query.uniqueResult();
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public void updatePaymentStatus(Integer orderId, Order.PaymentStatus paymentStatus) {
        Query query = getCurrentSession().createQuery(
            "UPDATE Order SET paymentStatus = :status WHERE orderId = :orderId");
        query.setParameter("status", paymentStatus);
        query.setParameter("orderId", orderId);
        query.executeUpdate();
    }
    
    public void updateShippingStatus(Integer orderId, Order.ShippingStatus shippingStatus) {
        Query query = getCurrentSession().createQuery(
            "UPDATE Order SET shippingStatus = :status WHERE orderId = :orderId");
        query.setParameter("status", shippingStatus);
        query.setParameter("orderId", orderId);
        query.executeUpdate();
    }
    
    public void updateTrackingNumber(Integer orderId, String trackingNumber) {
        Query query = getCurrentSession().createQuery(
            "UPDATE Order SET trackingNumber = :trackingNumber WHERE orderId = :orderId");
        query.setParameter("trackingNumber", trackingNumber);
        query.setParameter("orderId", orderId);
        query.executeUpdate();
    }
    
    public void delete(Order order) {
        getCurrentSession().delete(order);
    }
    
    public void deleteById(Integer orderId) {
        Order order = findById(orderId);
        if (order != null) {
            delete(order);
        }
    }
    
    public Order update(Order order) {
        return (Order) getCurrentSession().merge(order);
    }
    
    public List<Object[]> getOrderStatistics() {
        Query<Object[]> query = getCurrentSession().createQuery(
            "SELECT COUNT(o), SUM(o.totalAmount), AVG(o.totalAmount), " +
            "COUNT(CASE WHEN o.paymentStatus = 'PAID' THEN 1 END), " +
            "COUNT(CASE WHEN o.shippingStatus = 'DELIVERED' THEN 1 END) " +
            "FROM Order o", Object[].class);
        return query.getResultList();
    }
    
    public List<Object[]> getSellerOrderStatistics(Integer sellerId) {
        Query<Object[]> query = getCurrentSession().createQuery(
            "SELECT COUNT(o), SUM(o.totalAmount), AVG(o.totalAmount) " +
            "FROM Order o WHERE o.seller.userId = :sellerId AND o.paymentStatus = 'PAID'", Object[].class);
        query.setParameter("sellerId", sellerId);
        return query.getResultList();
    }
    
    public List<Object[]> getBuyerOrderStatistics(Integer buyerId) {
        Query<Object[]> query = getCurrentSession().createQuery(
            "SELECT COUNT(o), SUM(o.totalAmount), AVG(o.totalAmount) " +
            "FROM Order o WHERE o.buyer.userId = :buyerId", Object[].class);
        query.setParameter("buyerId", buyerId);
        return query.getResultList();
    }
    
    public List<Order> findRecentOrders(int limit) {
        Query<Order> query = getCurrentSession().createQuery(
            "FROM Order ORDER BY orderDate DESC", Order.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
}