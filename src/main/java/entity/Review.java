package entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Review entity representing feedback between users for transactions
 * Supports both seller reviews (buyer rating seller) and buyer reviews (seller rating buyer)
 */
@Entity
@Table(name = "reviews")
public class Review {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Integer reviewId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer; // Person giving the review
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewee_id", nullable = false)
    private User reviewee; // Person receiving the review
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // Related order/transaction
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product; // Related product (optional, for product reviews)
    
    @Enumerated(EnumType.STRING)
    @Column(name = "review_type", nullable = false)
    private ReviewType reviewType;
    
    @Column(name = "rating", nullable = false, precision = 2, scale = 1)
    private BigDecimal rating; // 1.0 to 5.0 rating
    
    @Column(name = "title", length = 200)
    private String title;
    
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReviewStatus status = ReviewStatus.ACTIVE;
    
    @Column(name = "created_at")
    private Timestamp createdAt;
    
    @Column(name = "updated_at")
    private Timestamp updatedAt;
    
    @Column(name = "helpful_count")
    private Integer helpfulCount = 0;
    
    @Column(name = "not_helpful_count")
    private Integer notHelpfulCount = 0;
    
    @Column(name = "verified_purchase")
    private Boolean verifiedPurchase = false;
    
    // Detailed ratings (for comprehensive feedback)
    @Column(name = "communication_rating", precision = 2, scale = 1)
    private BigDecimal communicationRating;
    
    @Column(name = "shipping_rating", precision = 2, scale = 1)
    private BigDecimal shippingRating;
    
    @Column(name = "item_description_rating", precision = 2, scale = 1)
    private BigDecimal itemDescriptionRating;
    
    @Column(name = "response_time_rating", precision = 2, scale = 1)
    private BigDecimal responseTimeRating;
    
    // Moderation fields
    @Column(name = "flagged")
    private Boolean flagged = false;
    
    @Column(name = "flagged_reason", length = 500)
    private String flaggedReason;
    
    @Column(name = "moderated_by")
    private Integer moderatedBy;
    
    @Column(name = "moderated_at")
    private Timestamp moderatedAt;
    
    // Response from reviewee
    @Column(name = "response", columnDefinition = "TEXT")
    private String response;
    
    @Column(name = "response_date")
    private Timestamp responseDate;
    
    // Relationships
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewHelpfulness> helpfulnessVotes;
    
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReviewImage> reviewImages;
    
    // Enums
    public enum ReviewType {
        SELLER_REVIEW,      // Buyer reviewing seller
        BUYER_REVIEW,       // Seller reviewing buyer
        PRODUCT_REVIEW,     // Product-specific review
        TRANSACTION_REVIEW  // General transaction review
    }
    
    public enum ReviewStatus {
        ACTIVE,        // Review is visible
        PENDING,       // Under moderation
        HIDDEN,        // Hidden by moderator
        DELETED,       // Soft deleted
        REPORTED       // Reported by users
    }
    
