package com.ebay.paymentservice.controller;

import com.ebay.paymentservice.dto.PaymentRequest;
import com.ebay.paymentservice.dto.PaymentResponse;
import com.ebay.paymentservice.dto.RefundRequest;
import com.ebay.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    /**
     * Process a payment
     */
    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@Valid @RequestBody PaymentRequest request) {
        try {
            PaymentResponse response = paymentService.processPayment(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    /**
     * Process a refund
     */
    @PostMapping("/refund")
    public ResponseEntity<?> processRefund(@Valid @RequestBody RefundRequest request) {
        try {
            PaymentResponse response = paymentService.processRefund(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    /**
     * Get payment by ID
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<?> getPayment(@PathVariable Integer paymentId) {
        try {
            PaymentResponse response = paymentService.getPaymentById(paymentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    
    /**
     * Get payment by order ID
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getPaymentByOrderId(@PathVariable Integer orderId) {
        try {
            PaymentResponse response = paymentService.getPaymentByOrderId(orderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    
    /**
     * Get payments by seller
     */
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsBySeller(@PathVariable Integer sellerId) {
        List<PaymentResponse> payments = paymentService.getPaymentsBySeller(sellerId);
        return ResponseEntity.ok(payments);
    }
    
    /**
     * Get payments by buyer
     */
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByBuyer(@PathVariable Integer buyerId) {
        List<PaymentResponse> payments = paymentService.getPaymentsByBuyer(buyerId);
        return ResponseEntity.ok(payments);
    }
    
    /**
     * Verify payment status
     */
    @PostMapping("/{paymentId}/verify")
    public ResponseEntity<?> verifyPayment(@PathVariable Integer paymentId) {
        try {
            PaymentResponse response = paymentService.verifyPayment(paymentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    /**
     * Get payment statistics for seller
     */
    @GetMapping("/seller/{sellerId}/statistics")
    public ResponseEntity<?> getSellerStatistics(
            @PathVariable Integer sellerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        try {
            // Default to last 30 days if no dates provided
            Timestamp start = startDate != null ? Timestamp.valueOf(startDate) : 
                new Timestamp(System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000));
            Timestamp end = endDate != null ? Timestamp.valueOf(endDate) : 
                new Timestamp(System.currentTimeMillis());
            
            PaymentService.PaymentStatistics stats = paymentService.getSellerPaymentStatistics(sellerId, start, end);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "payment-service");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }
}