package com.ebay.service;

import entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import service.CategoryService;
import service.UserService;

@Service
public class DemoDataService {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Bean
    public ApplicationRunner initializeData() {
        return args -> {
            try {
                // Initialize default categories
                categoryService.initializeDefaultCategories();
                
                // Create demo user if it doesn't exist
                if (userService.getUserByUsername("demo") == null) {
                    User demoUser = new User("demo", "demo@test.com", "demo123", "Demo", "User");
                    demoUser.setUserType(User.UserType.BOTH);
                    demoUser.setEmailVerified(true);
                    demoUser.setPhone("555-0123");
                    demoUser.setCity("Demo City");
                    demoUser.setState("CA");
                    demoUser.setCountry("USA");
                    userService.registerUser(demoUser);
                    System.out.println("✅ Demo user created - Username: demo, Password: demo123");
                }
                
                System.out.println("✅ Spring Boot application initialization completed successfully");
                
            } catch (Exception e) {
                System.err.println("❌ Error during application initialization: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}