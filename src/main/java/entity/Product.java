package entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "starting_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal startingPrice;
    
    @Column(name = "current_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentPrice;
    
    @Column(name = "buy_now_price", precision = 10, scale = 2)
    private BigDecimal buyNowPrice;
    
    @Column(name = "reserve_price", precision = 10, scale = 2)
    private BigDecimal reservePrice;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type", nullable = false)
    private ConditionType conditionType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "listing_type", nullable = false)
    private ListingType listingType;
    
    @Column(name = "auction_start_time")
    private Timestamp auctionStartTime;
    
    @Column(name = "auction_end_time")
    private Timestamp auctionEndTime;
    
    @Column(name = "quantity_available")
    private Integer quantityAvailable = 1;
    
    @Column(name = "quantity_sold")
    private Integer quantitySold = 0;
    
    @Column(name = "shipping_cost", precision = 8, scale = 2)
    private BigDecimal shippingCost = BigDecimal.ZERO;
    
    @Column(name = "shipping_method", length = 100)
    private String shippingMethod;
    
    @Column(name = "return_policy", columnDefinition = "TEXT")
    private String returnPolicy;
    
    @Column(name = "item_location", length = 100)
    private String itemLocation;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProductStatus status = ProductStatus.DRAFT;
    
    @Column(name = "view_count")
    private Integer viewCount = 0;
    
    @Column(name = "watch_count")
    private Integer watchCount = 0;
    
    @Column(name = "created_date")
    private Timestamp createdDate;
    
    @Column(name = "updated_date")
    private Timestamp updatedDate;
    
    // Relationships
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductImage> images;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bid> bids;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItem> cartItems;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Watchlist> watchers;
    
    // Enums
    public enum ConditionType {
        NEW, LIKE_NEW, VERY_GOOD, GOOD, ACCEPTABLE, FOR_PARTS
    }
    
    public enum ListingType {
        AUCTION, BUY_NOW, BOTH
    }
    
    public enum ProductStatus {
        DRAFT, ACTIVE, SOLD, ENDED, CANCELLED
    }
    
    // Constructors
    public Product() {
        this.createdDate = new Timestamp(System.currentTimeMillis());
        this.updatedDate = new Timestamp(System.currentTimeMillis());
    }
    
    public Product(String title, String description, BigDecimal startingPrice, ConditionType conditionType, ListingType listingType) {
        this();
        this.title = title;
        this.description = description;
        this.startingPrice = startingPrice;
        this.currentPrice = startingPrice;
        this.conditionType = conditionType;
        this.listingType = listingType;
    }
    
    // Getters and Setters
    public Integer getProductId() { return productId; }
    public void setProductId(Integer productId) { this.productId = productId; }
    
    public User getSeller() { return seller; }
    public void setSeller(User seller) { this.seller = seller; }
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getStartingPrice() { return startingPrice; }
    public void setStartingPrice(BigDecimal startingPrice) { this.startingPrice = startingPrice; }
    
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(BigDecimal currentPrice) { this.currentPrice = currentPrice; }
    
    public BigDecimal getBuyNowPrice() { return buyNowPrice; }
    public void setBuyNowPrice(BigDecimal buyNowPrice) { this.buyNowPrice = buyNowPrice; }
    
    public BigDecimal getReservePrice() { return reservePrice; }
    public void setReservePrice(BigDecimal reservePrice) { this.reservePrice = reservePrice; }
    
    public ConditionType getConditionType() { return conditionType; }
    public void setConditionType(ConditionType conditionType) { this.conditionType = conditionType; }
    
    public ListingType getListingType() { return listingType; }
    public void setListingType(ListingType listingType) { this.listingType = listingType; }
    
    public Timestamp getAuctionStartTime() { return auctionStartTime; }
    public void setAuctionStartTime(Timestamp auctionStartTime) { this.auctionStartTime = auctionStartTime; }
    
    public Timestamp getAuctionEndTime() { return auctionEndTime; }
    public void setAuctionEndTime(Timestamp auctionEndTime) { this.auctionEndTime = auctionEndTime; }
    
    public Integer getQuantityAvailable() { return quantityAvailable; }
    public void setQuantityAvailable(Integer quantityAvailable) { this.quantityAvailable = quantityAvailable; }
    
    public Integer getQuantitySold() { return quantitySold; }
    public void setQuantitySold(Integer quantitySold) { this.quantitySold = quantitySold; }
    
    public BigDecimal getShippingCost() { return shippingCost; }
    public void setShippingCost(BigDecimal shippingCost) { this.shippingCost = shippingCost; }
    
    public String getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }
    
    public String getReturnPolicy() { return returnPolicy; }
    public void setReturnPolicy(String returnPolicy) { this.returnPolicy = returnPolicy; }
    
    public String getItemLocation() { return itemLocation; }
    public void setItemLocation(String itemLocation) { this.itemLocation = itemLocation; }
    
    public ProductStatus getStatus() { return status; }
    public void setStatus(ProductStatus status) { this.status = status; }
    
    public Integer getViewCount() { return viewCount; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    
    public Integer getWatchCount() { return watchCount; }
    public void setWatchCount(Integer watchCount) { this.watchCount = watchCount; }
    
    public Timestamp getCreatedDate() { return createdDate; }
    public void setCreatedDate(Timestamp createdDate) { this.createdDate = createdDate; }
    
    public Timestamp getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(Timestamp updatedDate) { this.updatedDate = updatedDate; }
    
    // Relationship getters and setters
    public List<ProductImage> getImages() { return images; }
    public void setImages(List<ProductImage> images) { this.images = images; }
    
    public List<Bid> getBids() { return bids; }
    public void setBids(List<Bid> bids) { this.bids = bids; }
    
    public List<CartItem> getCartItems() { return cartItems; }
    public void setCartItems(List<CartItem> cartItems) { this.cartItems = cartItems; }
    
    public List<Watchlist> getWatchers() { return watchers; }
    public void setWatchers(List<Watchlist> watchers) { this.watchers = watchers; }
    
    // Utility methods
    public boolean isAuction() {
        return listingType == ListingType.AUCTION || listingType == ListingType.BOTH;
    }
    
    public boolean isBuyNowAvailable() {
        return listingType == ListingType.BUY_NOW || listingType == ListingType.BOTH;
    }
    
    public boolean isAuctionActive() {
        if (!isAuction() || status != ProductStatus.ACTIVE) {
            return false;
        }
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return auctionStartTime != null && auctionEndTime != null && 
               now.after(auctionStartTime) && now.before(auctionEndTime);
    }
    
    public boolean isAuctionEnded() {
        if (!isAuction()) {
            return false;
        }
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return auctionEndTime != null && now.after(auctionEndTime);
    }
    
    public long getTimeRemainingMillis() {
        if (auctionEndTime == null) {
            return 0;
        }
        long remaining = auctionEndTime.getTime() - System.currentTimeMillis();
        return Math.max(0, remaining);
    }
    
    public String getFormattedTimeRemaining() {
        long millis = getTimeRemainingMillis();
        if (millis <= 0) {
            return "Auction ended";
        }
        
        long days = millis / (24 * 60 * 60 * 1000);
        long hours = (millis % (24 * 60 * 60 * 1000)) / (60 * 60 * 1000);
        long minutes = (millis % (60 * 60 * 1000)) / (60 * 1000);
        
        if (days > 0) {
            return days + "d " + hours + "h " + minutes + "m";
        } else if (hours > 0) {
            return hours + "h " + minutes + "m";
        } else {
            return minutes + "m";
        }
    }
    
    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }
    
    public void incrementWatchCount() {
        this.watchCount = (this.watchCount == null ? 0 : this.watchCount) + 1;
    }
    
    public void decrementWatchCount() {
        this.watchCount = Math.max(0, (this.watchCount == null ? 0 : this.watchCount) - 1);
    }
}