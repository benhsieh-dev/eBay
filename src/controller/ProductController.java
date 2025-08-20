package controller;

import entity.Product;
import entity.Category;
import entity.User;
import entity.ProductImage;
import service.ProductService;
import service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/product")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CategoryService categoryService;
    
    // Show product listing form
    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        if (currentUser.getUserType() == User.UserType.BUYER) {
            model.addAttribute("error", "Only sellers can create product listings");
            return "redirect:/";
        }
        
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getActiveCategories());
        model.addAttribute("conditions", Product.ConditionType.values());
        model.addAttribute("listingTypes", Product.ListingType.values());
        
        return "product-create";
    }
    
    // Process product creation
    @PostMapping("/create")
    public ModelAndView createProduct(@ModelAttribute Product product, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        try {
            Product createdProduct = productService.createProduct(product, currentUser);
            mv.setViewName("redirect:/product/edit/" + createdProduct.getProductId());
            mv.addObject("message", "Product created successfully! Add images and activate when ready.");
            
        } catch (RuntimeException e) {
            mv.setViewName("product-create");
            mv.addObject("error", e.getMessage());
            mv.addObject("product", product);
            mv.addObject("categories", categoryService.getActiveCategories());
            mv.addObject("conditions", Product.ConditionType.values());
            mv.addObject("listingTypes", Product.ListingType.values());
        }
        
        return mv;
    }
    
    // Show product details
    @GetMapping("/view/{productId}")
    public ModelAndView viewProduct(@PathVariable Integer productId, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        Product product = productService.getProductByIdAndIncrementView(productId);
        if (product == null) {
            mv.setViewName("redirect:/");
            mv.addObject("error", "Product not found");
            return mv;
        }
        
        User currentUser = (User) session.getAttribute("currentUser");
        
        mv.setViewName("product-detail");
        mv.addObject("product", product);
        mv.addObject("images", productService.getProductImages(productId));
        mv.addObject("isOwner", currentUser != null && 
                     currentUser.getUserId().equals(product.getSeller().getUserId()));
        
        return mv;
    }
    
    // Show edit product form
    @GetMapping("/edit/{productId}")
    public ModelAndView showEditForm(@PathVariable Integer productId, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        Product product = productService.getProductById(productId);
        if (product == null) {
            mv.setViewName("redirect:/");
            mv.addObject("error", "Product not found");
            return mv;
        }
        
        // Check ownership
        if (!product.getSeller().getUserId().equals(currentUser.getUserId())) {
            mv.setViewName("redirect:/");
            mv.addObject("error", "You can only edit your own products");
            return mv;
        }
        
        mv.setViewName("product-edit");
        mv.addObject("product", product);
        mv.addObject("categories", categoryService.getActiveCategories());
        mv.addObject("conditions", Product.ConditionType.values());
        mv.addObject("listingTypes", Product.ListingType.values());
        mv.addObject("images", productService.getProductImages(productId));
        
        return mv;
    }
    
    // Process product update
    @PostMapping("/update")
    public ModelAndView updateProduct(@ModelAttribute Product product, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        try {
            // Verify ownership
            Product existingProduct = productService.getProductById(product.getProductId());
            if (existingProduct == null || 
                !existingProduct.getSeller().getUserId().equals(currentUser.getUserId())) {
                throw new RuntimeException("You can only edit your own products");
            }
            
            // Preserve certain fields that shouldn't be updated
            product.setSeller(existingProduct.getSeller());
            product.setCreatedDate(existingProduct.getCreatedDate());
            product.setViewCount(existingProduct.getViewCount());
            product.setWatchCount(existingProduct.getWatchCount());
            product.setQuantitySold(existingProduct.getQuantitySold());
            
            Product updatedProduct = productService.updateProduct(product);
            
            mv.setViewName("redirect:/product/edit/" + updatedProduct.getProductId());
            mv.addObject("message", "Product updated successfully!");
            
        } catch (RuntimeException e) {
            mv.setViewName("product-edit");
            mv.addObject("error", e.getMessage());
            mv.addObject("product", product);
            mv.addObject("categories", categoryService.getActiveCategories());
            mv.addObject("conditions", Product.ConditionType.values());
            mv.addObject("listingTypes", Product.ListingType.values());
            mv.addObject("images", productService.getProductImages(product.getProductId()));
        }
        
        return mv;
    }
    
    // Activate product
    @PostMapping("/activate/{productId}")
    public ModelAndView activateProduct(@PathVariable Integer productId, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        try {
            productService.activateProduct(productId, currentUser.getUserId());
            mv.setViewName("redirect:/product/view/" + productId);
            mv.addObject("message", "Product activated successfully!");
            
        } catch (RuntimeException e) {
            mv.setViewName("redirect:/product/edit/" + productId);
            mv.addObject("error", e.getMessage());
        }
        
        return mv;
    }
    
    // Deactivate product
    @PostMapping("/deactivate/{productId}")
    public ModelAndView deactivateProduct(@PathVariable Integer productId, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        try {
            productService.deactivateProduct(productId, currentUser.getUserId());
            mv.setViewName("redirect:/product/edit/" + productId);
            mv.addObject("message", "Product deactivated successfully!");
            
        } catch (RuntimeException e) {
            mv.setViewName("redirect:/product/edit/" + productId);
            mv.addObject("error", e.getMessage());
        }
        
        return mv;
    }
    
    // Delete product
    @PostMapping("/delete/{productId}")
    public ModelAndView deleteProduct(@PathVariable Integer productId, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        try {
            productService.deleteProduct(productId, currentUser.getUserId());
            mv.setViewName("redirect:/user/profile");
            mv.addObject("message", "Product deleted successfully!");
            
        } catch (RuntimeException e) {
            mv.setViewName("redirect:/product/edit/" + productId);
            mv.addObject("error", e.getMessage());
        }
        
        return mv;
    }
    
    // Add product image (AJAX)
    @PostMapping("/add-image")
    @ResponseBody
    public Map<String, Object> addProductImage(@RequestParam Integer productId,
                                               @RequestParam String imageUrl,
                                               @RequestParam(required = false) String altText,
                                               @RequestParam(required = false) Boolean isPrimary,
                                               HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "You must be logged in");
            return response;
        }
        
        try {
            Product product = productService.getProductById(productId);
            if (product == null || !product.getSeller().getUserId().equals(currentUser.getUserId())) {
                response.put("success", false);
                response.put("message", "Product not found or access denied");
                return response;
            }
            
            ProductImage image = productService.addProductImage(productId, imageUrl, altText, isPrimary);
            response.put("success", true);
            response.put("imageId", image.getImageId());
            response.put("message", "Image added successfully!");
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // Delete product image (AJAX)
    @PostMapping("/delete-image/{imageId}")
    @ResponseBody
    public Map<String, Object> deleteProductImage(@PathVariable Integer imageId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "You must be logged in");
            return response;
        }
        
        try {
            productService.deleteProductImage(imageId);
            response.put("success", true);
            response.put("message", "Image deleted successfully!");
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // Set primary image (AJAX)
    @PostMapping("/set-primary-image/{imageId}")
    @ResponseBody
    public Map<String, Object> setPrimaryImage(@PathVariable Integer imageId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "You must be logged in");
            return response;
        }
        
        try {
            productService.setPrimaryImage(imageId);
            response.put("success", true);
            response.put("message", "Primary image updated!");
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // Search products
    @GetMapping("/search")
    public ModelAndView searchProducts(@RequestParam(required = false) String q,
                                       @RequestParam(required = false) Integer category,
                                       @RequestParam(required = false) BigDecimal minPrice,
                                       @RequestParam(required = false) BigDecimal maxPrice,
                                       @RequestParam(required = false) String condition,
                                       @RequestParam(required = false) String listingType) {
        ModelAndView mv = new ModelAndView();
        
        Product.ConditionType conditionType = null;
        if (condition != null && !condition.isEmpty()) {
            try {
                conditionType = Product.ConditionType.valueOf(condition);
            } catch (IllegalArgumentException e) {
                // Invalid condition, ignore
            }
        }
        
        Product.ListingType listingTypeEnum = null;
        if (listingType != null && !listingType.isEmpty()) {
            try {
                listingTypeEnum = Product.ListingType.valueOf(listingType);
            } catch (IllegalArgumentException e) {
                // Invalid listing type, ignore
            }
        }
        
        List<Product> products = productService.searchProductsWithFilters(q, category, minPrice, maxPrice, conditionType, listingTypeEnum);
        
        mv.setViewName("product-search");
        mv.addObject("products", products);
        mv.addObject("searchTerm", q);
        mv.addObject("selectedCategory", category);
        mv.addObject("minPrice", minPrice);
        mv.addObject("maxPrice", maxPrice);
        mv.addObject("selectedCondition", condition);
        mv.addObject("selectedListingType", listingType);
        mv.addObject("categories", categoryService.getActiveCategories());
        mv.addObject("conditions", Product.ConditionType.values());
        mv.addObject("listingTypes", Product.ListingType.values());
        
        return mv;
    }
    
    // Browse by category
    @GetMapping("/category/{categoryId}")
    public ModelAndView browseByCategory(@PathVariable Integer categoryId) {
        ModelAndView mv = new ModelAndView();
        
        Category category = categoryService.getCategoryById(categoryId);
        if (category == null) {
            mv.setViewName("redirect:/");
            mv.addObject("error", "Category not found");
            return mv;
        }
        
        List<Product> products = productService.getProductsByCategory(categoryId);
        
        mv.setViewName("product-category");
        mv.addObject("products", products);
        mv.addObject("category", category);
        mv.addObject("subcategories", categoryService.getSubcategories(categoryId));
        
        return mv;
    }
    
    // Add current user to all models
    @ModelAttribute("currentUser")
    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }
}