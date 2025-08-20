<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order Confirmation - eBay</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .confirmation-container {
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            text-align: center;
        }
        
        .success-icon {
            font-size: 80px;
            color: #28a745;
            margin-bottom: 20px;
        }
        
        .confirmation-header {
            margin-bottom: 30px;
        }
        
        .confirmation-header h1 {
            color: #28a745;
            margin-bottom: 10px;
        }
        
        .order-number {
            font-size: 24px;
            font-weight: bold;
            color: #333;
            margin-bottom: 20px;
        }
        
        .order-details {
            background: #f9f9f9;
            padding: 30px;
            border-radius: 8px;
            margin: 30px 0;
            text-align: left;
        }
        
        .details-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 30px;
            margin-bottom: 30px;
        }
        
        .detail-section h3 {
            margin-bottom: 15px;
            color: #333;
            border-bottom: 1px solid #e5e5e5;
            padding-bottom: 5px;
        }
        
        .detail-item {
            margin-bottom: 8px;
        }
        
        .detail-label {
            font-weight: bold;
            margin-right: 10px;
        }
        
        .order-items {
            margin-top: 20px;
        }
        
        .item-row {
            display: flex;
            align-items: center;
            padding: 15px 0;
            border-bottom: 1px solid #e5e5e5;
        }
        
        .item-row:last-child {
            border-bottom: none;
        }
        
        .item-image {
            width: 60px;
            height: 60px;
            object-fit: cover;
            margin-right: 15px;
            border-radius: 4px;
        }
        
        .item-info {
            flex: 1;
        }
        
        .item-title {
            font-weight: bold;
            margin-bottom: 5px;
        }
        
        .item-details {
            color: #666;
            font-size: 14px;
        }
        
        .item-price {
            font-weight: bold;
            color: #B12704;
            text-align: right;
            min-width: 100px;
        }
        
        .order-summary {
            background: white;
            border: 1px solid #e5e5e5;
            border-radius: 4px;
            padding: 20px;
            margin-top: 20px;
        }
        
        .summary-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
        }
        
        .summary-total {
            font-weight: bold;
            font-size: 18px;
            color: #B12704;
            border-top: 1px solid #ccc;
            padding-top: 10px;
        }
        
        .status-section {
            background: #fff3cd;
            border: 1px solid #ffeaa7;
            border-radius: 4px;
            padding: 20px;
            margin: 30px 0;
        }
        
        .status-section h3 {
            margin-bottom: 15px;
            color: #856404;
        }
        
        .status-item {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
        }
        
        .status-badge {
            padding: 4px 12px;
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
        
        .status-processing {
            background: #cce5ff;
            color: #004085;
        }
        
        .action-buttons {
            margin: 40px 0;
            display: flex;
            gap: 15px;
            justify-content: center;
            flex-wrap: wrap;
        }
        
        .btn {
            padding: 12px 25px;
            border-radius: 4px;
            text-decoration: none;
            font-weight: bold;
            border: none;
            cursor: pointer;
            font-size: 14px;
        }
        
        .btn-primary {
            background: #0654ba;
            color: white;
        }
        
        .btn-primary:hover {
            background: #054a9e;
        }
        
        .btn-secondary {
            background: #6c757d;
            color: white;
        }
        
        .btn-secondary:hover {
            background: #5a6268;
        }
        
        .btn-success {
            background: #28a745;
            color: white;
        }
        
        .next-steps {
            background: #e7f3ff;
            border: 1px solid #b3d9ff;
            border-radius: 4px;
            padding: 20px;
            margin: 30px 0;
        }
        
        .next-steps h3 {
            color: #004085;
            margin-bottom: 15px;
        }
        
        .next-steps ul {
            text-align: left;
            max-width: 500px;
            margin: 0 auto;
        }
        
        .next-steps li {
            margin-bottom: 10px;
        }
        
        @media (max-width: 768px) {
            .details-grid {
                grid-template-columns: 1fr;
                gap: 20px;
            }
            
            .action-buttons {
                flex-direction: column;
                align-items: center;
            }
            
            .btn {
                width: 100%;
                max-width: 300px;
            }
        }
    </style>
</head>
<body>
    <jsp:include page="header.jsp"/>
    
    <div class="confirmation-container">
        <div class="success-icon">✅</div>
        
        <div class="confirmation-header">
            <h1>Order Confirmed!</h1>
            <p>Thank you for your purchase. Your order has been successfully placed.</p>
        </div>
        
        <div class="order-number">
            Order #${order.orderId}
        </div>
        
        <div class="order-details">
            <div class="details-grid">
                <div class="detail-section">
                    <h3>Order Information</h3>
                    <div class="detail-item">
                        <span class="detail-label">Order Date:</span>
                        <fmt:formatDate value="${order.orderDate}" pattern="MMMM dd, yyyy 'at' h:mm a"/>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Payment Method:</span>
                        <c:choose>
                            <c:when test="${order.paymentMethod == 'CREDIT_CARD'}">Credit/Debit Card</c:when>
                            <c:when test="${order.paymentMethod == 'PAYPAL'}">PayPal</c:when>
                            <c:when test="${order.paymentMethod == 'BANK_TRANSFER'}">Bank Transfer</c:when>
                            <c:otherwise>${order.paymentMethod}</c:otherwise>
                        </c:choose>
                    </div>
                    <div class="detail-item">
                        <span class="detail-label">Seller:</span>
                        ${order.seller.firstName} ${order.seller.lastName}
                    </div>
                </div>
                
                <div class="detail-section">
                    <h3>Shipping Address</h3>
                    <div style="white-space: pre-line;">${order.shippingAddress}</div>
                </div>
            </div>
            
            <div class="order-items">
                <h3>Items Ordered</h3>
                <c:forEach var="item" items="${order.orderItems}">
                    <div class="item-row">
                        <img src="${pageContext.request.contextPath}/assets/images/products/${item.product.imageUrl != null ? item.product.imageUrl : 'default.jpg'}" 
                             alt="${item.product.title}" class="item-image">
                        <div class="item-info">
                            <div class="item-title">${item.product.title}</div>
                            <div class="item-details">
                                Quantity: ${item.quantity} | 
                                Unit Price: $<fmt:formatNumber value="${item.unitPrice}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                            </div>
                        </div>
                        <div class="item-price">
                            $<fmt:formatNumber value="${item.totalPrice}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                        </div>
                    </div>
                </c:forEach>
            </div>
            
            <div class="order-summary">
                <div class="summary-row">
                    <span>Subtotal:</span>
                    <span>$<fmt:formatNumber value="${order.totalAmount - order.shippingCost}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
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
        </div>
        
        <div class="status-section">
            <h3>Order Status</h3>
            <div class="status-item">
                <span>Payment Status:</span>
                <span class="status-badge ${order.paymentStatus == 'PAID' ? 'status-paid' : 'status-pending'}">
                    ${order.paymentStatus}
                </span>
            </div>
            <div class="status-item">
                <span>Shipping Status:</span>
                <span class="status-badge ${order.shippingStatus == 'PROCESSING' ? 'status-processing' : 'status-pending'}">
                    ${order.shippingStatus}
                </span>
            </div>
        </div>
        
        <div class="next-steps">
            <h3>What happens next?</h3>
            <ul>
                <li>You will receive an email confirmation shortly with your order details</li>
                <li>The seller will process your order and prepare it for shipping</li>
                <li>You'll receive a shipping notification with tracking information once your order ships</li>
                <li>You can track your order status in your account under "My Orders"</li>
            </ul>
        </div>
        
        <div class="action-buttons">
            <a href="${pageContext.request.contextPath}/checkout/order/${order.orderId}" class="btn btn-primary">
                View Order Details
            </a>
            <a href="${pageContext.request.contextPath}/user/orders" class="btn btn-secondary">
                My Orders
            </a>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-success">
                Continue Shopping
            </a>
        </div>
        
        <div style="margin-top: 40px; padding: 20px; background: #f8f9fa; border-radius: 4px;">
            <h4>Need Help?</h4>
            <p>If you have any questions about your order, please contact our customer service team.</p>
            <p>Order #${order.orderId} | <a href="mailto:support@ebay.com">support@ebay.com</a> | 1-800-EBAY-HELP</p>
        </div>
    </div>
    
    <jsp:include page="footer.jsp"/>
    
    <script>
        // Send order confirmation event (for analytics)
        if (typeof gtag !== 'undefined') {
            gtag('event', 'purchase', {
                'transaction_id': '${order.orderId}',
                'value': ${order.totalAmount},
                'currency': 'USD'
            });
        }
        
        // Print function
        function printOrder() {
            window.print();
        }
        
        // Add print button if needed
        document.addEventListener('DOMContentLoaded', function() {
            const actionButtons = document.querySelector('.action-buttons');
            if (actionButtons) {
                const printBtn = document.createElement('button');
                printBtn.className = 'btn btn-secondary';
                printBtn.textContent = 'Print Order';
                printBtn.onclick = printOrder;
                actionButtons.appendChild(printBtn);
            }
        });
    </script>
</body>
</html>