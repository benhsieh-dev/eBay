package service;

import dao.CategoryDAO;
import entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class CategoryService {
    
    @Autowired
    private CategoryDAO categoryDAO;
    
    public Category createCategory(Category category) {
        // Validate category data
        validateCategory(category);
        
        // Check if category name already exists
        if (categoryDAO.existsByName(category.getCategoryName())) {
            throw new RuntimeException("Category name already exists");
        }
        
        // Set creation date
        category.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        
        // Set active by default
        if (category.getIsActive() == null) {
            category.setIsActive(true);
        }
        
        return categoryDAO.save(category);
    }
    
    public Category getCategoryById(Integer categoryId) {
        return categoryDAO.findById(categoryId);
    }
    
    public Category getCategoryByName(String categoryName) {
        return categoryDAO.findByName(categoryName);
    }
    
    public List<Category> getAllCategories() {
        return categoryDAO.findAll();
    }
    
    public List<Category> getActiveCategories() {
        return categoryDAO.findActiveCategories();
    }
    
    public List<Category> getTopLevelCategories() {
        return categoryDAO.findTopLevelCategories();
    }
    
    public List<Category> getSubcategories(Integer parentCategoryId) {
        return categoryDAO.findSubcategories(parentCategoryId);
    }
    
    public List<Category> searchCategories(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllCategories();
        }
        return categoryDAO.searchCategories(searchTerm.trim());
    }
    
    public Category updateCategory(Category category) {
        Category existingCategory = categoryDAO.findById(category.getCategoryId());
        if (existingCategory == null) {
            throw new RuntimeException("Category not found");
        }
        
        // Validate category data
        validateCategory(category);
        
        // Check if new name conflicts with existing category (excluding current)
        Category existingByName = categoryDAO.findByName(category.getCategoryName());
        if (existingByName != null && !existingByName.getCategoryId().equals(category.getCategoryId())) {
            throw new RuntimeException("Category name already exists");
        }
        
        // Prevent setting parent to self or creating circular reference
        if (category.getParentCategory() != null && 
            category.getParentCategory().getCategoryId().equals(category.getCategoryId())) {
            throw new RuntimeException("Category cannot be its own parent");
        }
        
        return categoryDAO.update(category);
    }
    
    public void deleteCategory(Integer categoryId) {
        Category category = categoryDAO.findById(categoryId);
        if (category == null) {
            throw new RuntimeException("Category not found");
        }
        
        // Check if category has products
        Long productCount = categoryDAO.getProductCount(categoryId);
        if (productCount > 0) {
            throw new RuntimeException("Cannot delete category with existing products. Move products first.");
        }
        
        // Check if category has subcategories
        List<Category> subcategories = categoryDAO.findSubcategories(categoryId);
        if (!subcategories.isEmpty()) {
            throw new RuntimeException("Cannot delete category with subcategories. Delete subcategories first.");
        }
        
        categoryDAO.delete(category);
    }
    
    public void deactivateCategory(Integer categoryId) {
        Category category = categoryDAO.findById(categoryId);
        if (category != null) {
            category.setIsActive(false);
            categoryDAO.update(category);
        }
    }
    
    public void activateCategory(Integer categoryId) {
        Category category = categoryDAO.findById(categoryId);
        if (category != null) {
            category.setIsActive(true);
            categoryDAO.update(category);
        }
    }
    
    public Long getProductCount(Integer categoryId) {
        return categoryDAO.getProductCount(categoryId);
    }
    
    public boolean isCategoryNameAvailable(String categoryName) {
        return !categoryDAO.existsByName(categoryName);
    }
    
    public boolean isCategoryNameAvailable(String categoryName, Integer excludeCategoryId) {
        Category existing = categoryDAO.findByName(categoryName);
        return existing == null || existing.getCategoryId().equals(excludeCategoryId);
    }
    
    public List<Category> getCategoryHierarchy(Integer categoryId) {
        return categoryDAO.getCategoryHierarchy(categoryId);
    }
    
    public String getCategoryPath(Integer categoryId) {
        Category category = categoryDAO.findById(categoryId);
        if (category == null) {
            return "";
        }
        return category.getFullCategoryPath();
    }
    
    /**
     * Validate category data
     */
    private void validateCategory(Category category) {
        if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
            throw new RuntimeException("Category name is required");
        }
        
        if (category.getCategoryName().length() > 100) {
            throw new RuntimeException("Category name must be 100 characters or less");
        }
        
        if (category.getDescription() != null && category.getDescription().length() > 1000) {
            throw new RuntimeException("Category description must be 1000 characters or less");
        }
    }
    
    /**
     * Initialize default categories if none exist
     */
    public void initializeDefaultCategories() {
        if (categoryDAO.findAll().isEmpty()) {
            createDefaultCategories();
        }
    }
    
    private void createDefaultCategories() {
        // Create main categories
        Category electronics = new Category("Electronics", "Computers, phones, and electronic devices");
        electronics = categoryDAO.save(electronics);
        
        Category fashion = new Category("Fashion", "Clothing, shoes, and accessories");
        fashion = categoryDAO.save(fashion);
        
        Category homeGarden = new Category("Home & Garden", "Furniture, decor, and garden supplies");
        homeGarden = categoryDAO.save(homeGarden);
        
        Category sports = new Category("Sports & Outdoors", "Sports equipment and outdoor gear");
        sports = categoryDAO.save(sports);
        
        Category automotive = new Category("Automotive", "Car parts and automotive accessories");
        automotive = categoryDAO.save(automotive);
        
        Category books = new Category("Books & Media", "Books, movies, music, and games");
        books = categoryDAO.save(books);
        
        Category collectibles = new Category("Collectibles", "Antiques, coins, stamps, trading cards, and vintage items");
        collectibles = categoryDAO.save(collectibles);
        
        Category toys = new Category("Toys & Hobbies", "Action figures, dolls, plush toys, model kits, and hobby supplies");
        toys = categoryDAO.save(toys);
        
        // Create subcategories for Electronics
        categoryDAO.save(new Category("Smartphones", "Mobile phones and accessories", electronics));
        categoryDAO.save(new Category("Laptops", "Laptop computers and accessories", electronics));
        categoryDAO.save(new Category("Gaming", "Video games and gaming consoles", electronics));
        categoryDAO.save(new Category("Audio", "Headphones, speakers, and audio equipment", electronics));
    }
}