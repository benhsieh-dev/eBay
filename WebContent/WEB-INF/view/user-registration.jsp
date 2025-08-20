<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Register - eBay Marketplace</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
    <style>
        .registration-container {
            max-width: 600px;
            margin: 50px auto;
            padding: 30px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #333;
        }
        
        .form-group input, .form-group select {
            width: 100%;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 5px;
            font-size: 16px;
            transition: border-color 0.3s;
        }
        
        .form-group input:focus, .form-group select:focus {
            outline: none;
            border-color: #0073e6;
        }
        
        .form-row {
            display: flex;
            gap: 15px;
        }
        
        .form-row .form-group {
            flex: 1;
        }
        
        .btn {
            background: #0073e6;
            color: white;
            padding: 12px 30px;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            transition: background 0.3s;
        }
        
        .btn:hover {
            background: #005bb5;
        }
        
        .btn-secondary {
            background: #6c757d;
        }
        
        .btn-secondary:hover {
            background: #545b62;
        }
        
        .error {
            color: #dc3545;
            font-size: 14px;
            margin-top: 5px;
        }
        
        .success {
            color: #28a745;
            font-size: 14px;
            margin-top: 5px;
        }
        
        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
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
        
        .availability-check {
            font-size: 12px;
            margin-top: 5px;
        }
        
        .available {
            color: #28a745;
        }
        
        .unavailable {
            color: #dc3545;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="registration-container">
            <h2 style="text-align: center; margin-bottom: 30px; color: #333;">Join eBay Marketplace</h2>
            
            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    ${error}
                </div>
            </c:if>
            
            <c:if test="${not empty message}">
                <div class="alert alert-success">
                    ${message}
                </div>
            </c:if>
            
            <form:form method="POST" action="/eBay/user/register" modelAttribute="user">
                
                <!-- Account Information -->
                <h3 style="color: #0073e6; border-bottom: 2px solid #0073e6; padding-bottom: 10px;">Account Information</h3>
                
                <div class="form-group">
                    <label for="username">Username *</label>
                    <form:input path="username" id="username" required="true" 
                               onblur="checkUsernameAvailability()" />
                    <div id="username-availability" class="availability-check"></div>
                </div>
                
                <div class="form-group">
                    <label for="email">Email Address *</label>
                    <form:input path="email" type="email" id="email" required="true" 
                               onblur="checkEmailAvailability()" />
                    <div id="email-availability" class="availability-check"></div>
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="password">Password *</label>
                        <form:password path="passwordHash" id="password" required="true" 
                                      minlength="6" onkeyup="checkPasswordStrength()" />
                        <div id="password-strength" class="availability-check"></div>
                    </div>
                    <div class="form-group">
                        <label for="confirmPassword">Confirm Password *</label>
                        <input type="password" id="confirmPassword" required 
                               onkeyup="checkPasswordMatch()" />
                        <div id="password-match" class="availability-check"></div>
                    </div>
                </div>
                
                <!-- Personal Information -->
                <h3 style="color: #0073e6; border-bottom: 2px solid #0073e6; padding-bottom: 10px; margin-top: 30px;">Personal Information</h3>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="firstName">First Name *</label>
                        <form:input path="firstName" id="firstName" required="true" />
                    </div>
                    <div class="form-group">
                        <label for="lastName">Last Name *</label>
                        <form:input path="lastName" id="lastName" required="true" />
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="phone">Phone Number</label>
                    <form:input path="phone" id="phone" type="tel" />
                </div>
                
                <!-- Address Information -->
                <h3 style="color: #0073e6; border-bottom: 2px solid #0073e6; padding-bottom: 10px; margin-top: 30px;">Address Information</h3>
                
                <div class="form-group">
                    <label for="addressLine1">Address Line 1</label>
                    <form:input path="addressLine1" id="addressLine1" />
                </div>
                
                <div class="form-group">
                    <label for="addressLine2">Address Line 2</label>
                    <form:input path="addressLine2" id="addressLine2" />
                </div>
                
                <div class="form-row">
                    <div class="form-group">
                        <label for="city">City</label>
                        <form:input path="city" id="city" />
                    </div>
                    <div class="form-group">
                        <label for="state">State</label>
                        <form:input path="state" id="state" />
                    </div>
                    <div class="form-group">
                        <label for="zipCode">ZIP Code</label>
                        <form:input path="zipCode" id="zipCode" />
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="country">Country</label>
                    <form:select path="country" id="country">
                        <form:option value="USA">United States</form:option>
                        <form:option value="Canada">Canada</form:option>
                        <form:option value="UK">United Kingdom</form:option>
                        <form:option value="Other">Other</form:option>
                    </form:select>
                </div>
                
                <!-- Account Type -->
                <div class="form-group">
                    <label for="userType">Account Type</label>
                    <form:select path="userType" id="userType">
                        <form:option value="BUYER">Buyer Only</form:option>
                        <form:option value="SELLER">Seller Only</form:option>
                        <form:option value="BOTH" selected="true">Both Buyer & Seller</form:option>
                    </form:select>
                </div>
                
                <div style="text-align: center; margin-top: 30px;">
                    <button type="submit" class="btn" id="registerBtn" disabled>Create Account</button>
                    <a href="/eBay/user/login" class="btn btn-secondary" style="margin-left: 15px; text-decoration: none;">Already have an account?</a>
                </div>
            </form:form>
        </div>
    </div>
    
    <script>
        let usernameAvailable = false;
        let emailAvailable = false;
        let passwordValid = false;
        let passwordMatch = false;
        
        function checkUsernameAvailability() {
            const username = document.getElementById('username').value;
            const availabilityDiv = document.getElementById('username-availability');
            
            if (username.length < 3) {
                availabilityDiv.innerHTML = '<span class="unavailable">Username must be at least 3 characters</span>';
                usernameAvailable = false;
                updateRegisterButton();
                return;
            }
            
            fetch('/eBay/user/check-username?username=' + encodeURIComponent(username))
                .then(response => response.json())
                .then(data => {
                    if (data.available) {
                        availabilityDiv.innerHTML = '<span class="available">✓ Username is available</span>';
                        usernameAvailable = true;
                    } else {
                        availabilityDiv.innerHTML = '<span class="unavailable">✗ Username is already taken</span>';
                        usernameAvailable = false;
                    }
                    updateRegisterButton();
                })
                .catch(error => {
                    console.error('Error checking username:', error);
                });
        }
        
        function checkEmailAvailability() {
            const email = document.getElementById('email').value;
            const availabilityDiv = document.getElementById('email-availability');
            
            if (!email.includes('@')) {
                availabilityDiv.innerHTML = '<span class="unavailable">Please enter a valid email</span>';
                emailAvailable = false;
                updateRegisterButton();
                return;
            }
            
            fetch('/eBay/user/check-email?email=' + encodeURIComponent(email))
                .then(response => response.json())
                .then(data => {
                    if (data.available) {
                        availabilityDiv.innerHTML = '<span class="available">✓ Email is available</span>';
                        emailAvailable = true;
                    } else {
                        availabilityDiv.innerHTML = '<span class="unavailable">✗ Email is already registered</span>';
                        emailAvailable = false;
                    }
                    updateRegisterButton();
                })
                .catch(error => {
                    console.error('Error checking email:', error);
                });
        }
        
        function checkPasswordStrength() {
            const password = document.getElementById('password').value;
            const strengthDiv = document.getElementById('password-strength');
            
            if (password.length < 6) {
                strengthDiv.innerHTML = '<span class="unavailable">Password must be at least 6 characters</span>';
                passwordValid = false;
            } else if (password.length < 8) {
                strengthDiv.innerHTML = '<span style="color: orange;">Password strength: Weak</span>';
                passwordValid = true;
            } else if (password.match(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/)) {
                strengthDiv.innerHTML = '<span class="available">Password strength: Strong</span>';
                passwordValid = true;
            } else {
                strengthDiv.innerHTML = '<span style="color: orange;">Password strength: Medium</span>';
                passwordValid = true;
            }
            
            checkPasswordMatch();
            updateRegisterButton();
        }
        
        function checkPasswordMatch() {
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            const matchDiv = document.getElementById('password-match');
            
            if (confirmPassword.length === 0) {
                matchDiv.innerHTML = '';
                passwordMatch = false;
            } else if (password === confirmPassword) {
                matchDiv.innerHTML = '<span class="available">✓ Passwords match</span>';
                passwordMatch = true;
            } else {
                matchDiv.innerHTML = '<span class="unavailable">✗ Passwords do not match</span>';
                passwordMatch = false;
            }
            
            updateRegisterButton();
        }
        
        function updateRegisterButton() {
            const registerBtn = document.getElementById('registerBtn');
            const canRegister = usernameAvailable && emailAvailable && passwordValid && passwordMatch;
            
            registerBtn.disabled = !canRegister;
            registerBtn.style.opacity = canRegister ? '1' : '0.6';
        }
    </script>
</body>
</html>