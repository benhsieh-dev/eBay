package event;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuctionEvent {
    
    public enum AuctionEventType {
        AUCTION_STARTED, AUCTION_ENDED, AUCTION_EXTENDED, RESERVE_MET, BUY_NOW_ACTIVATED
    }
    
    private Long productId;
    private String productTitle;
    private Long sellerId;
    private String sellerUsername;
    private AuctionEventType eventType;
    private BigDecimal currentPrice;
    private BigDecimal reservePrice;
    private BigDecimal buyNowPrice;
    private Long currentWinnerId;
    private String currentWinnerUsername;
    private Integer totalBids;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime auctionStartTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime auctionEndTime;
    
    // Default constructor
    public AuctionEvent() {
        this.eventTime = LocalDateTime.now();
    }
    
    public AuctionEvent(Long productId, String productTitle, Long sellerId, String sellerUsername,
                       AuctionEventType eventType, BigDecimal currentPrice, 
                       LocalDateTime auctionStartTime, LocalDateTime auctionEndTime) {
        this();
        this.productId = productId;
        this.productTitle = productTitle;
        this.sellerId = sellerId;
        this.sellerUsername = sellerUsername;
        this.eventType = eventType;
        this.currentPrice = currentPrice;
        this.auctionStartTime = auctionStartTime;
        this.auctionEndTime = auctionEndTime;
    }
    
    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }
    
    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }
    
    public String getSellerUsername() { return sellerUsername; }
    public void setSellerUsername(String sellerUsername) { this.sellerUsername = sellerUsername; }
    
    public AuctionEventType getEventType() { return eventType; }
    public void setEventType(AuctionEventType eventType) { this.eventType = eventType; }
    
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    
    public BigDecimal getReservePrice() { return reservePrice; }
    public void setReservePrice(BigDecimal reservePrice) { this.reservePrice = reservePrice; }
    
    public BigDecimal getBuyNowPrice() { return buyNowPrice; }
    public void setBuyNowPrice(BigDecimal buyNowPrice) { this.buyNowPrice = buyNowPrice; }
    
    public Long getCurrentWinnerId() { return currentWinnerId; }
    public void setCurrentWinnerId(Long currentWinnerId) { this.currentWinnerId = currentWinnerId; }
    
    public String getCurrentWinnerUsername() { return currentWinnerUsername; }
    public void setCurrentWinnerUsername(String currentWinnerUsername) { this.currentWinnerUsername = currentWinnerUsername; }
    
    public Integer getTotalBids() { return totalBids; }
    public void setTotalBids(Integer totalBids) { this.totalBids = totalBids; }
    
    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }
    
    public LocalDateTime getAuctionStartTime() { return auctionStartTime; }
    public void setAuctionStartTime(LocalDateTime auctionStartTime) { this.auctionStartTime = auctionStartTime; }
    
    public LocalDateTime getAuctionEndTime() { return auctionEndTime; }
    public void setAuctionEndTime(LocalDateTime auctionEndTime) { this.auctionEndTime = auctionEndTime; }
    
    @Override
    public String toString() {
        return "AuctionEvent{" +
                "productId=" + productId +
                ", productTitle='" + productTitle + '\'' +
                ", sellerId=" + sellerId +
                ", sellerUsername='" + sellerUsername + '\'' +
                ", eventType=" + eventType +
                ", currentPrice=" + currentPrice +
                ", reservePrice=" + reservePrice +
                ", buyNowPrice=" + buyNowPrice +
                ", currentWinnerId=" + currentWinnerId +
                ", currentWinnerUsername='" + currentWinnerUsername + '\'' +
                ", totalBids=" + totalBids +
                ", eventTime=" + eventTime +
                ", auctionStartTime=" + auctionStartTime +
                ", auctionEndTime=" + auctionEndTime +
                '}';
    }
}