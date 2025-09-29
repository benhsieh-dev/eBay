package controller.api;

import entity.Product;
import entity.Category;
import service.ProductService;
import service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;
import entity.User;
import entity.ProductImage;
import service.UserService;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = {"http://localhost:3000", "https://ebay-u3h1.onrender.com"})
public class ProductApiController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sort) {
        
        try {
            List<Product> products;
            long totalCount;
            
            if (category != null && !category.isEmpty()) {
                Category categoryEntity = categoryService.getCategoryByName(category);
                if (categoryEntity != null) {
                    products = productService.getProductsByCategory(categoryEntity.getCategoryId());
                } else {
                    products = productService.getAllProducts();
                }
            } else {
                products = productService.getAllProducts();
            }
            
            // Apply sorting if specified
            if (sort != null && !sort.isEmpty()) {
                products = applySorting(products, sort);
            }
            
            totalCount = products.size();
            
            // Apply pagination
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, products.size());
            List<Product> paginatedProducts = products.subList(startIndex, endIndex);
            
            // Convert to safe DTOs
            List<Map<String, Object>> productDTOs = paginatedProducts.stream()
                .map(this::convertToSafeDTO)
                .toList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("products", productDTOs);
            response.put("totalCount", totalCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to fetch products");
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping("/featured")
    public ResponseEntity<Map<String, Object>> getFeaturedProducts() {
        try {
            List<Product> products = productService.getFeaturedProducts(12);
            
            // Convert to safe DTOs
            List<Map<String, Object>> productDTOs = products.stream()
                .map(this::convertToSafeDTO)
                .toList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("products", productDTOs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("products", List.of());
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Integer id) {
        try {
            Product product = productService.getProductById(id);
            if (product != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("product", convertToSafeDTO(product));
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Product not found");
                return ResponseEntity.status(404).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to fetch product");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Integer categoryId) {
        try {
            List<Product> products = productService.getProductsByCategory(categoryId);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<Product> products = productService.searchProducts(query);
            
            long totalCount = products.size();
            
            // Apply pagination
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, products.size());
            List<Product> paginatedProducts = products.subList(startIndex, endIndex);
            
            // Convert to safe DTOs
            List<Map<String, Object>> productDTOs = paginatedProducts.stream()
                .map(this::convertToSafeDTO)
                .toList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("products", productDTOs);
            response.put("totalCount", totalCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Search failed");
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping("/my-listings")
    public ResponseEntity<Map<String, Object>> getUserProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            HttpSession session) {
        try {
            // Get current user from session
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "User not logged in");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            List<Product> userProducts = productService.getProductsBySeller(userId);
            
            long totalCount = userProducts.size();
            
            // Apply pagination
            int startIndex = page * size;
            int endIndex = Math.min(startIndex + size, userProducts.size());
            List<Product> paginatedProducts = userProducts.subList(startIndex, endIndex);
            
            // Convert to safe DTOs
            List<Map<String, Object>> productDTOs = paginatedProducts.stream()
                .map(this::convertToSafeDTO)
                .toList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("products", productDTOs);
            response.put("totalCount", totalCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to fetch user products");
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getCategories() {
        try {
            List<Category> categories = categoryService.getActiveCategories();
            
            // Transform categories to match frontend expectations
            List<Map<String, Object>> categoryList = categories.stream()
                .map(category -> {
                    Map<String, Object> categoryMap = new HashMap<>();
                    categoryMap.put("categoryId", category.getCategoryId());
                    categoryMap.put("name", category.getCategoryName()); // Frontend expects 'name' field
                    categoryMap.put("description", category.getDescription());
                    return categoryMap;
                })
                .toList();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("categories", categoryList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to fetch categories");
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    // Debug endpoint to check and initialize categories
    @GetMapping("/debug/categories")
    public ResponseEntity<Map<String, Object>> debugCategories() {
        try {
            List<Category> allCategories = categoryService.getAllCategories();
            
            // If no categories exist, initialize them
            if (allCategories.isEmpty()) {
                categoryService.initializeDefaultCategories();
                allCategories = categoryService.getAllCategories();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("totalCategories", allCategories.size());
            response.put("categories", allCategories.stream()
                .map(category -> {
                    Map<String, Object> categoryMap = new HashMap<>();
                    categoryMap.put("categoryId", category.getCategoryId());
                    categoryMap.put("name", category.getCategoryName());
                    categoryMap.put("description", category.getDescription());
                    categoryMap.put("isActive", category.getIsActive());
                    return categoryMap;
                })
                .toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Debug failed: " + e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    // Debug endpoint to add missing categories
    @PostMapping("/debug/add-missing-categories")
    public ResponseEntity<Map<String, Object>> addMissingCategories() {
        try {
            // Check if Collectibles category exists
            if (categoryService.getCategoryByName("Collectibles") == null) {
                entity.Category collectibles = new entity.Category("Collectibles", "Antiques, coins, stamps, trading cards, and vintage items");
                categoryService.createCategory(collectibles);
            }
            
            // Check if Toys & Hobbies category exists  
            if (categoryService.getCategoryByName("Toys & Hobbies") == null) {
                entity.Category toys = new entity.Category("Toys & Hobbies", "Action figures, dolls, plush toys, model kits, and hobby supplies");
                categoryService.createCategory(toys);
            }
            
            // Return updated list
            List<entity.Category> allCategories = categoryService.getAllCategories();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Missing categories added successfully");
            response.put("totalCategories", allCategories.size());
            response.put("categories", allCategories.stream()
                .map(category -> {
                    Map<String, Object> categoryMap = new HashMap<>();
                    categoryMap.put("categoryId", category.getCategoryId());
                    categoryMap.put("name", category.getCategoryName());
                    categoryMap.put("description", category.getDescription());
                    categoryMap.put("isActive", category.getIsActive());
                    return categoryMap;
                })
                .toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to add categories: " + e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody Map<String, Object> productData, HttpSession session) {
        try {
            // Get current user from session
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "User not logged in");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            // Get the seller user
            User seller = userService.getUserById(userId);
            if (seller == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "User not found");
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            // Create product from the request data
            Product product = createProductFromRequestData(productData);
            
            // Create the product
            Product createdProduct = productService.createProduct(product, seller);
            
            // Create a safe response DTO to avoid circular references
            Map<String, Object> productResponse = new HashMap<>();
            productResponse.put("productId", createdProduct.getProductId());
            productResponse.put("title", createdProduct.getTitle());
            productResponse.put("description", createdProduct.getDescription());
            productResponse.put("currentPrice", createdProduct.getCurrentPrice());
            productResponse.put("startingPrice", createdProduct.getStartingPrice());
            productResponse.put("status", createdProduct.getStatus().toString());
            productResponse.put("listingType", createdProduct.getListingType().toString());
            productResponse.put("conditionType", createdProduct.getConditionType().toString());
            productResponse.put("quantityAvailable", createdProduct.getQuantityAvailable());
            productResponse.put("shippingCost", createdProduct.getShippingCost());
            productResponse.put("itemLocation", createdProduct.getItemLocation());
            productResponse.put("auctionStartTime", createdProduct.getAuctionStartTime());
            productResponse.put("auctionEndTime", createdProduct.getAuctionEndTime());
            productResponse.put("createdDate", createdProduct.getCreatedDate());
            
            // Add category info safely
            if (createdProduct.getCategory() != null) {
                Map<String, Object> categoryInfo = new HashMap<>();
                categoryInfo.put("categoryId", createdProduct.getCategory().getCategoryId());
                categoryInfo.put("name", createdProduct.getCategory().getCategoryName());
                categoryInfo.put("description", createdProduct.getCategory().getDescription());
                productResponse.put("category", categoryInfo);
            }
            
            // Add seller info safely
            if (createdProduct.getSeller() != null) {
                Map<String, Object> sellerInfo = new HashMap<>();
                sellerInfo.put("userId", createdProduct.getSeller().getUserId());
                sellerInfo.put("username", createdProduct.getSeller().getUsername());
                productResponse.put("seller", sellerInfo);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("product", productResponse);
            response.put("message", "Product created successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to create product: " + e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Integer id, @RequestBody Product product, HttpSession session) {
        try {
            // Get current user from session
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "User not logged in");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            // Check if product exists and user owns it
            Product existingProduct = productService.getProductById(id);
            if (existingProduct == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Product not found");
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            if (!existingProduct.getSeller().getUserId().equals(userId)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Not authorized to update this product");
                return ResponseEntity.status(403).body(errorResponse);
            }
            
            // Update the product
            product.setProductId(id);
            product.setSeller(existingProduct.getSeller());
            Product updatedProduct = productService.updateProduct(product);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("product", updatedProduct);
            response.put("message", "Product updated successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to update product: " + e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }
    
    @PostMapping("/{productId}/images")
    public ResponseEntity<Map<String, Object>> uploadProductImages(
            @PathVariable Integer productId,
            @RequestParam("images") MultipartFile[] files,
            HttpSession session) {
        
        try {
            // Check authentication
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "User not logged in");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            // Check if product exists and user owns it
            Product product = productService.getProductById(productId);
            if (product == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Product not found");
                return ResponseEntity.status(404).body(errorResponse);
            }
            
            if (!product.getSeller().getUserId().equals(userId)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "Not authorized to upload images for this product");
                return ResponseEntity.status(403).body(errorResponse);
            }
            
            List<String> imageUrls = new ArrayList<>();
            
            // Process each uploaded file
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    // Validate file type
                    String contentType = file.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("success", false);
                        errorResponse.put("error", "Only image files are allowed");
                        return ResponseEntity.badRequest().body(errorResponse);
                    }
                    
                    // Validate file size (max 10MB)
                    if (file.getSize() > 10 * 1024 * 1024) {
                        Map<String, Object> errorResponse = new HashMap<>();
                        errorResponse.put("success", false);
                        errorResponse.put("error", "File size must be less than 10MB");
                        return ResponseEntity.badRequest().body(errorResponse);
                    }
                    
                    // Get image data as byte array
                    byte[] imageData = file.getBytes();
                    String originalFilename = file.getOriginalFilename();
                    
                    // Save ProductImage to database as BLOB (for render deployment)
                    Boolean isPrimary = imageUrls.size() == 0; // First image is primary
                    ProductImage savedImage = productService.addProductImageBlob(
                        productId, 
                        imageData,
                        contentType,
                        originalFilename,
                        product.getTitle(), 
                        isPrimary
                    );
                    
                    // Add the API endpoint URL to response
                    String imageUrl = "/api/images/" + savedImage.getImageId();
                    imageUrls.add(imageUrl);
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("imageUrls", imageUrls);
            response.put("message", "Images uploaded successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to save images: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to upload images: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    private List<Product> applySorting(List<Product> products, String sort) {
        switch (sort) {
            case "price_asc":
                return products.stream()
                    .sorted((p1, p2) -> p1.getCurrentPrice().compareTo(p2.getCurrentPrice()))
                    .toList();
            case "price_desc":
                return products.stream()
                    .sorted((p1, p2) -> p2.getCurrentPrice().compareTo(p1.getCurrentPrice()))
                    .toList();
            case "newest":
                return products.stream()
                    .sorted((p1, p2) -> p2.getCreatedDate().compareTo(p1.getCreatedDate()))
                    .toList();
            case "ending_soon":
                return products.stream()
                    .filter(p -> p.getAuctionEndTime() != null)
                    .sorted((p1, p2) -> p1.getAuctionEndTime().compareTo(p2.getAuctionEndTime()))
                    .toList();
            default:
                return products;
        }
    }
    
    private Product createProductFromRequestData(Map<String, Object> productData) {
        Product product = new Product();
        
        // Basic information
        product.setTitle((String) productData.get("title"));
        product.setDescription((String) productData.get("description"));
        
        // Category - resolve from categoryId
        Object categoryIdObj = productData.get("categoryId");
        if (categoryIdObj != null) {
            Integer categoryId = Integer.valueOf(categoryIdObj.toString());
            Category category = categoryService.getCategoryById(categoryId);
            if (category == null) {
                throw new RuntimeException("Invalid category ID: " + categoryId);
            }
            product.setCategory(category);
        }
        
        // Condition
        String condition = (String) productData.get("condition");
        if (condition != null) {
            product.setConditionType(Product.ConditionType.valueOf(condition));
        }
        
        // Listing type
        String listingType = (String) productData.get("listingType");
        if (listingType != null) {
            product.setListingType(Product.ListingType.valueOf(listingType));
        }
        
        // Prices
        Object startingPriceObj = productData.get("startingPrice");
        if (startingPriceObj != null) {
            BigDecimal startingPrice = new BigDecimal(startingPriceObj.toString());
            product.setStartingPrice(startingPrice);
            product.setCurrentPrice(startingPrice);
        }
        
        Object buyNowPriceObj = productData.get("buyNowPrice");
        if (buyNowPriceObj != null) {
            product.setBuyNowPrice(new BigDecimal(buyNowPriceObj.toString()));
        }
        
        Object reservePriceObj = productData.get("reservePrice");
        if (reservePriceObj != null) {
            product.setReservePrice(new BigDecimal(reservePriceObj.toString()));
        }
        
        Object shippingCostObj = productData.get("shippingCost");
        if (shippingCostObj != null) {
            product.setShippingCost(new BigDecimal(shippingCostObj.toString()));
        }
        
        // Quantity
        Object quantityObj = productData.get("quantity");
        if (quantityObj != null) {
            product.setQuantityAvailable(Integer.valueOf(quantityObj.toString()));
        }
        
        // Location
        String location = (String) productData.get("location");
        if (location != null) {
            product.setItemLocation(location);
        }
        
        // Auction timing
        Object startTimeObj = productData.get("startTime");
        if (startTimeObj != null && !startTimeObj.toString().equals("null")) {
            try {
                String timeStr = startTimeObj.toString();
                // Handle ISO format: 2024-09-27T16:12:34.123Z
                timeStr = timeStr.replace("T", " ").replace("Z", "");
                if (timeStr.contains(".")) {
                    timeStr = timeStr.substring(0, timeStr.indexOf("."));
                }
                product.setAuctionStartTime(Timestamp.valueOf(timeStr));
            } catch (Exception e) {
                // If parsing fails, use current time
                product.setAuctionStartTime(new Timestamp(System.currentTimeMillis()));
            }
        }
        
        Object endTimeObj = productData.get("endTime");
        if (endTimeObj != null && !endTimeObj.toString().equals("null")) {
            try {
                String timeStr = endTimeObj.toString();
                // Handle ISO format: 2024-09-27T16:12:34.123Z  
                timeStr = timeStr.replace("T", " ").replace("Z", "");
                if (timeStr.contains(".")) {
                    timeStr = timeStr.substring(0, timeStr.indexOf("."));
                }
                product.setAuctionEndTime(Timestamp.valueOf(timeStr));
            } catch (Exception e) {
                // If parsing fails, set a default end time (7 days from now)
                product.setAuctionEndTime(new Timestamp(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L)));
            }
        }
        
        // Status
        String status = (String) productData.get("status");
        if (status != null) {
            product.setStatus(Product.ProductStatus.valueOf(status));
        }
        
        return product;
    }
    
    private Map<String, Object> convertToSafeDTO(Product product) {
        Map<String, Object> productDTO = new HashMap<>();
        
        // Basic product information
        productDTO.put("productId", product.getProductId());
        productDTO.put("title", product.getTitle());
        productDTO.put("description", product.getDescription());
        productDTO.put("currentPrice", product.getCurrentPrice());
        productDTO.put("startingPrice", product.getStartingPrice());
        productDTO.put("buyNowPrice", product.getBuyNowPrice());
        productDTO.put("status", product.getStatus().toString());
        productDTO.put("listingType", product.getListingType().toString());
        productDTO.put("condition", product.getConditionType().toString());
        productDTO.put("quantityAvailable", product.getQuantityAvailable());
        productDTO.put("shippingCost", product.getShippingCost());
        productDTO.put("itemLocation", product.getItemLocation());
        productDTO.put("createdDate", product.getCreatedDate());
        
        // Auction timing
        if (product.getAuctionStartTime() != null) {
            productDTO.put("startTime", product.getAuctionStartTime().toString());
        }
        if (product.getAuctionEndTime() != null) {
            productDTO.put("endTime", product.getAuctionEndTime().toString());
        }
        
        // Category info safely
        if (product.getCategory() != null) {
            Map<String, Object> categoryInfo = new HashMap<>();
            categoryInfo.put("categoryId", product.getCategory().getCategoryId());
            categoryInfo.put("categoryName", product.getCategory().getCategoryName());
            productDTO.put("category", categoryInfo);
        }
        
        // Seller info safely (minimal)
        if (product.getSeller() != null) {
            productDTO.put("sellerId", product.getSeller().getUserId());
            Map<String, Object> sellerInfo = new HashMap<>();
            sellerInfo.put("userId", product.getSeller().getUserId());
            sellerInfo.put("username", product.getSeller().getUsername());
            sellerInfo.put("firstName", product.getSeller().getFirstName());
            sellerInfo.put("lastName", product.getSeller().getLastName());
            productDTO.put("seller", sellerInfo);
        }
        
        // Add primary image URL if available
        String primaryImageUrl = productService.getPrimaryImageUrl(product.getProductId());
        productDTO.put("imageUrl", primaryImageUrl);
        
        return productDTO;
    }
}