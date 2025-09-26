package service.payment;

import entity.Payment;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Interface for payment processing implementations
 */
public interface PaymentProcessor {
    
    /**
     * Process a payment
     * @param payment The payment to process
     * @param paymentDetails Additional payment details (card info, etc.)
     * @return PaymentResult containing success status and details
     */
    PaymentResult processPayment(Payment payment, Map<String, String> paymentDetails);
    
    /**
     * Process a refund
     * @param payment The original payment
     * @param refundAmount Amount to refund
     * @param reason Reason for refund
     * @return PaymentResult containing refund status and details
     */
    PaymentResult processRefund(Payment payment, BigDecimal refundAmount, String reason);
    
    /**
     * Verify a payment with the processor
     * @param processorPaymentId External payment ID
     * @return PaymentResult with current status
     */
    PaymentResult verifyPayment(String processorPaymentId);
    
    /**
     * Calculate processing fees for this processor
     * @param amount Payment amount
     * @return Processing fee amount
     */
    BigDecimal calculateProcessingFee(BigDecimal amount);
    
    /**
     * Get the processor name
     * @return Processor identifier
     */
    String getProcessorName();
    
    /**
     * Check if this processor supports the given payment method
     * @param paymentMethod Payment method to check
     * @return true if supported
     */
    boolean supportsPaymentMethod(Payment.PaymentMethod paymentMethod);
    
    /**
     * Validate payment details for this processor
     * @param paymentDetails Payment details to validate
     * @return ValidationResult with any errors
     */
    ValidationResult validatePaymentDetails(Map<String, String> paymentDetails);
    
    /**
     * Payment processing result
     */
    class PaymentResult {
        private final boolean success;
        private final String processorPaymentId;
        private final String processorTransactionId;
        private final String message;
        private final BigDecimal processingFee;
        private final Map<String, Object> additionalData;
        
        public PaymentResult(boolean success, String processorPaymentId, String processorTransactionId, 
                           String message, BigDecimal processingFee, Map<String, Object> additionalData) {
            this.success = success;
            this.processorPaymentId = processorPaymentId;
            this.processorTransactionId = processorTransactionId;
            this.message = message;
            this.processingFee = processingFee;
            this.additionalData = additionalData;
        }
        
        public boolean isSuccess() { return success; }
        public String getProcessorPaymentId() { return processorPaymentId; }
        public String getProcessorTransactionId() { return processorTransactionId; }
        public String getMessage() { return message; }
        public BigDecimal getProcessingFee() { return processingFee; }
        public Map<String, Object> getAdditionalData() { return additionalData; }
        
        public static PaymentResult success(String processorPaymentId, String processorTransactionId, 
                                          BigDecimal processingFee) {
            return new PaymentResult(true, processorPaymentId, processorTransactionId, 
                                   "Payment processed successfully", processingFee, null);
        }
        
        public static PaymentResult failure(String message) {
            return new PaymentResult(false, null, null, message, null, null);
        }
    }
    
    /**
     * Validation result for payment details
     */
    class ValidationResult {
        private final boolean valid;
        private final Map<String, String> errors;
        
        public ValidationResult(boolean valid, Map<String, String> errors) {
            this.valid = valid;
            this.errors = errors;
        }
        
        public boolean isValid() { return valid; }
        public Map<String, String> getErrors() { return errors; }
        
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }
        
        public static ValidationResult failure(Map<String, String> errors) {
            return new ValidationResult(false, errors);
        }
    }
}