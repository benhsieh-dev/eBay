<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Shopping Cart - eBay</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .cart-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .cart-header {
            border-bottom: 2px solid #e5e5e5;
            padding-bottom: 15px;
            margin-bottom: 25px;
        }
        
        .cart-item {
            display: flex;
            align-items: center;
            padding: 20px 0;
            border-bottom: 1px solid #e5e5e5;
        }
        
        .item-image {
            width: 80px;
            height: 80px;
            object-fit: cover;
            margin-right: 15px;
            border-radius: 4px;
        }
        
        .item-details {
            flex: 1;
            margin-right: 15px;
        }
        
        .item-title {
            font-weight: bold;
            margin-bottom: 5px;
        }
        
        .item-condition {
            color: #666;
            font-size: 14px;
            margin-bottom: 5px;
        }
        
        .item-seller {
            color: #0654ba;
            font-size: 14px;
        }
        
        .quantity-controls {
            display: flex;
            align-items: center;
            margin: 0 15px;
        }
        
        .quantity-btn {
            background: #f0f0f0;
            border: 1px solid #ccc;
            width: 30px;
            height: 30px;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .quantity-input {
            width: 50px;
            text-align: center;
            border: 1px solid #ccc;
            height: 30px;
            margin: 0 5px;
        }
        
        .item-price {
            font-weight: bold;
            color: #B12704;
            font-size: 18px;
            margin: 0 15px;
            min-width: 80px;
            text-align: right;
        }
        
        .remove-btn {
            color: #0654ba;
            text-decoration: none;
            font-size: 14px;
            cursor: pointer;
        }
        
        .remove-btn:hover {
            text-decoration: underline;
        }
        
        .cart-summary {
            background: #f9f9f9;
            padding: 20px;
            border-radius: 4px;
            margin-top: 30px;
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
        
        .checkout-section {
            text-align: center;
            margin-top: 20px;
        }
        
        .checkout-btn {
            background: #ff9900;
            color: white;
            border: none;
            padding: 12px 30px;
            font-size: 16px;
            border-radius: 4px;
            cursor: pointer;
            margin: 10px;
        }
        
        .checkout-btn:hover {
            background: #e88900;
        }
        
        .clear-cart-btn {
            background: #767676;
            color: white;
            border: none;
            padding: 8px 20px;
            font-size: 14px;
            border-radius: 4px;
            cursor: pointer;
        }
        
        .empty-cart {
            text-align: center;
            padding: 60px 20px;
            color: #666;
        }
        
        .inactive-items {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 2px solid #e5e5e5;
        }
        
        .inactive-item {
            opacity: 0.6;
            background: #f9f9f9;
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
    </style>
</head>
<body>
    <jsp:include page="header.jsp"/>
    
    <div class="cart-container">
        <div class="cart-header">
            <h1>Shopping Cart</h1>
            <c:if test="${not empty cartItems}">
                <p>${cartSummary.itemCount} item(s) in your cart</p>
            </c:if>
        </div>
        
        <div id="cart-messages"></div>
        
        <c:choose>
            <c:when test="${empty cartItems}">
                <div class="empty-cart">
                    <h2>Your cart is empty</h2>
                    <p>Start shopping to add items to your cart!</p>
                    <a href="${pageContext.request.contextPath}/products" class="checkout-btn">Continue Shopping</a>
                </div>
            </c:when>
            <c:otherwise>
                <div id="cart-items">
                    <c:forEach var="item" items="${cartItems}">
                        <div class="cart-item" data-cart-id="${item.cartId}">
                            <img src="${pageContext.request.contextPath}/assets/images/products/${item.product.imageUrl != null ? item.product.imageUrl : 'default.jpg'}" 
                                 alt="${item.product.title}" class="item-image">
                            
                            <div class="item-details">
                                <div class="item-title">
                                    <a href="${pageContext.request.contextPath}/products/view/${item.product.productId}">
                                        ${item.product.title}
                                    </a>
                                </div>
                                <div class="item-condition">${item.product.condition}</div>
                                <div class="item-seller">
                                    Sold by: ${item.product.seller.firstName} ${item.product.seller.lastName}
                                </div>
                            </div>
                            
                            <div class="quantity-controls">
                                <button type="button" class="quantity-btn" onclick="updateQuantity(${item.cartId}, ${item.quantity - 1})">-</button>
                                <input type="number" class="quantity-input" value="${item.quantity}" 
                                       onchange="updateQuantity(${item.cartId}, this.value)" min="1" max="${item.product.quantityAvailable}">
                                <button type="button" class="quantity-btn" onclick="updateQuantity(${item.cartId}, ${item.quantity + 1})">+</button>
                            </div>
                            
                            <div class="item-price">
                                $<fmt:formatNumber value="${item.product.currentPrice * item.quantity}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                            </div>
                            
                            <a href="javascript:void(0)" class="remove-btn" onclick="removeFromCart(${item.product.productId})">Remove</a>
                        </div>
                    </c:forEach>
                </div>
                
                <div class="cart-summary">
                    <div class="summary-row">
                        <span>Subtotal (${cartSummary.totalQuantity} items):</span>
                        <span>$<fmt:formatNumber value="${cartSummary.subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
                    </div>
                    <div class="summary-row">
                        <span>Shipping:</span>
                        <span>$<fmt:formatNumber value="${cartSummary.shippingTotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
                    </div>
                    <div class="summary-row summary-total">
                        <span>Total:</span>
                        <span id="cart-total">$<fmt:formatNumber value="${cartSummary.total}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
                    </div>
                    
                    <div class="checkout-section">
                        <button type="button" class="checkout-btn" onclick="proceedToCheckout()">Proceed to Checkout</button>
                        <button type="button" class="clear-cart-btn" onclick="clearCart()">Clear Cart</button>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
        
        <!-- Inactive Items Section -->
        <c:if test="${not empty inactiveItems}">
            <div class="inactive-items">
                <h3>Items No Longer Available</h3>
                <p>These items are no longer available for purchase (auction ended or sold out):</p>
                
                <c:forEach var="item" items="${inactiveItems}">
                    <div class="cart-item inactive-item">
                        <img src="${pageContext.request.contextPath}/assets/images/products/${item.product.imageUrl != null ? item.product.imageUrl : 'default.jpg'}" 
                             alt="${item.product.title}" class="item-image">
                        
                        <div class="item-details">
                            <div class="item-title">${item.product.title}</div>
                            <div class="item-condition">
                                <c:choose>
                                    <c:when test="${item.product.status == 'SOLD'}">SOLD OUT</c:when>
                                    <c:when test="${item.product.status == 'EXPIRED'}">AUCTION ENDED</c:when>
                                    <c:otherwise>NOT AVAILABLE</c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                        
                        <a href="javascript:void(0)" class="remove-btn" onclick="removeFromCart(${item.product.productId})">Remove</a>
                    </div>
                </c:forEach>
                
                <div style="text-align: center; margin-top: 15px;">
                    <button type="button" class="clear-cart-btn" onclick="clearInactiveItems()">Remove All Inactive Items</button>
                </div>
            </div>
        </c:if>
    </div>
    
    <jsp:include page="footer.jsp"/>
    
    <script>
        function updateQuantity(cartId, quantity) {
            if (quantity <= 0) {
                return;
            }
            
            fetch('${pageContext.request.contextPath}/cart/update-quantity', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'cartId=' + cartId + '&quantity=' + quantity
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    location.reload(); // Refresh to show updated prices
                } else {
                    showMessage(data.message, 'error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showMessage('Error updating quantity', 'error');
            });
        }
        
        function removeFromCart(productId) {
            if (!confirm('Remove this item from your cart?')) {
                return;
            }
            
            fetch('${pageContext.request.contextPath}/cart/remove', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'productId=' + productId
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    location.reload(); // Refresh to remove item
                } else {
                    showMessage(data.message, 'error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showMessage('Error removing item', 'error');
            });
        }
        
        function clearCart() {
            if (!confirm('Remove all items from your cart?')) {
                return;
            }
            
            fetch('${pageContext.request.contextPath}/cart/clear', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                }
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    location.reload();
                } else {
                    showMessage(data.message, 'error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showMessage('Error clearing cart', 'error');
            });
        }
        
        function clearInactiveItems() {
            fetch('${pageContext.request.contextPath}/cart/clear-inactive', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                }
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    location.reload();
                } else {
                    showMessage(data.message, 'error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showMessage('Error removing inactive items', 'error');
            });
        }
        
        function proceedToCheckout() {
            window.location.href = '${pageContext.request.contextPath}/checkout/';
        }
        
        function showMessage(message, type) {
            const messagesDiv = document.getElementById('cart-messages');
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