package service;

import event.AuctionEvent;
import event.BidPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EventPublisherService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventPublisherService.class);
    
    // Kafka topic names
    public static final String BID_PLACED_TOPIC = "bid-placed";
    public static final String AUCTION_EVENTS_TOPIC = "auction-events";
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Autowired
    public EventPublisherService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    /**
     * Publishes a bid placed event to Kafka
     */
    public void publishBidPlacedEvent(BidPlacedEvent event) {
        try {
            String key = "product-" + event.getProductId();
            
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(BID_PLACED_TOPIC, key, event);
            
            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    logger.info("Successfully published bid placed event: bidId={}, productId={}, bidderId={}, amount={}", 
                               event.getBidId(), event.getProductId(), event.getBidderId(), event.getBidAmount());
                } else {
                    logger.error("Failed to publish bid placed event: bidId={}, productId={}", 
                               event.getBidId(), event.getProductId(), exception);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error publishing bid placed event: {}", event, e);
        }
    }
    
    /**
     * Publishes an auction event to Kafka
     */
    public void publishAuctionEvent(AuctionEvent event) {
        try {
            String key = "product-" + event.getProductId();
            
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(AUCTION_EVENTS_TOPIC, key, event);
            
            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    logger.info("Successfully published auction event: productId={}, eventType={}, currentPrice={}", 
                               event.getProductId(), event.getEventType(), event.getCurrentPrice());
                } else {
                    logger.error("Failed to publish auction event: productId={}, eventType={}", 
                               event.getProductId(), event.getEventType(), exception);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error publishing auction event: {}", event, e);
        }
    }
    
    /**
     * Publishes a generic event to a specified topic
     */
    public void publishEvent(String topic, String key, Object event) {
        try {
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(topic, key, event);
            
            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    logger.info("Successfully published event to topic: {}, key: {}", topic, key);
                } else {
                    logger.error("Failed to publish event to topic: {}, key: {}", topic, key, exception);
                }
            });
            
        } catch (Exception e) {
            logger.error("Error publishing event to topic: {}, key: {}, event: {}", topic, key, event, e);
        }
    }
}