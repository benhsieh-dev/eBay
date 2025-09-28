package entity;

import jakarta.persistence.*;
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
    
    @Column(name = "image_url")
    private String imageUrl; // Keep for backward compatibility, but will be null for BLOB storage
    
    @Lob
    @Column(name = "image_data", columnDefinition = "BYTEA")
    private byte[] imageData; // Store actual image data as BLOB
    
    @Column(name = "content_type", length = 100)
    private String contentType; // MIME type (image/jpeg, image/png, etc.)
    
    @Column(name = "original_filename", length = 255)
    private String originalFilename; // Original uploaded filename
    
    @Column(name = "file_size")
    private Long fileSize; // File size in bytes
    
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
    
    public ProductImage(Product product, byte[] imageData, String contentType, String originalFilename, String altText) {
        this();
        this.product = product;
        this.imageData = imageData;
        this.contentType = contentType;
        this.originalFilename = originalFilename;
        this.fileSize = (long) imageData.length;
        this.altText = altText;
    }
    
    // Getters and Setters
    public Integer getImageId() { return imageId; }
    public void setImageId(Integer imageId) { this.imageId = imageId; }
    
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public byte[] getImageData() { return imageData; }
    public void setImageData(byte[] imageData) { this.imageData = imageData; }
    
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getAltText() { return altText; }
    public void setAltText(String altText) { this.altText = altText; }
    
    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
    
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    
    public Timestamp getUploadedDate() { return uploadedDate; }
    public void setUploadedDate(Timestamp uploadedDate) { this.uploadedDate = uploadedDate; }
    
    // Helper methods
    public boolean hasImageData() {
        return imageData != null && imageData.length > 0;
    }
    
    public String getEffectiveImageUrl() {
        // If we have BLOB data, return API endpoint URL
        if (hasImageData()) {
            return "/api/images/" + imageId;
        }
        // Otherwise return legacy file URL
        return imageUrl;
    }
}