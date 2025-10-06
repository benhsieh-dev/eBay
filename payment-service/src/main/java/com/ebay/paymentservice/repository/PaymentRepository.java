package com.ebay.paymentservice.repository;

import com.ebay.paymentservice.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    
    /**
     * Find payment by order ID
     */
    Optional<Payment> findByOrderId(Integer orderId);
    
    /**
     * Find payments by buyer ID
     */
    List<Payment> findByBuyerIdOrderByCreatedDateDesc(Integer buyerId);
    
    /**
     * Find payments by seller ID
     */
    List<Payment> findBySellerIdOrderByCreatedDateDesc(Integer sellerId);
    
    /**
     * Find payments by seller and date range
     */
    @Query("SELECT p FROM Payment p WHERE p.sellerId = :sellerId " +
           "AND p.createdDate >= :startDate AND p.createdDate <= :endDate " +
           "ORDER BY p.createdDate DESC")
    List<Payment> findBySellerAndDateRange(@Param("sellerId") Integer sellerId,
                                         @Param("startDate") Timestamp startDate,
                                         @Param("endDate") Timestamp endDate);
    
    /**
     * Find payments by buyer and date range
     */
    @Query("SELECT p FROM Payment p WHERE p.buyerId = :buyerId " +
           "AND p.createdDate >= :startDate AND p.createdDate <= :endDate " +
           "ORDER BY p.createdDate DESC")
    List<Payment> findByBuyerAndDateRange(@Param("buyerId") Integer buyerId,
                                        @Param("startDate") Timestamp startDate,
                                        @Param("endDate") Timestamp endDate);
    
    /**
     * Find payments by status
     */
    List<Payment> findByPaymentStatusOrderByCreatedDateDesc(Payment.PaymentStatus status);
    
    /**
     * Find payments by processor payment ID
     */
    Optional<Payment> findByProcessorPaymentId(String processorPaymentId);
    
    /**
     * Find failed payments that need retry
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentStatus = 'FAILED' " +
           "AND p.createdDate >= :cutoffDate ORDER BY p.createdDate DESC")
    List<Payment> findFailedPaymentsForRetry(@Param("cutoffDate") Timestamp cutoffDate);
    
    /**
     * Find pending payments older than specified time
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentStatus = 'PENDING' " +
           "AND p.createdDate < :cutoffDate ORDER BY p.createdDate ASC")
    List<Payment> findStaleePendingPayments(@Param("cutoffDate") Timestamp cutoffDate);
    
    /**
     * Count payments by status for a seller
     */
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.sellerId = :sellerId AND p.paymentStatus = :status")
    Long countBySellerAndStatus(@Param("sellerId") Integer sellerId, 
                               @Param("status") Payment.PaymentStatus status);
    
    /**
     * Get total revenue for a seller
     */
    @Query("SELECT COALESCE(SUM(p.netAmount), 0) FROM Payment p " +
           "WHERE p.sellerId = :sellerId AND p.paymentStatus = 'COMPLETED'")
    java.math.BigDecimal getTotalRevenueForSeller(@Param("sellerId") Integer sellerId);
}