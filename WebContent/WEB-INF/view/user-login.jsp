<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - eBay Marketplace</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
    <style>
        .login-container {
            max-width: 400px;
            margin: 100px auto;
            padding: 40px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
        }
        
        .logo {
            text-align: center;
            margin-bottom: 30px;
        }
        
        .logo h1 {
            color: #0073e6;
            font-size: 36px;
            margin: 0;
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
        
        .form-group input {
            width: 100%;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 5px;
            font-size: 16px;
            transition: border-color 0.3s;
        }
        
        .form-group input:focus {
            outline: none;
            border-color: #0073e6;
        }
        
        .btn {
            width: 100%;
            background: #0073e6;
            color: white;
            padding: 12px;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            transition: background 0.3s;
            margin-bottom: 15px;
        }
        
        .btn:hover {
            background: #005bb5;
        }
        
        .btn-secondary {
            background: #28a745;
        }
        
        .btn-secondary:hover {
            background: #218838;
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
        
        .links {
            text-align: center;
            margin-top: 20px;
        }
        
        .links a {
            color: #0073e6;
            text-decoration: none;
            margin: 0 10px;
        }
        
        .links a:hover {
            text-decoration: underline;
        }
        
        .divider {
            text-align: center;
            margin: 20px 0;
            color: #666;
        }
        
        .demo-credentials {
            background: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 5px;
            padding: 15px;
            margin-bottom: 20px;
            font-size: 14px;
        }
        
        .demo-credentials h4 {
            margin: 0 0 10px 0;
            color: #495057;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="login-container">
            <div class="logo">
                <h1>eBay</h1>
                <p style="color: #666; margin: 0;">Marketplace</p>
            </div>
            
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
            
            <!-- Demo Credentials -->
            <div class="demo-credentials">
                <h4>Demo Login:</h4>
                <strong>Username:</strong> admin<br>
                <strong>Password:</strong> admin123
            </div>
            
            <form method="POST" action="/eBay/user/login">
                <div class="form-group">
                    <label for="usernameOrEmail">Username or Email</label>
                    <input type="text" id="usernameOrEmail" name="usernameOrEmail" 
                           required placeholder="Enter your username or email">
                </div>
                
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" 
                           required placeholder="Enter your password">
                </div>
                
                <button type="submit" class="btn">Sign In</button>
                
                <div class="divider">
                    <span>Don't have an account?</span>
                </div>
                
                <a href="/eBay/user/register" class="btn btn-secondary" style="text-decoration: none; display: block; text-align: center;">
                    Create New Account
                </a>
            </form>
            
            <div class="links">
                <a href="/eBay/">← Back to Home</a>
                <a href="#" onclick="alert('Feature coming soon!')">Forgot Password?</a>
            </div>
        </div>
    </div>
    
    <script>
        // Auto-fill demo credentials for testing
        document.addEventListener('DOMContentLoaded', function() {
            const urlParams = new URLSearchParams(window.location.search);
            if (urlParams.get('demo') === 'true') {
                document.getElementById('usernameOrEmail').value = 'admin';
                document.getElementById('password').value = 'admin123';
            }
        });
        
        // Add demo login button functionality
        function fillDemoCredentials() {
            document.getElementById('usernameOrEmail').value = 'admin';
            document.getElementById('password').value = 'admin123';
        }
        
        // Add click handler to demo credentials
        document.querySelector('.demo-credentials').addEventListener('click', fillDemoCredentials);
        document.querySelector('.demo-credentials').style.cursor = 'pointer';
        document.querySelector('.demo-credentials').title = 'Click to auto-fill credentials';
    </script>
</body>
</html>