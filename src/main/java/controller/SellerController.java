package controller;

import entity.Product;
import entity.User;
import entity.Order;
import entity.OrderItem;
import service.ProductService;
import service.OrderService;
import service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/seller")
public class SellerController {
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CategoryService categoryService;
    
    // Seller Dashboard Main Page
    @GetMapping("/dashboard")
    public ModelAndView dashboard(HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        if (currentUser.getUserType() == User.UserType.BUYER) {
            mv.setViewName("redirect:/");
            mv.addObject("error", "Access denied. Seller account required.");
            return mv;
        }
        
        // Get seller's products statistics
        List<Product> sellerProducts = productService.getProductsBySeller(currentUser.getUserId());
        List<Order> sellerOrders = orderService.getOrdersBySeller(currentUser.getUserId());
        
        // Calculate statistics
        long activeListings = sellerProducts.stream()
            .filter(p -> p.getStatus() == Product.ProductStatus.ACTIVE)
            .count();
        
        long draftListings = sellerProducts.stream()
            .filter(p -> p.getStatus() == Product.ProductStatus.DRAFT)
            .count();
            
        long soldListings = sellerProducts.stream()
            .filter(p -> p.getStatus() == Product.ProductStatus.SOLD)
            .count();
        
        // Recent orders (last 10)
        List<Order> recentOrders = sellerOrders.stream()
            .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
            .limit(10)
            .collect(Collectors.toList());
        
        // Calculate total revenue
        BigDecimal totalRevenue = sellerOrders.stream()
            .filter(order -> order.getStatus() == Order.OrderStatus.COMPLETED)
            .map(Order::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Pending orders count
        long pendingOrders = sellerOrders.stream()
            .filter(order -> order.getStatus() == Order.OrderStatus.PENDING_SHIPMENT || 
                           order.getStatus() == Order.OrderStatus.PROCESSING)
            .count();
        
        mv.setViewName("seller-dashboard");
        mv.addObject("activeListings", activeListings);
        mv.addObject("draftListings", draftListings);
        mv.addObject("soldListings", soldListings);
        mv.addObject("totalRevenue", totalRevenue);
        mv.addObject("pendingOrders", pendingOrders);
        mv.addObject("recentOrders", recentOrders);
        mv.addObject("recentProducts", sellerProducts.stream()
            .sorted((p1, p2) -> p2.getUpdatedDate().compareTo(p1.getUpdatedDate()))
            .limit(5)
            .collect(Collectors.toList()));
        
        return mv;
    }
    
    // My Listings - Show all seller's products
    @GetMapping("/listings")
    public ModelAndView myListings(@RequestParam(defaultValue = "all") String status,
                                  @RequestParam(defaultValue = "1") int page,
                                  HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        if (currentUser.getUserType() == User.UserType.BUYER) {
            mv.setViewName("redirect:/");
            mv.addObject("error", "Access denied. Seller account required.");
            return mv;
        }
        
        List<Product> products = productService.getProductsBySeller(currentUser.getUserId());
        
        // Filter by status if specified
        if (!"all".equals(status)) {
            try {
                Product.ProductStatus statusFilter = Product.ProductStatus.valueOf(status.toUpperCase());
                products = products.stream()
                    .filter(p -> p.getStatus() == statusFilter)
                    .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Invalid status, show all
            }
        }
        
        // Sort by most recent first
        products = products.stream()
            .sorted((p1, p2) -> p2.getUpdatedDate().compareTo(p1.getUpdatedDate()))
            .collect(Collectors.toList());
        
        mv.setViewName("seller-listings");
        mv.addObject("products", products);
        mv.addObject("currentStatus", status);
        mv.addObject("statuses", Product.ProductStatus.values());
        
        return mv;
    }
    
    // Orders Management
    @GetMapping("/orders")
    public ModelAndView myOrders(@RequestParam(defaultValue = "all") String status,
                                HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        if (currentUser.getUserType() == User.UserType.BUYER) {
            mv.setViewName("redirect:/");
            mv.addObject("error", "Access denied. Seller account required.");
            return mv;
        }
        
        List<Order> orders = orderService.getOrdersBySeller(currentUser.getUserId());
        
        // Filter by status if specified
        if (!"all".equals(status)) {
            try {
                Order.OrderStatus statusFilter = Order.OrderStatus.valueOf(status.toUpperCase());
                orders = orders.stream()
                    .filter(o -> o.getStatus() == statusFilter)
                    .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // Invalid status, show all
            }
        }
        
        // Sort by most recent first
        orders = orders.stream()
            .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
            .collect(Collectors.toList());
        
        mv.setViewName("seller-orders");
        mv.addObject("orders", orders);
        mv.addObject("currentStatus", status);
        mv.addObject("statuses", Order.OrderStatus.values());
        
        return mv;
    }
    
    // Update Order Status
    @PostMapping("/orders/{orderId}/update-status")
    public ModelAndView updateOrderStatus(@PathVariable Integer orderId,
                                         @RequestParam String status,
                                         @RequestParam(required = false) String trackingNumber,
                                         HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        try {
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status);
            orderService.updateOrderStatus(orderId, newStatus, currentUser.getUserId(), trackingNumber);
            
            mv.setViewName("redirect:/seller/orders");
            mv.addObject("message", "Order status updated successfully!");
            
        } catch (IllegalArgumentException e) {
            mv.setViewName("redirect:/seller/orders");
            mv.addObject("error", "Invalid status");
        } catch (RuntimeException e) {
            mv.setViewName("redirect:/seller/orders");
            mv.addObject("error", e.getMessage());
        }
        
        return mv;
    }
    
