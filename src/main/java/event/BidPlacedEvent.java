package event;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BidPlacedEvent {
    
    private Long bidId;
    private Long productId;
    private String productTitle;
    private Long bidderId;
    private String bidderUsername;
    private BigDecimal bidAmount;
    private BigDecimal previousHighestBid;
    private String bidType;
    private boolean isWinningBid;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime bidTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime auctionEndTime;
    
    // Default constructor for JSON deserialization
    public BidPlacedEvent() {
        this.bidTime = LocalDateTime.now();
    }
    
    public BidPlacedEvent(Long bidId, Long productId, String productTitle, 
                         Long bidderId, String bidderUsername, BigDecimal bidAmount,
                         BigDecimal previousHighestBid, String bidType, boolean isWinningBid,
                         LocalDateTime auctionEndTime) {
        this();
        this.bidId = bidId;
        this.productId = productId;
        this.productTitle = productTitle;
        this.bidderId = bidderId;
        this.bidderUsername = bidderUsername;
        this.bidAmount = bidAmount;
        this.previousHighestBid = previousHighestBid;
        this.bidType = bidType;
        this.isWinningBid = isWinningBid;
        this.auctionEndTime = auctionEndTime;
    }
    
    // Getters and Setters
    public Long getBidId() { return bidId; }
    public void setBidId(Long bidId) { this.bidId = bidId; }
    
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    
    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }
    
    public Long getBidderId() { return bidderId; }
    public void setBidderId(Long bidderId) { this.bidderId = bidderId; }
    
    public String getBidderUsername() { return bidderUsername; }
    public void setBidderUsername(String bidderUsername) { this.bidderUsername = bidderUsername; }
    
    public BigDecimal getBidAmount() { return bidAmount; }
    public void setBidAmount(BigDecimal bidAmount) { this.bidAmount = bidAmount; }
    
    public BigDecimal getPreviousHighestBid() { return previousHighestBid; }
    public void setPreviousHighestBid(BigDecimal previousHighestBid) { this.previousHighestBid = previousHighestBid; }
    
    public String getBidType() { return bidType; }
    public void setBidType(String bidType) { this.bidType = bidType; }
    
    public boolean isWinningBid() { return isWinningBid; }
    public void setWinningBid(boolean winningBid) { isWinningBid = winningBid; }
    
    public LocalDateTime getBidTime() { return bidTime; }
    public void setBidTime(LocalDateTime bidTime) { this.bidTime = bidTime; }
    
    public LocalDateTime getAuctionEndTime() { return auctionEndTime; }
    public void setAuctionEndTime(LocalDateTime auctionEndTime) { this.auctionEndTime = auctionEndTime; }
    
    @Override
    public String toString() {
        return "BidPlacedEvent{" +
                "bidId=" + bidId +
                ", productId=" + productId +
                ", productTitle='" + productTitle + '\'' +
                ", bidderId=" + bidderId +
                ", bidderUsername='" + bidderUsername + '\'' +
                ", bidAmount=" + bidAmount +
                ", previousHighestBid=" + previousHighestBid +
                ", bidType='" + bidType + '\'' +
                ", isWinningBid=" + isWinningBid +
                ", bidTime=" + bidTime +
                ", auctionEndTime=" + auctionEndTime +
                '}';
    }
}