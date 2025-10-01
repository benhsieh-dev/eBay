package com.ebay.notification.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationEventConsumer.class);

    public void testMethod() {
        logger.info("Notification service is running!");
    }

    // TODO: Add Kafka listeners when event classes are available
    /*
    @KafkaListener(topics = "bid-placed", groupId = "notification-service")
    public void handleBidPlacedEvent(String message) {
        logger.info("Received bid placed event: {}", message);
        // Handle notification logic here
    }

    @KafkaListener(topics = "auction-events", groupId = "notification-service")
    public void handleAuctionEvent(String message) {
        logger.info("Received auction event: {}", message);
        // Handle notification logic here
    }
    */
}