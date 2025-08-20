package entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "product_images")
public class ProductImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "image_url", nullable = false)
    private String imageUrl;
    
    @Column(name = "alt_text", length = 200)
    private String altText;
    
    @Column(name = "is_primary")
    private Boolean isPrimary = false;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Column(name = "uploaded_date")
    private Timestamp uploadedDate;
    
    // Constructors
    public ProductImage() {
        this.uploadedDate = new Timestamp(System.currentTimeMillis());
    }
    
    public ProductImage(Product product, String imageUrl, String altText) {
        this();
        this.product = product;
        this.imageUrl = imageUrl;
        this.altText = altText;
    }
    
    // Getters and Setters
    public Integer getImageId() { return imageId; }
    public void setImageId(Integer imageId) { this.imageId = imageId; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getAltText() { return altText; }
    public void setAltText(String altText) { this.altText = altText; }
    
    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
    
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    
    public Timestamp getUploadedDate() { return uploadedDate; }
    public void setUploadedDate(Timestamp uploadedDate) { this.uploadedDate = uploadedDate; }
}