package com.ebay.paymentservice.service;

import com.ebay.paymentservice.dto.PaymentRequest;
import com.ebay.paymentservice.dto.PaymentResponse;
import com.ebay.paymentservice.dto.RefundRequest;
import com.ebay.paymentservice.entity.Payment;
import com.ebay.paymentservice.processor.PaymentProcessor;
import com.ebay.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private List<PaymentProcessor> paymentProcessors;
    
    @Autowired
    private PaymentEventService paymentEventService;
    
    /**
     * Process payment for an order
     */
    public PaymentResponse processPayment(PaymentRequest request) {
        try {
            // Create payment record
            Payment payment = new Payment(
                request.getOrderId(),
                request.getBuyerId(), 
                request.getSellerId(),
                request.getPaymentAmount(),
                request.getPaymentMethod()
            );
            payment.setCurrency(request.getCurrency());
            
            // Find appropriate payment processor
            PaymentProcessor processor = findProcessor(request.getPaymentMethod());
            if (processor == null) {
                throw new RuntimeException("Payment method not supported: " + request.getPaymentMethod());
            }
            
            // Validate payment details
            PaymentProcessor.ValidationResult validation = processor.validatePaymentDetails(request.getPaymentDetails());
            if (!validation.isValid()) {
                StringBuilder errorMsg = new StringBuilder("Payment validation failed: ");
                validation.getErrors().forEach((field, error) -> errorMsg.append(field).append(": ").append(error).append("; "));
                throw new RuntimeException(errorMsg.toString());
            }
            
            // Calculate and set processing fees
            BigDecimal processingFee = processor.calculateProcessingFee(payment.getPaymentAmount());
            payment.setProcessingFee(processingFee);
            payment.setNetAmount(payment.getPaymentAmount().subtract(processingFee));
            payment.setPaymentProcessor(processor.getProcessorName());
            
            // Save payment record
            payment = paymentRepository.save(payment);
            
            // Process payment with external processor
            PaymentProcessor.PaymentResult result = processor.processPayment(payment, request.getPaymentDetails());
            
            // Update payment record based on result
            updatePaymentFromResult(payment, result);
            
            // Update payment status
            if (result.isSuccess()) {
                payment.setPaymentDate(new Timestamp(System.currentTimeMillis()));
                payment.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
                
                // Publish payment completed event
                paymentEventService.publishPaymentCompleted(payment);
            } else {
                payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
                payment.setFailureReason(result.getMessage());
                
                // Publish payment failed event
                paymentEventService.publishPaymentFailed(payment, result.getMessage());
            }
            
            payment.updateTimestamp();
            payment = paymentRepository.save(payment);
            
            return new PaymentResponse(payment);
            
        } catch (Exception e) {
            throw new RuntimeException("Payment processing error: " + e.getMessage(), e);
        }
    }
    
    /**
     * Process a refund
     */
    public PaymentResponse processRefund(RefundRequest request) {
        try {
            Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new RuntimeException("Payment not found"));
            
            if (!payment.canBeRefunded()) {
                throw new RuntimeException("Payment cannot be refunded");
            }
            
            if (request.getRefundAmount().compareTo(payment.getRemainingRefundableAmount()) > 0) {
                throw new RuntimeException("Refund amount exceeds remaining refundable amount");
            }
            
            // Find processor and process refund
            PaymentProcessor processor = findProcessor(payment.getPaymentMethod());
            if (processor == null) {
                throw new RuntimeException("Payment processor not available for refund");
            }
            
            PaymentProcessor.PaymentResult result = processor.processRefund(payment, request.getRefundAmount(), request.getReason());
            
            if (result.isSuccess()) {
                // Update payment record
                BigDecimal currentRefund = payment.getRefundAmount() != null ? payment.getRefundAmount() : BigDecimal.ZERO;
                BigDecimal newRefundAmount = currentRefund.add(request.getRefundAmount());
                
                payment.setRefundAmount(newRefundAmount);
                payment.setRefundDate(new Timestamp(System.currentTimeMillis()));
                payment.setRefundReason(request.getReason());
                
                // Update status based on refund amount
                if (newRefundAmount.compareTo(payment.getPaymentAmount()) >= 0) {
                    payment.setPaymentStatus(Payment.PaymentStatus.REFUNDED);
                } else {
                    payment.setPaymentStatus(Payment.PaymentStatus.PARTIALLY_REFUNDED);
                }
                
                payment.updateTimestamp();
                payment = paymentRepository.save(payment);
                
                // Publish refund event
                paymentEventService.publishRefundProcessed(payment, request.getRefundAmount());
                
                return new PaymentResponse(payment);
            } else {
                throw new RuntimeException("Refund processing failed: " + result.getMessage());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("Refund processing error: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get payment by ID
     */
    public PaymentResponse getPaymentById(Integer paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found"));
        return new PaymentResponse(payment);
    }
    
    /**
     * Get payment by order ID
     */
    public PaymentResponse getPaymentByOrderId(Integer orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("Payment not found for order"));
        return new PaymentResponse(payment);
    }
    
    /**
     * Get payments by seller
     */
    public List<PaymentResponse> getPaymentsBySeller(Integer sellerId) {
        return paymentRepository.findBySellerIdOrderByCreatedDateDesc(sellerId)
            .stream()
            .map(PaymentResponse::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Get payments by buyer
     */
    public List<PaymentResponse> getPaymentsByBuyer(Integer buyerId) {
        return paymentRepository.findByBuyerIdOrderByCreatedDateDesc(buyerId)
            .stream()
            .map(PaymentResponse::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Verify payment status with processor
     */
    public PaymentResponse verifyPayment(Integer paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new RuntimeException("Payment not found"));
        
        PaymentProcessor processor = findProcessor(payment.getPaymentMethod());
        if (processor == null) {
            throw new RuntimeException("Payment processor not available");
        }
        
        PaymentProcessor.PaymentResult result = processor.verifyPayment(payment.getProcessorPaymentId());
        
        if (!result.isSuccess()) {
            throw new RuntimeException("Payment verification failed: " + result.getMessage());
        }
        
        return new PaymentResponse(payment);
    }
    
    /**
     * Get payment statistics for seller
     */
    public PaymentStatistics getSellerPaymentStatistics(Integer sellerId, Timestamp startDate, Timestamp endDate) {
        List<Payment> payments = paymentRepository.findBySellerAndDateRange(sellerId, startDate, endDate);
        
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
    private void updatePaymentFromResult(Payment payment, PaymentProcessor.PaymentResult result) {
        payment.setProcessorPaymentId(result.getProcessorPaymentId());
        payment.setProcessorTransactionId(result.getProcessorTransactionId());
        
        if (result.getProcessingFee() != null) {
            payment.setProcessingFee(result.getProcessingFee());
            payment.setNetAmount(payment.getPaymentAmount().subtract(result.getProcessingFee()));
        }
        
        if (result.getAdditionalData() != null) {
            // Convert additional data to JSON string
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