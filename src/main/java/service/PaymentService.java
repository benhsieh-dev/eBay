package service;

import dao.PaymentDAO;
import entity.Order;
import entity.Payment;
import service.payment.PaymentProcessor;
import service.payment.PaymentProcessor.PaymentResult;
import service.payment.PaymentProcessor.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PaymentService {
    
    @Autowired
    private PaymentDAO paymentDAO;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private List<PaymentProcessor> paymentProcessors;
    
    /**
     * Process payment for an order
     */
    public PaymentResult processOrderPayment(Integer orderId, Payment.PaymentMethod paymentMethod, 
                                           Map<String, String> paymentDetails) {
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null) {
                return PaymentResult.failure("Order not found");
            }
            
            if (order.getPaymentStatus() == Order.PaymentStatus.PAID) {
                return PaymentResult.failure("Order is already paid");
            }
            
            // Create payment record
            Payment payment = new Payment(order, order.getTotalAmount(), paymentMethod);
            
            // Find appropriate payment processor
            PaymentProcessor processor = findProcessor(paymentMethod);
            if (processor == null) {
                return PaymentResult.failure("Payment method not supported");
            }
            
            // Validate payment details
            ValidationResult validation = processor.validatePaymentDetails(paymentDetails);
            if (!validation.isValid()) {
                StringBuilder errorMsg = new StringBuilder("Payment validation failed: ");
                validation.getErrors().forEach((field, error) -> errorMsg.append(field).append(": ").append(error).append("; "));
                return PaymentResult.failure(errorMsg.toString());
            }
            
            // Calculate and set processing fees
            BigDecimal processingFee = processor.calculateProcessingFee(payment.getPaymentAmount());
            payment.setProcessingFee(processingFee);
            payment.setNetAmount(payment.getPaymentAmount().subtract(processingFee));
            payment.setPaymentProcessor(processor.getProcessorName());
            
            // Save payment record
            payment = paymentDAO.save(payment);
            
            // Process payment with external processor
            PaymentResult result = processor.processPayment(payment, paymentDetails);
            
            // Update payment record based on result
            updatePaymentFromResult(payment, result);
            
            // Update order status if payment successful
            if (result.isSuccess()) {
                orderService.updatePaymentStatus(orderId, Order.PaymentStatus.PAID);
                payment.setPaymentDate(new Timestamp(System.currentTimeMillis()));
                payment.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
            } else {
                payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
                payment.setFailureReason(result.getMessage());
            }
            
            payment.updateTimestamp();
            paymentDAO.update(payment);
            
            return result;
            
        } catch (Exception e) {
            return PaymentResult.failure("Payment processing error: " + e.getMessage());
        }
    }
    
    /**
     * Process a refund
     */
    public PaymentResult processRefund(Integer paymentId, BigDecimal refundAmount, String reason) {
        try {
            Payment payment = paymentDAO.findById(paymentId);
            if (payment == null) {
                return PaymentResult.failure("Payment not found");
            }
            
            if (!payment.canBeRefunded()) {
                return PaymentResult.failure("Payment cannot be refunded");
            }
            
            if (refundAmount.compareTo(payment.getRemainingRefundableAmount()) > 0) {
                return PaymentResult.failure("Refund amount exceeds remaining refundable amount");
            }
            
            // Find processor and process refund
            PaymentProcessor processor = findProcessor(payment.getPaymentMethod());
            if (processor == null) {
                return PaymentResult.failure("Payment processor not available for refund");
            }
            
            PaymentResult result = processor.processRefund(payment, refundAmount, reason);
            
            if (result.isSuccess()) {
                // Update payment record
                BigDecimal currentRefund = payment.getRefundAmount() != null ? payment.getRefundAmount() : BigDecimal.ZERO;
                BigDecimal newRefundAmount = currentRefund.add(refundAmount);
                
                payment.setRefundAmount(newRefundAmount);
                payment.setRefundDate(new Timestamp(System.currentTimeMillis()));
                payment.setRefundReason(reason);
                
                // Update status based on refund amount
                if (newRefundAmount.compareTo(payment.getPaymentAmount()) >= 0) {
                    payment.setPaymentStatus(Payment.PaymentStatus.REFUNDED);
                } else {
                    payment.setPaymentStatus(Payment.PaymentStatus.PARTIALLY_REFUNDED);
                }
                
                payment.updateTimestamp();
                paymentDAO.update(payment);
            }
            
            return result;
            
        } catch (Exception e) {
            return PaymentResult.failure("Refund processing error: " + e.getMessage());
        }
    }
    
    /**
     * Get payment by ID
     */
    public Payment getPaymentById(Integer paymentId) {
        return paymentDAO.findById(paymentId);
    }
    
    /**
     * Get payment by order ID
     */
    public Payment getPaymentByOrderId(Integer orderId) {
        return paymentDAO.findByOrderId(orderId);
    }
    
    /**
     * Get payments by seller (for seller analytics)
     */
    public List<Payment> getPaymentsBySeller(Integer sellerId) {
        return paymentDAO.findBySellerId(sellerId);
    }
    
    /**
     * Get payments by buyer
     */
    public List<Payment> getPaymentsByBuyer(Integer buyerId) {
        return paymentDAO.findByBuyerId(buyerId);
    }
    
    /**
     * Verify payment status with processor
     */
    public PaymentResult verifyPayment(Integer paymentId) {
        Payment payment = paymentDAO.findById(paymentId);
        if (payment == null) {
            return PaymentResult.failure("Payment not found");
        }
        
        PaymentProcessor processor = findProcessor(payment.getPaymentMethod());
        if (processor == null) {
            return PaymentResult.failure("Payment processor not available");
        }
        
        return processor.verifyPayment(payment.getProcessorPaymentId());
    }
    
    /**
     * Get payment statistics for seller
     */
    public PaymentStatistics getSellerPaymentStatistics(Integer sellerId, Timestamp startDate, Timestamp endDate) {
        List<Payment> payments = paymentDAO.findBySellerAndDateRange(sellerId, startDate, endDate);
        
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalFees = BigDecimal.ZERO;
        BigDecimal totalRefunds = BigDecimal.ZERO;
        int completedPayments = 0;
        int failedPayments = 0;
        
        for (Payment payment : payments) {
            if (payment.getPaymentStatus() == Payment.PaymentStatus.COMPLETED) {
                totalRevenue = totalRevenue.add(payment.getNetAmount());
                totalFees = totalFees.add(payment.getProcessingFee());
                completedPayments++;
            } else if (payment.getPaymentStatus() == Payment.PaymentStatus.FAILED) {
                failedPayments++;
            }
            
            if (payment.getRefundAmount() != null) {
                totalRefunds = totalRefunds.add(payment.getRefundAmount());
            }
        }
        
        return new PaymentStatistics(totalRevenue, totalFees, totalRefunds, 
                                   completedPayments, failedPayments, payments.size());
    }
    
    /**
     * Find appropriate payment processor for method
     */
    private PaymentProcessor findProcessor(Payment.PaymentMethod paymentMethod) {
        return paymentProcessors.stream()
                .filter(processor -> processor.supportsPaymentMethod(paymentMethod))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Update payment record from processor result
     */
    private void updatePaymentFromResult(Payment payment, PaymentResult result) {
        payment.setProcessorPaymentId(result.getProcessorPaymentId());
        payment.setProcessorTransactionId(result.getProcessorTransactionId());
        
        if (result.getProcessingFee() != null) {
            payment.setProcessingFee(result.getProcessingFee());
            payment.setNetAmount(payment.getPaymentAmount().subtract(result.getProcessingFee()));
        }
        
        if (result.getAdditionalData() != null) {
            // Convert additional data to JSON string
            // In a real implementation, you'd use a JSON library like Jackson
            payment.setPaymentDetails(result.getAdditionalData().toString());
        }
    }
    
    /**
     * Payment statistics helper class
     */
    public static class PaymentStatistics {
        private final BigDecimal totalRevenue;
        private final BigDecimal totalFees;
        private final BigDecimal totalRefunds;
        private final int completedPayments;
        private final int failedPayments;
        private final int totalPayments;
        
        public PaymentStatistics(BigDecimal totalRevenue, BigDecimal totalFees, BigDecimal totalRefunds,
                               int completedPayments, int failedPayments, int totalPayments) {
            this.totalRevenue = totalRevenue;
            this.totalFees = totalFees;
            this.totalRefunds = totalRefunds;
            this.completedPayments = completedPayments;
            this.failedPayments = failedPayments;
            this.totalPayments = totalPayments;
        }
        
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public BigDecimal getTotalFees() { return totalFees; }
        public BigDecimal getTotalRefunds() { return totalRefunds; }
        public int getCompletedPayments() { return completedPayments; }
        public int getFailedPayments() { return failedPayments; }
        public int getTotalPayments() { return totalPayments; }
        
        public BigDecimal getNetRevenue() {
            return totalRevenue.subtract(totalRefunds);
        }
        
        public double getSuccessRate() {
            if (totalPayments == 0) return 0.0;
            return (double) completedPayments / totalPayments * 100.0;
        }
    }
}