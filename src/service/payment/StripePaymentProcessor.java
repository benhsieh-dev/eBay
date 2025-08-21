package service.payment;

import entity.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Stripe payment processor implementation
 * In a production environment, this would integrate with Stripe's API
 */
@Component
public class StripePaymentProcessor implements PaymentProcessor {
    
    @Value("${stripe.secret.key:}")
    private String secretKey;
    
    @Value("${stripe.publishable.key:}")
    private String publishableKey;
    
    @Value("${stripe.webhook.secret:}")
    private String webhookSecret;
    
    @Value("${stripe.processing.fee.percentage:2.9}")
    private double processingFeePercentage;
    
    @Value("${stripe.processing.fee.fixed:0.30}")
    private double processingFeeFixed;
    
    @Override
    public PaymentResult processPayment(Payment payment, Map<String, String> paymentDetails) {
        try {
            // In a real implementation, this would make API calls to Stripe
            // For now, we'll simulate the process
            
            // Validate Stripe-specific requirements
            if (payment.getPaymentAmount().compareTo(new BigDecimal("0.50")) < 0) {
                return PaymentResult.failure("Stripe requires minimum payment of $0.50");
            }
            
            // Validate card details
            ValidationResult validation = validateCardDetails(paymentDetails);
            if (!validation.isValid()) {
                StringBuilder errorMsg = new StringBuilder("Card validation failed: ");
                validation.getErrors().forEach((field, error) -> errorMsg.append(field).append(": ").append(error).append("; "));
                return PaymentResult.failure(errorMsg.toString());
            }
            
            // Simulate Stripe payment processing
            String stripeChargeId = "ch_" + generateStripeId();
            String stripeTransactionId = "txn_" + generateStripeId();
            
            // Calculate processing fee
            BigDecimal processingFee = calculateProcessingFee(payment.getPaymentAmount());
            
            // Simulate processing delay and possible failure
            Thread.sleep(800); // Simulate network delay
            
            // Simulate different failure scenarios based on card number
            String cardNumber = paymentDetails.get("cardNumber").replaceAll("\\s", "");
            if (cardNumber.equals("4000000000000002")) {
                return PaymentResult.failure("Your card was declined. (Generic decline)");
            } else if (cardNumber.equals("4000000000000069")) {
                return PaymentResult.failure("Your card has expired.");
            } else if (cardNumber.equals("4000000000000127")) {
                return PaymentResult.failure("Your card's security code is incorrect.");
            }
            
            // Simulate 97% success rate for valid cards
            if (Math.random() > 0.97) {
                return PaymentResult.failure("Your card was declined. Please try again or use a different card.");
            }
            
            // Create additional data
            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put("stripe_charge_id", stripeChargeId);
            additionalData.put("card_last4", cardNumber.substring(cardNumber.length() - 4));
            additionalData.put("card_brand", getCardBrand(cardNumber));
            additionalData.put("processing_time", System.currentTimeMillis());
            additionalData.put("stripe_fee", processingFee.toString());
            
            return new PaymentResult(true, stripeChargeId, stripeTransactionId, 
                                   "Card payment processed successfully", processingFee, additionalData);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return PaymentResult.failure("Payment processing interrupted");
        } catch (Exception e) {
            return PaymentResult.failure("Payment processing error: " + e.getMessage());
        }
    }
    
