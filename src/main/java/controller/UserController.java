package controller;

import entity.User;
import service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

// @Controller  // Temporarily disabled
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // Show registration form
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "user-registration";
    }
    
    // Process registration
    @PostMapping("/register")
    public ModelAndView registerUser(@ModelAttribute User user, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        try {
            // Validate user input
            userService.validateUserRegistration(user);
            
            // Register user
            User registeredUser = userService.registerUser(user);
            
            // Auto-login after registration
            session.setAttribute("currentUser", registeredUser);
            session.setAttribute("userId", registeredUser.getUserId());
            session.setAttribute("username", registeredUser.getUsername());
            
            mv.setViewName("redirect:/user/profile");
            mv.addObject("message", "Registration successful! Welcome to eBay!");
            
        } catch (RuntimeException e) {
            mv.setViewName("user-registration");
            mv.addObject("error", e.getMessage());
            mv.addObject("user", user);
        }
        
        return mv;
    }
    
    // Show login form
    @GetMapping("/login")
    public String showLoginForm() {
        return "user-login";
    }
    
    // Process login
    @PostMapping("/login")
    public ModelAndView loginUser(@RequestParam String usernameOrEmail, 
                                  @RequestParam String password, 
                                  HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        try {
            User user = userService.authenticateUser(usernameOrEmail, password);
            
            if (user != null) {
                // Set session attributes
                session.setAttribute("currentUser", user);
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("username", user.getUsername());
                session.setAttribute("userType", user.getUserType());
                
                mv.setViewName("redirect:/");
                
            } else {
                mv.setViewName("user-login");
                mv.addObject("error", "Invalid username/email or password");
            }
            
        } catch (RuntimeException e) {
            mv.setViewName("user-login");
            mv.addObject("error", e.getMessage());
        }
        
        return mv;
    }
    
    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
    // Show user profile
    @GetMapping("/profile")
    public ModelAndView showProfile(HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        // Get fresh user data
        User user = userService.getUserById(currentUser.getUserId());
        
        mv.setViewName("user-profile");
        mv.addObject("user", user);
        
        return mv;
    }
    
    // Show edit profile form
    @GetMapping("/edit")
    public ModelAndView showEditProfile(HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        User user = userService.getUserById(currentUser.getUserId());
        mv.setViewName("user-edit");
        mv.addObject("user", user);
        
        return mv;
    }
    
    // Update profile
    @PostMapping("/update")
    public ModelAndView updateProfile(@ModelAttribute User user, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
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
            
            mv.setViewName("redirect:/user/profile");
            mv.addObject("message", "Profile updated successfully!");
            
        } catch (RuntimeException e) {
            mv.setViewName("user-edit");
            mv.addObject("error", e.getMessage());
            mv.addObject("user", user);
        }
        
        return mv;
    }
    
    // Change password form
    @GetMapping("/change-password")
    public String showChangePasswordForm(HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        return "user-change-password";
    }
    
    // Process password change
    @PostMapping("/change-password")
    public ModelAndView changePassword(@RequestParam String currentPassword,
                                       @RequestParam String newPassword,
                                       @RequestParam String confirmPassword,
                                       HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        try {
            if (!newPassword.equals(confirmPassword)) {
                throw new RuntimeException("New passwords do not match");
            }
            
            if (newPassword.length() < 6) {
                throw new RuntimeException("New password must be at least 6 characters");
            }
            
            boolean success = userService.changePassword(currentUser.getUserId(), 
                                                        currentPassword, newPassword);
            
            if (success) {
                mv.setViewName("redirect:/user/profile");
                mv.addObject("message", "Password changed successfully!");
            } else {
                mv.setViewName("user-change-password");
                mv.addObject("error", "Current password is incorrect");
            }
            
        } catch (RuntimeException e) {
            mv.setViewName("user-change-password");
            mv.addObject("error", e.getMessage());
        }
        
        return mv;
    }
    
    // Check username availability (AJAX)
    @GetMapping("/check-username")
    @ResponseBody
    public Map<String, Object> checkUsernameAvailability(@RequestParam String username) {
        Map<String, Object> response = new HashMap<>();
        boolean available = userService.isUsernameAvailable(username);
        response.put("available", available);
        response.put("message", available ? "Username is available" : "Username is already taken");
        return response;
    }
    
    // Check email availability (AJAX)
    @GetMapping("/check-email")
    @ResponseBody
    public Map<String, Object> checkEmailAvailability(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        boolean available = userService.isEmailAvailable(email);
        response.put("available", available);
        response.put("message", available ? "Email is available" : "Email is already registered");
        return response;
    }
    
    // Admin: List all users
    @GetMapping("/admin/list")
    public ModelAndView listAllUsers(HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !"admin".equals(currentUser.getUsername())) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        mv.setViewName("admin-users");
        mv.addObject("users", userService.getAllUsers());
        
        return mv;
    }
    
    // Get current user info (for navbar, etc.)
    @ModelAttribute("currentUser")
    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }
}