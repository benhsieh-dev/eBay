package dao;

import entity.Payment;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Repository
@Transactional
public class PaymentDAO {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    public Payment save(Payment payment) {
        if (payment.getPaymentId() == null) {
            entityManager.persist(payment);
        } else {
            entityManager.merge(payment);
        }
        return payment;
    }
    
    public Payment findById(Integer paymentId) {
        return entityManager.find(Payment.class, paymentId);
    }
    
    public Payment findByOrderId(Integer orderId) {
        TypedQuery<Payment> query = entityManager.createQuery(
            "FROM Payment WHERE order.orderId = :orderId", Payment.class);
        query.setParameter("orderId", orderId);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public List<Payment> findAll() {
        TypedQuery<Payment> query = entityManager.createQuery(
            "FROM Payment ORDER BY createdDate DESC", Payment.class);
        return query.getResultList();
    }
    
    public List<Payment> findByBuyerId(Integer buyerId) {
        TypedQuery<Payment> query = entityManager.createQuery(
            "FROM Payment p WHERE p.order.buyer.userId = :buyerId ORDER BY p.createdDate DESC", Payment.class);
        query.setParameter("buyerId", buyerId);
        return query.getResultList();
    }
    
    public List<Payment> findBySellerId(Integer sellerId) {
        TypedQuery<Payment> query = entityManager.createQuery(
            "FROM Payment p WHERE p.order.seller.userId = :sellerId ORDER BY p.createdDate DESC", Payment.class);
        query.setParameter("sellerId", sellerId);
        return query.getResultList();
    }
    
    public List<Payment> findByStatus(Payment.PaymentStatus status) {
        TypedQuery<Payment> query = entityManager.createQuery(
            "FROM Payment WHERE paymentStatus = :status ORDER BY createdDate DESC", Payment.class);
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    public List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod) {
        TypedQuery<Payment> query = entityManager.createQuery(
            "FROM Payment WHERE paymentMethod = :paymentMethod ORDER BY createdDate DESC", Payment.class);
        query.setParameter("paymentMethod", paymentMethod);
        return query.getResultList();
    }
    
    public List<Payment> findByProcessor(String processor) {
        TypedQuery<Payment> query = entityManager.createQuery(
            "FROM Payment WHERE paymentProcessor = :processor ORDER BY createdDate DESC", Payment.class);
        query.setParameter("processor", processor);
        return query.getResultList();
    }
    
    public List<Payment> findByDateRange(Timestamp startDate, Timestamp endDate) {
        TypedQuery<Payment> query = entityManager.createQuery(
            "FROM Payment WHERE createdDate BETWEEN :startDate AND :endDate ORDER BY createdDate DESC", Payment.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    public List<Payment> findBySellerAndDateRange(Integer sellerId, Timestamp startDate, Timestamp endDate) {
        TypedQuery<Payment> query = entityManager.createQuery(
            "FROM Payment p WHERE p.order.seller.userId = :sellerId AND p.createdDate BETWEEN :startDate AND :endDate ORDER BY p.createdDate DESC", Payment.class);
        query.setParameter("sellerId", sellerId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    public List<Payment> findByBuyerAndDateRange(Integer buyerId, Timestamp startDate, Timestamp endDate) {
        TypedQuery<Payment> query = entityManager.createQuery(
            "FROM Payment p WHERE p.order.buyer.userId = :buyerId AND p.createdDate BETWEEN :startDate AND :endDate ORDER BY p.createdDate DESC", Payment.class);
        query.setParameter("buyerId", buyerId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    public List<Payment> findFailedPayments() {
        TypedQuery<Payment> query = entityManager.createQuery(
            "FROM Payment WHERE paymentStatus = 'FAILED' ORDER BY createdDate DESC", Payment.class);
        return query.getResultList();
    }
    
    public List<Payment> findPendingPayments() {
        TypedQuery<Payment> query = entityManager.createQuery(
            "FROM Payment WHERE paymentStatus = 'PENDING' OR paymentStatus = 'PROCESSING' ORDER BY createdDate ASC", Payment.class);
        return query.getResultList();
    }
    
    public List<Payment> findRefundablePayments(Integer sellerId) {
        TypedQuery<Payment> query = entityManager.createQuery(
            "FROM Payment p WHERE p.order.seller.userId = :sellerId AND p.paymentStatus = 'COMPLETED' AND " +
            "(p.refundAmount IS NULL OR p.refundAmount < p.paymentAmount) ORDER BY p.createdDate DESC", Payment.class);
        query.setParameter("sellerId", sellerId);
        return query.getResultList();
    }
    
    public Payment findByProcessorPaymentId(String processorPaymentId) {
        TypedQuery<Payment> query = entityManager.createQuery(
            "FROM Payment WHERE processorPaymentId = :processorPaymentId", Payment.class);
        query.setParameter("processorPaymentId", processorPaymentId);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public Payment findByProcessorTransactionId(String processorTransactionId) {
        TypedQuery<Payment> query = entityManager.createQuery(
            "FROM Payment WHERE processorTransactionId = :processorTransactionId", Payment.class);
        query.setParameter("processorTransactionId", processorTransactionId);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public List<Payment> findPendingPaymentsSince(Timestamp since) {
        TypedQuery<Payment> query = entityManager.createQuery(
            "FROM Payment WHERE (paymentStatus = 'PENDING' OR paymentStatus = 'PROCESSING') " +
            "AND createdDate >= :since ORDER BY createdDate ASC", Payment.class);
        query.setParameter("since", since);
        return query.getResultList();
    }
    
    public List<Payment> findBySellerIdOrderByDate(Integer sellerId) {
        TypedQuery<Payment> query = entityManager.createQuery(
            "FROM Payment p WHERE p.order.seller.userId = :sellerId ORDER BY p.createdDate DESC", Payment.class);
        query.setParameter("sellerId", sellerId);
        return query.getResultList();
    }
    
    public List<Payment> findByBuyerIdOrderByDate(Integer buyerId) {
        TypedQuery<Payment> query = entityManager.createQuery(
            "FROM Payment p WHERE p.order.buyer.userId = :buyerId ORDER BY p.createdDate DESC", Payment.class);
        query.setParameter("buyerId", buyerId);
        return query.getResultList();
    }
    
    public Long getPaymentCount() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(p) FROM Payment p", Long.class);
        return query.getSingleResult();
    }
    
    public Long getPaymentCountBySeller(Integer sellerId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(p) FROM Payment p WHERE p.order.seller.userId = :sellerId", Long.class);
        query.setParameter("sellerId", sellerId);
        return query.getSingleResult();
    }
    
    public Long getPaymentCountByBuyer(Integer buyerId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(p) FROM Payment p WHERE p.order.buyer.userId = :buyerId", Long.class);
        query.setParameter("buyerId", buyerId);
        return query.getSingleResult();
    }
    
    public Payment update(Payment payment) {
        return entityManager.merge(payment);
    }
    
    public void delete(Payment payment) {
        if (entityManager.contains(payment)) {
            entityManager.remove(payment);
        } else {
            entityManager.remove(entityManager.merge(payment));
        }
    }
    
    public void deleteById(Integer paymentId) {
        Payment payment = findById(paymentId);
        if (payment != null) {
            delete(payment);
        }
    }
    
    // Statistics queries
    public List<Object[]> getPaymentStatistics() {
        TypedQuery<Object[]> query = entityManager.createQuery(
            "SELECT COUNT(p), SUM(p.paymentAmount), AVG(p.paymentAmount), SUM(p.processingFee), " +
            "COUNT(CASE WHEN p.paymentStatus = 'COMPLETED' THEN 1 END), " +
            "COUNT(CASE WHEN p.paymentStatus = 'FAILED' THEN 1 END) " +
            "FROM Payment p", Object[].class);
        return query.getResultList();
    }
    
    public List<Object[]> getSellerPaymentStatistics(Integer sellerId) {
        TypedQuery<Object[]> query = entityManager.createQuery(
            "SELECT COUNT(p), SUM(p.netAmount), AVG(p.paymentAmount), SUM(p.processingFee), SUM(p.refundAmount) " +
            "FROM Payment p WHERE p.order.seller.userId = :sellerId AND p.paymentStatus = 'COMPLETED'", Object[].class);
        query.setParameter("sellerId", sellerId);
        return query.getResultList();
    }
    
    public List<Object[]> getBuyerPaymentStatistics(Integer buyerId) {
        TypedQuery<Object[]> query = entityManager.createQuery(
            "SELECT COUNT(p), SUM(p.paymentAmount), AVG(p.paymentAmount) " +
            "FROM Payment p WHERE p.order.buyer.userId = :buyerId", Object[].class);
        query.setParameter("buyerId", buyerId);
        return query.getResultList();
    }
}