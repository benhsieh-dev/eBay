package service;

import dao.WatchlistDAO;
import dao.ProductDAO;
import dao.UserDAO;
import entity.Watchlist;
import entity.Product;
import entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WatchlistService {
    
    @Autowired
    private WatchlistDAO watchlistDAO;
    
    @Autowired
    private ProductDAO productDAO;
    
    @Autowired
    private UserDAO userDAO;
    
    public Watchlist addToWatchlist(Integer userId, Integer productId) {
        // Get the user and product
        User user = userDAO.findById(userId);
        Product product = productDAO.findById(productId);
        
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        
        // Check if item is already in watchlist
        if (isProductInWatchlist(userId, productId)) {
            throw new RuntimeException("Product is already in your watchlist");
        }
        
        // Check if user is trying to watch their own item
        if (product.getSeller().getUserId().equals(userId)) {
            throw new RuntimeException("You cannot add your own item to watchlist");
        }
        
        // Create and save watchlist entry
        Watchlist watchlist = new Watchlist(user, product);
        return watchlistDAO.save(watchlist);
    }
    
    public void removeFromWatchlist(Integer userId, Integer productId) {
        // Verify user exists
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        // Check if item is in watchlist
        if (!isProductInWatchlist(userId, productId)) {
            throw new RuntimeException("Product is not in your watchlist");
        }
        
        // Remove from watchlist
        watchlistDAO.deleteByUserIdAndProductId(userId, productId);
    }
    
    public boolean isProductInWatchlist(Integer userId, Integer productId) {
        return watchlistDAO.isProductInUserWatchlist(userId, productId);
    }
    
    public List<Watchlist> getUserWatchlist(Integer userId) {
        // Verify user exists
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        return watchlistDAO.findByUserId(userId);
    }
    
    public Long getWatchlistCount(Integer userId) {
        return watchlistDAO.getWatchlistCountForUser(userId);
    }
    
    public Long getWatchersCount(Integer productId) {
        return watchlistDAO.getWatchlistCountForProduct(productId);
    }
    
    public List<Watchlist> getProductWatchers(Integer productId) {
        // Verify product exists
        Product product = productDAO.findById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        
        return watchlistDAO.findByProductId(productId);
    }
    
    public void clearUserWatchlist(Integer userId) {
        // Verify user exists
        User user = userDAO.findById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        
        watchlistDAO.deleteAllForUser(userId);
    }
    
    public void clearProductFromAllWatchlists(Integer productId) {
        // This might be called when a product is deleted
        watchlistDAO.deleteAllForProduct(productId);
    }
    
    public List<Watchlist> getRecentWatchlistActivity(int limit) {
        return watchlistDAO.findRecentWatchlistItems(limit);
    }
    
    public Watchlist findWatchlistEntry(Integer userId, Integer productId) {
        return watchlistDAO.findByUserIdAndProductId(userId, productId);
    }
    
    // Utility method to toggle watchlist status
    public boolean toggleWatchlist(Integer userId, Integer productId) {
        if (isProductInWatchlist(userId, productId)) {
            removeFromWatchlist(userId, productId);
            return false; // Removed from watchlist
        } else {
            addToWatchlist(userId, productId);
            return true; // Added to watchlist
        }
    }
}