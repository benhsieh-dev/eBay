package controller.api;

import entity.User;
import service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserApiController {
    
    @Autowired
    private UserService userService;
    
    // User registration
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody User user, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate user input
            userService.validateUserRegistration(user);
            
            // Register user
            User registeredUser = userService.registerUser(user);
            
            // Auto-login after registration
            session.setAttribute("currentUser", registeredUser);
            session.setAttribute("userId", registeredUser.getUserId());
            session.setAttribute("username", registeredUser.getUsername());
            
            response.put("success", true);
            response.put("message", "Registration successful! Welcome to eBay!");
            response.put("user", registeredUser);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // User login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> loginRequest, 
                                                        HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String usernameOrEmail = loginRequest.get("usernameOrEmail");
            String password = loginRequest.get("password");
            
            User user = userService.authenticateUser(usernameOrEmail, password);
            
            if (user != null) {
                // Set session attributes
                session.setAttribute("currentUser", user);
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("userType", user.getUserType());
                
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("user", user);
                
                return ResponseEntity.ok(response);
                
            } else {
                response.put("success", false);
                response.put("error", "Invalid username/email or password");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // User logout
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        session.invalidate();
        response.put("success", true);
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }
    
    // Get current user profile
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("error", "Please login first");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        // Get fresh user data
        User user = userService.getUserById(currentUser.getUserId());
        
        response.put("success", true);
        response.put("user", user);
        
        return ResponseEntity.ok(response);
    }
    
    // Update user profile
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody User user, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("error", "Please login first");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        try {
            // Ensure we're updating the correct user
            user.setUserId(currentUser.getUserId());
            
            // Don't allow password changes through this method
            User existingUser = userService.getUserById(currentUser.getUserId());
            user.setPasswordHash(existingUser.getPasswordHash());
            user.setRegistrationDate(existingUser.getRegistrationDate());
            user.setEmailVerified(existingUser.getEmailVerified());
            
            User updatedUser = userService.updateUser(user);
            
            // Update session
            session.setAttribute("currentUser", updatedUser);
            
            response.put("success", true);
            response.put("message", "Profile updated successfully!");
            response.put("user", updatedUser);
            
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Change password
    @PostMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> passwordRequest,
                                                             HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("error", "Please login first");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        
        try {
            String currentPassword = passwordRequest.get("currentPassword");
            String newPassword = passwordRequest.get("newPassword");
            String confirmPassword = passwordRequest.get("confirmPassword");
            
            if (!newPassword.equals(confirmPassword)) {
                throw new RuntimeException("New passwords do not match");
            }
            
            if (newPassword.length() < 6) {
                throw new RuntimeException("New password must be at least 6 characters");
            }
            
            boolean success = userService.changePassword(currentUser.getUserId(), 
                                                        currentPassword, newPassword);
            
            if (success) {
                response.put("success", true);
                response.put("message", "Password changed successfully!");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Current password is incorrect");
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Check username availability
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Object>> checkUsernameAvailability(@RequestParam String username) {
        Map<String, Object> response = new HashMap<>();
        boolean available = userService.isUsernameAvailable(username);
        response.put("available", available);
        response.put("message", available ? "Username is available" : "Username is already taken");
        return ResponseEntity.ok(response);
    }
    
    // Check email availability
    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmailAvailability(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        boolean available = userService.isEmailAvailable(email);
        response.put("available", available);
        response.put("message", available ? "Email is available" : "Email is already registered");
        return ResponseEntity.ok(response);
    }
    
    // Get current user session info
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("authenticated", false);
            return ResponseEntity.ok(response);
        }
        
        response.put("success", true);
        response.put("authenticated", true);
        response.put("user", currentUser);
        
        return ResponseEntity.ok(response);
    }
}