package service;

import entity.Payment;
import entity.Order;
import entity.User;
import dao.PaymentDAO;
import dao.OrderDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Service for handling webhook notifications and payment status updates
 */
@Service
public class WebhookNotificationService {
    
    @Autowired
    private PaymentDAO paymentDAO;
    
    @Autowired
    private OrderDAO orderDAO;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Process Stripe webhook event
     */
    @Async
    public CompletableFuture<Void> processStripeWebhook(String eventType, Map<String, Object> eventData) {
        try {
            switch (eventType) {
                case "payment_intent.succeeded":
                    handlePaymentSucceeded(eventData);
                    break;
                case "payment_intent.payment_failed":
                    handlePaymentFailed(eventData);
                    break;
                case "charge.dispute.created":
                    handleChargeDispute(eventData);
                    break;
                case "invoice.payment_succeeded":
                    handlePaymentSucceeded(eventData);
                    break;
                default:
                    System.out.println("Unhandled Stripe webhook event: " + eventType);
            }
        } catch (Exception e) {
            System.err.println("Error processing Stripe webhook: " + e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Process PayPal webhook event
     */
    @Async
    public CompletableFuture<Void> processPayPalWebhook(String eventType, Map<String, Object> eventData) {
        try {
            switch (eventType) {
                case "PAYMENT.CAPTURE.COMPLETED":
                    handlePayPalPaymentCompleted(eventData);
                    break;
                case "PAYMENT.CAPTURE.DENIED":
                    handlePayPalPaymentDenied(eventData);
                    break;
                case "CUSTOMER.DISPUTE.CREATED":
                    handlePayPalDispute(eventData);
                    break;
                default:
                    System.out.println("Unhandled PayPal webhook event: " + eventType);
            }
        } catch (Exception e) {
            System.err.println("Error processing PayPal webhook: " + e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }
    
    /**
     * Handle successful payment notification
     */
    private void handlePaymentSucceeded(Map<String, Object> eventData) {
        String paymentIntentId = (String) eventData.get("id");
        
        // Find payment by processor payment ID
        Payment payment = paymentDAO.findByProcessorPaymentId(paymentIntentId);
        if (payment != null) {
            // Update payment status
            payment.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
            paymentDAO.update(payment);
            
            // Update order status
            Order order = payment.getOrder();
            if (order.getStatus() == Order.OrderStatus.PENDING) {
                order.setStatus(Order.OrderStatus.PROCESSING);
                orderDAO.update(order);
            }
            
            // Send confirmation emails
            sendPaymentSuccessNotifications(payment);
        }
    }
    
    /**
     * Handle failed payment notification
     */
    private void handlePaymentFailed(Map<String, Object> eventData) {
        String paymentIntentId = (String) eventData.get("id");
        String failureReason = (String) eventData.get("failure_reason");
        
        Payment payment = paymentDAO.findByProcessorPaymentId(paymentIntentId);
        if (payment != null) {
            payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason(failureReason);
            paymentDAO.update(payment);
            
            // Send failure notification
            sendPaymentFailureNotification(payment, failureReason);
        }
    }
    
    /**
     * Handle PayPal payment completed
     */
    private void handlePayPalPaymentCompleted(Map<String, Object> eventData) {
        // Extract PayPal payment details
        Map<String, Object> resource = (Map<String, Object>) eventData.get("resource");
        String paypalId = (String) resource.get("id");
        
        Payment payment = paymentDAO.findByProcessorPaymentId(paypalId);
        if (payment != null) {
            payment.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
            paymentDAO.update(payment);
            
            // Update order status
            Order order = payment.getOrder();
            if (order.getStatus() == Order.OrderStatus.PENDING) {
                order.setStatus(Order.OrderStatus.PROCESSING);
                orderDAO.update(order);
            }
            
            sendPaymentSuccessNotifications(payment);
        }
    }
    
    /**
     * Handle PayPal payment denied
     */
    private void handlePayPalPaymentDenied(Map<String, Object> eventData) {
        Map<String, Object> resource = (Map<String, Object>) eventData.get("resource");
        String paypalId = (String) resource.get("id");
        String reason = (String) resource.get("reason_code");
        
        Payment payment = paymentDAO.findByProcessorPaymentId(paypalId);
        if (payment != null) {
            payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
            payment.setFailureReason("PayPal denial: " + reason);
            paymentDAO.update(payment);
            
            sendPaymentFailureNotification(payment, reason);
        }
    }
    
    /**
     * Handle charge dispute creation
     */
    private void handleChargeDispute(Map<String, Object> eventData) {
        String chargeId = (String) eventData.get("charge");
        String disputeReason = (String) eventData.get("reason");
        BigDecimal disputeAmount = new BigDecimal(eventData.get("amount").toString()).divide(new BigDecimal("100"));
        
        Payment payment = paymentDAO.findByProcessorPaymentId(chargeId);
        if (payment != null) {
            // Create dispute record (would need Dispute entity)
            sendDisputeNotification(payment, disputeReason, disputeAmount);
        }
    }
    
    /**
     * Handle PayPal dispute creation
     */
    private void handlePayPalDispute(Map<String, Object> eventData) {
        Map<String, Object> resource = (Map<String, Object>) eventData.get("resource");
        String transactionId = (String) resource.get("disputed_transactions");
        String disputeReason = (String) resource.get("reason");
        
        Payment payment = paymentDAO.findByProcessorTransactionId(transactionId);
        if (payment != null) {
            sendDisputeNotification(payment, disputeReason, payment.getPaymentAmount());
        }
    }
    
    /**
     * Send payment success notifications to buyer and seller
     */
    private void sendPaymentSuccessNotifications(Payment payment) {
        Order order = payment.getOrder();
        User buyer = order.getBuyer();
        User seller = order.getSeller();
        
        // Send buyer confirmation
        Map<String, Object> buyerData = new HashMap<>();
        buyerData.put("order", order);
        buyerData.put("payment", payment);
        buyerData.put("user", buyer);
        
        emailService.sendPaymentConfirmationEmail(buyer.getEmail(), buyerData);
        
        // Send seller notification
        Map<String, Object> sellerData = new HashMap<>();
        sellerData.put("order", order);
        sellerData.put("payment", payment);
        sellerData.put("user", seller);
        
        emailService.sendPaymentReceivedEmail(seller.getEmail(), sellerData);
    }
    
    /**
     * Send payment failure notification
     */
    private void sendPaymentFailureNotification(Payment payment, String reason) {
        Order order = payment.getOrder();
        User buyer = order.getBuyer();
        
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("payment", payment);
        data.put("user", buyer);
        data.put("failureReason", reason);
        
        emailService.sendPaymentFailureEmail(buyer.getEmail(), data);
    }
    
    /**
     * Send dispute notification
     */
    private void sendDisputeNotification(Payment payment, String reason, BigDecimal amount) {
        Order order = payment.getOrder();
        User seller = order.getSeller();
        
        Map<String, Object> data = new HashMap<>();
        data.put("order", order);
        data.put("payment", payment);
        data.put("user", seller);
        data.put("disputeReason", reason);
        data.put("disputeAmount", amount);
        
        emailService.sendDisputeNotificationEmail(seller.getEmail(), data);
    }
    
    /**
     * Synchronize payment status with payment processors
     */
    public void syncPaymentStatuses() {
        // Get all pending payments from last 24 hours
        Timestamp yesterday = new Timestamp(System.currentTimeMillis() - (24 * 60 * 60 * 1000));
        List<Payment> pendingPayments = paymentDAO.findPendingPaymentsSince(yesterday);
        
        for (Payment payment : pendingPayments) {
            try {
                syncSinglePaymentStatus(payment);
                Thread.sleep(100); // Rate limiting
            } catch (Exception e) {
                System.err.println("Error syncing payment " + payment.getPaymentId() + ": " + e.getMessage());
            }
        }
    }
    
    /**
     * Sync individual payment status
     */
    private void syncSinglePaymentStatus(Payment payment) {
        // This would make API calls to payment processors to verify status
        // For now, we'll simulate the process
        
        if (payment.getPaymentMethod() == Payment.PaymentMethod.STRIPE && 
            payment.getProcessorPaymentId() != null &&
            payment.getProcessorPaymentId().startsWith("ch_")) {
            
            // Simulate Stripe status check
            // In production: stripe.charges.retrieve(payment.getProcessorPaymentId())
            
        } else if (payment.getPaymentMethod() == Payment.PaymentMethod.PAYPAL && 
                   payment.getProcessorPaymentId() != null &&
                   payment.getProcessorPaymentId().startsWith("PAYPAL_")) {
            
            // Simulate PayPal status check
            // In production: PayPal REST API call
        }
    }
    
    /**
     * Get payment tracking information for a user
     */
    public Map<String, Object> getPaymentTrackingInfo(Integer userId, boolean isSeller) {
        Map<String, Object> trackingInfo = new HashMap<>();
        
        List<Payment> payments;
        if (isSeller) {
            payments = paymentDAO.findBySellerIdOrderByDate(userId);
        } else {
            payments = paymentDAO.findByBuyerIdOrderByDate(userId);
        }
        
        // Calculate statistics
        int totalPayments = payments.size();
        int completedPayments = 0;
        int failedPayments = 0;
        int pendingPayments = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (Payment payment : payments) {
            switch (payment.getPaymentStatus()) {
                case COMPLETED:
                    completedPayments++;
                    totalAmount = totalAmount.add(payment.getPaymentAmount());
                    break;
                case FAILED:
                    failedPayments++;
                    break;
                case PENDING:
                    pendingPayments++;
                    break;
            }
        }
        
        trackingInfo.put("payments", payments);
        trackingInfo.put("totalPayments", totalPayments);
        trackingInfo.put("completedPayments", completedPayments);
        trackingInfo.put("failedPayments", failedPayments);
        trackingInfo.put("pendingPayments", pendingPayments);
        trackingInfo.put("totalAmount", totalAmount);
        trackingInfo.put("successRate", totalPayments > 0 ? (completedPayments * 100.0) / totalPayments : 0);
        
        return trackingInfo;
    }
}