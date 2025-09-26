package dao;

import entity.Order;
import entity.OrderItem;
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
public class OrderDAO {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public Order save(Order order) {
        if (order.getOrderId() == null) {
            entityManager.persist(order);
        } else {
            entityManager.merge(order);
        }
        return order;
    }
    
    public Order findById(Integer orderId) {
        return entityManager.find(Order.class, orderId);
    }
    
    public List<Order> findAll() {
        TypedQuery<Order> query = entityManager.createQuery(
            "FROM Order ORDER BY orderDate DESC", Order.class);
        return query.getResultList();
    }
    
    public List<Order> findByBuyerId(Integer buyerId) {
        TypedQuery<Order> query = entityManager.createQuery(
            "FROM Order WHERE buyer.userId = :buyerId ORDER BY orderDate DESC", Order.class);
        query.setParameter("buyerId", buyerId);
        return query.getResultList();
    }
    
    public List<Order> findBySellerId(Integer sellerId) {
        TypedQuery<Order> query = entityManager.createQuery(
            "FROM Order WHERE seller.userId = :sellerId ORDER BY orderDate DESC", Order.class);
        query.setParameter("sellerId", sellerId);
        return query.getResultList();
    }
    
    public List<Order> findByPaymentStatus(Order.PaymentStatus paymentStatus) {
        TypedQuery<Order> query = entityManager.createQuery(
            "FROM Order WHERE paymentStatus = :status ORDER BY orderDate DESC", Order.class);
        query.setParameter("status", paymentStatus);
        return query.getResultList();
    }
    
    public List<Order> findByShippingStatus(Order.ShippingStatus shippingStatus) {
        TypedQuery<Order> query = entityManager.createQuery(
            "FROM Order WHERE shippingStatus = :status ORDER BY orderDate DESC", Order.class);
        query.setParameter("status", shippingStatus);
        return query.getResultList();
    }
    
    public List<Order> findOrdersInDateRange(Timestamp startDate, Timestamp endDate) {
        TypedQuery<Order> query = entityManager.createQuery(
            "FROM Order WHERE orderDate BETWEEN :startDate AND :endDate ORDER BY orderDate DESC", Order.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    public List<Order> findPendingOrders() {
        TypedQuery<Order> query = entityManager.createQuery(
            "FROM Order WHERE paymentStatus = 'PENDING' ORDER BY orderDate ASC", Order.class);
        return query.getResultList();
    }
    
    public List<Order> findOrdersToShip() {
        TypedQuery<Order> query = entityManager.createQuery(
            "FROM Order WHERE paymentStatus = 'PAID' AND shippingStatus = 'PENDING' ORDER BY orderDate ASC", Order.class);
        return query.getResultList();
    }
    
    public Long getOrderCount() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(o) FROM Order o", Long.class);
        return query.getSingleResult();
    }
    
    public Long getOrderCountByBuyer(Integer buyerId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(o) FROM Order o WHERE o.buyer.userId = :buyerId", Long.class);
        query.setParameter("buyerId", buyerId);
        return query.getSingleResult();
    }
    
    public Long getOrderCountBySeller(Integer sellerId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(o) FROM Order o WHERE o.seller.userId = :sellerId", Long.class);
        query.setParameter("sellerId", sellerId);
        return query.getSingleResult();
    }
    
    public BigDecimal getTotalSalesBySeller(Integer sellerId) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
            "WHERE o.seller.userId = :sellerId AND o.paymentStatus = 'PAID'", BigDecimal.class);
        query.setParameter("sellerId", sellerId);
        BigDecimal total = query.getSingleResult();
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public BigDecimal getTotalPurchasesByBuyer(Integer buyerId) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
            "WHERE o.buyer.userId = :buyerId AND o.paymentStatus = 'PAID'", BigDecimal.class);
        query.setParameter("buyerId", buyerId);
        BigDecimal total = query.getSingleResult();
        return total != null ? total : BigDecimal.ZERO;
    }
    
    public void updatePaymentStatus(Integer orderId, Order.PaymentStatus paymentStatus) {
        entityManager.createQuery(
            "UPDATE Order SET paymentStatus = :status WHERE orderId = :orderId")
            .setParameter("status", paymentStatus)
            .setParameter("orderId", orderId)
            .executeUpdate();
    }
    
    public void updateShippingStatus(Integer orderId, Order.ShippingStatus shippingStatus) {
        entityManager.createQuery(
            "UPDATE Order SET shippingStatus = :status WHERE orderId = :orderId")
            .setParameter("status", shippingStatus)
            .setParameter("orderId", orderId)
            .executeUpdate();
    }
    
    public void updateTrackingNumber(Integer orderId, String trackingNumber) {
        entityManager.createQuery(
            "UPDATE Order SET trackingNumber = :trackingNumber WHERE orderId = :orderId")
            .setParameter("trackingNumber", trackingNumber)
            .setParameter("orderId", orderId)
            .executeUpdate();
    }
    
    public void delete(Order order) {
        if (entityManager.contains(order)) {
            entityManager.remove(order);
        } else {
            entityManager.remove(entityManager.merge(order));
        }
    }
    
    public void deleteById(Integer orderId) {
        Order order = findById(orderId);
        if (order != null) {
            delete(order);
        }
    }
    
    public Order update(Order order) {
        return entityManager.merge(order);
    }
    
    public List<Object[]> getOrderStatistics() {
        TypedQuery<Object[]> query = entityManager.createQuery(
            "SELECT COUNT(o), SUM(o.totalAmount), AVG(o.totalAmount), " +
            "COUNT(CASE WHEN o.paymentStatus = 'PAID' THEN 1 END), " +
            "COUNT(CASE WHEN o.shippingStatus = 'DELIVERED' THEN 1 END) " +
            "FROM Order o", Object[].class);
        return query.getResultList();
    }
    
    public List<Object[]> getSellerOrderStatistics(Integer sellerId) {
        TypedQuery<Object[]> query = entityManager.createQuery(
            "SELECT COUNT(o), SUM(o.totalAmount), AVG(o.totalAmount) " +
            "FROM Order o WHERE o.seller.userId = :sellerId AND o.paymentStatus = 'PAID'", Object[].class);
        query.setParameter("sellerId", sellerId);
        return query.getResultList();
    }
    
    public List<Object[]> getBuyerOrderStatistics(Integer buyerId) {
        TypedQuery<Object[]> query = entityManager.createQuery(
            "SELECT COUNT(o), SUM(o.totalAmount), AVG(o.totalAmount) " +
            "FROM Order o WHERE o.buyer.userId = :buyerId", Object[].class);
        query.setParameter("buyerId", buyerId);
        return query.getResultList();
    }
    
    public List<Order> findRecentOrders(int limit) {
        TypedQuery<Order> query = entityManager.createQuery(
            "FROM Order ORDER BY orderDate DESC", Order.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }
    
    public List<Order> findBySellerAndDateRange(Integer sellerId, Timestamp startDate, Timestamp endDate) {
        TypedQuery<Order> query = entityManager.createQuery(
            "FROM Order WHERE seller.userId = :sellerId AND orderDate BETWEEN :startDate AND :endDate ORDER BY orderDate DESC", Order.class);
        query.setParameter("sellerId", sellerId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
}