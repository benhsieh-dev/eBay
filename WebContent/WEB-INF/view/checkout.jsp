<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Checkout - eBay</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .checkout-container {
            max-width: 1000px;
            margin: 0 auto;
            padding: 20px;
            display: grid;
            grid-template-columns: 2fr 1fr;
            gap: 30px;
        }
        
        .checkout-form {
            background: white;
            padding: 20px;
            border-radius: 4px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .form-section {
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 1px solid #e5e5e5;
        }
        
        .form-section:last-child {
            border-bottom: none;
        }
        
        .section-title {
            font-size: 18px;
            font-weight: bold;
            margin-bottom: 15px;
            color: #333;
        }
        
        .form-row {
            display: flex;
            gap: 15px;
            margin-bottom: 15px;
        }
        
        .form-group {
            flex: 1;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        
        .form-group input,
        .form-group select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 14px;
        }
        
        .form-group input:focus,
        .form-group select:focus {
            border-color: #0654ba;
            outline: none;
        }
        
        .checkbox-group {
            display: flex;
            align-items: center;
            margin: 15px 0;
        }
        
        .checkbox-group input {
            margin-right: 10px;
        }
        
        .order-summary {
            background: #f9f9f9;
            padding: 20px;
            border-radius: 4px;
            position: sticky;
            top: 20px;
            height: fit-content;
        }
        
        .summary-title {
            font-size: 18px;
            font-weight: bold;
            margin-bottom: 15px;
        }
        
        .summary-item {
            display: flex;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid #e5e5e5;
        }
        
        .summary-item:last-child {
            border-bottom: none;
        }
        
        .item-image {
            width: 50px;
            height: 50px;
            object-fit: cover;
            margin-right: 10px;
            border-radius: 4px;
        }
        
        .item-details {
            flex: 1;
            font-size: 14px;
        }
        
        .item-title {
            font-weight: bold;
            margin-bottom: 2px;
        }
        
        .item-quantity {
            color: #666;
        }
        
        .item-price {
            font-weight: bold;
            color: #B12704;
        }
        
        .summary-totals {
            margin-top: 15px;
            padding-top: 15px;
            border-top: 1px solid #ccc;
        }
        
        .total-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 8px;
        }
        
        .total-final {
            font-weight: bold;
            font-size: 18px;
            color: #B12704;
        }
        
        .payment-methods {
            margin-top: 15px;
        }
        
        .payment-option {
            display: flex;
            align-items: center;
            margin-bottom: 10px;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            cursor: pointer;
        }
        
        .payment-option:hover {
            background: #f0f0f0;
        }
        
        .payment-option.selected {
            border-color: #0654ba;
            background: #f0f8ff;
        }
        
        .payment-option input {
            margin-right: 10px;
        }
        
        .place-order-btn {
            width: 100%;
            background: #ff9900;
            color: white;
            border: none;
            padding: 15px;
            font-size: 16px;
            border-radius: 4px;
            cursor: pointer;
            margin-top: 20px;
        }
        
        .place-order-btn:hover {
            background: #e88900;
        }
        
        .place-order-btn:disabled {
            background: #ccc;
            cursor: not-allowed;
        }
        
        .alert {
            padding: 10px;
            margin: 10px 0;
            border-radius: 4px;
        }
        
        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        @media (max-width: 768px) {
            .checkout-container {
                grid-template-columns: 1fr;
                gap: 20px;
            }
            
            .form-row {
                flex-direction: column;
                gap: 10px;
            }
        }
    </style>
