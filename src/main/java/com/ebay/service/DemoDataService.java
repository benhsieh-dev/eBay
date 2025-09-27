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
                System.out.println("🔄 Initializing default categories...");
                categoryService.initializeDefaultCategories();
                System.out.println("✅ Default categories initialized");
                
                // Create demo users if they don't exist
                if (userService.getUserByUsername("demo_user_1") == null) {
                    User demoUser1 = new User("demo_user_1", "demo1@test.com", "demo123", "Alice", "Demo");
                    demoUser1.setUserType(User.UserType.BOTH);
                    demoUser1.setEmailVerified(true);
                    demoUser1.setPhone("555-0101");
                    demoUser1.setCity("San Francisco");
                    demoUser1.setState("CA");
                    demoUser1.setCountry("USA");
                    userService.registerUser(demoUser1);
                    System.out.println("✅ Demo User 1 created - Username: demo_user_1, Password: demo123");
                }
                
                if (userService.getUserByUsername("demo_user_2") == null) {
                    User demoUser2 = new User("demo_user_2", "demo2@test.com", "demo123", "Bob", "Demo");
                    demoUser2.setUserType(User.UserType.BOTH);
                    demoUser2.setEmailVerified(true);
                    demoUser2.setPhone("555-0202");
                    demoUser2.setCity("Los Angeles");
                    demoUser2.setState("CA");
                    demoUser2.setCountry("USA");
                    userService.registerUser(demoUser2);
                    System.out.println("✅ Demo User 2 created - Username: demo_user_2, Password: demo123");
                }
                
                // Create third demo user for backward compatibility
                if (userService.getUserByUsername("demo_user") == null) {
                    User demoUser = new User("demo_user", "demo@test.com", "demo123", "Charlie", "Demo");
                    demoUser.setUserType(User.UserType.BOTH);
                    demoUser.setEmailVerified(true);
                    demoUser.setPhone("555-0303");
                    demoUser.setCity("Seattle");
                    demoUser.setState("WA");
                    demoUser.setCountry("USA");
                    userService.registerUser(demoUser);
                    System.out.println("✅ Demo User 3 created - Username: demo_user, Password: demo123");
                }
                
                System.out.println("✅ Spring Boot application initialization completed successfully");
                
            } catch (Exception e) {
                System.err.println("❌ Error during application initialization: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}