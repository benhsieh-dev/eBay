package entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "watchlist")
public class Watchlist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "watchlist_id")
    private Integer watchlistId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "added_date")
    private Timestamp addedDate;
    
    // Constructors
    public Watchlist() {
        this.addedDate = new Timestamp(System.currentTimeMillis());
    }
    
    public Watchlist(User user, Product product) {
        this();
        this.user = user;
        this.product = product;
    }
    
    // Getters and Setters
    public Integer getWatchlistId() { return watchlistId; }
    public void setWatchlistId(Integer watchlistId) { this.watchlistId = watchlistId; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public Timestamp getAddedDate() { return addedDate; }
    public void setAddedDate(Timestamp addedDate) { this.addedDate = addedDate; }
}