    @Override
    public PaymentResult processRefund(Payment payment, BigDecimal refundAmount, String reason) {
        try {
            // In a real implementation, this would make API calls to Stripe
            
            if (payment.getProcessorPaymentId() == null || !payment.getProcessorPaymentId().startsWith("ch_")) {
                return PaymentResult.failure("Invalid Stripe charge ID for refund");
            }
            
            // Simulate Stripe refund processing
            String refundId = "re_" + generateStripeId();
            
            Thread.sleep(500); // Simulate network delay
            
            // Simulate 99% success rate for refunds
            if (Math.random() > 0.99) {
                return PaymentResult.failure("Refund failed. Please try again later.");
            }
            
            Map<String, Object> additionalData = new HashMap<>();
            additionalData.put("stripe_refund_id", refundId);
            additionalData.put("refund_reason", reason);
            additionalData.put("refund_amount", refundAmount.toString());
            additionalData.put("refund_time", System.currentTimeMillis());
            
            return new PaymentResult(true, refundId, null, 
                                   "Refund processed successfully", BigDecimal.ZERO, additionalData);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return PaymentResult.failure("Refund processing interrupted");
        } catch (Exception e) {
            return PaymentResult.failure("Refund processing error: " + e.getMessage());
        }
    }
    
    @Override
    public PaymentResult verifyPayment(String processorPaymentId) {
        try {
            // In a real implementation, this would query Stripe's API
            
            if (processorPaymentId == null || !processorPaymentId.startsWith("ch_")) {
                return PaymentResult.failure("Invalid Stripe charge ID");
            }
            
            Thread.sleep(200); // Simulate API call
            
            // Simulate verification response
            Map<String, Object> verificationData = new HashMap<>();
            verificationData.put("status", "succeeded");
            verificationData.put("verified_at", System.currentTimeMillis());
            
            return new PaymentResult(true, processorPaymentId, null, 
                                   "Payment verified with Stripe", null, verificationData);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return PaymentResult.failure("Stripe verification interrupted");
        } catch (Exception e) {
            return PaymentResult.failure("Stripe verification error: " + e.getMessage());
        }
    }
    
    @Override
    public BigDecimal calculateProcessingFee(BigDecimal amount) {
        // Stripe fee structure: percentage + fixed fee
        BigDecimal percentageFee = amount.multiply(new BigDecimal(processingFeePercentage / 100.0));
        BigDecimal fixedFee = new BigDecimal(processingFeeFixed);
        
        return percentageFee.add(fixedFee).setScale(2, RoundingMode.HALF_UP);
    }
    
    @Override
    public String getProcessorName() {
        return "STRIPE";
    }
    
    @Override
    public boolean supportsPaymentMethod(Payment.PaymentMethod paymentMethod) {
        return paymentMethod == Payment.PaymentMethod.CREDIT_CARD || 
               paymentMethod == Payment.PaymentMethod.DEBIT_CARD ||
               paymentMethod == Payment.PaymentMethod.STRIPE ||
               paymentMethod == Payment.PaymentMethod.APPLE_PAY ||
               paymentMethod == Payment.PaymentMethod.GOOGLE_PAY;
    }
    
    @Override
    public ValidationResult validatePaymentDetails(Map<String, String> paymentDetails) {
        if (paymentDetails.get("stripe_token") != null) {
            // If using Stripe token, minimal validation needed
            return validateStripeToken(paymentDetails);
        } else {
            // If using raw card details, full validation needed
            return validateCardDetails(paymentDetails);
        }
    }
    
    private ValidationResult validateStripeToken(Map<String, String> paymentDetails) {
        Map<String, String> errors = new HashMap<>();
        
        String token = paymentDetails.get("stripe_token");
        if (token == null || token.trim().isEmpty()) {
            errors.put("stripe_token", "Stripe token is required");
        } else if (!token.startsWith("tok_")) {
            errors.put("stripe_token", "Invalid Stripe token format");
        }
        
        if (errors.isEmpty()) {
            return ValidationResult.success();
        } else {
            return ValidationResult.failure(errors);
        }
    }
    
    private ValidationResult validateCardDetails(Map<String, String> paymentDetails) {
        Map<String, String> errors = new HashMap<>();
        
        // Validate card number
        String cardNumber = paymentDetails.get("cardNumber");
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            errors.put("cardNumber", "Card number is required");
        } else {
            cardNumber = cardNumber.replaceAll("\\s", "");
            if (!isValidCardNumber(cardNumber)) {
                errors.put("cardNumber", "Invalid card number");
            }
        }
        
