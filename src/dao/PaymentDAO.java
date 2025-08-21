package dao;

import entity.Payment;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Repository
@Transactional
public class PaymentDAO {
    
    @Autowired
    private SessionFactory sessionFactory;
    
    private Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
    
    public Payment save(Payment payment) {
        getCurrentSession().saveOrUpdate(payment);
        return payment;
    }
    
    public Payment findById(Integer paymentId) {
        return getCurrentSession().get(Payment.class, paymentId);
    }
    
    public Payment findByOrderId(Integer orderId) {
        Query<Payment> query = getCurrentSession().createQuery(
            "FROM Payment WHERE order.orderId = :orderId", Payment.class);
        query.setParameter("orderId", orderId);
        return query.uniqueResult();
    }
    
    public List<Payment> findAll() {
        Query<Payment> query = getCurrentSession().createQuery(
            "FROM Payment ORDER BY createdDate DESC", Payment.class);
        return query.getResultList();
    }
    
    public List<Payment> findByBuyerId(Integer buyerId) {
        Query<Payment> query = getCurrentSession().createQuery(
            "FROM Payment p WHERE p.order.buyer.userId = :buyerId ORDER BY p.createdDate DESC", Payment.class);
        query.setParameter("buyerId", buyerId);
        return query.getResultList();
    }
    
    public List<Payment> findBySellerId(Integer sellerId) {
        Query<Payment> query = getCurrentSession().createQuery(
            "FROM Payment p WHERE p.order.seller.userId = :sellerId ORDER BY p.createdDate DESC", Payment.class);
        query.setParameter("sellerId", sellerId);
        return query.getResultList();
    }
    
    public List<Payment> findByStatus(Payment.PaymentStatus status) {
        Query<Payment> query = getCurrentSession().createQuery(
            "FROM Payment WHERE paymentStatus = :status ORDER BY createdDate DESC", Payment.class);
        query.setParameter("status", status);
        return query.getResultList();
    }
    