    // Inventory Management
    @GetMapping("/inventory")
    public ModelAndView inventory(HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        if (currentUser.getUserType() == User.UserType.BUYER) {
            mv.setViewName("redirect:/");
            mv.addObject("error", "Access denied. Seller account required.");
            return mv;
        }
        
        List<Product> products = productService.getProductsBySeller(currentUser.getUserId());
        
        // Filter only buy-now products (not auctions)
        products = products.stream()
            .filter(p -> p.isBuyNowAvailable() && p.getStatus() == Product.ProductStatus.ACTIVE)
            .sorted((p1, p2) -> p1.getTitle().compareToIgnoreCase(p2.getTitle()))
            .collect(Collectors.toList());
        
        mv.setViewName("seller-inventory");
        mv.addObject("products", products);
        
        return mv;
    }
    
    // Update Inventory Quantity (AJAX)
    @PostMapping("/inventory/update-quantity")
    @ResponseBody
    public Map<String, Object> updateInventoryQuantity(@RequestParam Integer productId,
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
            productService.updateProductQuantity(productId, quantity, currentUser.getUserId());
            response.put("success", true);
            response.put("message", "Quantity updated successfully!");
            
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // Sales Analytics
    @GetMapping("/analytics")
    public ModelAndView analytics(@RequestParam(defaultValue = "30") int days,
                                 HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        if (currentUser.getUserType() == User.UserType.BUYER) {
            mv.setViewName("redirect:/");
            mv.addObject("error", "Access denied. Seller account required.");
            return mv;
        }
        
        // Calculate date range
        Timestamp endDate = new Timestamp(System.currentTimeMillis());
        Timestamp startDate = new Timestamp(System.currentTimeMillis() - (days * 24L * 60L * 60L * 1000L));
        
        List<Order> orders = orderService.getOrdersBySellerAndDateRange(
            currentUser.getUserId(), startDate, endDate);
        
        // Calculate metrics
        BigDecimal totalRevenue = orders.stream()
            .filter(o -> o.getStatus() == Order.OrderStatus.COMPLETED)
            .map(Order::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        int totalOrders = orders.size();
        
        int totalItemsSold = orders.stream()
            .flatMap(order -> order.getOrderItems().stream())
            .mapToInt(OrderItem::getQuantity)
            .sum();
        
        BigDecimal averageOrderValue = totalOrders > 0 ? 
            totalRevenue.divide(new BigDecimal(totalOrders), 2, BigDecimal.ROUND_HALF_UP) : 
            BigDecimal.ZERO;
        
        // Top selling products
        Map<Product, Integer> productSales = new HashMap<>();
        orders.stream()
            .flatMap(order -> order.getOrderItems().stream())
            .forEach(item -> {
                productSales.merge(item.getProduct(), item.getQuantity(), Integer::sum);
            });
        
        List<Map.Entry<Product, Integer>> topProducts = productSales.entrySet()
            .stream()
            .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
            .limit(10)
            .collect(Collectors.toList());
        
        mv.setViewName("seller-analytics");
        mv.addObject("totalRevenue", totalRevenue);
        mv.addObject("totalOrders", totalOrders);
        mv.addObject("totalItemsSold", totalItemsSold);
        mv.addObject("averageOrderValue", averageOrderValue);
        mv.addObject("topProducts", topProducts);
        mv.addObject("selectedDays", days);
        mv.addObject("startDate", startDate);
        mv.addObject("endDate", endDate);
        
        return mv;
    }
    
    // Auction Management
    @GetMapping("/auctions")
    public ModelAndView manageAuctions(HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        if (currentUser.getUserType() == User.UserType.BUYER) {
            mv.setViewName("redirect:/");
            mv.addObject("error", "Access denied. Seller account required.");
            return mv;
        }
        
        List<Product> sellerProducts = productService.getProductsBySeller(currentUser.getUserId());
        
        // Filter only auction products
        List<Product> auctions = sellerProducts.stream()
            .filter(Product::isAuction)
            .sorted((p1, p2) -> {
                // Sort by auction end time, with active auctions first
                if (p1.isAuctionActive() && !p2.isAuctionActive()) return -1;
                if (!p1.isAuctionActive() && p2.isAuctionActive()) return 1;
                if (p1.getAuctionEndTime() != null && p2.getAuctionEndTime() != null) {
                    return p1.getAuctionEndTime().compareTo(p2.getAuctionEndTime());
                }
                return p2.getUpdatedDate().compareTo(p1.getUpdatedDate());
            })
            .collect(Collectors.toList());
        
        // Calculate auction statistics
        long activeAuctions = auctions.stream()
            .filter(Product::isAuctionActive)
            .count();
        
        long endingSoon = auctions.stream()
            .filter(p -> p.isAuctionActive() && p.getTimeRemainingMillis() < 24 * 60 * 60 * 1000) // 24 hours
            .count();
        
        long endedAuctions = auctions.stream()
            .filter(Product::isAuctionEnded)
            .count();
        
        mv.setViewName("seller-auctions");
        mv.addObject("auctions", auctions);
        mv.addObject("activeAuctions", activeAuctions);
        mv.addObject("endingSoon", endingSoon);
        mv.addObject("endedAuctions", endedAuctions);
        
        return mv;
    }
    
    // Extend Auction End Time
    @PostMapping("/auctions/{productId}/extend")
    public ModelAndView extendAuction(@PathVariable Integer productId,
                                     @RequestParam int hours,
                                     HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        try {
            Product product = productService.getProductById(productId);
            if (product == null || !product.getSeller().getUserId().equals(currentUser.getUserId())) {
                throw new RuntimeException("Product not found or access denied");
            }
            
            if (!product.isAuction() || !product.isAuctionActive()) {
                throw new RuntimeException("Can only extend active auctions");
            }
            
            if (hours < 1 || hours > 72) {
                throw new RuntimeException("Extension must be between 1 and 72 hours");
            }
            
            // Extend auction end time
            long extensionMillis = hours * 60L * 60L * 1000L;
            Timestamp newEndTime = new Timestamp(product.getAuctionEndTime().getTime() + extensionMillis);
            product.setAuctionEndTime(newEndTime);
            product.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            
            productService.updateProduct(product);
            
            mv.setViewName("redirect:/seller/auctions");
            mv.addObject("message", "Auction extended by " + hours + " hours");
            
        } catch (RuntimeException e) {
            mv.setViewName("redirect:/seller/auctions");
            mv.addObject("error", e.getMessage());
        }
        
        return mv;
    }
    
    // End Auction Early
    @PostMapping("/auctions/{productId}/end")
    public ModelAndView endAuctionEarly(@PathVariable Integer productId, HttpSession session) {
        ModelAndView mv = new ModelAndView();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            mv.setViewName("redirect:/user/login");
            return mv;
        }
        
        try {
            Product product = productService.getProductById(productId);
            if (product == null || !product.getSeller().getUserId().equals(currentUser.getUserId())) {
                throw new RuntimeException("Product not found or access denied");
            }
            
            if (!product.isAuction() || !product.isAuctionActive()) {
                throw new RuntimeException("Can only end active auctions");
            }
            
            // End auction by setting end time to now
            product.setAuctionEndTime(new Timestamp(System.currentTimeMillis()));
            product.setStatus(Product.ProductStatus.ENDED);
            product.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
            
            productService.updateProduct(product);
            
            mv.setViewName("redirect:/seller/auctions");
            mv.addObject("message", "Auction ended successfully");
            
        } catch (RuntimeException e) {
            mv.setViewName("redirect:/seller/auctions");
            mv.addObject("error", e.getMessage());
        }
        
        return mv;
    }
    
    // Add current user to all models
    @ModelAttribute("currentUser")
    public User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("currentUser");
    }
}