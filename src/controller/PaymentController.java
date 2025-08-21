package controller;

import entity.Order;
import entity.Payment;
import entity.User;
import service.PaymentService;
import service.OrderService;
import service.WebhookNotificationService;
import service.payment.PaymentProcessor.PaymentResult;
import service.payment.PayPalPaymentProcessor;
import service.payment.StripePaymentProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/payment")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private PayPalPaymentProcessor payPalProcessor;
    
    @Autowired
    private StripePaymentProcessor stripeProcessor;
    
    @Autowired
    private WebhookNotificationService webhookService;
    
    // Process payment (enhanced version)
    @PostMapping("/process")
    @ResponseBody
    public Map<String, Object> processPayment(@RequestParam Integer orderId,
                                              @RequestParam String paymentMethod,
                                              @RequestParam(required = false) String cardNumber,
                                              @RequestParam(required = false) String expiryDate,
                                              @RequestParam(required = false) String cvv,
                                              @RequestParam(required = false) String cardHolderName,
                                              @RequestParam(required = false) String stripeToken,
                                              @RequestParam(required = false) String paypalEmail,
                                              HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "You must be logged in");
            return response;
        }
        
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null || !order.getBuyer().getUserId().equals(currentUser.getUserId())) {
                response.put("success", false);
                response.put("message", "Order not found or access denied");
                return response;
            }
            
            // Prepare payment details
            Map<String, String> paymentDetails = new HashMap<>();
            
            Payment.PaymentMethod method = Payment.PaymentMethod.valueOf(paymentMethod);
            
            switch (method) {
                case CREDIT_CARD:
                case DEBIT_CARD:
                case STRIPE:
                    if (stripeToken != null) {
                        paymentDetails.put("stripe_token", stripeToken);
                    } else {
                        paymentDetails.put("cardNumber", cardNumber);
                        paymentDetails.put("expiryDate", expiryDate);
                        paymentDetails.put("cvv", cvv);
                        paymentDetails.put("cardHolderName", cardHolderName);
                    }
                    break;
                    
                case PAYPAL:
                    paymentDetails.put("paypal_email", paypalEmail);
                    paymentDetails.put("base_url", getBaseUrl(session));
                    paymentDetails.put("return_url", getBaseUrl(session) + "/payment/paypal/return");
                    paymentDetails.put("cancel_url", getBaseUrl(session) + "/payment/paypal/cancel");
                    break;
                    
                default:
                    response.put("success", false);
                    response.put("message", "Unsupported payment method");
                    return response;
            }
            
            // Process payment
            PaymentResult result = paymentService.processOrderPayment(orderId, method, paymentDetails);
            
            if (result.isSuccess()) {
                response.put("success", true);
                response.put("message", result.getMessage());
                
                if (method == Payment.PaymentMethod.PAYPAL) {
                    // For PayPal, return redirect URL
                    String checkoutUrl = payPalProcessor.generateCheckoutUrl(
                        paymentService.getPaymentByOrderId(orderId), paymentDetails);
                    response.put("redirect_type", "external");
                    response.put("redirect_url", checkoutUrl);
                } else {
                    // For other methods, redirect to confirmation
                    response.put("redirect_type", "internal");
                    response.put("redirect_url", "/checkout/confirmation/" + orderId);
                }
                
            } else {
                response.put("success", false);
                response.put("message", result.getMessage());
            }
            
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Invalid payment method");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Payment processing error: " + e.getMessage());
        }
        
        return response;
    }
    
    // PayPal return handler
    @GetMapping("/paypal/return")
    public ModelAndView handlePayPalReturn(@RequestParam String token,
                                          @RequestParam String PayerID,
                                          @RequestParam(required = false) String orderId,
                                          HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        try {
            // Execute PayPal payment
            PaymentResult result = payPalProcessor.handlePayPalReturn(token, PayerID);
            
            if (result.isSuccess()) {
                mv.setViewName("redirect:/checkout/confirmation/" + orderId);
                mv.addObject("message", "PayPal payment completed successfully!");
            } else {
                mv.setViewName("redirect:/checkout/payment/" + orderId);
                mv.addObject("error", "PayPal payment failed: " + result.getMessage());
            }
            
        } catch (Exception e) {
            mv.setViewName("redirect:/checkout/payment/" + orderId);
            mv.addObject("error", "PayPal payment processing error");
        }
        
        return mv;
    }
    
    // PayPal cancel handler
    @GetMapping("/paypal/cancel")
    public ModelAndView handlePayPalCancel(@RequestParam(required = false) String orderId,
                                          HttpSession session) {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("redirect:/checkout/payment/" + orderId);
        mv.addObject("message", "PayPal payment was cancelled");
        return mv;
    }
    
    // Create Stripe Payment Intent
    @PostMapping("/stripe/create-intent")
    @ResponseBody
    public Map<String, Object> createStripePaymentIntent(@RequestParam Integer orderId,
                                                         HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "You must be logged in");
            return response;
        }
        
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null || !order.getBuyer().getUserId().equals(currentUser.getUserId())) {
                response.put("success", false);
                response.put("message", "Order not found or access denied");
                return response;
            }
            
            // Create payment record
            Payment payment = new Payment(order, order.getTotalAmount(), Payment.PaymentMethod.STRIPE);
            
            // Create Stripe Payment Intent
            Map<String, Object> intent = stripeProcessor.createPaymentIntent(payment);
            
            response.put("success", true);
            response.put("client_secret", intent.get("client_secret"));
            response.put("publishable_key", "pk_test_example"); // This would come from config
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error creating payment intent: " + e.getMessage());
        }
        
        return response;
    }
    
    // Process refund
    @PostMapping("/refund")
    @ResponseBody
    public Map<String, Object> processRefund(@RequestParam Integer paymentId,
                                            @RequestParam BigDecimal refundAmount,
                                            @RequestParam String reason,
                                            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "You must be logged in");
            return response;
        }
        
        try {
            Payment payment = paymentService.getPaymentById(paymentId);
            if (payment == null) {
                response.put("success", false);
                response.put("message", "Payment not found");
                return response;
            }
            
            // Check if user is the seller
            if (!payment.getOrder().getSeller().getUserId().equals(currentUser.getUserId())) {
                response.put("success", false);
                response.put("message", "You can only refund your own sales");
                return response;
            }
            
            PaymentResult result = paymentService.processRefund(paymentId, refundAmount, reason);
            
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Refund processing error: " + e.getMessage());
        }
        
        return response;
    }
    
    // Verify payment status
    @PostMapping("/verify")
    @ResponseBody
    public Map<String, Object> verifyPayment(@RequestParam Integer paymentId,
                                            HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "You must be logged in");
            return response;
        }
        
        try {
            PaymentResult result = paymentService.verifyPayment(paymentId);
            
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            
            if (result.getAdditionalData() != null) {
                response.put("details", result.getAdditionalData());
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Verification error: " + e.getMessage());
        }
        
        return response;
    }
    
    // Show payment details
    @GetMapping("/details/{paymentId}")
    public ModelAndView showPaymentDetails(@PathVariable Integer paymentId,
                                          HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        Payment payment = paymentService.getPaymentById(paymentId);
        if (payment == null) {
            mv.setViewName("redirect:/");
            mv.addObject("error", "Payment not found");
            return mv;
        }
        
        // Check access permissions
        boolean isBuyer = payment.getOrder().getBuyer().getUserId().equals(currentUser.getUserId());
        boolean isSeller = payment.getOrder().getSeller().getUserId().equals(currentUser.getUserId());
        
        if (!isBuyer && !isSeller) {
            mv.setViewName("redirect:/");
            mv.addObject("error", "Access denied");
            return mv;
        }
        
        mv.setViewName("payment-details");
        mv.addObject("payment", payment);
        mv.addObject("isBuyer", isBuyer);
        mv.addObject("isSeller", isSeller);
        
        return mv;
    }
    
    // Webhook handler for Stripe
    @PostMapping("/webhook/stripe")
    @ResponseBody
    public String handleStripeWebhook(@RequestBody String payload,
                                     @RequestHeader("Stripe-Signature") String signature) {
        try {
            boolean processed = stripeProcessor.handleWebhook(payload, signature);
            if (processed) {
                // Process webhook with notification service
                // In production, parse the webhook payload to extract event type and data
                String eventType = "payment_intent.succeeded"; // Extract from payload
                Map<String, Object> eventData = new HashMap<>(); // Parse from payload
                webhookService.processStripeWebhook(eventType, eventData);
            }
            return processed ? "success" : "failed";
        } catch (Exception e) {
            return "error";
        }
    }
    
    // Webhook handler for PayPal
    @PostMapping("/webhook/paypal")
    @ResponseBody
    public String handlePayPalWebhook(@RequestBody String payload,
                                     @RequestHeader("PAYPAL-TRANSMISSION-ID") String transmissionId) {
        try {
            // In production, verify PayPal webhook signature
            // For now, simulate processing
            String eventType = "PAYMENT.CAPTURE.COMPLETED"; // Extract from payload
            Map<String, Object> eventData = new HashMap<>(); // Parse from payload
            webhookService.processPayPalWebhook(eventType, eventData);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }
    
    // Show payment tracking page
    @GetMapping("/tracking")
    public ModelAndView showPaymentTracking(@RequestParam(defaultValue = "buyer") String view,
                                          HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        boolean isSeller = "seller".equals(view);
        Map<String, Object> trackingInfo = webhookService.getPaymentTrackingInfo(currentUser.getUserId(), isSeller);
        
        mv.setViewName("payment-tracking");
        mv.addObject("trackingInfo", trackingInfo);
        mv.addObject("currentUser", currentUser);
        
        return mv;
    }
    
    // Export payment data
    @GetMapping("/export")
    public void exportPayments(@RequestParam(required = false) String format,
                              @RequestParam(required = false) String status,
                              @RequestParam(required = false) String method,
                              @RequestParam(required = false) String dateFrom,
                              @RequestParam(required = false) String dateTo,
                              @RequestParam(defaultValue = "buyer") String view,
                              HttpSession session,
                              javax.servlet.http.HttpServletResponse response) {
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.setStatus(401);
            return;
        }
        
        try {
            // Set response headers for CSV download
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=payments_export.csv");
            
            // Get filtered payment data
            boolean isSeller = "seller".equals(view);
            Map<String, Object> trackingInfo = webhookService.getPaymentTrackingInfo(currentUser.getUserId(), isSeller);
            
            // Generate CSV content
            StringBuilder csv = new StringBuilder();
            csv.append("Date,Order ID,Amount,Method,Status,Counterparty,Processor ID\n");
            
            // In production, filter based on parameters and generate proper CSV
            csv.append("Sample data would be generated here based on filters\n");
            
            response.getWriter().write(csv.toString());
            response.getWriter().flush();
            
        } catch (Exception e) {
            response.setStatus(500);
        }
    }
    
    // Get payment statistics for seller dashboard
    @GetMapping("/stats/seller")
    @ResponseBody
    public Map<String, Object> getSellerPaymentStats(@RequestParam(defaultValue = "30") int days,
                                                     HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "You must be logged in");
            return response;
        }
        
        try {
            java.sql.Timestamp endDate = new java.sql.Timestamp(System.currentTimeMillis());
            java.sql.Timestamp startDate = new java.sql.Timestamp(
                System.currentTimeMillis() - (days * 24L * 60L * 60L * 1000L));
            
            PaymentService.PaymentStatistics stats = 
                paymentService.getSellerPaymentStatistics(currentUser.getUserId(), startDate, endDate);
            
            response.put("success", true);
            response.put("totalRevenue", stats.getTotalRevenue());
            response.put("totalFees", stats.getTotalFees());
            response.put("netRevenue", stats.getNetRevenue());
            response.put("totalRefunds", stats.getTotalRefunds());
            response.put("completedPayments", stats.getCompletedPayments());
            response.put("failedPayments", stats.getFailedPayments());
            response.put("successRate", stats.getSuccessRate());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error retrieving statistics: " + e.getMessage());
        }
        
        return response;
    }
    
    // Helper method to get base URL
    private String getBaseUrl(HttpSession session) {
        // In a real implementation, this would be configured or derived from request
        return "http://localhost:8080/eBay";
    }
    
    // Add current user to all models
    @ModelAttribute("currentUser")
    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }
}