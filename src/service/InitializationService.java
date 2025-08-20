package service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

@Service
@Transactional
public class InitializationService {
    
    @Autowired
    private CategoryService categoryService;
    
    @PostConstruct
    public void initializeApplication() {
        try {
            // Initialize default categories if none exist
            categoryService.initializeDefaultCategories();
            
            System.out.println("✅ Application initialization completed successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error during application initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
}