<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Secure Payment - eBay</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <script src="https://js.stripe.com/v3/"></script>
    <style>
        .payment-container {
            max-width: 900px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .payment-header {
            text-align: center;
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 2px solid #e5e5e5;
        }
        
        .security-badge {
            display: inline-flex;
            align-items: center;
            background: #d4edda;
            color: #155724;
            padding: 10px 15px;
            border-radius: 20px;
            font-size: 14px;
            margin-bottom: 20px;
        }
        
        .security-badge i {
            margin-right: 8px;
        }
        
        .order-summary {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 30px;
            border: 1px solid #dee2e6;
        }
        
        .summary-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
        }
        
        .total-amount {
            font-size: 24px;
            font-weight: bold;
            color: #B12704;
            text-align: center;
            margin: 20px 0;
            padding: 15px;
            background: white;
            border-radius: 8px;
            border: 2px solid #B12704;
        }
        
        .payment-methods {
            display: grid;
            gap: 20px;
        }
        
        .payment-method-card {
            background: white;
            border: 2px solid #e9ecef;
            border-radius: 12px;
            padding: 20px;
            cursor: pointer;
            transition: all 0.3s ease;
        }
        
        .payment-method-card:hover {
            border-color: #0064d2;
            box-shadow: 0 4px 12px rgba(0,100,210,0.15);
        }
        
        .payment-method-card.selected {
            border-color: #0064d2;
            background: #f8f9ff;
            box-shadow: 0 4px 12px rgba(0,100,210,0.15);
        }
        
        .method-header {
            display: flex;
            align-items: center;
            margin-bottom: 15px;
        }
        
        .method-radio {
            margin-right: 15px;
            transform: scale(1.2);
        }
        
        .method-icon {
            font-size: 24px;
            margin-right: 10px;
        }
        
        .method-title {
            font-size: 18px;
            font-weight: 600;
            flex: 1;
        }
        
        .method-description {
            color: #666;
            font-size: 14px;
            margin-bottom: 15px;
        }
        
        .method-form {
            display: none;
        }
        
        .method-form.active {
            display: block;
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
            font-weight: 600;
            color: #333;
        }
        
        .form-group input,
        .form-group select {
            width: 100%;
            padding: 12px 15px;
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 16px;
            transition: border-color 0.3s;
        }
        
        .form-group input:focus,
        .form-group select:focus {
            border-color: #0064d2;
            outline: none;
            box-shadow: 0 0 0 3px rgba(0,100,210,0.1);
        }
        
        .stripe-element {
            padding: 12px 15px;
            border: 1px solid #ccc;
            border-radius: 6px;
            background: white;
        }
        
        .stripe-element.StripeElement--focus {
            border-color: #0064d2;
            box-shadow: 0 0 0 3px rgba(0,100,210,0.1);
        }
        
        .stripe-element.StripeElement--invalid {
            border-color: #dc3545;
        }
        
        .paypal-info {
            background: #fff3cd;
            border: 1px solid #ffeaa7;
            padding: 15px;
            border-radius: 6px;
            text-align: center;
        }
        
        .security-features {
            background: #e9f7ef;
            border: 1px solid #c3e6cb;
            padding: 15px;
            border-radius: 6px;
            margin: 20px 0;
        }
        
        .security-features h4 {
            margin-bottom: 10px;
            color: #155724;
        }
        
        .security-features ul {
            margin: 0;
            padding-left: 20px;
            color: #155724;
        }
        
        .pay-button {
            width: 100%;
            background: linear-gradient(135deg, #ff9900, #e88900);
            color: white;
            border: none;
            padding: 18px;
            font-size: 18px;
            font-weight: 600;
            border-radius: 8px;
            cursor: pointer;
            margin-top: 20px;
            transition: all 0.3s ease;
        }
        
        .pay-button:hover {
            background: linear-gradient(135deg, #e88900, #d17d00);
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(255,153,0,0.3);
        }
        
        .pay-button:disabled {
            background: #ccc;
            cursor: not-allowed;
            transform: none;
            box-shadow: none;
        }
        
        .processing-overlay {
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.8);
            display: none;
            justify-content: center;
            align-items: center;
            z-index: 10000;
        }
        
        .processing-content {
            background: white;
            padding: 40px;
            border-radius: 12px;
            text-align: center;
            max-width: 400px;
            width: 90%;
        }
        
        .spinner {
            border: 4px solid #f3f3f3;
            border-top: 4px solid #ff9900;
            border-radius: 50%;
            width: 50px;
            height: 50px;
            animation: spin 1s linear infinite;
            margin: 0 auto 20px;
        }
        
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
        
        .alert {
            padding: 15px;
            margin: 15px 0;
            border-radius: 6px;
            border: 1px solid transparent;
        }
        
        .alert-success {
            background: #d4edda;
            color: #155724;
            border-color: #c3e6cb;
        }
        
        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border-color: #f5c6cb;
        }
        
        .alert-warning {
            background: #fff3cd;
            color: #856404;
            border-color: #ffeaa7;
        }
        
        .trust-badges {
            display: flex;
            justify-content: center;
            gap: 20px;
            margin: 20px 0;
            flex-wrap: wrap;
        }
        
        .trust-badge {
            padding: 8px 12px;
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 6px;
            font-size: 12px;
            color: #666;
        }
        
        @media (max-width: 768px) {
            .form-row {
                flex-direction: column;
            }
            
            .payment-methods {
                gap: 15px;
            }
            
            .trust-badges {
                gap: 10px;
            }
        }
    </style>
