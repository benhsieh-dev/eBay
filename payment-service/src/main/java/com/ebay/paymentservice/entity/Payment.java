package com.ebay.paymentservice.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "payments")
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;
    
    // Store order reference as ID instead of entity (microservice boundary)
    @Column(name = "order_id", nullable = false)
    private Integer orderId;
    
    @Column(name = "buyer_id", nullable = false)
    private Integer buyerId;
    
    @Column(name = "seller_id", nullable = false)  
    private Integer sellerId;
    
    @Column(name = "payment_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal paymentAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    
    @Column(name = "payment_processor", length = 50)
    private String paymentProcessor; // STRIPE, PAYPAL, etc.
    
    @Column(name = "processor_payment_id", length = 255)
    private String processorPaymentId; // External payment ID from processor
    
    @Column(name = "processor_transaction_id", length = 255)
    private String processorTransactionId;
    
    @Column(name = "payment_date")
    private Timestamp paymentDate;
    
    @Column(name = "processing_fee", precision = 8, scale = 2)
    private BigDecimal processingFee = BigDecimal.ZERO;
    
    @Column(name = "net_amount", precision = 10, scale = 2)
    private BigDecimal netAmount; // Amount after fees
    
    @Column(name = "currency", length = 3)
    private String currency = "USD";
    
    @Column(name = "payment_details", columnDefinition = "TEXT")
    private String paymentDetails; // JSON string with additional details
    
    @Column(name = "failure_reason", length = 500)
    private String failureReason;
    
    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount = BigDecimal.ZERO;
    
    @Column(name = "refund_date")
    private Timestamp refundDate;
    
    @Column(name = "refund_reason", length = 500)
    private String refundReason;
    
    @Column(name = "created_date")
    private Timestamp createdDate;
    
    @Column(name = "updated_date")
    private Timestamp updatedDate;
    
    // Enums
    public enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, PAYPAL, STRIPE, BANK_TRANSFER, APPLE_PAY, GOOGLE_PAY
    }
    
    public enum PaymentStatus {
        PENDING, PROCESSING, COMPLETED, FAILED, CANCELLED, REFUNDED, PARTIALLY_REFUNDED
    }
    
    // Constructors
    public Payment() {
        this.createdDate = new Timestamp(System.currentTimeMillis());
        this.updatedDate = new Timestamp(System.currentTimeMillis());
    }
    
    public Payment(Integer orderId, Integer buyerId, Integer sellerId, BigDecimal paymentAmount, PaymentMethod paymentMethod) {
        this();
        this.orderId = orderId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
        this.netAmount = paymentAmount; // Will be updated when processing fees are calculated
    }
    
    // Getters and Setters
    public Integer getPaymentId() { return paymentId; }
    public void setPaymentId(Integer paymentId) { this.paymentId = paymentId; }
    
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
    
    public Integer getBuyerId() { return buyerId; }
    public void setBuyerId(Integer buyerId) { this.buyerId = buyerId; }
    
    public Integer getSellerId() { return sellerId; }
    public void setSellerId(Integer sellerId) { this.sellerId = sellerId; }
    
    public BigDecimal getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(BigDecimal paymentAmount) { this.paymentAmount = paymentAmount; }
    
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    
    public String getPaymentProcessor() { return paymentProcessor; }
    public void setPaymentProcessor(String paymentProcessor) { this.paymentProcessor = paymentProcessor; }
    
    public String getProcessorPaymentId() { return processorPaymentId; }
    public void setProcessorPaymentId(String processorPaymentId) { this.processorPaymentId = processorPaymentId; }
    
    public String getProcessorTransactionId() { return processorTransactionId; }
    public void setProcessorTransactionId(String processorTransactionId) { this.processorTransactionId = processorTransactionId; }
    
    public Timestamp getPaymentDate() { return paymentDate; }
    public void setPaymentDate(Timestamp paymentDate) { this.paymentDate = paymentDate; }
    
    public BigDecimal getProcessingFee() { return processingFee; }
    public void setProcessingFee(BigDecimal processingFee) { this.processingFee = processingFee; }
    
    public BigDecimal getNetAmount() { return netAmount; }
    public void setNetAmount(BigDecimal netAmount) { this.netAmount = netAmount; }
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public String getPaymentDetails() { return paymentDetails; }
    public void setPaymentDetails(String paymentDetails) { this.paymentDetails = paymentDetails; }
    
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    
    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    
    public Timestamp getRefundDate() { return refundDate; }
    public void setRefundDate(Timestamp refundDate) { this.refundDate = refundDate; }
    
    public String getRefundReason() { return refundReason; }
    public void setRefundReason(String refundReason) { this.refundReason = refundReason; }
    
    public Timestamp getCreatedDate() { return createdDate; }
    public void setCreatedDate(Timestamp createdDate) { this.createdDate = createdDate; }
    
    public Timestamp getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Timestamp updatedDate) { this.updatedDate = updatedDate; }
    
    // Utility methods
    public boolean isCompleted() {
        return paymentStatus == PaymentStatus.COMPLETED;
    }
    
    public boolean canBeRefunded() {
        return paymentStatus == PaymentStatus.COMPLETED && 
               (refundAmount == null || refundAmount.compareTo(paymentAmount) < 0);
    }
    
    public BigDecimal getRemainingRefundableAmount() {
        if (refundAmount == null) {
            return paymentAmount;
        }
        return paymentAmount.subtract(refundAmount);
    }
    
    public boolean isPartiallyRefunded() {
        return paymentStatus == PaymentStatus.PARTIALLY_REFUNDED ||
               (refundAmount != null && refundAmount.compareTo(BigDecimal.ZERO) > 0 && 
                refundAmount.compareTo(paymentAmount) < 0);
    }
    
    public boolean isFullyRefunded() {
        return paymentStatus == PaymentStatus.REFUNDED ||
               (refundAmount != null && refundAmount.compareTo(paymentAmount) >= 0);
    }
    
    public void updateTimestamp() {
        this.updatedDate = new Timestamp(System.currentTimeMillis());
    }
}