package entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Entity representing user votes on review helpfulness
 * Tracks whether users found a review helpful or not helpful
 */
@Entity
@Table(name = "review_helpfulness", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"review_id", "user_id"})
})
public class ReviewHelpfulness {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "helpfulness_id")
    private Integer helpfulnessId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "is_helpful", nullable = false)
    private Boolean isHelpful; // true = helpful, false = not helpful
    
    @Column(name = "created_at")
    private Timestamp createdAt;
    
    // Constructors
    public ReviewHelpfulness() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
    
    public ReviewHelpfulness(Review review, User user, Boolean isHelpful) {
        this();
        this.review = review;
        this.user = user;
        this.isHelpful = isHelpful;
    }
    
    // Getters and Setters
    public Integer getHelpfulnessId() { return helpfulnessId; }
    public void setHelpfulnessId(Integer helpfulnessId) { this.helpfulnessId = helpfulnessId; }
    
    public Review getReview() { return review; }
    public void setReview(Review review) { this.review = review; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Boolean getIsHelpful() { return isHelpful; }
    public void setIsHelpful(Boolean isHelpful) { this.isHelpful = isHelpful; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    // Utility methods
    public boolean isHelpfulVote() {
        return isHelpful != null && isHelpful;
    }
    
    public boolean isNotHelpfulVote() {
        return isHelpful != null && !isHelpful;
    }
}