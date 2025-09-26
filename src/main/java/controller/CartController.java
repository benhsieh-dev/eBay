package controller;

import entity.CartItem;
import entity.User;
import service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// @Controller  // Temporarily disabled
@RequestMapping("/cart")
public class CartController {
    
    @Autowired
    private CartService cartService;
    
    // Add item to cart (AJAX)
    @PostMapping("/add")
    @ResponseBody
    public Map<String, Object> addToCart(@RequestParam Integer productId,
                                         @RequestParam(defaultValue = "1") Integer quantity,
                                         HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "You must be logged in to add items to cart");
            return response;
        }
        
        try {
            CartItem cartItem = cartService.addToCart(currentUser.getUserId(), productId, quantity);
            
            // Get updated cart summary
            CartService.CartSummary summary = cartService.getCartSummary(currentUser.getUserId());
            
            response.put("success", true);
            response.put("message", "Item added to cart successfully!");
            response.put("cartItemCount", summary.getItemCount());
            response.put("cartTotal", summary.getTotal());
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // Remove item from cart (AJAX)
    @PostMapping("/remove")
    @ResponseBody
    public Map<String, Object> removeFromCart(@RequestParam Integer productId,
                                              HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "You must be logged in");
            return response;
        }
        
        try {
            cartService.removeFromCart(currentUser.getUserId(), productId);
            
            // Get updated cart summary
            CartService.CartSummary summary = cartService.getCartSummary(currentUser.getUserId());
            
            response.put("success", true);
            response.put("message", "Item removed from cart");
            response.put("cartItemCount", summary.getItemCount());
            response.put("cartTotal", summary.getTotal());
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // Update item quantity (AJAX)
    @PostMapping("/update-quantity")
    @ResponseBody
    public Map<String, Object> updateQuantity(@RequestParam Integer cartId,
                                              @RequestParam Integer quantity,
                                              HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "You must be logged in");
            return response;
        }
        
        try {
            cartService.updateQuantity(cartId, quantity, currentUser.getUserId());
            
            // Get updated cart summary
            CartService.CartSummary summary = cartService.getCartSummary(currentUser.getUserId());
            
            response.put("success", true);
            response.put("message", "Quantity updated");
            response.put("cartItemCount", summary.getItemCount());
            response.put("cartTotal", summary.getTotal());
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // Show shopping cart
    @GetMapping("/view")
    public String viewCart(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        List<CartItem> cartItems = cartService.getCartItems(currentUser.getUserId());
        List<CartItem> inactiveItems = cartService.getInactiveCartItems(currentUser.getUserId());
        CartService.CartSummary summary = cartService.getCartSummary(currentUser.getUserId());
        
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("inactiveItems", inactiveItems);
        model.addAttribute("cartSummary", summary);
        
        return "shopping-cart";
    }
    
    // Clear entire cart
    @PostMapping("/clear")
    @ResponseBody
    public Map<String, Object> clearCart(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "You must be logged in");
            return response;
        }
        
        try {
            cartService.clearCart(currentUser.getUserId());
            
            response.put("success", true);
            response.put("message", "Cart cleared");
            response.put("cartItemCount", 0);
            response.put("cartTotal", 0);
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // Clear inactive items
    @PostMapping("/clear-inactive")
    @ResponseBody
    public Map<String, Object> clearInactiveItems(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "You must be logged in");
            return response;
        }
        
        try {
            cartService.clearInactiveItems(currentUser.getUserId());
            
            response.put("success", true);
            response.put("message", "Inactive items removed");
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // Get cart summary (AJAX)
    @GetMapping("/summary")
    @ResponseBody
    public Map<String, Object> getCartSummary(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "Not logged in");
            return response;
        }
        
        try {
            CartService.CartSummary summary = cartService.getCartSummary(currentUser.getUserId());
            
            response.put("success", true);
            response.put("itemCount", summary.getItemCount());
            response.put("totalQuantity", summary.getTotalQuantity());
            response.put("subtotal", summary.getSubtotal());
            response.put("shippingTotal", summary.getShippingTotal());
            response.put("total", summary.getTotal());
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // Check if item is in cart (AJAX)
    @GetMapping("/check/{productId}")
    @ResponseBody
    public Map<String, Object> checkInCart(@PathVariable Integer productId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("inCart", false);
            return response;
        }
        
        boolean inCart = cartService.isInCart(currentUser.getUserId(), productId);
        response.put("inCart", inCart);
        
        return response;
    }
    
    // Add current user to all models
    @ModelAttribute("currentUser")
    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }
}