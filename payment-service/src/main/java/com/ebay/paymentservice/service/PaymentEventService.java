package com.ebay.paymentservice.service;

import com.ebay.paymentservice.entity.Payment;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentEventService {
    
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Value("${kafka.topic.payment-completed:payment-completed}")
    private String paymentCompletedTopic;
    
    @Value("${kafka.topic.payment-failed:payment-failed}")
    private String paymentFailedTopic;
    
    @Value("${kafka.topic.refund-processed:refund-processed}")
    private String refundProcessedTopic;
    
    /**
     * Publish payment completed event
     */
    public void publishPaymentCompleted(Payment payment) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "PAYMENT_COMPLETED");
            event.put("paymentId", payment.getPaymentId());
            event.put("orderId", payment.getOrderId());
            event.put("buyerId", payment.getBuyerId());
            event.put("sellerId", payment.getSellerId());
            event.put("paymentAmount", payment.getPaymentAmount());
            event.put("netAmount", payment.getNetAmount());
            event.put("processingFee", payment.getProcessingFee());
            event.put("paymentMethod", payment.getPaymentMethod().toString());
            event.put("paymentProcessor", payment.getPaymentProcessor());
            event.put("processorPaymentId", payment.getProcessorPaymentId());
            event.put("timestamp", System.currentTimeMillis());
            
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(paymentCompletedTopic, payment.getOrderId().toString(), eventJson);
            
            System.out.println("Published payment completed event: paymentId=" + payment.getPaymentId() + 
                             ", orderId=" + payment.getOrderId());
            
        } catch (Exception e) {
            System.err.println("Failed to publish payment completed event: " + e.getMessage());
        }
    }
    
    /**
     * Publish payment failed event
     */
    public void publishPaymentFailed(Payment payment, String reason) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "PAYMENT_FAILED");
            event.put("paymentId", payment.getPaymentId());
            event.put("orderId", payment.getOrderId());
            event.put("buyerId", payment.getBuyerId());
            event.put("sellerId", payment.getSellerId());
            event.put("paymentAmount", payment.getPaymentAmount());
            event.put("paymentMethod", payment.getPaymentMethod().toString());
            event.put("failureReason", reason);
            event.put("timestamp", System.currentTimeMillis());
            
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(paymentFailedTopic, payment.getOrderId().toString(), eventJson);
            
            System.out.println("Published payment failed event: paymentId=" + payment.getPaymentId() + 
                             ", orderId=" + payment.getOrderId() + ", reason=" + reason);
            
        } catch (Exception e) {
            System.err.println("Failed to publish payment failed event: " + e.getMessage());
        }
    }
    
    /**
     * Publish refund processed event
     */
    public void publishRefundProcessed(Payment payment, BigDecimal refundAmount) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventType", "REFUND_PROCESSED");
            event.put("paymentId", payment.getPaymentId());
            event.put("orderId", payment.getOrderId());
            event.put("buyerId", payment.getBuyerId());
            event.put("sellerId", payment.getSellerId());
            event.put("originalAmount", payment.getPaymentAmount());
            event.put("refundAmount", refundAmount);
            event.put("totalRefunded", payment.getRefundAmount());
            event.put("refundReason", payment.getRefundReason());
            event.put("paymentStatus", payment.getPaymentStatus().toString());
            event.put("timestamp", System.currentTimeMillis());
            
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(refundProcessedTopic, payment.getOrderId().toString(), eventJson);
            
            System.out.println("Published refund processed event: paymentId=" + payment.getPaymentId() + 
                             ", orderId=" + payment.getOrderId() + ", refundAmount=" + refundAmount);
            
        } catch (Exception e) {
            System.err.println("Failed to publish refund processed event: " + e.getMessage());
        }
    }
}