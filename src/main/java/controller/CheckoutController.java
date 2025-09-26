package controller;

import entity.Order;
import entity.User;
import service.CartService;
import service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

// @Controller  // Temporarily disabled
@RequestMapping("/checkout")
public class CheckoutController {
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private OrderService orderService;
    
    // Show checkout form
    @GetMapping("/")
    public String showCheckout(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        try {
            // Validate cart
            cartService.validateCartForCheckout(currentUser.getUserId());
            
            CartService.CartSummary cartSummary = cartService.getCartSummary(currentUser.getUserId());
            
            model.addAttribute("cartItems", cartService.getCartItems(currentUser.getUserId()));
            model.addAttribute("cartSummary", cartSummary);
            model.addAttribute("user", currentUser);
            
            return "checkout";
            
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/cart/view";
        }
    }
    
    // Process checkout
    @PostMapping("/process")
    public ModelAndView processCheckout(@RequestParam String shippingFirstName,
                                       @RequestParam String shippingLastName,
                                       @RequestParam String shippingAddress1,
                                       @RequestParam(required = false) String shippingAddress2,
                                       @RequestParam String shippingCity,
                                       @RequestParam String shippingState,
                                       @RequestParam String shippingZip,
                                       @RequestParam String shippingCountry,
                                       @RequestParam(defaultValue = "false") boolean sameAsBilling,
                                       @RequestParam(required = false) String billingFirstName,
                                       @RequestParam(required = false) String billingLastName,
                                       @RequestParam(required = false) String billingAddress1,
                                       @RequestParam(required = false) String billingAddress2,
                                       @RequestParam(required = false) String billingCity,
                                       @RequestParam(required = false) String billingState,
                                       @RequestParam(required = false) String billingZip,
                                       @RequestParam(required = false) String billingCountry,
                                       @RequestParam String paymentMethod,
                                       HttpSession session) {
        
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        try {
            // Build addresses
            String shippingAddress = buildAddress(shippingFirstName, shippingLastName, 
                                                 shippingAddress1, shippingAddress2, 
                                                 shippingCity, shippingState, shippingZip, shippingCountry);
            
            String billingAddress;
            if (sameAsBilling) {
                billingAddress = shippingAddress;
            } else {
                billingAddress = buildAddress(billingFirstName, billingLastName,
                                            billingAddress1, billingAddress2,
                                            billingCity, billingState, billingZip, billingCountry);
            }
            
            // Create order
            Order order = orderService.createOrderFromCart(currentUser.getUserId(), 
                                                          shippingAddress, billingAddress, paymentMethod);
            
            mv.setViewName("redirect:/checkout/payment/" + order.getOrderId());
            
        } catch (RuntimeException e) {
            mv.setViewName("checkout");
            mv.addObject("error", e.getMessage());
            mv.addObject("cartItems", cartService.getCartItems(currentUser.getUserId()));
            mv.addObject("cartSummary", cartService.getCartSummary(currentUser.getUserId()));
            mv.addObject("user", currentUser);
        }
        
        return mv;
    }
    
    // Show payment page
    @GetMapping("/payment/{orderId}")
    public ModelAndView showPayment(@PathVariable Integer orderId, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        Order order = orderService.getOrderById(orderId);
        if (order == null || !order.getBuyer().getUserId().equals(currentUser.getUserId())) {
            mv.setViewName("redirect:/");
            mv.addObject("error", "Order not found");
            return mv;
        }
        
        mv.setViewName("payment");
        mv.addObject("order", order);
        
        return mv;
    }
    
    // Process payment
    @PostMapping("/pay")
    @ResponseBody
    public Map<String, Object> processPayment(@RequestParam Integer orderId,
                                              @RequestParam String paymentMethod,
                                              @RequestParam(required = false) String cardNumber,
                                              @RequestParam(required = false) String expiryDate,
                                              @RequestParam(required = false) String cvv,
                                              @RequestParam(required = false) String cardHolderName,
                                              HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "You must be logged in");
            return response;
        }
        
        try {
            Order order = orderService.getOrderById(orderId);
            if (order == null || !order.getBuyer().getUserId().equals(currentUser.getUserId())) {
                response.put("success", false);
                response.put("message", "Order not found");
                return response;
            }
            
            // Process payment (simulation)
            boolean paymentSuccess = orderService.processPayment(orderId, paymentMethod);
            
            if (paymentSuccess) {
                response.put("success", true);
                response.put("message", "Payment processed successfully!");
                response.put("redirect", "/eBay/checkout/confirmation/" + orderId);
            } else {
                response.put("success", false);
                response.put("message", "Payment failed. Please try again or use a different payment method.");
            }
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // Show confirmation page
    @GetMapping("/confirmation/{orderId}")
    public ModelAndView showConfirmation(@PathVariable Integer orderId, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        Order order = orderService.getOrderById(orderId);
        if (order == null || !order.getBuyer().getUserId().equals(currentUser.getUserId())) {
            mv.setViewName("redirect:/");
            mv.addObject("error", "Order not found");
            return mv;
        }
        
        mv.setViewName("order-confirmation");
        mv.addObject("order", order);
        
        return mv;
    }
    
    // Show order details
    @GetMapping("/order/{orderId}")
    public ModelAndView showOrderDetails(@PathVariable Integer orderId, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            mv.setViewName("redirect:/");
            mv.addObject("error", "Order not found");
            return mv;
        }
        
        // Check if user has access to this order
        boolean hasAccess = order.getBuyer().getUserId().equals(currentUser.getUserId()) ||
                           order.getSeller().getUserId().equals(currentUser.getUserId());
        
        if (!hasAccess) {
            mv.setViewName("redirect:/");
            mv.addObject("error", "Access denied");
            return mv;
        }
        
        mv.setViewName("order-details");
        mv.addObject("order", order);
        mv.addObject("isBuyer", order.getBuyer().getUserId().equals(currentUser.getUserId()));
        mv.addObject("isSeller", order.getSeller().getUserId().equals(currentUser.getUserId()));
        
        return mv;
    }
    
    // Cancel order
    @PostMapping("/cancel/{orderId}")
    @ResponseBody
    public Map<String, Object> cancelOrder(@PathVariable Integer orderId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "You must be logged in");
            return response;
        }
        
        try {
            orderService.cancelOrder(orderId, currentUser.getUserId());
            
            response.put("success", true);
            response.put("message", "Order cancelled successfully");
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // Helper method to build address string
    private String buildAddress(String firstName, String lastName, String address1, String address2,
                               String city, String state, String zip, String country) {
        StringBuilder sb = new StringBuilder();
        
        if (firstName != null && !firstName.trim().isEmpty()) {
            sb.append(firstName.trim()).append(" ");
        }
        if (lastName != null && !lastName.trim().isEmpty()) {
            sb.append(lastName.trim()).append("\n");
        }
        
        if (address1 != null && !address1.trim().isEmpty()) {
            sb.append(address1.trim()).append("\n");
        }
        if (address2 != null && !address2.trim().isEmpty()) {
            sb.append(address2.trim()).append("\n");
        }
        
        if (city != null && !city.trim().isEmpty()) {
            sb.append(city.trim()).append(", ");
        }
        if (state != null && !state.trim().isEmpty()) {
            sb.append(state.trim()).append(" ");
        }
        if (zip != null && !zip.trim().isEmpty()) {
            sb.append(zip.trim()).append("\n");
        }
        if (country != null && !country.trim().isEmpty()) {
            sb.append(country.trim());
        }
        
        return sb.toString().trim();
    }
    
    // Add current user to all models
    @ModelAttribute("currentUser")
    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }
}