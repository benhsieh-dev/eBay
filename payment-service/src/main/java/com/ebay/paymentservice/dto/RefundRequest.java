package com.ebay.paymentservice.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class RefundRequest {
    
    @NotNull(message = "Payment ID is required")
    private Integer paymentId;
    
    @NotNull(message = "Refund amount is required")
    @DecimalMin(value = "0.01", message = "Refund amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid refund amount format")
    private BigDecimal refundAmount;
    
    @NotBlank(message = "Refund reason is required")
    @Size(max = 500, message = "Refund reason cannot exceed 500 characters")
    private String reason;
    
    // Constructors
    public RefundRequest() {}
    
    public RefundRequest(Integer paymentId, BigDecimal refundAmount, String reason) {
        this.paymentId = paymentId;
        this.refundAmount = refundAmount;
        this.reason = reason;
    }
    
    // Getters and Setters
    public Integer getPaymentId() { return paymentId; }
    public void setPaymentId(Integer paymentId) { this.paymentId = paymentId; }
    
    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}