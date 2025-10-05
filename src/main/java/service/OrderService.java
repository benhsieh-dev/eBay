package service;

import dao.OrderDAO;
import dao.CartItemDAO;
import dao.ProductDAO;
import entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {
    
    @Autowired
    private OrderDAO orderDAO;
    
    @Autowired
    private CartItemDAO cartItemDAO;
    
    @Autowired
    private ProductDAO productDAO;
    
    @Autowired
    private CartService cartService;
    
    public Order createOrderFromCart(Integer userId, String shippingAddress, String billingAddress, String paymentMethod) {
        // Validate cart
        cartService.validateCartForCheckout(userId);
        
        List<CartItem> cartItems = cartService.getCartItems(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        // Group cart items by seller
        Map<Integer, List<CartItem>> itemsBySeller = cartItems.stream()
            .collect(Collectors.groupingBy(item -> item.getProduct().getSeller().getUserId()));
        
        List<Order> orders = new ArrayList<>();
        
        // Create separate orders for each seller
        for (Map.Entry<Integer, List<CartItem>> entry : itemsBySeller.entrySet()) {
            Integer sellerId = entry.getKey();
            List<CartItem> sellerItems = entry.getValue();
            
            Order order = createOrderForSeller(userId, sellerId, sellerItems, 
                                             shippingAddress, billingAddress, paymentMethod);
            orders.add(order);
        }
        
        // Clear cart after successful order creation
        cartService.clearCart(userId);
        
        // Return the first order (in a real system, you might return a list or summary)
        return orders.getFirst();
    }
    
    public Order createOrderFromBid(Bid winningBid, String shippingAddress, String billingAddress, String paymentMethod) {
        if (winningBid == null || winningBid.getBidStatus() != Bid.BidStatus.WON) {
            throw new RuntimeException("Invalid bid for order creation");
        }
        
        Product product = winningBid.getProduct();
        User buyer = winningBid.getBidder();
        User seller = product.getSeller();
        
        // Calculate totals
        BigDecimal itemTotal = winningBid.getBidAmount();
        BigDecimal shippingCost = product.getShippingCost();
        BigDecimal totalAmount = itemTotal.add(shippingCost);
        
        // Create order
        Order order = new Order();
        order.setBuyer(buyer);
        order.setSeller(seller);
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        order.setTotalAmount(totalAmount);
        order.setShippingCost(shippingCost);
        order.setTaxAmount(BigDecimal.ZERO); // Tax calculation would go here
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus(Order.PaymentStatus.PENDING);
        order.setShippingStatus(Order.ShippingStatus.PENDING);
        order.setShippingAddress(shippingAddress);
        order.setBillingAddress(billingAddress);
        
        order = orderDAO.save(order);
        
        // Create order item
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(1);
        orderItem.setUnitPrice(winningBid.getBidAmount());
        orderItem.setTotalPrice(winningBid.getBidAmount());
        
        List<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        order.setOrderItems(orderItems);
        
        // Update product quantities
        updateProductQuantities(order);
        
        return order;
    }
    
    private Order createOrderForSeller(Integer buyerId, Integer sellerId, List<CartItem> cartItems,
                                     String shippingAddress, String billingAddress, String paymentMethod) {
        
        // Calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal shippingTotal = BigDecimal.ZERO;
        
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            BigDecimal itemTotal = product.getCurrentPrice().multiply(new BigDecimal(item.getQuantity()));
            subtotal = subtotal.add(itemTotal);
            shippingTotal = shippingTotal.add(product.getShippingCost());
        }
        
        BigDecimal totalAmount = subtotal.add(shippingTotal);
        
        // Create order
        Order order = new Order();
        order.setBuyer(new User());
        order.getBuyer().setUserId(buyerId);
        order.setSeller(new User());
        order.getSeller().setUserId(sellerId);
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        order.setTotalAmount(totalAmount);
        order.setShippingCost(shippingTotal);
        order.setTaxAmount(BigDecimal.ZERO); // Tax calculation would go here
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus(Order.PaymentStatus.PENDING);
        order.setShippingStatus(Order.ShippingStatus.PENDING);
        order.setShippingAddress(shippingAddress);
        order.setBillingAddress(billingAddress);
        
        order = orderDAO.save(order);
        
        // Create order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getProduct().getCurrentPrice());
            orderItem.setTotalPrice(cartItem.getProduct().getCurrentPrice().multiply(new BigDecimal(cartItem.getQuantity())));
            
            orderItems.add(orderItem);
        }
        
        order.setOrderItems(orderItems);
        
        // Update product quantities
        updateProductQuantities(order);
        
        return order;
    }
    
    private void updateProductQuantities(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            int newQuantity = product.getQuantityAvailable() - item.getQuantity();
            int newSoldQuantity = product.getQuantitySold() + item.getQuantity();
            
            product.setQuantityAvailable(Math.max(0, newQuantity));
            product.setQuantitySold(newSoldQuantity);
            
            // Mark as sold if no quantity remaining
            if (product.getQuantityAvailable() == 0) {
                product.setStatus(Product.ProductStatus.SOLD);
            }
            
            productDAO.update(product);
        }
    }
    
    public Order getOrderById(Integer orderId) {
        return orderDAO.findById(orderId);
    }
    
    public List<Order> getUserOrders(Integer userId) {
        return orderDAO.findByBuyerId(userId);
    }
    
    public List<Order> getSellerOrders(Integer sellerId) {
        return orderDAO.findBySellerId(sellerId);
    }
    
    public List<Order> getOrdersBySeller(Integer sellerId) {
        return orderDAO.findBySellerId(sellerId);
    }
    
    public List<Order> getOrdersBySellerAndDateRange(Integer sellerId, Timestamp startDate, Timestamp endDate) {
        return orderDAO.findBySellerAndDateRange(sellerId, startDate, endDate);
    }
    
    public void updateOrderStatus(Integer orderId, Order.OrderStatus status, Integer userId, String trackingNumber) {
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        
        if (!order.getSeller().getUserId().equals(userId)) {
            throw new RuntimeException("You can only update your own orders");
        }
        
        order.setStatus(status);
        
        // Update related statuses based on order status
        switch (status) {
            case PROCESSING:
                order.setPaymentStatus(Order.PaymentStatus.PAID);
                order.setShippingStatus(Order.ShippingStatus.PROCESSING);
                break;
            case SHIPPED:
                order.setShippingStatus(Order.ShippingStatus.SHIPPED);
                if (trackingNumber != null && !trackingNumber.trim().isEmpty()) {
                    order.setTrackingNumber(trackingNumber);
                }
                break;
            case DELIVERED:
                order.setShippingStatus(Order.ShippingStatus.DELIVERED);
                break;
            case COMPLETED:
                order.setPaymentStatus(Order.PaymentStatus.PAID);
                order.setShippingStatus(Order.ShippingStatus.DELIVERED);
                break;
            case CANCELLED:
                order.setPaymentStatus(Order.PaymentStatus.FAILED);
                // Restore product quantities
                for (OrderItem item : order.getOrderItems()) {
                    Product product = item.getProduct();
                    product.setQuantityAvailable(product.getQuantityAvailable() + item.getQuantity());
                    product.setQuantitySold(Math.max(0, product.getQuantitySold() - item.getQuantity()));
                    
                    if (product.getStatus() == Product.ProductStatus.SOLD && product.getQuantityAvailable() > 0) {
                        product.setStatus(Product.ProductStatus.ACTIVE);
                    }
                    productDAO.update(product);
                }
                break;
        }
        
        orderDAO.update(order);
    }
    
    public void updatePaymentStatus(Integer orderId, Order.PaymentStatus paymentStatus) {
        orderDAO.updatePaymentStatus(orderId, paymentStatus);
        
        // If payment is successful, update shipping status
        if (paymentStatus == Order.PaymentStatus.PAID) {
            orderDAO.updateShippingStatus(orderId, Order.ShippingStatus.PROCESSING);
        }
    }
    
    public void updateShippingStatus(Integer orderId, Order.ShippingStatus shippingStatus, String trackingNumber) {
        orderDAO.updateShippingStatus(orderId, shippingStatus);
        
        if (trackingNumber != null && !trackingNumber.trim().isEmpty()) {
            orderDAO.updateTrackingNumber(orderId, trackingNumber);
        }
    }
    
    public void shipOrder(Integer orderId, String trackingNumber) {
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        
        if (order.getPaymentStatus() != Order.PaymentStatus.PAID) {
            throw new RuntimeException("Order must be paid before shipping");
        }
        
        orderDAO.updateShippingStatus(orderId, Order.ShippingStatus.SHIPPED);
        if (trackingNumber != null && !trackingNumber.trim().isEmpty()) {
            orderDAO.updateTrackingNumber(orderId, trackingNumber);
        }
    }
    
    public void markOrderDelivered(Integer orderId) {
        orderDAO.updateShippingStatus(orderId, Order.ShippingStatus.DELIVERED);
    }
    
    public void cancelOrder(Integer orderId, Integer userId) {
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        
        // Only allow cancellation by buyer if payment is pending
        if (!order.getBuyer().getUserId().equals(userId)) {
            throw new RuntimeException("You can only cancel your own orders");
        }
        
        if (order.getPaymentStatus() != Order.PaymentStatus.PENDING) {
            throw new RuntimeException("Can only cancel orders with pending payment");
        }
        
        // Restore product quantities
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setQuantityAvailable(product.getQuantityAvailable() + item.getQuantity());
            product.setQuantitySold(Math.max(0, product.getQuantitySold() - item.getQuantity()));
            
            if (product.getStatus() == Product.ProductStatus.SOLD && product.getQuantityAvailable() > 0) {
                product.setStatus(Product.ProductStatus.ACTIVE);
            }
            
            productDAO.update(product);
        }
        
        // Mark order as cancelled (using payment status for simplicity)
        orderDAO.updatePaymentStatus(orderId, Order.PaymentStatus.FAILED);
    }
    
    public OrderStatistics getOrderStatistics() {
        List<Object[]> stats = orderDAO.getOrderStatistics();
        if (stats.isEmpty()) {
            return new OrderStatistics(0L, BigDecimal.ZERO, BigDecimal.ZERO, 0L, 0L);
        }
        
        Object[] data = stats.getFirst();
        return new OrderStatistics(
            (Long) data[0],
            (BigDecimal) data[1],
            (BigDecimal) data[2],
            (Long) data[3],
            (Long) data[4]
        );
    }
    
    public OrderStatistics getSellerStatistics(Integer sellerId) {
        List<Object[]> stats = orderDAO.getSellerOrderStatistics(sellerId);
        if (stats.isEmpty()) {
            return new OrderStatistics(0L, BigDecimal.ZERO, BigDecimal.ZERO, 0L, 0L);
        }
        
        Object[] data = stats.getFirst();
        return new OrderStatistics(
            (Long) data[0],
            (BigDecimal) data[1],
            (BigDecimal) data[2],
            (Long) data[0], // Assuming all seller orders are paid
            0L // Delivered count not available in this query
        );
    }
    
    public List<Order> getPendingOrders() {
        return orderDAO.findPendingOrders();
    }
    
    public List<Order> getOrdersToShip() {
        return orderDAO.findOrdersToShip();
    }
    
    /**
     * Process payment simulation
     */
    public boolean processPayment(Integer orderId, String paymentMethod) {
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found");
        }
        
        if (order.getPaymentStatus() != Order.PaymentStatus.PENDING) {
            throw new RuntimeException("Order payment is not pending");
        }
        
        // Simulate payment processing
        try {
            // In a real system, this would integrate with payment processors
            Thread.sleep(1000); // Simulate processing time
            
            // Randomly succeed (90% success rate for demo)
            boolean success = Math.random() > 0.1;
            
            if (success) {
                updatePaymentStatus(orderId, Order.PaymentStatus.PAID);
                return true;
            } else {
                updatePaymentStatus(orderId, Order.PaymentStatus.FAILED);
                return false;
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            updatePaymentStatus(orderId, Order.PaymentStatus.FAILED);
            return false;
        }
    }
    
    /**
     * Order statistics helper class
     */
    public static class OrderStatistics {
        private final Long totalOrders;
        private final BigDecimal totalRevenue;
        private final BigDecimal averageOrderValue;
        private final Long paidOrders;
        private final Long deliveredOrders;
        
        public OrderStatistics(Long totalOrders, BigDecimal totalRevenue, BigDecimal averageOrderValue, 
                              Long paidOrders, Long deliveredOrders) {
            this.totalOrders = totalOrders;
            this.totalRevenue = totalRevenue != null ? totalRevenue : BigDecimal.ZERO;
            this.averageOrderValue = averageOrderValue != null ? averageOrderValue : BigDecimal.ZERO;
            this.paidOrders = paidOrders;
            this.deliveredOrders = deliveredOrders;
        }
        
        public Long getTotalOrders() { return totalOrders; }
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public BigDecimal getAverageOrderValue() { return averageOrderValue; }
        public Long getPaidOrders() { return paidOrders; }
        public Long getDeliveredOrders() { return deliveredOrders; }
    }
}