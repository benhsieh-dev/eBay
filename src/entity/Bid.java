package entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "bids")
public class Bid {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bid_id")
    private Integer bidId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bidder_id", nullable = false)
    private User bidder;
    
    @Column(name = "bid_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal bidAmount;
    
    @Column(name = "bid_time")
    private Timestamp bidTime;
    
    @Column(name = "is_winning_bid")
    private Boolean isWinningBid = false;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "bid_type")
    private BidType bidType = BidType.REGULAR;
    
    @Column(name = "max_proxy_amount", precision = 10, scale = 2)
    private BigDecimal maxProxyAmount;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "bid_status")
    private BidStatus bidStatus = BidStatus.ACTIVE;
    
    // Enums
    public enum BidType {
        REGULAR, PROXY, BUY_NOW
    }
    
    public enum BidStatus {
        ACTIVE, OUTBID, WINNING, WON, CANCELLED
    }
    
    // Constructors
    public Bid() {
        this.bidTime = new Timestamp(System.currentTimeMillis());
    }
    
    public Bid(Product product, User bidder, BigDecimal bidAmount) {
        this();
        this.product = product;
        this.bidder = bidder;
        this.bidAmount = bidAmount;
    }
    
    public Bid(Product product, User bidder, BigDecimal bidAmount, BidType bidType) {
        this(product, bidder, bidAmount);
        this.bidType = bidType;
    }
    
    // Getters and Setters
    public Integer getBidId() { return bidId; }
    public void setBidId(Integer bidId) { this.bidId = bidId; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public User getBidder() { return bidder; }
    public void setBidder(User bidder) { this.bidder = bidder; }
    
    public BigDecimal getBidAmount() { return bidAmount; }
    public void setBidAmount(BigDecimal bidAmount) { this.bidAmount = bidAmount; }
    
    public Timestamp getBidTime() { return bidTime; }
    public void setBidTime(Timestamp bidTime) { this.bidTime = bidTime; }
    
    public Boolean getIsWinningBid() { return isWinningBid; }
    public void setIsWinningBid(Boolean isWinningBid) { this.isWinningBid = isWinningBid; }
    
    public BidType getBidType() { return bidType; }
    public void setBidType(BidType bidType) { this.bidType = bidType; }
    
    public BigDecimal getMaxProxyAmount() { return maxProxyAmount; }
    public void setMaxProxyAmount(BigDecimal maxProxyAmount) { this.maxProxyAmount = maxProxyAmount; }
    
    public BidStatus getBidStatus() { return bidStatus; }
    public void setBidStatus(BidStatus bidStatus) { this.bidStatus = bidStatus; }
    
    // Utility methods
    public boolean isProxyBid() {
        return bidType == BidType.PROXY;
    }
    
    public boolean isBuyNowBid() {
        return bidType == BidType.BUY_NOW;
    }
    
    public boolean isActive() {
        return bidStatus == BidStatus.ACTIVE || bidStatus == BidStatus.WINNING;
    }
}