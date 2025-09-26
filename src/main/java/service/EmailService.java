package service;

import entity.Order;
import entity.Payment;
import entity.User;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Email service for payment notifications
 * In production, this would integrate with an email service like SendGrid, AWS SES, etc.
 */
@Service
public class EmailService {
    
    /**
     * Send payment confirmation email to buyer
     */
    public void sendPaymentConfirmationEmail(String toEmail, Map<String, Object> data) {
        try {
            Order order = (Order) data.get("order");
            Payment payment = (Payment) data.get("payment");
            User user = (User) data.get("user");
            
            // Simulate email sending
            System.out.println("=== PAYMENT CONFIRMATION EMAIL ===");
            System.out.println("To: " + toEmail);
            System.out.println("Subject: Payment Confirmed - Order #" + order.getOrderId());
            System.out.println("Dear " + user.getFirstName() + ",");
            System.out.println("Your payment of $" + payment.getPaymentAmount() + " has been successfully processed.");
            System.out.println("Order ID: " + order.getOrderId());
            System.out.println("Payment Method: " + payment.getPaymentMethod());
            System.out.println("Transaction ID: " + payment.getProcessorPaymentId());
            System.out.println("===================================");
            
        } catch (Exception e) {
            System.err.println("Error sending payment confirmation email: " + e.getMessage());
        }
    }
    
    /**
     * Send payment received notification to seller
     */
    public void sendPaymentReceivedEmail(String toEmail, Map<String, Object> data) {
        try {
            Order order = (Order) data.get("order");
            Payment payment = (Payment) data.get("payment");
            User user = (User) data.get("user");
            
            // Simulate email sending
            System.out.println("=== PAYMENT RECEIVED EMAIL ===");
            System.out.println("To: " + toEmail);
            System.out.println("Subject: Payment Received - Order #" + order.getOrderId());
            System.out.println("Dear " + user.getFirstName() + ",");
            System.out.println("You have received a payment of $" + payment.getPaymentAmount() + " for your sale.");
            System.out.println("Order ID: " + order.getOrderId());
            System.out.println("Net Amount (after fees): $" + payment.getNetAmount());
            System.out.println("Processing Fee: $" + payment.getProcessingFee());
            System.out.println("Please prepare the item for shipment.");
            System.out.println("===============================");
            
        } catch (Exception e) {
            System.err.println("Error sending payment received email: " + e.getMessage());
        }
    }
    
    /**
     * Send payment failure notification to buyer
     */
    public void sendPaymentFailureEmail(String toEmail, Map<String, Object> data) {
        try {
            Order order = (Order) data.get("order");
            Payment payment = (Payment) data.get("payment");
            User user = (User) data.get("user");
            String failureReason = (String) data.get("failureReason");
            
            // Simulate email sending
            System.out.println("=== PAYMENT FAILURE EMAIL ===");
            System.out.println("To: " + toEmail);
            System.out.println("Subject: Payment Failed - Order #" + order.getOrderId());
            System.out.println("Dear " + user.getFirstName() + ",");
            System.out.println("Unfortunately, your payment could not be processed.");
            System.out.println("Order ID: " + order.getOrderId());
            System.out.println("Amount: $" + payment.getPaymentAmount());
            System.out.println("Reason: " + failureReason);
            System.out.println("Please try again with a different payment method or contact support.");
            System.out.println("==============================");
            
        } catch (Exception e) {
            System.err.println("Error sending payment failure email: " + e.getMessage());
        }
    }
    
    /**
     * Send dispute notification to seller
     */
    public void sendDisputeNotificationEmail(String toEmail, Map<String, Object> data) {
        try {
            Order order = (Order) data.get("order");
            Payment payment = (Payment) data.get("payment");
            User user = (User) data.get("user");
            String disputeReason = (String) data.get("disputeReason");
            Object disputeAmount = data.get("disputeAmount");
            
            // Simulate email sending
            System.out.println("=== DISPUTE NOTIFICATION EMAIL ===");
            System.out.println("To: " + toEmail);
            System.out.println("Subject: Payment Dispute Created - Order #" + order.getOrderId());
            System.out.println("Dear " + user.getFirstName() + ",");
            System.out.println("A dispute has been created for a payment you received.");
            System.out.println("Order ID: " + order.getOrderId());
            System.out.println("Disputed Amount: $" + disputeAmount);
            System.out.println("Reason: " + disputeReason);
            System.out.println("Please review and respond to this dispute in your seller dashboard.");
            System.out.println("===================================");
            
        } catch (Exception e) {
            System.err.println("Error sending dispute notification email: " + e.getMessage());
        }
    }
    
    /**
     * Send refund notification to buyer
     */
    public void sendRefundNotificationEmail(String toEmail, Map<String, Object> data) {
        try {
            Order order = (Order) data.get("order");
            Payment payment = (Payment) data.get("payment");
            User user = (User) data.get("user");
            Object refundAmount = data.get("refundAmount");
            String refundReason = (String) data.get("refundReason");
            
            // Simulate email sending
            System.out.println("=== REFUND NOTIFICATION EMAIL ===");
            System.out.println("To: " + toEmail);
            System.out.println("Subject: Refund Processed - Order #" + order.getOrderId());
            System.out.println("Dear " + user.getFirstName() + ",");
            System.out.println("A refund has been processed for your order.");
            System.out.println("Order ID: " + order.getOrderId());
            System.out.println("Refund Amount: $" + refundAmount);
            System.out.println("Reason: " + refundReason);
            System.out.println("The refund will appear in your account within 3-5 business days.");
            System.out.println("===================================");
            
        } catch (Exception e) {
            System.err.println("Error sending refund notification email: " + e.getMessage());
        }
    }
    
    /**
     * Send general payment status update
     */
    public void sendPaymentStatusUpdateEmail(String toEmail, String subject, String message) {
        try {
            // Simulate email sending
            System.out.println("=== PAYMENT STATUS UPDATE EMAIL ===");
            System.out.println("To: " + toEmail);
            System.out.println("Subject: " + subject);
            System.out.println(message);
            System.out.println("====================================");
            
        } catch (Exception e) {
            System.err.println("Error sending payment status update email: " + e.getMessage());
        }
    }
}