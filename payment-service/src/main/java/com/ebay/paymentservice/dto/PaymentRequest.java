package com.ebay.paymentservice.dto;

import com.ebay.paymentservice.entity.Payment;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Map;

public class PaymentRequest {
    
    @NotNull(message = "Order ID is required")
    private Integer orderId;
    
    @NotNull(message = "Buyer ID is required")
    private Integer buyerId;
    
    @NotNull(message = "Seller ID is required") 
    private Integer sellerId;
    
    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Payment amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid payment amount format")
    private BigDecimal paymentAmount;
    
    @NotNull(message = "Payment method is required")
    private Payment.PaymentMethod paymentMethod;
    
    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    private String currency = "USD";
    
    @NotNull(message = "Payment details are required")
    private Map<String, String> paymentDetails;
    
    // Constructors
    public PaymentRequest() {}
    
    public PaymentRequest(Integer orderId, Integer buyerId, Integer sellerId, 
                         BigDecimal paymentAmount, Payment.PaymentMethod paymentMethod,
                         Map<String, String> paymentDetails) {
        this.orderId = orderId;
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
        this.paymentDetails = paymentDetails;
    }
    
    // Getters and Setters
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
    
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    
    public Map<String, String> getPaymentDetails() { return paymentDetails; }
    public void setPaymentDetails(Map<String, String> paymentDetails) { this.paymentDetails = paymentDetails; }
}