        // Validate expiry date
        String expiryDate = paymentDetails.get("expiryDate");
        if (expiryDate == null || expiryDate.trim().isEmpty()) {
            errors.put("expiryDate", "Expiry date is required");
        } else if (!expiryDate.matches("\\d{2}/\\d{2}")) {
            errors.put("expiryDate", "Invalid expiry date format (MM/YY)");
        } else if (isCardExpired(expiryDate)) {
            errors.put("expiryDate", "Card has expired");
        }
        
        // Validate CVV
        String cvv = paymentDetails.get("cvv");
        if (cvv == null || cvv.trim().isEmpty()) {
            errors.put("cvv", "CVV is required");
        } else if (!cvv.matches("\\d{3,4}")) {
            errors.put("cvv", "Invalid CVV format");
        }
        
        // Validate cardholder name
        String cardHolderName = paymentDetails.get("cardHolderName");
        if (cardHolderName == null || cardHolderName.trim().isEmpty()) {
            errors.put("cardHolderName", "Cardholder name is required");
        } else if (cardHolderName.length() < 2) {
            errors.put("cardHolderName", "Cardholder name too short");
        }
        
        if (errors.isEmpty()) {
            return ValidationResult.success();
        } else {
            return ValidationResult.failure(errors);
        }
    }
    
    /**
     * Create Stripe Payment Intent
     * In a real implementation, this would create a PaymentIntent with Stripe
     */
    public Map<String, Object> createPaymentIntent(Payment payment) {
        Map<String, Object> intent = new HashMap<>();
        intent.put("id", "pi_" + generateStripeId());
        intent.put("client_secret", "pi_" + generateStripeId() + "_secret_" + generateStripeId());
        intent.put("amount", payment.getPaymentAmount().multiply(new BigDecimal(100)).intValue()); // Stripe uses cents
        intent.put("currency", payment.getCurrency().toLowerCase());
        intent.put("status", "requires_payment_method");
        intent.put("created", System.currentTimeMillis() / 1000);
        
        return intent;
    }
    
    /**
     * Handle Stripe webhook
     */
    public boolean handleWebhook(String payload, String signature) {
        try {
            // In a real implementation, this would verify the webhook signature
            // and process the event
            
            if (signature == null || !signature.startsWith("t=")) {
                return false;
            }
            
            // Simulate webhook processing
            Thread.sleep(100);
            
            return true;
            
        } catch (Exception e) {
            return false;
        }
    }
    
    // Helper methods
    private boolean isValidCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 13 || cardNumber.length() > 19) {
            return false;
        }
        
        // Luhn algorithm for card validation
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            char c = cardNumber.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }
            
            int n = Character.getNumericValue(c);
            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = n % 10 + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        
        return sum % 10 == 0;
    }
    
    private boolean isCardExpired(String expiryDate) {
        try {
            String[] parts = expiryDate.split("/");
            int month = Integer.parseInt(parts[0]);
            int year = 2000 + Integer.parseInt(parts[1]);
            
            java.util.Calendar cal = java.util.Calendar.getInstance();
            int currentYear = cal.get(java.util.Calendar.YEAR);
            int currentMonth = cal.get(java.util.Calendar.MONTH) + 1;
            
            return year < currentYear || (year == currentYear && month < currentMonth);
            
        } catch (Exception e) {
            return true; // If we can't parse, consider it expired
        }
    }
    
    private String getCardBrand(String cardNumber) {
        if (cardNumber.startsWith("4")) return "visa";
        if (cardNumber.startsWith("5") || cardNumber.startsWith("2")) return "mastercard";
        if (cardNumber.startsWith("3")) return "amex";
        if (cardNumber.startsWith("6")) return "discover";
        return "unknown";
    }
    
    private String generateStripeId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 24);
    }
}