    public List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod) {
        Query<Payment> query = getCurrentSession().createQuery(
            "FROM Payment WHERE paymentMethod = :paymentMethod ORDER BY createdDate DESC", Payment.class);
        query.setParameter("paymentMethod", paymentMethod);
        return query.getResultList();
    }
    
    public List<Payment> findByProcessor(String processor) {
        Query<Payment> query = getCurrentSession().createQuery(
            "FROM Payment WHERE paymentProcessor = :processor ORDER BY createdDate DESC", Payment.class);
        query.setParameter("processor", processor);
        return query.getResultList();
    }
    
    public List<Payment> findByDateRange(Timestamp startDate, Timestamp endDate) {
        Query<Payment> query = getCurrentSession().createQuery(
            "FROM Payment WHERE createdDate BETWEEN :startDate AND :endDate ORDER BY createdDate DESC", Payment.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    public List<Payment> findBySellerAndDateRange(Integer sellerId, Timestamp startDate, Timestamp endDate) {
        Query<Payment> query = getCurrentSession().createQuery(
            "FROM Payment p WHERE p.order.seller.userId = :sellerId AND p.createdDate BETWEEN :startDate AND :endDate ORDER BY p.createdDate DESC", Payment.class);
        query.setParameter("sellerId", sellerId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    public List<Payment> findByBuyerAndDateRange(Integer buyerId, Timestamp startDate, Timestamp endDate) {
        Query<Payment> query = getCurrentSession().createQuery(
            "FROM Payment p WHERE p.order.buyer.userId = :buyerId AND p.createdDate BETWEEN :startDate AND :endDate ORDER BY p.createdDate DESC", Payment.class);
        query.setParameter("buyerId", buyerId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }
    
    public List<Payment> findFailedPayments() {
        Query<Payment> query = getCurrentSession().createQuery(
            "FROM Payment WHERE paymentStatus = 'FAILED' ORDER BY createdDate DESC", Payment.class);
        return query.getResultList();
    }
    
    public List<Payment> findPendingPayments() {
        Query<Payment> query = getCurrentSession().createQuery(
            "FROM Payment WHERE paymentStatus = 'PENDING' OR paymentStatus = 'PROCESSING' ORDER BY createdDate ASC", Payment.class);
        return query.getResultList();
    }
    
    public List<Payment> findRefundablePayments(Integer sellerId) {
        Query<Payment> query = getCurrentSession().createQuery(
            "FROM Payment p WHERE p.order.seller.userId = :sellerId AND p.paymentStatus = 'COMPLETED' AND " +
            "(p.refundAmount IS NULL OR p.refundAmount < p.paymentAmount) ORDER BY p.createdDate DESC", Payment.class);
        query.setParameter("sellerId", sellerId);
        return query.getResultList();
    }
    
    public Payment findByProcessorPaymentId(String processorPaymentId) {
        Query<Payment> query = getCurrentSession().createQuery(
            "FROM Payment WHERE processorPaymentId = :processorPaymentId", Payment.class);
        query.setParameter("processorPaymentId", processorPaymentId);
        return query.uniqueResult();
    }
    
    public Payment findByProcessorTransactionId(String processorTransactionId) {
        Query<Payment> query = getCurrentSession().createQuery(
            "FROM Payment WHERE processorTransactionId = :processorTransactionId", Payment.class);
        query.setParameter("processorTransactionId", processorTransactionId);
        return query.uniqueResult();
    }
    
    public List<Payment> findPendingPaymentsSince(Timestamp since) {
        Query<Payment> query = getCurrentSession().createQuery(
            "FROM Payment WHERE (paymentStatus = 'PENDING' OR paymentStatus = 'PROCESSING') " +
            "AND createdDate >= :since ORDER BY createdDate ASC", Payment.class);
        query.setParameter("since", since);
        return query.getResultList();
    }
    
    public List<Payment> findBySellerIdOrderByDate(Integer sellerId) {
        Query<Payment> query = getCurrentSession().createQuery(
            "FROM Payment p WHERE p.order.seller.userId = :sellerId ORDER BY p.createdDate DESC", Payment.class);
        query.setParameter("sellerId", sellerId);
        return query.getResultList();
    }
    
    public List<Payment> findByBuyerIdOrderByDate(Integer buyerId) {
        Query<Payment> query = getCurrentSession().createQuery(
            "FROM Payment p WHERE p.order.buyer.userId = :buyerId ORDER BY p.createdDate DESC", Payment.class);
        query.setParameter("buyerId", buyerId);
        return query.getResultList();
    }
    
    public Long getPaymentCount() {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(p) FROM Payment p", Long.class);
        return query.uniqueResult();
    }
    
    public Long getPaymentCountBySeller(Integer sellerId) {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(p) FROM Payment p WHERE p.order.seller.userId = :sellerId", Long.class);
        query.setParameter("sellerId", sellerId);
        return query.uniqueResult();
    }
    
    public Long getPaymentCountByBuyer(Integer buyerId) {
        Query<Long> query = getCurrentSession().createQuery(
            "SELECT COUNT(p) FROM Payment p WHERE p.order.buyer.userId = :buyerId", Long.class);
        query.setParameter("buyerId", buyerId);
        return query.uniqueResult();
    }
    
    public Payment update(Payment payment) {
        return (Payment) getCurrentSession().merge(payment);
    }
    
    public void delete(Payment payment) {
        getCurrentSession().delete(payment);
    }
    
    public void deleteById(Integer paymentId) {
        Payment payment = findById(paymentId);
        if (payment != null) {
            delete(payment);
        }
    }
    
    // Statistics queries
    public List<Object[]> getPaymentStatistics() {
        Query<Object[]> query = getCurrentSession().createQuery(
            "SELECT COUNT(p), SUM(p.paymentAmount), AVG(p.paymentAmount), SUM(p.processingFee), " +
            "COUNT(CASE WHEN p.paymentStatus = 'COMPLETED' THEN 1 END), " +
            "COUNT(CASE WHEN p.paymentStatus = 'FAILED' THEN 1 END) " +
            "FROM Payment p", Object[].class);
        return query.getResultList();
    }
    
    public List<Object[]> getSellerPaymentStatistics(Integer sellerId) {
        Query<Object[]> query = getCurrentSession().createQuery(
            "SELECT COUNT(p), SUM(p.netAmount), AVG(p.paymentAmount), SUM(p.processingFee), SUM(p.refundAmount) " +
            "FROM Payment p WHERE p.order.seller.userId = :sellerId AND p.paymentStatus = 'COMPLETED'", Object[].class);
        query.setParameter("sellerId", sellerId);
        return query.getResultList();
    }
    
    public List<Object[]> getBuyerPaymentStatistics(Integer buyerId) {
        Query<Object[]> query = getCurrentSession().createQuery(
            "SELECT COUNT(p), SUM(p.paymentAmount), AVG(p.paymentAmount) " +
            "FROM Payment p WHERE p.order.buyer.userId = :buyerId", Object[].class);
        query.setParameter("buyerId", buyerId);
        return query.getResultList();
    }
}