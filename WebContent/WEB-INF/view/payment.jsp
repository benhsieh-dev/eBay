<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payment - eBay</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .payment-container {
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .payment-header {
            text-align: center;
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 2px solid #e5e5e5;
        }
        
        .order-info {
            background: #f9f9f9;
            padding: 20px;
            border-radius: 4px;
            margin-bottom: 30px;
        }
        
        .order-details {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
        }
        
        .order-total {
            font-size: 24px;
            font-weight: bold;
            color: #B12704;
            text-align: center;
            margin: 20px 0;
        }
        
        .payment-section {
            background: white;
            padding: 20px;
            border-radius: 4px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 20px;
        }
        
        .section-title {
            font-size: 18px;
            font-weight: bold;
            margin-bottom: 20px;
            color: #333;
        }
        
        .payment-method-display {
            display: flex;
            align-items: center;
            padding: 15px;
            background: #f0f8ff;
            border: 1px solid #0654ba;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        
        .payment-icon {
            margin-right: 10px;
            font-size: 20px;
        }
        
        .credit-card-form {
            display: grid;
            gap: 15px;
        }
        
        .form-row {
            display: flex;
            gap: 15px;
        }
        
        .form-group {
            flex: 1;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        
        .form-group input {
            width: 100%;
            padding: 12px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 16px;
        }
        
        .form-group input:focus {
            border-color: #0654ba;
            outline: none;
            box-shadow: 0 0 5px rgba(6,84,186,0.3);
        }
        
        .security-info {
            background: #fff3cd;
            border: 1px solid #ffeaa7;
            padding: 15px;
            border-radius: 4px;
            margin: 20px 0;
            font-size: 14px;
        }
        
        .pay-now-btn {
            width: 100%;
            background: #ff9900;
            color: white;
            border: none;
            padding: 15px;
            font-size: 18px;
            border-radius: 4px;
            cursor: pointer;
            margin-top: 20px;
        }
        
        .pay-now-btn:hover {
            background: #e88900;
        }
        
        .pay-now-btn:disabled {
            background: #ccc;
            cursor: not-allowed;
        }
        
        .processing-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.7);
            display: none;
            justify-content: center;
            align-items: center;
            z-index: 1000;
        }
        
        .processing-content {
            background: white;
            padding: 30px;
            border-radius: 8px;
            text-align: center;
        }
        
        .spinner {
            border: 4px solid #f3f3f3;
            border-top: 4px solid #ff9900;
            border-radius: 50%;
            width: 40px;
            height: 40px;
            animation: spin 1s linear infinite;
            margin: 0 auto 20px;
        }
        
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
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
        
        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        @media (max-width: 768px) {
            .form-row {
                flex-direction: column;
            }
            
            .order-details {
                flex-direction: column;
                gap: 5px;
            }
        }
    </style>
</head>
<body>
    <jsp:include page="header.jsp"/>
    
    <div class="payment-container">
        <div class="payment-header">
            <h1>Complete Your Payment</h1>
            <p>Order #${order.orderId}</p>
        </div>
        
        <div class="order-info">
            <div class="order-details">
                <span><strong>Order Date:</strong></span>
                <span><fmt:formatDate value="${order.orderDate}" pattern="MMM dd, yyyy"/></span>
            </div>
            <div class="order-details">
                <span><strong>Items:</strong></span>
                <span>${order.orderItems.size()} item(s)</span>
            </div>
            <div class="order-details">
                <span><strong>Shipping Address:</strong></span>
                <span style="white-space: pre-line;">${order.shippingAddress}</span>
            </div>
            
            <div class="order-total">
                Total: $<fmt:formatNumber value="${order.totalAmount}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
            </div>
        </div>
        
        <div class="payment-section">
            <div class="section-title">Payment Method</div>
            
            <div class="payment-method-display">
                <span class="payment-icon">
                    <c:choose>
                        <c:when test="${order.paymentMethod == 'CREDIT_CARD'}">💳</c:when>
                        <c:when test="${order.paymentMethod == 'PAYPAL'}">🔵</c:when>
                        <c:when test="${order.paymentMethod == 'BANK_TRANSFER'}">🏦</c:when>
                        <c:otherwise>💳</c:otherwise>
                    </c:choose>
                </span>
                <span>
                    <c:choose>
                        <c:when test="${order.paymentMethod == 'CREDIT_CARD'}">Credit/Debit Card</c:when>
                        <c:when test="${order.paymentMethod == 'PAYPAL'}">PayPal</c:when>
                        <c:when test="${order.paymentMethod == 'BANK_TRANSFER'}">Bank Transfer</c:when>
                        <c:otherwise>Credit/Debit Card</c:otherwise>
                    </c:choose>
                </span>
            </div>
            
            <div id="payment-messages"></div>
            
            <c:choose>
                <c:when test="${order.paymentMethod == 'CREDIT_CARD'}">
                    <form id="payment-form" class="credit-card-form">
                        <div class="form-group">
                            <label for="cardNumber">Card Number *</label>
                            <input type="text" id="cardNumber" name="cardNumber" 
                                   placeholder="1234 5678 9012 3456" maxlength="19" required>
                        </div>
                        
                        <div class="form-group">
                            <label for="cardHolderName">Cardholder Name *</label>
                            <input type="text" id="cardHolderName" name="cardHolderName" 
                                   value="${order.buyer.firstName} ${order.buyer.lastName}" required>
                        </div>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="expiryDate">Expiry Date *</label>
                                <input type="text" id="expiryDate" name="expiryDate" 
                                       placeholder="MM/YY" maxlength="5" required>
                            </div>
                            <div class="form-group">
                                <label for="cvv">CVV *</label>
                                <input type="text" id="cvv" name="cvv" 
                                       placeholder="123" maxlength="4" required>
                            </div>
                        </div>
                    </form>
                </c:when>
                
                <c:when test="${order.paymentMethod == 'PAYPAL'}">
                    <div style="text-align: center; padding: 30px;">
                        <p>You will be redirected to PayPal to complete your payment.</p>
                        <p><strong>Amount: $<fmt:formatNumber value="${order.totalAmount}" type="number" minFractionDigits="2" maxFractionDigits="2"/></strong></p>
                    </div>
                </c:when>
                
                <c:when test="${order.paymentMethod == 'BANK_TRANSFER'}">
                    <div style="padding: 20px; background: #f8f9fa; border-radius: 4px;">
                        <p><strong>Bank Transfer Instructions:</strong></p>
                        <p>Account Name: eBay Marketplace Inc.</p>
                        <p>Account Number: 1234567890</p>
                        <p>Routing Number: 987654321</p>
                        <p>Reference: Order #${order.orderId}</p>
                        <p><strong>Amount: $<fmt:formatNumber value="${order.totalAmount}" type="number" minFractionDigits="2" maxFractionDigits="2"/></strong></p>
                    </div>
                </c:when>
            </c:choose>
            
            <div class="security-info">
                🔒 Your payment information is secure and encrypted. We never store your payment details.
            </div>
            
            <button type="button" id="pay-now-btn" class="pay-now-btn" onclick="processPayment()">
                Pay Now - $<fmt:formatNumber value="${order.totalAmount}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
            </button>
        </div>
    </div>
    
    <!-- Processing Overlay -->
    <div id="processing-overlay" class="processing-overlay">
        <div class="processing-content">
            <div class="spinner"></div>
            <h3>Processing Payment...</h3>
            <p>Please do not close this window or press the back button.</p>
        </div>
    </div>
    
    <jsp:include page="footer.jsp"/>
    
    <script>
        // Format card number input
        document.getElementById('cardNumber')?.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\s/g, '').replace(/[^0-9]/gi, '');
            let formattedValue = value.match(/.{1,4}/g)?.join(' ') || value;
            if (formattedValue !== e.target.value) {
                e.target.value = formattedValue;
            }
        });
        
        // Format expiry date input
        document.getElementById('expiryDate')?.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\D/g, '');
            if (value.length >= 2) {
                value = value.substring(0, 2) + '/' + value.substring(2, 4);
            }
            e.target.value = value;
        });
        
        // Format CVV input
        document.getElementById('cvv')?.addEventListener('input', function(e) {
            e.target.value = e.target.value.replace(/\D/g, '');
        });
        
        function processPayment() {
            const paymentMethod = '${order.paymentMethod}';
            const orderId = ${order.orderId};
            
            // Validate form if credit card
            if (paymentMethod === 'CREDIT_CARD') {
                const form = document.getElementById('payment-form');
                if (!form.checkValidity()) {
                    form.reportValidity();
                    return;
                }
                
                // Basic card validation
                const cardNumber = document.getElementById('cardNumber').value.replace(/\s/g, '');
                const expiryDate = document.getElementById('expiryDate').value;
                const cvv = document.getElementById('cvv').value;
                
                if (cardNumber.length < 13) {
                    showMessage('Please enter a valid card number', 'error');
                    return;
                }
                
                if (!/^\d{2}\/\d{2}$/.test(expiryDate)) {
                    showMessage('Please enter a valid expiry date (MM/YY)', 'error');
                    return;
                }
                
                if (cvv.length < 3) {
                    showMessage('Please enter a valid CVV', 'error');
                    return;
                }
            }
            
            // Show processing overlay
            document.getElementById('processing-overlay').style.display = 'flex';
            document.getElementById('pay-now-btn').disabled = true;
            
            // Prepare form data
            const formData = new FormData();
            formData.append('orderId', orderId);
            formData.append('paymentMethod', paymentMethod);
            
            if (paymentMethod === 'CREDIT_CARD') {
                formData.append('cardNumber', document.getElementById('cardNumber').value);
                formData.append('expiryDate', document.getElementById('expiryDate').value);
                formData.append('cvv', document.getElementById('cvv').value);
                formData.append('cardHolderName', document.getElementById('cardHolderName').value);
            }
            
            // Process payment
            fetch('${pageContext.request.contextPath}/checkout/pay', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                document.getElementById('processing-overlay').style.display = 'none';
                document.getElementById('pay-now-btn').disabled = false;
                
                if (data.success) {
                    showMessage(data.message, 'success');
                    
                    // Redirect to confirmation page after 2 seconds
                    setTimeout(() => {
                        window.location.href = data.redirect;
                    }, 2000);
                } else {
                    showMessage(data.message, 'error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                document.getElementById('processing-overlay').style.display = 'none';
                document.getElementById('pay-now-btn').disabled = false;
                showMessage('Payment processing failed. Please try again.', 'error');
            });
        }
        
        function showMessage(message, type) {
            const messagesDiv = document.getElementById('payment-messages');
            const alertDiv = document.createElement('div');
            alertDiv.className = 'alert alert-' + type;
            alertDiv.textContent = message;
            messagesDiv.innerHTML = '';
            messagesDiv.appendChild(alertDiv);
            
            // Auto-hide success messages after 5 seconds
            if (type === 'success') {
                setTimeout(() => {
                    alertDiv.remove();
                }, 5000);
            }
        }
        
        // Prevent form submission on enter
        document.addEventListener('keydown', function(e) {
            if (e.key === 'Enter' && e.target.tagName === 'INPUT') {
                e.preventDefault();
                processPayment();
            }
        });
    </script>
</body>
</html>