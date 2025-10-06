package com.ebay.paymentservice.processor;

import com.ebay.paymentservice.entity.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * PayPal payment processor implementation
 * In a production environment, this would integrate with PayPal's REST API
 */
@Component
public class PayPalPaymentProcessor implements PaymentProcessor {
    
    @Value("${paypal.client.id:}")
    private String clientId;
    
    @Value("${paypal.client.secret:}")
    private String clientSecret;
    
    @Value("${paypal.mode:sandbox}")
    private String mode; // sandbox or live
    
    @Value("${paypal.processing.fee.percentage:2.9}")
    private double processingFeePercentage;
    
    @Value("${paypal.processing.fee.fixed:0.30}")
    private double processingFeeFixed;
    
    @Override
    public PaymentResult processPayment(Payment payment, Map<String, String> paymentDetails) {
        try {
            // In a real implementation, this would make API calls to PayPal
            // For now, we'll simulate the process
            
            // Validate PayPal-specific requirements
            if (payment.getPaymentAmount().compareTo(new BigDecimal("0.50")) < 0) {
                return PaymentResult.failure("PayPal requires minimum payment of $0.50");
            }
            
            if (payment.getPaymentAmount().compareTo(new BigDecimal("10000.00")) > 0) {
                return PaymentResult.failure("PayPal payment limit exceeded ($10,000)");
            }
            
            // Simulate PayPal payment processing
            String paypalPaymentId = "PAYPAL_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String paypalTransactionId = "TXN_" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
            
            // Calculate processing fee
            BigDecimal processingFee = calculateProcessingFee(payment.getPaymentAmount());
            
            // Simulate processing delay and possible failure
            Thread.sleep(1000); // Simulate network delay
            
            // Simulate 95% success rate
            if (Math.random() > 0.95) {
                return PaymentResult.failure("PayPal payment declined. Please try again or use a different payment method.");
            }
            
            // Create additional data
            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put("paypal_payer_email", paymentDetails.get("paypal_email"));
            additionalData.put("paypal_mode", mode);
            additionalData.put("processing_time", System.currentTimeMillis());
            
            return new PaymentResult(true, paypalPaymentId, paypalTransactionId, 
                                   "PayPal payment processed successfully", processingFee, additionalData);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return PaymentResult.failure("PayPal payment processing interrupted");
        } catch (Exception e) {
            return PaymentResult.failure("PayPal payment processing error: " + e.getMessage());
        }
    }
    
    @Override
    public PaymentResult processRefund(Payment payment, BigDecimal refundAmount, String reason) {
        try {
            // In a real implementation, this would make API calls to PayPal
            
            if (payment.getProcessorPaymentId() == null || !payment.getProcessorPaymentId().startsWith("PAYPAL_")) {
                return PaymentResult.failure("Invalid PayPal payment ID for refund");
            }
            
            // Simulate PayPal refund processing
            String refundId = "REFUND_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            Thread.sleep(500); // Simulate network delay
            
            // Simulate 98% success rate for refunds
            if (Math.random() > 0.98) {
                return PaymentResult.failure("PayPal refund failed. Please try again later.");
            }
            
            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put("refund_id", refundId);
            additionalData.put("refund_reason", reason);
            additionalData.put("refund_time", System.currentTimeMillis());
            
            return new PaymentResult(true, refundId, null, 
                                   "PayPal refund processed successfully", BigDecimal.ZERO, additionalData);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return PaymentResult.failure("PayPal refund processing interrupted");
        } catch (Exception e) {
            return PaymentResult.failure("PayPal refund processing error: " + e.getMessage());
        }
    }
    
    @Override
    public PaymentResult verifyPayment(String processorPaymentId) {
        try {
            // In a real implementation, this would query PayPal's API
            
            if (processorPaymentId == null || !processorPaymentId.startsWith("PAYPAL_")) {
                return PaymentResult.failure("Invalid PayPal payment ID");
            }
            
            Thread.sleep(300); // Simulate API call
            
            // Simulate verification response
            Map<String, Object> verificationData = new HashMap<>();
            verificationData.put("status", "COMPLETED");
            verificationData.put("verified_at", System.currentTimeMillis());
            
            return new PaymentResult(true, processorPaymentId, null, 
                                   "Payment verified with PayPal", null, verificationData);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return PaymentResult.failure("PayPal verification interrupted");
        } catch (Exception e) {
            return PaymentResult.failure("PayPal verification error: " + e.getMessage());
        }
    }
    
    @Override
    public BigDecimal calculateProcessingFee(BigDecimal amount) {
        // PayPal fee structure: percentage + fixed fee
        BigDecimal percentageFee = amount.multiply(new BigDecimal(processingFeePercentage / 100.0));
        BigDecimal fixedFee = new BigDecimal(processingFeeFixed);
        
        return percentageFee.add(fixedFee).setScale(2, RoundingMode.HALF_UP);
    }
    
    @Override
    public String getProcessorName() {
        return "PAYPAL";
    }
    
    @Override
    public boolean supportsPaymentMethod(Payment.PaymentMethod paymentMethod) {
        return paymentMethod == Payment.PaymentMethod.PAYPAL;
    }
    
    @Override
    public ValidationResult validatePaymentDetails(Map<String, String> paymentDetails) {
        Map<String, String> errors = new HashMap<>();
        
        // PayPal typically requires email for guest checkout or redirects to PayPal
        String email = paymentDetails.get("paypal_email");
        if (email == null || email.trim().isEmpty()) {
            errors.put("paypal_email", "PayPal email is required");
        } else if (!isValidEmail(email)) {
            errors.put("paypal_email", "Invalid email format");
        }
        
        // Validate return URLs if provided
        String returnUrl = paymentDetails.get("return_url");
        String cancelUrl = paymentDetails.get("cancel_url");
        
        if (returnUrl != null && !isValidUrl(returnUrl)) {
            errors.put("return_url", "Invalid return URL format");
        }
        
        if (cancelUrl != null && !isValidUrl(cancelUrl)) {
            errors.put("cancel_url", "Invalid cancel URL format");
        }
        
        if (errors.isEmpty()) {
            return ValidationResult.success();
        } else {
            return ValidationResult.failure(errors);
        }
    }
    
    // Helper methods
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    }
    
    private boolean isValidUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }
}