package dto;

import entity.Product;
import java.math.BigDecimal;

public class ProductGraphQLDto {
    private Integer productId;
    private String title;
    private BigDecimal currentPrice;
    private BigDecimal startingPrice;
    private BigDecimal buyNowPrice;
    private String description;
    private String conditionType;
    private String listingType;
    private Long bidCount;
    private SellerDto seller;
    private CategoryDto category;

    public ProductGraphQLDto(Product product) {
        this.productId = product.getProductId();
        this.title = product.getTitle();
        this.currentPrice = product.getCurrentPrice();
        this.startingPrice = product.getStartingPrice();
        this.buyNowPrice = product.getBuyNowPrice();
        this.description = product.getDescription();
        this.conditionType = product.getConditionType() != null ? product.getConditionType().toString() : null;
        this.listingType = product.getListingType() != null ? product.getListingType().toString() : null;
        this.bidCount = 0L; // Will be set separately
        
        if (product.getSeller() != null) {
            this.seller = new SellerDto(
                product.getSeller().getUserId(),
                product.getSeller().getUsername(),
                product.getSeller().getFirstName(),
                product.getSeller().getLastName()
            );
        }
        
        if (product.getCategory() != null) {
            this.category = new CategoryDto(
                product.getCategory().getCategoryId(),
                product.getCategory().getCategoryName()
            );
        }
    }

    // Getters
    public Integer getProductId() { return productId; }
    public String getTitle() { return title; }
    public BigDecimal getCurrentPrice() { return currentPrice; }
    public BigDecimal getStartingPrice() { return startingPrice; }
    public BigDecimal getBuyNowPrice() { return buyNowPrice; }
    public String getDescription() { return description; }
    public String getConditionType() { return conditionType; }
    public String getListingType() { return listingType; }
    public Long getBidCount() { return bidCount; }
    public SellerDto getSeller() { return seller; }
    public CategoryDto getCategory() { return category; }
    
    // Setter for bidCount (to be populated by service)
    public void setBidCount(Long bidCount) { this.bidCount = bidCount; }

    public static class SellerDto {
        private Integer userId;
        private String username;
        private String firstName;
        private String lastName;

        public SellerDto(Integer userId, String username, String firstName, String lastName) {
            this.userId = userId;
            this.username = username;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public Integer getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
    }

    public static class CategoryDto {
        private Integer categoryId;
        private String categoryName;

        public CategoryDto(Integer categoryId, String categoryName) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
        }

        public Integer getCategoryId() { return categoryId; }
        public String getCategoryName() { return categoryName; }
    }
}