</head>
<body>
    <jsp:include page="header.jsp"/>
    
    <div class="payment-container">
        <div class="payment-header">
            <div class="security-badge">
                🔒 SSL Encrypted Secure Payment
            </div>
            <h1>Complete Your Payment</h1>
            <p>Order #${order.orderId} • <fmt:formatDate value="${order.orderDate}" pattern="MMM dd, yyyy"/></p>
        </div>
        
        <div class="order-summary">
            <h3>Order Summary</h3>
            <div class="summary-row">
                <span>Items (${order.orderItems.size()})</span>
                <span>$<fmt:formatNumber value="${order.totalAmount - order.shippingCost}" type="number" maxFractionDigits="2"/></span>
            </div>
            <div class="summary-row">
                <span>Shipping & Handling</span>
                <span>$<fmt:formatNumber value="${order.shippingCost}" type="number" maxFractionDigits="2"/></span>
            </div>
            <div class="summary-row">
                <span>Tax</span>
                <span>$0.00</span>
            </div>
            <hr style="margin: 15px 0;">
            <div class="total-amount">
                Total: $<fmt:formatNumber value="${order.totalAmount}" type="number" maxFractionDigits="2"/>
            </div>
        </div>
        
        <div id="payment-messages"></div>
        
        <div class="payment-methods">
            <!-- Credit/Debit Card with Stripe -->
            <div class="payment-method-card" onclick="selectPaymentMethod('STRIPE')" id="stripe-card">
                <div class="method-header">
                    <input type="radio" name="paymentMethod" value="STRIPE" class="method-radio" id="stripe-radio">
                    <div class="method-icon">💳</div>
                    <div class="method-title">Credit or Debit Card</div>
                </div>
                <div class="method-description">
                    Securely processed by Stripe. We accept Visa, Mastercard, American Express, and Discover.
                </div>
                <div class="method-form" id="stripe-form">
                    <div class="form-group">
                        <label>Card Information</label>
                        <div id="stripe-card-element" class="stripe-element">
                            <!-- Stripe Elements will create form elements here -->
                        </div>
                        <div id="stripe-card-errors" class="alert alert-error" style="display: none;"></div>
                    </div>
                    <div class="form-group">
                        <label for="stripe-cardholder-name">Cardholder Name</label>
                        <input type="text" id="stripe-cardholder-name" value="${order.buyer.firstName} ${order.buyer.lastName}">
                    </div>
                </div>
            </div>
            
            <!-- PayPal -->
            <div class="payment-method-card" onclick="selectPaymentMethod('PAYPAL')" id="paypal-card">
                <div class="method-header">
                    <input type="radio" name="paymentMethod" value="PAYPAL" class="method-radio" id="paypal-radio">
                    <div class="method-icon">🔵</div>
                    <div class="method-title">PayPal</div>
                </div>
                <div class="method-description">
                    Pay with your PayPal account or credit card through PayPal's secure checkout.
                </div>
                <div class="method-form" id="paypal-form">
                    <div class="paypal-info">
                        <p><strong>You'll be redirected to PayPal to complete your payment.</strong></p>
                        <p>After completing payment, you'll return to eBay for order confirmation.</p>
                    </div>
                    <div class="form-group">
                        <label for="paypal-email">PayPal Email (Optional)</label>
                        <input type="email" id="paypal-email" placeholder="your@email.com">
                    </div>
                </div>
            </div>
            
            <!-- Traditional Credit Card Form (Fallback) -->
            <div class="payment-method-card" onclick="selectPaymentMethod('CREDIT_CARD')" id="creditcard-card">
                <div class="method-header">
                    <input type="radio" name="paymentMethod" value="CREDIT_CARD" class="method-radio" id="creditcard-radio">
                    <div class="method-icon">💳</div>
                    <div class="method-title">Credit Card (Direct)</div>
                </div>
                <div class="method-description">
                    Enter your card details directly. All information is encrypted and secure.
                </div>
                <div class="method-form" id="creditcard-form">
                    <div class="form-group">
                        <label for="card-number">Card Number</label>
                        <input type="text" id="card-number" placeholder="1234 5678 9012 3456" maxlength="19">
                    </div>
                    <div class="form-group">
                        <label for="cardholder-name">Cardholder Name</label>
                        <input type="text" id="cardholder-name" value="${order.buyer.firstName} ${order.buyer.lastName}">
                    </div>
                    <div class="form-row">
                        <div class="form-group">
                            <label for="expiry-date">Expiry Date</label>
                            <input type="text" id="expiry-date" placeholder="MM/YY" maxlength="5">
                        </div>
                        <div class="form-group">
                            <label for="cvv">CVV</label>
                            <input type="text" id="cvv" placeholder="123" maxlength="4">
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="security-features">
            <h4>🛡️ Your Payment is Protected</h4>
            <ul>
                <li>256-bit SSL encryption protects your data</li>
                <li>PCI DSS compliant payment processing</li>
                <li>Fraud monitoring and detection</li>
                <li>Your card details are never stored</li>
            </ul>
        </div>
        
        <div class="trust-badges">
            <div class="trust-badge">🔒 SSL Secured</div>
            <div class="trust-badge">💳 PCI Compliant</div>
            <div class="trust-badge">🛡️ Fraud Protected</div>
            <div class="trust-badge">⚡ Instant Processing</div>
        </div>
        
        <button type="button" id="pay-button" class="pay-button" onclick="processPayment()">
            Pay Now - $<fmt:formatNumber value="${order.totalAmount}" type="number" maxFractionDigits="2"/>
        </button>
    </div>
    
    <!-- Processing Overlay -->
    <div id="processing-overlay" class="processing-overlay">
        <div class="processing-content">
            <div class="spinner"></div>
            <h3 id="processing-title">Processing Payment...</h3>
            <p id="processing-message">Please wait while we process your payment securely.</p>
            <p style="color: #666; font-size: 14px;">Do not close this window or press the back button.</p>
        </div>
    </div>
    
    <jsp:include page="footer.jsp"/>
    
    <script>
        let selectedPaymentMethod = null;
        let stripe = null;
        let stripeElements = null;
        let stripeCard = null;
        
        // Initialize Stripe
        function initializeStripe() {
            // In production, get this from your server or config
            stripe = Stripe('pk_test_example'); // Replace with actual publishable key
            stripeElements = stripe.elements();
            
            // Create card element
            stripeCard = stripeElements.create('card', {
                style: {
                    base: {
                        fontSize: '16px',
                        color: '#424770',
                        '::placeholder': {
                            color: '#aab7c4',
                        },
                    },
                },
            });
            
            stripeCard.mount('#stripe-card-element');
            
            // Handle real-time validation errors from the card Element
            stripeCard.on('change', function(event) {
                const displayError = document.getElementById('stripe-card-errors');
                if (event.error) {
                    displayError.textContent = event.error.message;
                    displayError.style.display = 'block';
                } else {
                    displayError.textContent = '';
                    displayError.style.display = 'none';
                }
            });
        }
        
        // Initialize on page load
        document.addEventListener('DOMContentLoaded', function() {
            initializeStripe();
            selectPaymentMethod('STRIPE'); // Default selection
        });
        
        function selectPaymentMethod(method) {
            selectedPaymentMethod = method;
            
            // Remove selected class from all cards
            document.querySelectorAll('.payment-method-card').forEach(card => {
                card.classList.remove('selected');
            });
            
            // Hide all forms
            document.querySelectorAll('.method-form').forEach(form => {
                form.classList.remove('active');
            });
            
            // Uncheck all radios
            document.querySelectorAll('input[name="paymentMethod"]').forEach(radio => {
                radio.checked = false;
            });
            
            // Select the chosen method
            const selectedCard = document.getElementById(method.toLowerCase() + '-card');
            const selectedRadio = document.getElementById(method.toLowerCase() + '-radio');
            const selectedForm = document.getElementById(method.toLowerCase() + '-form');
            
            if (selectedCard) selectedCard.classList.add('selected');
            if (selectedRadio) selectedRadio.checked = true;
            if (selectedForm) selectedForm.classList.add('active');
        }
        
        async function processPayment() {
            if (!selectedPaymentMethod) {
                showMessage('Please select a payment method', 'error');
                return;
            }
            
            showProcessingOverlay('Processing Payment...', 'Securely processing your payment...');
            document.getElementById('pay-button').disabled = true;
            
            try {
                let paymentData = {
                    orderId: ${order.orderId},
                    paymentMethod: selectedPaymentMethod
                };
                
                if (selectedPaymentMethod === 'STRIPE') {
                    const result = await processStripePayment(paymentData);
                    if (!result.success) {
                        throw new Error(result.message);
                    }
                } else if (selectedPaymentMethod === 'PAYPAL') {
                    const result = await processPayPalPayment(paymentData);
                    if (!result.success) {
                        throw new Error(result.message);
                    }
                } else if (selectedPaymentMethod === 'CREDIT_CARD') {
                    const result = await processCreditCardPayment(paymentData);
                    if (!result.success) {
                        throw new Error(result.message);
                    }
                }
                
            } catch (error) {
                hideProcessingOverlay();
                document.getElementById('pay-button').disabled = false;
                showMessage(error.message, 'error');
            }
        }
        
        async function processStripePayment(paymentData) {
            try {
                // Create payment method
                const {paymentMethod, error} = await stripe.createPaymentMethod({
                    type: 'card',
                    card: stripeCard,
                    billing_details: {
                        name: document.getElementById('stripe-cardholder-name').value,
                    },
                });
                
                if (error) {
                    throw new Error(error.message);
                }
                
                // Send to server
                paymentData.stripePaymentMethodId = paymentMethod.id;
                
                const response = await fetch('/eBay/payment/process', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams(paymentData)
                });
                
                const result = await response.json();
                
                if (result.success) {
                    showMessage('Payment successful!', 'success');
                    setTimeout(() => {
                        window.location.href = result.redirect_url;
                    }, 2000);
                }
                
                return result;
                
            } catch (error) {
                throw error;
            }
        }
        
        async function processPayPalPayment(paymentData) {
            try {
                paymentData.paypalEmail = document.getElementById('paypal-email').value;
                
                const response = await fetch('/eBay/payment/process', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams(paymentData)
                });
                
                const result = await response.json();
                
                if (result.success && result.redirect_type === 'external') {
                    window.location.href = result.redirect_url;
                }
                
                return result;
                
            } catch (error) {
                throw error;
            }
        }
        
        async function processCreditCardPayment(paymentData) {
            try {
                // Validate form
                const cardNumber = document.getElementById('card-number').value;
                const expiryDate = document.getElementById('expiry-date').value;
                const cvv = document.getElementById('cvv').value;
                const cardHolderName = document.getElementById('cardholder-name').value;
                
                if (!cardNumber || !expiryDate || !cvv || !cardHolderName) {
                    throw new Error('Please fill in all card details');
                }
                
                paymentData.cardNumber = cardNumber;
                paymentData.expiryDate = expiryDate;
                paymentData.cvv = cvv;
                paymentData.cardHolderName = cardHolderName;
                
                const response = await fetch('/eBay/payment/process', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: new URLSearchParams(paymentData)
                });
                
                const result = await response.json();
                
                if (result.success) {
                    showMessage('Payment successful!', 'success');
                    setTimeout(() => {
                        window.location.href = result.redirect_url;
                    }, 2000);
                }
                
                return result;
                
            } catch (error) {
                throw error;
            }
        }
        
        function showProcessingOverlay(title, message) {
            document.getElementById('processing-title').textContent = title;
            document.getElementById('processing-message').textContent = message;
            document.getElementById('processing-overlay').style.display = 'flex';
        }
        
        function hideProcessingOverlay() {
            document.getElementById('processing-overlay').style.display = 'none';
        }
        
        function showMessage(message, type) {
            const messagesDiv = document.getElementById('payment-messages');
            const alertDiv = document.createElement('div');
            alertDiv.className = 'alert alert-' + type;
            alertDiv.textContent = message;
            messagesDiv.innerHTML = '';
            messagesDiv.appendChild(alertDiv);
            
            // Scroll to message
            alertDiv.scrollIntoView({ behavior: 'smooth', block: 'center' });
            
            // Auto-hide success messages
            if (type === 'success') {
                setTimeout(() => {
                    alertDiv.remove();
                }, 5000);
            }
        }
        
        // Format card number input
        document.getElementById('card-number')?.addEventListener('input', function(e) {
            let value = e.target.value.replace(/\s/g, '').replace(/[^0-9]/gi, '');
            let formattedValue = value.match(/.{1,4}/g)?.join(' ') || value;
            if (formattedValue !== e.target.value) {
                e.target.value = formattedValue;
            }
        });
        
        // Format expiry date input
        document.getElementById('expiry-date')?.addEventListener('input', function(e) {
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