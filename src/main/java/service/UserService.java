package service;

import dao.UserDAO;
import entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserDAO userDAO;
    
    public User registerUser(User user) {
        // Check if username or email already exists
        if (userDAO.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userDAO.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Hash the password
        String hashedPassword = hashPassword(user.getPasswordHash());
        user.setPasswordHash(hashedPassword);
        
        // Set default values
        user.setRegistrationDate(new Timestamp(System.currentTimeMillis()));
        user.setAccountStatus(User.AccountStatus.ACTIVE);
        user.setEmailVerified(false);
        
        if (user.getUserType() == null) {
            user.setUserType(User.UserType.BOTH);
        }
        
        if (user.getCountry() == null || user.getCountry().isEmpty()) {
            user.setCountry("USA");
        }
        
        return userDAO.save(user);
    }
    
    public User authenticateUser(String usernameOrEmail, String password) {
        System.out.println("🔍 Authenticating user: " + usernameOrEmail);
        User user = userDAO.findByUsernameOrEmail(usernameOrEmail);
        
        if (user == null) {
            System.out.println("❌ User not found: " + usernameOrEmail);
            return null; // User not found
        }
        
        System.out.println("✅ User found: " + user.getUsername() + ", Status: " + user.getAccountStatus());
        
        if (user.getAccountStatus() != User.AccountStatus.ACTIVE) {
            System.out.println("❌ Account not active: " + user.getAccountStatus());
            throw new RuntimeException("Account is not active");
        }
        
        String hashedPassword = hashPassword(password);
        System.out.println("🔐 Password hash comparison - Input: " + hashedPassword + ", Stored: " + user.getPasswordHash());
        if (!hashedPassword.equals(user.getPasswordHash())) {
            System.out.println("❌ Password mismatch");
            return null; // Invalid password
        }
        
        // Update last login
        userDAO.updateLastLogin(user.getUserId());
        
        return user;
    }
    
    public User getUserById(Integer userId) {
        return userDAO.findById(userId);
    }
    
    public User getUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }
    
    public User getUserByEmail(String email) {
        return userDAO.findByEmail(email);
    }
    
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }
    
    public List<User> getActiveUsers() {
        return userDAO.findActiveUsers();
    }
    
    public List<User> getUsersByType(User.UserType userType) {
        return userDAO.findByUserType(userType);
    }
    
    public User updateUser(User user) {
        User existingUser = userDAO.findById(user.getUserId());
        if (existingUser == null) {
            throw new RuntimeException("User not found");
        }
        
        // Don't allow username/email changes if they conflict
        if (!existingUser.getUsername().equals(user.getUsername()) && 
            userDAO.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (!existingUser.getEmail().equals(user.getEmail()) && 
            userDAO.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        return userDAO.update(user);
    }
    
    public boolean changePassword(Integer userId, String currentPassword, String newPassword) {
        User user = userDAO.findById(userId);
        if (user == null) {
            return false;
        }
        
        String hashedCurrentPassword = hashPassword(currentPassword);
        if (!hashedCurrentPassword.equals(user.getPasswordHash())) {
            return false; // Current password is wrong
        }
        
        String hashedNewPassword = hashPassword(newPassword);
        user.setPasswordHash(hashedNewPassword);
        userDAO.update(user);
        
        return true;
    }
    
    public void deleteUser(Integer userId) {
        userDAO.deleteById(userId);
    }
    
    public boolean isUsernameAvailable(String username) {
        return !userDAO.existsByUsername(username);
    }
    
    public boolean isEmailAvailable(String email) {
        return !userDAO.existsByEmail(email);
    }
    
    public List<User> searchUsers(String searchTerm) {
        return userDAO.searchUsers(searchTerm);
    }
    
    public List<User> getTopSellers(int limit) {
        return userDAO.findTopSellers(limit);
    }
    
    public void activateUser(Integer userId) {
        User user = userDAO.findById(userId);
        if (user != null) {
            user.setAccountStatus(User.AccountStatus.ACTIVE);
            userDAO.update(user);
        }
    }
    
    public void suspendUser(Integer userId) {
        User user = userDAO.findById(userId);
        if (user != null) {
            user.setAccountStatus(User.AccountStatus.SUSPENDED);
            userDAO.update(user);
        }
    }
    
    public void verifyEmail(Integer userId) {
        User user = userDAO.findById(userId);
        if (user != null) {
            user.setEmailVerified(true);
            userDAO.update(user);
        }
    }
    
    /**
     * Simple password hashing using SHA-256
     * In production, use bcrypt or similar
     */
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Validate user input for registration
     */
    public void validateUserRegistration(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        
        if (user.getUsername().length() < 3 || user.getUsername().length() > 50) {
            throw new RuntimeException("Username must be between 3 and 50 characters");
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new RuntimeException("Email is required");
        }
        
        if (!isValidEmail(user.getEmail())) {
            throw new RuntimeException("Invalid email format");
        }
        
        if (user.getPasswordHash() == null || user.getPasswordHash().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }
        
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new RuntimeException("First name is required");
        }
        
        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new RuntimeException("Last name is required");
        }
    }
    
    private boolean isValidEmail(String email) {
        return email.contains("@") && email.contains(".");
    }
}