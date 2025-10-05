package service;

import dao.CartItemDAO;
import dao.ProductDAO;
import entity.CartItem;
import entity.Product;
import entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class CartService {
    
    @Autowired
    private CartItemDAO cartItemDAO;
    
    @Autowired
    private ProductDAO productDAO;
    
    public CartItem addToCart(Integer userId, Integer productId, Integer quantity) {
        // Validate product
        Product product = productDAO.findById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        
        // Validate product is available for immediate purchase
        if (!product.isBuyNowAvailable()) {
            throw new RuntimeException("This item is not available for immediate purchase (auction only)");
        }
        
        if (product.getStatus() != Product.ProductStatus.ACTIVE) {
            throw new RuntimeException("This item is no longer available");
        }
        
        // Check if user is trying to buy their own product
        if (product.getSeller().getUserId().equals(userId)) {
            throw new RuntimeException("You cannot add your own items to cart");
        }
        
        // Validate quantity
        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }
        
        if (quantity > product.getQuantityAvailable()) {
            throw new RuntimeException("Only " + product.getQuantityAvailable() + " items available");
        }
        
        // Check if item already in cart
        CartItem existingItem = cartItemDAO.findByUserIdAndProductId(userId, productId);
        
        if (existingItem != null) {
            // Update quantity
            int newQuantity = existingItem.getQuantity() + quantity;
            if (newQuantity > product.getQuantityAvailable()) {
                throw new RuntimeException("Cannot add more items. Only " + product.getQuantityAvailable() + " available");
            }
            
            existingItem.setQuantity(newQuantity);
            existingItem.setAddedDate(new Timestamp(System.currentTimeMillis()));
            return cartItemDAO.update(existingItem);
        } else {
            // Create new cart item
            CartItem cartItem = new CartItem();
            cartItem.setUser(new User());
            cartItem.getUser().setUserId(userId);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setAddedDate(new Timestamp(System.currentTimeMillis()));
            
            return cartItemDAO.save(cartItem);
        }
    }
    
    public void removeFromCart(Integer userId, Integer productId) {
        cartItemDAO.deleteByUserIdAndProductId(userId, productId);
    }
    
    public void removeCartItem(Integer cartId, Integer userId) {
        CartItem cartItem = cartItemDAO.findById(cartId);
        if (cartItem == null) {
            throw new RuntimeException("Cart item not found");
        }
        
        if (!cartItem.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("You can only remove your own cart items");
        }
        
        cartItemDAO.delete(cartItem);
    }
    
    public void updateQuantity(Integer cartId, Integer quantity, Integer userId) {
        CartItem cartItem = cartItemDAO.findById(cartId);
        if (cartItem == null) {
            throw new RuntimeException("Cart item not found");
        }
        
        if (!cartItem.getUser().getUserId().equals(userId)) {
            throw new RuntimeException("You can only update your own cart items");
        }
        
        if (quantity <= 0) {
            // Remove item if quantity is 0 or negative
            cartItemDAO.delete(cartItem);
            return;
        }
        
        // Validate quantity against available stock
        if (quantity > cartItem.getProduct().getQuantityAvailable()) {
            throw new RuntimeException("Only " + cartItem.getProduct().getQuantityAvailable() + " items available");
        }
        
        cartItem.setQuantity(quantity);
        cartItemDAO.update(cartItem);
    }
    
    public List<CartItem> getCartItems(Integer userId) {
        return cartItemDAO.findActiveCartItems(userId);
    }
    
    public List<CartItem> getAllCartItems(Integer userId) {
        return cartItemDAO.findByUserId(userId);
    }
    
    public List<CartItem> getInactiveCartItems(Integer userId) {
        return cartItemDAO.findInactiveCartItems(userId);
    }
    
    public Long getCartItemCount(Integer userId) {
        return cartItemDAO.getCartItemCount(userId);
    }
    
    public BigDecimal getCartTotal(Integer userId) {
        return cartItemDAO.getCartTotal(userId);
    }
    
    public CartSummary getCartSummary(Integer userId) {
        List<Object[]> summaryData = cartItemDAO.getCartSummary(userId);
        
        if (summaryData.isEmpty()) {
            return new CartSummary(0L, 0L, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        
        Object[] data = summaryData.getFirst();
        Long itemCount = (Long) data[0];
        Long totalQuantity = (Long) data[1];
        BigDecimal subtotal = (BigDecimal) data[2];
        BigDecimal shippingTotal = (BigDecimal) data[3];
        
        return new CartSummary(
            itemCount != null ? itemCount : 0L,
            totalQuantity != null ? totalQuantity : 0L,
            subtotal != null ? subtotal : BigDecimal.ZERO,
            shippingTotal != null ? shippingTotal : BigDecimal.ZERO
        );
    }
    
    public void clearCart(Integer userId) {
        cartItemDAO.clearCart(userId);
    }
    
    public void clearInactiveItems(Integer userId) {
        cartItemDAO.clearInactiveItems(userId);
    }
    
    public boolean isInCart(Integer userId, Integer productId) {
        return cartItemDAO.existsByUserIdAndProductId(userId, productId);
    }
    
    public void validateCartForCheckout(Integer userId) {
        List<CartItem> cartItems = getCartItems(userId);
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Your cart is empty");
        }
        
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            
            // Validate product is still active
            if (product.getStatus() != Product.ProductStatus.ACTIVE) {
                throw new RuntimeException("Item '" + product.getTitle() + "' is no longer available");
            }
            
            // Validate buy now availability
            if (!product.isBuyNowAvailable()) {
                throw new RuntimeException("Item '" + product.getTitle() + "' is not available for immediate purchase");
            }
            
            // Validate quantity
            if (item.getQuantity() > product.getQuantityAvailable()) {
                throw new RuntimeException("Only " + product.getQuantityAvailable() + 
                                         " of '" + product.getTitle() + "' available");
            }
        }
    }
    
    /**
     * Clean up expired cart items (called by scheduler)
     */
    public void cleanupExpiredCartItems() {
        // Remove cart items older than 30 days
        cartItemDAO.removeExpiredCartItems(30);
        
        // Clean up inactive items (auction-only or sold items)
        // This would need user iteration in a real implementation
        System.out.println("Cleaned up expired cart items");
    }
    
    /**
     * Cart summary helper class
     */
    public static class CartSummary {
        private final Long itemCount;
        private final Long totalQuantity;
        private final BigDecimal subtotal;
        private final BigDecimal shippingTotal;
        
        public CartSummary(Long itemCount, Long totalQuantity, BigDecimal subtotal, BigDecimal shippingTotal) {
            this.itemCount = itemCount;
            this.totalQuantity = totalQuantity;
            this.subtotal = subtotal;
            this.shippingTotal = shippingTotal;
        }
        
        public Long getItemCount() { return itemCount; }
        public Long getTotalQuantity() { return totalQuantity; }
        public BigDecimal getSubtotal() { return subtotal; }
        public BigDecimal getShippingTotal() { return shippingTotal; }
        
        public BigDecimal getTotal() {
            return subtotal.add(shippingTotal);
        }
    }
}