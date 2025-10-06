package com.ebay.paymentservice.dto;

import com.ebay.paymentservice.entity.Payment;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class PaymentResponse {
    
    private Integer paymentId;
    private Integer orderId;
    private Integer buyerId;
    private Integer sellerId;
    private BigDecimal paymentAmount;
    private Payment.PaymentMethod paymentMethod;
    private Payment.PaymentStatus paymentStatus;
    private String paymentProcessor;
    private String processorPaymentId;
    private String processorTransactionId;
    private Timestamp paymentDate;
    private BigDecimal processingFee;
    private BigDecimal netAmount;
    private String currency;
    private String failureReason;
    private BigDecimal refundAmount;
    private Timestamp refundDate;
    private String refundReason;
    private Timestamp createdDate;
    private Timestamp updatedDate;
    
    // Constructors
    public PaymentResponse() {}
    
    public PaymentResponse(Payment payment) {
        this.paymentId = payment.getPaymentId();
        this.orderId = payment.getOrderId();
        this.buyerId = payment.getBuyerId();
        this.sellerId = payment.getSellerId();
        this.paymentAmount = payment.getPaymentAmount();
        this.paymentMethod = payment.getPaymentMethod();
        this.paymentStatus = payment.getPaymentStatus();
        this.paymentProcessor = payment.getPaymentProcessor();
        this.processorPaymentId = payment.getProcessorPaymentId();
        this.processorTransactionId = payment.getProcessorTransactionId();
        this.paymentDate = payment.getPaymentDate();
        this.processingFee = payment.getProcessingFee();
        this.netAmount = payment.getNetAmount();
        this.currency = payment.getCurrency();
        this.failureReason = payment.getFailureReason();
        this.refundAmount = payment.getRefundAmount();
        this.refundDate = payment.getRefundDate();
        this.refundReason = payment.getRefundReason();
        this.createdDate = payment.getCreatedDate();
        this.updatedDate = payment.getUpdatedDate();
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
    
    public Payment.PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(Payment.PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public Payment.PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(Payment.PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }
    
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
        return paymentStatus == Payment.PaymentStatus.COMPLETED;
    }
    
    public boolean canBeRefunded() {
        return paymentStatus == Payment.PaymentStatus.COMPLETED && 
               (refundAmount == null || refundAmount.compareTo(paymentAmount) < 0);
    }
    
    public BigDecimal getRemainingRefundableAmount() {
        if (refundAmount == null) {
            return paymentAmount;
        }
        return paymentAmount.subtract(refundAmount);
    }
}