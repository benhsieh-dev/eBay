package entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Entity representing images attached to reviews
 * Allows users to include photos with their reviews
 */
@Entity
@Table(name = "review_images")
public class ReviewImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer imageId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;
    
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;
    
    @Column(name = "original_filename", length = 255)
    private String originalFilename;
    
    @Column(name = "file_size")
    private Long fileSize; // Size in bytes
    
    @Column(name = "mime_type", length = 100)
    private String mimeType;
    
    @Column(name = "width")
    private Integer width;
    
    @Column(name = "height")
    private Integer height;
    
    @Column(name = "caption", length = 500)
    private String caption;
    
    @Column(name = "display_order")
    private Integer displayOrder = 0;
    
    @Column(name = "is_primary")
    private Boolean isPrimary = false;
    
    @Column(name = "uploaded_at")
    private Timestamp uploadedAt;
    
    // Constructors
    public ReviewImage() {
        this.uploadedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public ReviewImage(Review review, String imageUrl, String originalFilename) {
        this();
        this.review = review;
        this.imageUrl = imageUrl;
        this.originalFilename = originalFilename;
    }
    
    public ReviewImage(Review review, String imageUrl, String originalFilename, 
                      String mimeType, Long fileSize, Integer width, Integer height) {
        this(review, imageUrl, originalFilename);
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.width = width;
        this.height = height;
    }
    
    // Getters and Setters
    public Integer getImageId() { return imageId; }
    public void setImageId(Integer imageId) { this.imageId = imageId; }
    
    public Review getReview() { return review; }
    public void setReview(Review review) { this.review = review; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    
    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }
    
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    
    public Integer getWidth() { return width; }
    public void setWidth(Integer width) { this.width = width; }
    
    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }
    
    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }
    
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    
    public Boolean getIsPrimary() { return isPrimary; }
    public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
    
    public Timestamp getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Timestamp uploadedAt) { this.uploadedAt = uploadedAt; }
    
    // Utility methods
    public boolean isImage() {
        return mimeType != null && mimeType.startsWith("image/");
    }
    
    public String getFormattedFileSize() {
        if (fileSize == null) return "Unknown size";
        
        long bytes = fileSize;
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }
    
    public String getThumbnailUrl() {
        // Return thumbnail version of image if available
        if (imageUrl != null && isImage()) {
            // Simple thumbnail URL generation - in production this would be more sophisticated
            return imageUrl.replace("/uploads/", "/uploads/thumbnails/");
        }
        return imageUrl;
    }
    
    public String getDimensions() {
        if (width != null && height != null) {
            return width + " × " + height;
        }
        return null;
    }
}