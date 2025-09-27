package service;

import event.AuctionEvent;
import event.BidPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class EventConsumerService {
    
    private static final Logger logger = LoggerFactory.getLogger(EventConsumerService.class);
    
    @Autowired
    private WebhookNotificationService webhookNotificationService;
    
    /**
     * Consumes bid placed events and handles real-time notifications
     */
    @KafkaListener(topics = "bid-placed", groupId = "ebay-marketplace")
    public void handleBidPlacedEvent(
            @Payload BidPlacedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            logger.info("Received bid placed event: bidId={}, productId={}, bidderId={}, amount={}", 
                       event.getBidId(), event.getProductId(), event.getBidderId(), event.getBidAmount());
            
            // Handle real-time notifications
            handleBidNotifications(event);
            
            // Send webhooks if configured
            webhookNotificationService.sendBidPlacedWebhook(event);
            
            // Acknowledge the message
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            logger.error("Error processing bid placed event: {}", event, e);
            // Don't acknowledge on error - message will be retried
        }
    }
    
    /**
     * Consumes auction events and handles notifications
     */
    @KafkaListener(topics = "auction-events", groupId = "ebay-marketplace")
    public void handleAuctionEvent(
            @Payload AuctionEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment acknowledgment) {
        
        try {
            logger.info("Received auction event: productId={}, eventType={}, currentPrice={}", 
                       event.getProductId(), event.getEventType(), event.getCurrentPrice());
            
            // Handle different auction event types
            switch (event.getEventType()) {
                case AUCTION_STARTED:
                    handleAuctionStarted(event);
                    break;
                case AUCTION_ENDED:
                    handleAuctionEnded(event);
                    break;
                case RESERVE_MET:
                    handleReserveMet(event);
                    break;
                case BUY_NOW_ACTIVATED:
                    handleBuyNowActivated(event);
                    break;
                case AUCTION_EXTENDED:
                    handleAuctionExtended(event);
                    break;
            }
            
            // Send webhooks if configured
            webhookNotificationService.sendAuctionEventWebhook(event);
            
            // Acknowledge the message
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            logger.error("Error processing auction event: {}", event, e);
            // Don't acknowledge on error - message will be retried
        }
    }
    
    /**
     * Handle bid-related notifications
     */
    private void handleBidNotifications(BidPlacedEvent event) {
        try {
            // Notify other bidders they've been outbid
            if (event.isWinningBid()) {
                logger.info("New winning bid placed for product {}: ${}", 
                           event.getProductId(), event.getBidAmount());
                
                // TODO: Send real-time notifications to frontend clients
                // TODO: Send email notifications to outbid users
                // TODO: Update product watchers
            }
            
            // Notify product seller of new bid
            logger.info("Notifying seller of new bid on product {}", event.getProductId());
            
            // TODO: Send real-time notification to seller
            // TODO: Update seller dashboard
            
        } catch (Exception e) {
            logger.error("Error handling bid notifications for event: {}", event, e);
        }
    }
    
    /**
     * Handle auction started event
     */
    private void handleAuctionStarted(AuctionEvent event) {
        logger.info("Auction started for product {}: {}", event.getProductId(), event.getProductTitle());
        
        // TODO: Notify watchers that auction has started
        // TODO: Send email notifications to interested users
        // TODO: Update homepage/category pages with new auction
    }
    
    /**
     * Handle auction ended event
     */
    private void handleAuctionEnded(AuctionEvent event) {
        logger.info("Auction ended for product {}: {} - Winner: {}", 
                   event.getProductId(), event.getProductTitle(), event.getCurrentWinnerUsername());
        
        // TODO: Notify winner they won the auction
        // TODO: Notify seller of auction completion
        // TODO: Create order if there was a winner
        // TODO: Send notifications to all bidders about auction result
    }
    
    /**
     * Handle reserve price met event
     */
    private void handleReserveMet(AuctionEvent event) {
        logger.info("Reserve price met for product {}: {}", event.getProductId(), event.getCurrentPrice());
        
        // TODO: Notify seller that reserve has been met
        // TODO: Update auction display to show reserve met
    }
    
    /**
     * Handle buy now activated event
     */
    private void handleBuyNowActivated(AuctionEvent event) {
        logger.info("Buy now activated for product {}: {}", event.getProductId(), event.getBuyNowPrice());
        
        // TODO: End auction immediately
        // TODO: Create order for buy now purchase
        // TODO: Notify all bidders that auction ended via buy now
    }
    
    /**
     * Handle auction extended event
     */
    private void handleAuctionExtended(AuctionEvent event) {
        logger.info("Auction extended for product {}: new end time {}", 
                   event.getProductId(), event.getAuctionEndTime());
        
        // TODO: Notify all bidders of time extension
        // TODO: Update auction countdown timers
    }
}