    // Constructors
    public Review() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = this.createdAt;
    }
    
    public Review(User reviewer, User reviewee, Order order, ReviewType reviewType, BigDecimal rating) {
        this();
        this.reviewer = reviewer;
        this.reviewee = reviewee;
        this.order = order;
        this.reviewType = reviewType;
        this.rating = rating;
    }
    
    public Review(User reviewer, User reviewee, Order order, Product product, ReviewType reviewType, 
                  BigDecimal rating, String title, String comment) {
        this(reviewer, reviewee, order, reviewType, rating);
        this.product = product;
        this.title = title;
        this.comment = comment;
    }
    
    // Getters and Setters
    public Integer getReviewId() { return reviewId; }
    public void setReviewId(Integer reviewId) { this.reviewId = reviewId; }
    
    public User getReviewer() { return reviewer; }
    public void setReviewer(User reviewer) { this.reviewer = reviewer; }
    
    public User getReviewee() { return reviewee; }
    public void setReviewee(User reviewee) { this.reviewee = reviewee; }
    
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public ReviewType getReviewType() { return reviewType; }
    public void setReviewType(ReviewType reviewType) { this.reviewType = reviewType; }
    
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public ReviewStatus getStatus() { return status; }
    public void setStatus(ReviewStatus status) { this.status = status; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public Integer getHelpfulCount() { return helpfulCount; }
    public void setHelpfulCount(Integer helpfulCount) { this.helpfulCount = helpfulCount; }
    
    public Integer getNotHelpfulCount() { return notHelpfulCount; }
    public void setNotHelpfulCount(Integer notHelpfulCount) { this.notHelpfulCount = notHelpfulCount; }
    
    public Boolean getVerifiedPurchase() { return verifiedPurchase; }
    public void setVerifiedPurchase(Boolean verifiedPurchase) { this.verifiedPurchase = verifiedPurchase; }
    
    public BigDecimal getCommunicationRating() { return communicationRating; }
    public void setCommunicationRating(BigDecimal communicationRating) { this.communicationRating = communicationRating; }
    
    public BigDecimal getShippingRating() { return shippingRating; }
    public void setShippingRating(BigDecimal shippingRating) { this.shippingRating = shippingRating; }
    
    public BigDecimal getItemDescriptionRating() { return itemDescriptionRating; }
    public void setItemDescriptionRating(BigDecimal itemDescriptionRating) { this.itemDescriptionRating = itemDescriptionRating; }
    
    public BigDecimal getResponseTimeRating() { return responseTimeRating; }
    public void setResponseTimeRating(BigDecimal responseTimeRating) { this.responseTimeRating = responseTimeRating; }
    
    public Boolean getFlagged() { return flagged; }
    public void setFlagged(Boolean flagged) { this.flagged = flagged; }
    
    public String getFlaggedReason() { return flaggedReason; }
    public void setFlaggedReason(String flaggedReason) { this.flaggedReason = flaggedReason; }
    
    public Integer getModeratedBy() { return moderatedBy; }
    public void setModeratedBy(Integer moderatedBy) { this.moderatedBy = moderatedBy; }
    
    public Timestamp getModeratedAt() { return moderatedAt; }
    public void setModeratedAt(Timestamp moderatedAt) { this.moderatedAt = moderatedAt; }
    
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
    
    public Timestamp getResponseDate() { return responseDate; }
    public void setResponseDate(Timestamp responseDate) { this.responseDate = responseDate; }
    
    public List<ReviewHelpfulness> getHelpfulnessVotes() { return helpfulnessVotes; }
    public void setHelpfulnessVotes(List<ReviewHelpfulness> helpfulnessVotes) { this.helpfulnessVotes = helpfulnessVotes; }
    
    public List<ReviewImage> getReviewImages() { return reviewImages; }
    public void setReviewImages(List<ReviewImage> reviewImages) { this.reviewImages = reviewImages; }
    
    // Utility methods
    public boolean isSellerReview() {
        return reviewType == ReviewType.SELLER_REVIEW;
    }
    
    public boolean isBuyerReview() {
        return reviewType == ReviewType.BUYER_REVIEW;
    }
    
    public boolean isProductReview() {
        return reviewType == ReviewType.PRODUCT_REVIEW;
    }
    
    public int getRatingAsInt() {
        return rating != null ? rating.intValue() : 0;
    }
    
    public String getRatingStars() {
        int ratingInt = getRatingAsInt();
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= ratingInt) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        return stars.toString();
    }
    
    public BigDecimal getOverallDetailedRating() {
        int count = 0;
        BigDecimal total = BigDecimal.ZERO;
        
        if (communicationRating != null) {
            total = total.add(communicationRating);
            count++;
        }
        if (shippingRating != null) {
            total = total.add(shippingRating);
            count++;
        }
        if (itemDescriptionRating != null) {
            total = total.add(itemDescriptionRating);
            count++;
        }
        if (responseTimeRating != null) {
            total = total.add(responseTimeRating);
            count++;
        }
        
        if (count == 0) return rating;
        return total.divide(new BigDecimal(count), 1, BigDecimal.ROUND_HALF_UP);
    }
    
    public int getHelpfulnessScore() {
        return helpfulCount - notHelpfulCount;
    }
    
    public double getHelpfulnessPercentage() {
        int total = helpfulCount + notHelpfulCount;
        if (total == 0) return 0.0;
        return (helpfulCount * 100.0) / total;
    }
    
    public boolean hasResponse() {
        return response != null && !response.trim().isEmpty();
    }
    
    public boolean canBeModified() {
        if (createdAt == null) return false;
        long timeElapsed = System.currentTimeMillis() - createdAt.getTime();
        return timeElapsed < (24 * 60 * 60 * 1000); // 24 hours
    }
    
    public String getShortComment(int maxLength) {
        if (comment == null || comment.length() <= maxLength) {
            return comment;
        }
        return comment.substring(0, maxLength) + "...";
    }
    
    public void addResponse(String responseText) {
        this.response = responseText;
        this.responseDate = new Timestamp(System.currentTimeMillis());
        this.updatedAt = this.responseDate;
    }
    
    public void incrementHelpfulCount() {
        this.helpfulCount++;
    }
    
    public void incrementNotHelpfulCount() {
        this.notHelpfulCount++;
    }
    
    public void flagForModeration(String reason) {
        this.flagged = true;
        this.flaggedReason = reason;
        this.status = ReviewStatus.REPORTED;
    }
    
    public void updateTimestamp() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
}