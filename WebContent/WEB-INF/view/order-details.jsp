<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order Details - eBay</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .order-container {
            max-width: 1000px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .order-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 2px solid #e5e5e5;
        }
        
        .order-title {
            margin: 0;
        }
        
        .order-date {
            color: #666;
            font-size: 14px;
        }
        
        .status-badges {
            display: flex;
            gap: 10px;
        }
        
        .status-badge {
            padding: 6px 12px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: bold;
            text-transform: uppercase;
        }
        
        .status-paid {
            background: #d4edda;
            color: #155724;
        }
        
        .status-pending {
            background: #fff3cd;
            color: #856404;
        }
        
        .status-failed {
            background: #f8d7da;
            color: #721c24;
        }
        
        .status-processing {
            background: #cce5ff;
            color: #004085;
        }
        
        .status-shipped {
            background: #d1ecf1;
            color: #0c5460;
        }
        
        .status-delivered {
            background: #d4edda;
            color: #155724;
        }
        
        .order-content {
            display: grid;
            grid-template-columns: 2fr 1fr;
            gap: 30px;
        }
        
        .order-items {
            background: white;
            border-radius: 4px;
            padding: 0;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .section-header {
            padding: 20px;
            border-bottom: 1px solid #e5e5e5;
            font-weight: bold;
            font-size: 18px;
        }
        
        .item-row {
            display: flex;
            align-items: center;
            padding: 20px;
            border-bottom: 1px solid #e5e5e5;
        }
        
        .item-row:last-child {
            border-bottom: none;
        }
        
        .item-image {
            width: 80px;
            height: 80px;
            object-fit: cover;
            margin-right: 20px;
            border-radius: 4px;
        }
        
        .item-details {
            flex: 1;
        }
        
        .item-title {
            font-weight: bold;
            margin-bottom: 5px;
        }
        
        .item-title a {
            text-decoration: none;
            color: #0654ba;
        }
        
        .item-title a:hover {
            text-decoration: underline;
        }
        
        .item-meta {
            color: #666;
            font-size: 14px;
            margin-bottom: 5px;
        }
        
        .item-price {
            text-align: right;
            min-width: 120px;
        }
        
        .unit-price {
            color: #666;
            font-size: 14px;
        }
        
        .total-price {
            font-weight: bold;
            color: #B12704;
            font-size: 16px;
        }
        
        .order-summary {
            background: white;
            border-radius: 4px;
            padding: 20px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            height: fit-content;
            position: sticky;
            top: 20px;
        }
        
        .summary-section {
            margin-bottom: 25px;
            padding-bottom: 20px;
            border-bottom: 1px solid #e5e5e5;
        }
        
        .summary-section:last-child {
            border-bottom: none;
            margin-bottom: 0;
        }
        
        .summary-title {
            font-weight: bold;
            font-size: 16px;
            margin-bottom: 15px;
        }
        
        .summary-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 8px;
        }
        
        .summary-total {
            font-weight: bold;
            font-size: 18px;
            color: #B12704;
            border-top: 1px solid #ccc;
            padding-top: 10px;
        }
        
        .address-section {
            margin-bottom: 15px;
        }
        
        .address-label {
            font-weight: bold;
            margin-bottom: 5px;
        }
        
        .address-text {
            white-space: pre-line;
            color: #666;
            font-size: 14px;
        }
        
        .tracking-section {
            background: #e7f3ff;
            border: 1px solid #b3d9ff;
            border-radius: 4px;
            padding: 15px;
            margin-bottom: 20px;
        }
        
        .tracking-title {
            font-weight: bold;
            margin-bottom: 10px;
            color: #004085;
        }
        
        .tracking-number {
            font-family: monospace;
            background: white;
            padding: 8px 12px;
            border-radius: 4px;
            border: 1px solid #ccc;
            display: inline-block;
        }
        
        .action-buttons {
            display: flex;
            flex-direction: column;
            gap: 10px;
        }
        
        .btn {
            padding: 10px 15px;
            border-radius: 4px;
            text-decoration: none;
            font-weight: bold;
            border: none;
            cursor: pointer;
            font-size: 14px;
            text-align: center;
        }
        
        .btn-primary {
            background: #0654ba;
            color: white;
        }
        
        .btn-secondary {
            background: #6c757d;
            color: white;
        }
        
        .btn-danger {
            background: #dc3545;
            color: white;
        }
        
        .btn-warning {
            background: #ffc107;
            color: #212529;
        }
        
        .btn:hover {
            opacity: 0.9;
        }
        
        .timeline {
            margin-top: 20px;
        }
        
        .timeline-item {
            display: flex;
            align-items: center;
            margin-bottom: 15px;
            padding: 10px;
            background: #f8f9fa;
            border-radius: 4px;
        }
        
        .timeline-icon {
            width: 30px;
            height: 30px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 15px;
            font-size: 14px;
        }
        
        .timeline-completed {
            background: #28a745;
            color: white;
        }
        
        .timeline-current {
            background: #ffc107;
            color: #212529;
        }
        
        .timeline-pending {
            background: #e9ecef;
            color: #6c757d;
        }
        
        .alert {
            padding: 10px;
            margin: 10px 0;
            border-radius: 4px;
        }
        
        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        @media (max-width: 768px) {
            .order-content {
                grid-template-columns: 1fr;
                gap: 20px;
            }
            
            .order-header {
                flex-direction: column;
                align-items: flex-start;
                gap: 15px;
            }
            
            .status-badges {
                flex-wrap: wrap;
            }
            
            .item-row {
                flex-direction: column;
                align-items: flex-start;
                gap: 15px;
            }
            
            .item-price {
                text-align: left;
            }
        }
    </style>
