package service;

import dao.ProductDAO;
import dao.ProductImageDAO;
import dao.CategoryDAO;
import entity.Product;
import entity.ProductImage;
import entity.Category;
import entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class ProductService {
    
    @Autowired
    private ProductDAO productDAO;
    
    @Autowired
    private ProductImageDAO productImageDAO;
    
    @Autowired
    private CategoryDAO categoryDAO;
    
    public Product createProduct(Product product, User seller) {
        // Validate product data
        validateProduct(product);
        
        // Set seller
        product.setSeller(seller);
        
        // Set timestamps
        product.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        product.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        
        // Set initial values
        if (product.getStatus() == null) {
            product.setStatus(Product.ProductStatus.DRAFT);
        }
        
        if (product.getQuantityAvailable() == null) {
            product.setQuantityAvailable(1);
        }
        
        if (product.getQuantitySold() == null) {
            product.setQuantitySold(0);
        }
        
        if (product.getViewCount() == null) {
            product.setViewCount(0);
        }
        
        if (product.getWatchCount() == null) {
            product.setWatchCount(0);
        }
        
        if (product.getShippingCost() == null) {
            product.setShippingCost(BigDecimal.ZERO);
        }
        
        // Set current price to starting price initially
        if (product.getCurrentPrice() == null) {
            product.setCurrentPrice(product.getStartingPrice());
        }
        
        // Validate auction settings
        if (product.isAuction()) {
            validateAuctionSettings(product);
        }
        
        return productDAO.save(product);
    }
    
    public Product getProductById(Integer productId) {
        return productDAO.findById(productId);
    }
    
    public Product getProductByIdAndIncrementView(Integer productId) {
        Product product = productDAO.findById(productId);
        if (product != null && product.getStatus() == Product.ProductStatus.ACTIVE) {
            productDAO.incrementViewCount(productId);
            product.incrementViewCount(); // Update the local object
        }
        return product;
    }
    
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }
    
    public List<Product> getActiveProducts() {
        return productDAO.findActiveProducts();
    }
    
    public List<Product> getProductsByCategory(Integer categoryId) {
        return productDAO.findByCategory(categoryId);
    }
    
    public List<Product> getProductsBySeller(Integer sellerId) {
        return productDAO.findBySeller(sellerId);
    }
    
    public List<Product> getProductsBySellerAndStatus(Integer sellerId, Product.ProductStatus status) {
        return productDAO.findBySellerAndStatus(sellerId, status);
    }
    
    public List<Product> getProductsByListingType(Product.ListingType listingType) {
        return productDAO.findByListingType(listingType);
    }
    
    public List<Product> getAuctionsEndingSoon(int hours) {
        return productDAO.findAuctionsEndingSoon(hours);
    }
    
    public List<Product> searchProducts(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getActiveProducts();
        }
        return productDAO.searchProducts(searchTerm.trim());
    }
    
    public List<Product> searchProductsWithFilters(String searchTerm, Integer categoryId,
                                                   BigDecimal minPrice, BigDecimal maxPrice,
                                                   Product.ConditionType condition,
                                                   Product.ListingType listingType) {
        return productDAO.searchProductsWithFilters(searchTerm, categoryId, minPrice, maxPrice, condition, listingType);
    }
    
    public List<Product> getFeaturedProducts(int limit) {
        return productDAO.findFeaturedProducts(limit);
    }
    
    public List<Product> getRecentProducts(int limit) {
        return productDAO.findRecentProducts(limit);
    }
    
    public List<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productDAO.findProductsByPriceRange(minPrice, maxPrice);
    }
    
    public Product updateProduct(Product product) {
        Product existingProduct = productDAO.findById(product.getProductId());
        if (existingProduct == null) {
            throw new RuntimeException("Product not found");
        }
        
        // Validate product data
        validateProduct(product);
        
        // Validate auction settings if it's an auction
        if (product.isAuction()) {
            validateAuctionSettings(product);
        }
        
        // Update timestamp
        product.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        
        return productDAO.update(product);
    }
    
    public void deleteProduct(Integer productId, Integer userId) {
        Product product = productDAO.findById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        
        // Check if user owns the product
        if (!product.getSeller().getUserId().equals(userId)) {
            throw new RuntimeException("You can only delete your own products");
        }
        
        // Check if product can be deleted (no active bids, etc.)
        if (product.getStatus() == Product.ProductStatus.ACTIVE && product.isAuction()) {
            // TODO: Check for active bids when bid system is implemented
        }
        
        // Delete associated images first
        productImageDAO.deleteByProductId(productId);
        
        productDAO.delete(product);
    }
    
    public void activateProduct(Integer productId, Integer userId) {
        Product product = productDAO.findById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        
        if (!product.getSeller().getUserId().equals(userId)) {
            throw new RuntimeException("You can only activate your own products");
        }
        
        // Validate product is ready for activation
        validateProductForActivation(product);
        
        product.setStatus(Product.ProductStatus.ACTIVE);
        
        // Set auction start time if it's an auction and not set
        if (product.isAuction() && product.getAuctionStartTime() == null) {
            product.setAuctionStartTime(new Timestamp(System.currentTimeMillis()));
        }
        
        productDAO.update(product);
    }
    
    public void deactivateProduct(Integer productId, Integer userId) {
        Product product = productDAO.findById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        
        if (!product.getSeller().getUserId().equals(userId)) {
            throw new RuntimeException("You can only deactivate your own products");
        }
        
        product.setStatus(Product.ProductStatus.CANCELLED);
        productDAO.update(product);
    }
    
    public void incrementWatchCount(Integer productId) {
        productDAO.incrementWatchCount(productId);
    }
    
    public void decrementWatchCount(Integer productId) {
        productDAO.decrementWatchCount(productId);
    }
    
    public Long getTotalProductCount() {
        return productDAO.getTotalProductCount();
    }
    
    public Long getProductCountByCategory(Integer categoryId) {
        return productDAO.getProductCountByCategory(categoryId);
    }
    
    public void updateProductQuantity(Integer productId, Integer quantity, Integer userId) {
        Product product = productDAO.findById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        
        if (!product.getSeller().getUserId().equals(userId)) {
            throw new RuntimeException("You can only update your own products");
        }
        
        if (quantity < 0) {
            throw new RuntimeException("Quantity cannot be negative");
        }
        
        product.setQuantityAvailable(quantity);
        product.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        
        // Update status based on quantity
        if (quantity == 0 && product.getStatus() == Product.ProductStatus.ACTIVE) {
            product.setStatus(Product.ProductStatus.SOLD);
        } else if (quantity > 0 && product.getStatus() == Product.ProductStatus.SOLD) {
            product.setStatus(Product.ProductStatus.ACTIVE);
        }
        
        productDAO.update(product);
    }
    
    // Product Image Management
    public ProductImage addProductImage(Integer productId, String imageUrl, String altText, Boolean isPrimary) {
        Product product = productDAO.findById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        
        ProductImage productImage = new ProductImage(product, imageUrl, altText);
        
        // If this is set as primary or no images exist yet, make it primary
        Long imageCount = productImageDAO.getImageCountByProductId(productId);
        if (Boolean.TRUE.equals(isPrimary) || imageCount == 0) {
            // Clear existing primary status
            productImageDAO.clearPrimaryStatus(productId);
            productImage.setIsPrimary(true);
        }
        
        // Set sort order
        productImage.setSortOrder(imageCount.intValue());
        
        return productImageDAO.save(productImage);
    }
    
    public List<ProductImage> getProductImages(Integer productId) {
        return productImageDAO.findByProductId(productId);
    }
    
    public ProductImage getPrimaryProductImage(Integer productId) {
        return productImageDAO.findPrimaryImageByProductId(productId);
    }
    
    public void deleteProductImage(Integer imageId) {
        productImageDAO.deleteById(imageId);
    }
    
    public void setPrimaryImage(Integer imageId) {
        productImageDAO.setPrimaryImage(imageId);
    }
    
    // Auction Management
    public List<Product> getExpiredAuctions() {
        return productDAO.findExpiredAuctions();
    }
    
    public void processExpiredAuctions() {
        List<Product> expiredAuctions = getExpiredAuctions();
        for (Product product : expiredAuctions) {
            productDAO.markAuctionAsEnded(product.getProductId());
            // TODO: Process winning bid and create order
        }
    }
    
    /**
     * Validate product data
     */
    private void validateProduct(Product product) {
        if (product.getTitle() == null || product.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Product title is required");
        }
        
        if (product.getTitle().length() > 200) {
            throw new RuntimeException("Product title must be 200 characters or less");
        }
        
        if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
            throw new RuntimeException("Product description is required");
        }
        
        if (product.getStartingPrice() == null || product.getStartingPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Starting price must be greater than 0");
        }
        
        if (product.getCategory() == null) {
            throw new RuntimeException("Product category is required");
        }
        
        if (product.getConditionType() == null) {
            throw new RuntimeException("Product condition is required");
        }
        
        if (product.getListingType() == null) {
            throw new RuntimeException("Listing type is required");
        }
        
        // Validate buy now price if set
        if (product.getBuyNowPrice() != null && 
            product.getBuyNowPrice().compareTo(product.getStartingPrice()) < 0) {
            throw new RuntimeException("Buy now price must be greater than or equal to starting price");
        }
        
        // Validate reserve price if set
        if (product.getReservePrice() != null && 
            product.getReservePrice().compareTo(product.getStartingPrice()) < 0) {
            throw new RuntimeException("Reserve price must be greater than or equal to starting price");
        }
    }
    
    private void validateAuctionSettings(Product product) {
        if (product.getAuctionEndTime() == null) {
            throw new RuntimeException("Auction end time is required for auction listings");
        }
        
        if (product.getAuctionEndTime().before(new Timestamp(System.currentTimeMillis()))) {
            throw new RuntimeException("Auction end time must be in the future");
        }
        
        // Auction should last at least 1 hour
        long duration = product.getAuctionEndTime().getTime() - System.currentTimeMillis();
        if (duration < 60 * 60 * 1000) { // 1 hour in milliseconds
            throw new RuntimeException("Auction must last at least 1 hour");
        }
    }
    
    private void validateProductForActivation(Product product) {
        // Check if product has at least one image
        Long imageCount = productImageDAO.getImageCountByProductId(product.getProductId());
        if (imageCount == 0) {
            throw new RuntimeException("Product must have at least one image before activation");
        }
        
        // Additional validation can be added here
    }
}