</head>
<body>
    <jsp:include page="header.jsp"/>
    
    <div class="checkout-container">
        <div class="checkout-form">
            <h1>Checkout</h1>
            
            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>
            
            <form id="checkout-form" action="${pageContext.request.contextPath}/checkout/process" method="post">
                
                <!-- Shipping Address Section -->
                <div class="form-section">
                    <div class="section-title">Shipping Address</div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="shippingFirstName">First Name *</label>
                            <input type="text" id="shippingFirstName" name="shippingFirstName" 
                                   value="${user.firstName}" required>
                        </div>
                        <div class="form-group">
                            <label for="shippingLastName">Last Name *</label>
                            <input type="text" id="shippingLastName" name="shippingLastName" 
                                   value="${user.lastName}" required>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="shippingAddress1">Address Line 1 *</label>
                        <input type="text" id="shippingAddress1" name="shippingAddress1" 
                               value="${user.address}" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="shippingAddress2">Address Line 2</label>
                        <input type="text" id="shippingAddress2" name="shippingAddress2">
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="shippingCity">City *</label>
                            <input type="text" id="shippingCity" name="shippingCity" 
                                   value="${user.city}" required>
                        </div>
                        <div class="form-group">
                            <label for="shippingState">State *</label>
                            <input type="text" id="shippingState" name="shippingState" 
                                   value="${user.state}" required>
                        </div>
                        <div class="form-group">
                            <label for="shippingZip">ZIP Code *</label>
                            <input type="text" id="shippingZip" name="shippingZip" 
                                   value="${user.zipCode}" required>
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="shippingCountry">Country *</label>
                        <select id="shippingCountry" name="shippingCountry" required>
                            <option value="United States" ${user.country == 'United States' ? 'selected' : ''}>United States</option>
                            <option value="Canada" ${user.country == 'Canada' ? 'selected' : ''}>Canada</option>
                            <option value="United Kingdom" ${user.country == 'United Kingdom' ? 'selected' : ''}>United Kingdom</option>
                            <option value="Australia" ${user.country == 'Australia' ? 'selected' : ''}>Australia</option>
                        </select>
                    </div>
                </div>
                
                <!-- Billing Address Section -->
                <div class="form-section">
                    <div class="section-title">Billing Address</div>
                    
                    <div class="checkbox-group">
                        <input type="checkbox" id="sameAsBilling" name="sameAsBilling" value="true" checked>
                        <label for="sameAsBilling">Same as shipping address</label>
                    </div>
                    
                    <div id="billing-fields" style="display: none;">
                        <div class="form-row">
                            <div class="form-group">
                                <label for="billingFirstName">First Name *</label>
                                <input type="text" id="billingFirstName" name="billingFirstName">
                            </div>
                            <div class="form-group">
                                <label for="billingLastName">Last Name *</label>
                                <input type="text" id="billingLastName" name="billingLastName">
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="billingAddress1">Address Line 1 *</label>
                            <input type="text" id="billingAddress1" name="billingAddress1">
                        </div>
                        
                        <div class="form-group">
                            <label for="billingAddress2">Address Line 2</label>
                            <input type="text" id="billingAddress2" name="billingAddress2">
                        </div>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="billingCity">City *</label>
                                <input type="text" id="billingCity" name="billingCity">
                            </div>
                            <div class="form-group">
                                <label for="billingState">State *</label>
                                <input type="text" id="billingState" name="billingState">
                            </div>
                            <div class="form-group">
                                <label for="billingZip">ZIP Code *</label>
                                <input type="text" id="billingZip" name="billingZip">
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="billingCountry">Country *</label>
                            <select id="billingCountry" name="billingCountry">
                                <option value="United States">United States</option>
                                <option value="Canada">Canada</option>
                                <option value="United Kingdom">United Kingdom</option>
                                <option value="Australia">Australia</option>
                            </select>
                        </div>
                    </div>
                </div>
                
                <!-- Payment Method Section -->
                <div class="form-section">
                    <div class="section-title">Payment Method</div>
                    
                    <div class="payment-methods">
                        <div class="payment-option selected" onclick="selectPaymentMethod('CREDIT_CARD')">
                            <input type="radio" name="paymentMethod" value="CREDIT_CARD" checked>
                            <span>Credit/Debit Card</span>
                        </div>
                        
                        <div class="payment-option" onclick="selectPaymentMethod('PAYPAL')">
                            <input type="radio" name="paymentMethod" value="PAYPAL">
                            <span>PayPal</span>
                        </div>
                        
                        <div class="payment-option" onclick="selectPaymentMethod('BANK_TRANSFER')">
                            <input type="radio" name="paymentMethod" value="BANK_TRANSFER">
                            <span>Bank Transfer</span>
                        </div>
                    </div>
                </div>
                
                <button type="submit" class="place-order-btn">Place Order</button>
            </form>
        </div>
        
        <!-- Order Summary -->
        <div class="order-summary">
            <div class="summary-title">Order Summary</div>
            
            <c:forEach var="item" items="${cartItems}">
                <div class="summary-item">
                    <img src="${pageContext.request.contextPath}/assets/images/products/${item.product.imageUrl != null ? item.product.imageUrl : 'default.jpg'}" 
                         alt="${item.product.title}" class="item-image">
                    <div class="item-details">
                        <div class="item-title">${item.product.title}</div>
                        <div class="item-quantity">Qty: ${item.quantity}</div>
                    </div>
                    <div class="item-price">
                        $<fmt:formatNumber value="${item.product.currentPrice * item.quantity}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                    </div>
                </div>
            </c:forEach>
            
            <div class="summary-totals">
                <div class="total-row">
                    <span>Subtotal:</span>
                    <span>$<fmt:formatNumber value="${cartSummary.subtotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
                </div>
                <div class="total-row">
                    <span>Shipping:</span>
                    <span>$<fmt:formatNumber value="${cartSummary.shippingTotal}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
                </div>
                <div class="total-row">
                    <span>Tax:</span>
                    <span>$0.00</span>
                </div>
                <div class="total-row total-final">
                    <span>Total:</span>
                    <span>$<fmt:formatNumber value="${cartSummary.total}" type="number" minFractionDigits="2" maxFractionDigits="2"/></span>
                </div>
            </div>
        </div>
    </div>
    
    <jsp:include page="footer.jsp"/>
    
    <script>
        // Toggle billing address fields
        document.getElementById('sameAsBilling').addEventListener('change', function() {
            const billingFields = document.getElementById('billing-fields');
            const billingInputs = billingFields.querySelectorAll('input, select');
            
            if (this.checked) {
                billingFields.style.display = 'none';
                billingInputs.forEach(input => {
                    if (input.hasAttribute('required')) {
                        input.removeAttribute('required');
                    }
                });
            } else {
                billingFields.style.display = 'block';
                billingInputs.forEach(input => {
                    if (input.id.includes('billingFirstName') || 
                        input.id.includes('billingLastName') ||
                        input.id.includes('billingAddress1') ||
                        input.id.includes('billingCity') ||
                        input.id.includes('billingState') ||
                        input.id.includes('billingZip') ||
                        input.id.includes('billingCountry')) {
                        input.setAttribute('required', 'required');
                    }
                });
            }
        });
        
        function selectPaymentMethod(method) {
            // Remove selected class from all options
            document.querySelectorAll('.payment-option').forEach(option => {
                option.classList.remove('selected');
            });
            
            // Add selected class to clicked option
            event.currentTarget.classList.add('selected');
            
            // Select the radio button
            document.querySelector('input[value="' + method + '"]').checked = true;
        }
        
        // Form submission handling
        document.getElementById('checkout-form').addEventListener('submit', function(e) {
            const submitBtn = document.querySelector('.place-order-btn');
            submitBtn.disabled = true;
            submitBtn.textContent = 'Processing Order...';
        });
    </script>
</body>
</html>