</head>
<body>
    <jsp:include page="header.jsp"/>
    
    <div class="order-container">
        <div class="order-header">
            <div>
                <h1 class="order-title">Order #${order.orderId}</h1>
                <div class="order-date">
                    Placed on <fmt:formatDate value="${order.orderDate}" pattern="MMMM dd, yyyy 'at' h:mm a"/>
                </div>
            </div>
            
            <div class="status-badges">
                <span class="status-badge ${order.paymentStatus == 'PAID' ? 'status-paid' : (order.paymentStatus == 'FAILED' ? 'status-failed' : 'status-pending')}">
                    Payment: ${order.paymentStatus}
                </span>
                <span class="status-badge ${order.shippingStatus == 'DELIVERED' ? 'status-delivered' : (order.shippingStatus == 'SHIPPED' ? 'status-shipped' : (order.shippingStatus == 'PROCESSING' ? 'status-processing' : 'status-pending'))}">
                    Shipping: ${order.shippingStatus}
                </span>
            </div>
        </div>
        
        <div id="order-messages"></div>
        
        <div class="order-content">
            <!-- Order Items -->
            <div class="order-items">
                <div class="section-header">
                    Items in this order
                </div>
                
                <c:forEach var="item" items="${order.orderItems}">
                    <div class="item-row">
                        <img src="${pageContext.request.contextPath}/assets/images/products/${item.product.imageUrl != null ? item.product.imageUrl : 'default.jpg'}" 
                             alt="${item.product.title}" class="item-image">
                        
                        <div class="item-details">
                            <div class="item-title">
                                <a href="${pageContext.request.contextPath}/products/view/${item.product.productId}">
                                    ${item.product.title}
                                </a>
                            </div>
                            <div class="item-meta">
                                Condition: ${item.product.condition} | 
                                Seller: ${order.seller.firstName} ${order.seller.lastName}
                            </div>
                            <div class="item-meta">
                                Quantity: ${item.quantity}
                            </div>
                        </div>
                        
                        <div class="item-price">
                            <div class="unit-price">
                                $<fmt:formatNumber value="${item.unitPrice}" type="number" minFractionDigits="2" maxFractionDigits="2"/> each
                            </div>
                            <div class="total-price">
                                $<fmt:formatNumber value="${item.totalPrice}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            
            <!-- Order Summary -->
            <div class="order-summary">
                <!-- Order Total -->
                <div class="summary-section">
                    <div class="summary-title">Order Summary</div>
                    <div class="summary-row">
                        <span>Subtotal:</span>
                        <span>$<fmt:formatNumber value="${order.totalAmount - order.shippingCost - order.taxAmount}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
                    </div>
                    <div class="summary-row">
                        <span>Shipping:</span>
                        <span>$<fmt:formatNumber value="${order.shippingCost}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
                    </div>
                    <div class="summary-row">
                        <span>Tax:</span>
                        <span>$<fmt:formatNumber value="${order.taxAmount}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
                    </div>
                    <div class="summary-row summary-total">
                        <span>Total:</span>
                        <span>$<fmt:formatNumber value="${order.totalAmount}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
                    </div>
                </div>
                
                <!-- Shipping Information -->
                <div class="summary-section">
                    <div class="summary-title">Shipping Information</div>
                    
                    <c:if test="${not empty order.trackingNumber}">
                        <div class="tracking-section">
                            <div class="tracking-title">Tracking Number:</div>
                            <div class="tracking-number">${order.trackingNumber}</div>
                        </div>
                    </c:if>
                    
                    <div class="address-section">
                        <div class="address-label">Shipping Address:</div>
                        <div class="address-text">${order.shippingAddress}</div>
                    </div>
                    
                    <div class="address-section">
                        <div class="address-label">Billing Address:</div>
                        <div class="address-text">${order.billingAddress}</div>
                    </div>
                </div>
                
                <!-- Payment Information -->
                <div class="summary-section">
                    <div class="summary-title">Payment Information</div>
                    <div class="summary-row">
                        <span>Payment Method:</span>
                        <span>
                            <c:choose>
                                <c:when test="${order.paymentMethod == 'CREDIT_CARD'}">Credit/Debit Card</c:when>
                                <c:when test="${order.paymentMethod == 'PAYPAL'}">PayPal</c:when>
                                <c:when test="${order.paymentMethod == 'BANK_TRANSFER'}">Bank Transfer</c:when>
                                <c:otherwise>${order.paymentMethod}</c:otherwise>
                            </c:choose>
                        </span>
                    </div>
                </div>
                
                <!-- Order Timeline -->
                <div class="summary-section">
                    <div class="summary-title">Order Progress</div>
                    <div class="timeline">
                        <div class="timeline-item">
                            <div class="timeline-icon timeline-completed">✓</div>
                            <div>
                                <strong>Order Placed</strong><br>
                                <small><fmt:formatDate value="${order.orderDate}" pattern="MMM dd, yyyy"/></small>
                            </div>
                        </div>
                        
                        <div class="timeline-item">
                            <div class="timeline-icon ${order.paymentStatus == 'PAID' ? 'timeline-completed' : (order.paymentStatus == 'PENDING' ? 'timeline-current' : 'timeline-pending')}">
                                ${order.paymentStatus == 'PAID' ? '✓' : (order.paymentStatus == 'PENDING' ? '○' : '✗')}
                            </div>
                            <div>
                                <strong>Payment ${order.paymentStatus == 'PAID' ? 'Confirmed' : order.paymentStatus}</strong>
                            </div>
                        </div>
                        
                        <div class="timeline-item">
                            <div class="timeline-icon ${order.shippingStatus == 'PROCESSING' || order.shippingStatus == 'SHIPPED' || order.shippingStatus == 'DELIVERED' ? 'timeline-completed' : 'timeline-pending'}">
                                ${order.shippingStatus == 'PROCESSING' || order.shippingStatus == 'SHIPPED' || order.shippingStatus == 'DELIVERED' ? '✓' : '○'}
                            </div>
                            <div>
                                <strong>Order Processing</strong>
                            </div>
                        </div>
                        
                        <div class="timeline-item">
                            <div class="timeline-icon ${order.shippingStatus == 'SHIPPED' || order.shippingStatus == 'DELIVERED' ? 'timeline-completed' : (order.shippingStatus == 'PROCESSING' ? 'timeline-current' : 'timeline-pending')}">
                                ${order.shippingStatus == 'SHIPPED' || order.shippingStatus == 'DELIVERED' ? '✓' : '○'}
                            </div>
                            <div>
                                <strong>Shipped</strong>
                            </div>
                        </div>
                        
                        <div class="timeline-item">
                            <div class="timeline-icon ${order.shippingStatus == 'DELIVERED' ? 'timeline-completed' : 'timeline-pending'}">
                                ${order.shippingStatus == 'DELIVERED' ? '✓' : '○'}
                            </div>
                            <div>
                                <strong>Delivered</strong>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Action Buttons -->
                <div class="action-buttons">
                    <c:if test="${isBuyer && order.paymentStatus == 'PENDING'}">
                        <button type="button" class="btn btn-danger" onclick="cancelOrder()">Cancel Order</button>
                    </c:if>
                    
                    <c:if test="${isSeller && order.paymentStatus == 'PAID' && order.shippingStatus == 'PENDING'}">
                        <button type="button" class="btn btn-warning" onclick="shipOrder()">Mark as Shipped</button>
                    </c:if>
                    
                    <a href="${pageContext.request.contextPath}/user/orders" class="btn btn-secondary">Back to Orders</a>
                    
                    <button type="button" class="btn btn-primary" onclick="window.print()">Print Order</button>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="footer.jsp"/>
    
    <script>
        function cancelOrder() {
            if (!confirm('Are you sure you want to cancel this order?')) {
                return;
            }
            
            fetch('${pageContext.request.contextPath}/checkout/cancel/${order.orderId}', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                }
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    showMessage(data.message, 'success');
                    setTimeout(() => {
                        location.reload();
                    }, 2000);
                } else {
                    showMessage(data.message, 'error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showMessage('Error cancelling order', 'error');
            });
        }
        
        function shipOrder() {
            const trackingNumber = prompt('Enter tracking number (optional):');
            
            // In a real system, this would be a more sophisticated form
            // For now, we'll just show a message
            showMessage('Shipping functionality would be implemented here', 'success');
        }
        
        function showMessage(message, type) {
            const messagesDiv = document.getElementById('order-messages');
            const alertDiv = document.createElement('div');
            alertDiv.className = 'alert alert-' + type;
            alertDiv.textContent = message;
            messagesDiv.innerHTML = '';
            messagesDiv.appendChild(alertDiv);
            
            // Auto-hide after 5 seconds
            setTimeout(() => {
                alertDiv.remove();
            }, 5000);
        }
    </script>
</body>
</html>