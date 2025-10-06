package service.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentServiceClient {
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${payment.service.url:http://localhost:8081}")
    private String paymentServiceUrl;
    
    /**
     * Process payment for an order
     */
    public PaymentResult processOrderPayment(Integer orderId, Integer buyerId, Integer sellerId,
                                           BigDecimal paymentAmount, Payment.PaymentMethod paymentMethod, 
                                           Map<String, String> paymentDetails) {
        try {
            String url = paymentServiceUrl + "/api/payments/process";
            
            Map<String, Object> request = new HashMap<>();
            request.put("orderId", orderId);
            request.put("buyerId", buyerId);
            request.put("sellerId", sellerId);
            request.put("paymentAmount", paymentAmount);
            request.put("paymentMethod", paymentMethod.toString());
            request.put("currency", "USD");
            request.put("paymentDetails", paymentDetails);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                return PaymentResult.success("Payment processed successfully", responseBody);
            } else {
                return PaymentResult.failure("Payment processing failed with status: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            return PaymentResult.failure("Payment service communication error: " + e.getMessage());
        }
    }
    
    /**
     * Process a refund
     */
    public PaymentResult processRefund(Integer paymentId, BigDecimal refundAmount, String reason) {
        try {
            String url = paymentServiceUrl + "/api/payments/refund";
            
            Map<String, Object> request = new HashMap<>();
            request.put("paymentId", paymentId);
            request.put("refundAmount", refundAmount);
            request.put("reason", reason);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                return PaymentResult.success("Refund processed successfully", responseBody);
            } else {
                return PaymentResult.failure("Refund processing failed with status: " + response.getStatusCode());
            }
            
        } catch (Exception e) {
            return PaymentResult.failure("Payment service communication error: " + e.getMessage());
        }
    }
    
    /**
     * Get payment by ID
     */
    public PaymentResult getPaymentById(Integer paymentId) {
        try {
            String url = paymentServiceUrl + "/api/payments/" + paymentId;
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                return PaymentResult.success("Payment retrieved successfully", responseBody);
            } else {
                return PaymentResult.failure("Payment not found");
            }
            
        } catch (Exception e) {
            return PaymentResult.failure("Payment service communication error: " + e.getMessage());
        }
    }
    
    /**
     * Get payment by order ID
     */
    public PaymentResult getPaymentByOrderId(Integer orderId) {
        try {
            String url = paymentServiceUrl + "/api/payments/order/" + orderId;
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                return PaymentResult.success("Payment retrieved successfully", responseBody);
            } else {
                return PaymentResult.failure("Payment not found for order");
            }
            
        } catch (Exception e) {
            return PaymentResult.failure("Payment service communication error: " + e.getMessage());
        }
    }
    
    /**
     * Get payments by seller
     */
    @SuppressWarnings("unchecked")
    public PaymentResult getPaymentsBySeller(Integer sellerId) {
        try {
            String url = paymentServiceUrl + "/api/payments/seller/" + sellerId;
            
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                List<Map<String, Object>> responseBody = response.getBody();
                return PaymentResult.success("Payments retrieved successfully", responseBody);
            } else {
                return PaymentResult.failure("Failed to retrieve payments");
            }
            
        } catch (Exception e) {
            return PaymentResult.failure("Payment service communication error: " + e.getMessage());
        }
    }
    
    /**
     * Get payments by buyer
     */
    @SuppressWarnings("unchecked")
    public PaymentResult getPaymentsByBuyer(Integer buyerId) {
        try {
            String url = paymentServiceUrl + "/api/payments/buyer/" + buyerId;
            
            ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                List<Map<String, Object>> responseBody = response.getBody();
                return PaymentResult.success("Payments retrieved successfully", responseBody);
            } else {
                return PaymentResult.failure("Failed to retrieve payments");
            }
            
        } catch (Exception e) {
            return PaymentResult.failure("Payment service communication error: " + e.getMessage());
        }
    }
    
    /**
     * Verify payment status
     */
    public PaymentResult verifyPayment(Integer paymentId) {
        try {
            String url = paymentServiceUrl + "/api/payments/" + paymentId + "/verify";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                return PaymentResult.success("Payment verified successfully", responseBody);
            } else {
                return PaymentResult.failure("Payment verification failed");
            }
            
        } catch (Exception e) {
            return PaymentResult.failure("Payment service communication error: " + e.getMessage());
        }
    }
    
    /**
     * Get payment statistics for seller
     */
    public PaymentResult getSellerPaymentStatistics(Integer sellerId, Timestamp startDate, Timestamp endDate) {
        try {
            String url = paymentServiceUrl + "/api/payments/seller/" + sellerId + "/statistics";
            
            if (startDate != null && endDate != null) {
                url += "?startDate=" + startDate.toLocalDateTime().toString() +
                       "&endDate=" + endDate.toLocalDateTime().toString();
            }
            
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                return PaymentResult.success("Statistics retrieved successfully", responseBody);
            } else {
                return PaymentResult.failure("Failed to retrieve statistics");
            }
            
        } catch (Exception e) {
            return PaymentResult.failure("Payment service communication error: " + e.getMessage());
        }
    }
    
    /**
     * Check payment service health
     */
    public boolean isPaymentServiceHealthy() {
        try {
            String url = paymentServiceUrl + "/api/payments/health";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> health = response.getBody();
                return "UP".equals(health.get("status"));
            }
            return false;
            
        } catch (Exception e) {
            System.err.println("Payment service health check failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Payment result wrapper class
     */
    public static class PaymentResult {
        private final boolean success;
        private final String message;
        private final Object data;
        
        private PaymentResult(boolean success, String message, Object data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Object getData() { return data; }
        
        @SuppressWarnings("unchecked")
        public <T> T getDataAs(Class<T> type) {
            if (data != null && type.isAssignableFrom(data.getClass())) {
                return (T) data;
            }
            return null;
        }
        
        public static PaymentResult success(String message, Object data) {
            return new PaymentResult(true, message, data);
        }
        
        public static PaymentResult failure(String message) {
            return new PaymentResult(false, message, null);
        }
    }
}