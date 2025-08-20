<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>eBay Marketplace - Buy & Sell Online</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: Arial, sans-serif;
            background: #f5f5f5;
        }
        
        /* Header Styles */
        .header {
            background: white;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        
        .top-nav {
            background: #f7f7f7;
            border-bottom: 1px solid #ddd;
            padding: 8px 0;
        }
        
        .top-nav .container {
            max-width: 1200px;
            margin: 0 auto;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 0 20px;
        }
        
        .nav-left, .nav-right {
            display: flex;
            gap: 20px;
            font-size: 13px;
        }
        
        .nav-left a, .nav-right a {
            color: #666;
            text-decoration: none;
        }
        
        .nav-left a:hover, .nav-right a:hover {
            color: #0073e6;
        }
        
        .main-nav {
            padding: 15px 0;
        }
        
        .main-nav .container {
            max-width: 1200px;
            margin: 0 auto;
            display: flex;
            align-items: center;
            gap: 30px;
            padding: 0 20px;
        }
        
        .logo {
            font-size: 36px;
            font-weight: bold;
            color: #e53238;
            text-decoration: none;
        }
        
        .search-bar {
            flex: 1;
            display: flex;
            max-width: 500px;
        }
        
        .search-bar input {
            flex: 1;
            padding: 12px;
            border: 2px solid #0073e6;
            border-right: none;
            font-size: 16px;
        }
        
        .search-bar button {
            background: #0073e6;
            color: white;
            border: 2px solid #0073e6;
            padding: 12px 20px;
            cursor: pointer;
            font-size: 16px;
        }
        
        .search-bar button:hover {
            background: #005bb5;
        }
        
        .user-menu {
            display: flex;
            align-items: center;
            gap: 15px;
        }
        
        .btn {
            background: #0073e6;
            color: white;
            padding: 8px 16px;
            border: none;
            border-radius: 4px;
            text-decoration: none;
            font-size: 14px;
            cursor: pointer;
            transition: background 0.3s;
        }
        
        .btn:hover {
            background: #005bb5;
        }
        
        .btn-outline {
            background: transparent;
            color: #0073e6;
            border: 1px solid #0073e6;
        }
        
        .btn-outline:hover {
            background: #0073e6;
            color: white;
        }
        
        /* Hero Section */
        .hero {
            background: linear-gradient(135deg, #0073e6, #005bb5);
            color: white;
            padding: 60px 0;
            text-align: center;
        }
        
        .hero h1 {
            font-size: 48px;
            margin-bottom: 20px;
        }
        
        .hero p {
            font-size: 20px;
            margin-bottom: 30px;
        }
        
        .hero-buttons {
            display: flex;
            gap: 20px;
            justify-content: center;
        }
        
        .hero-buttons .btn {
            font-size: 18px;
            padding: 15px 30px;
        }
        
        /* Categories Section */
        .categories {
            padding: 60px 0;
            background: white;
        }
        
        .categories h2 {
            text-align: center;
            margin-bottom: 40px;
            font-size: 32px;
            color: #333;
        }
        
        .category-grid {
            max-width: 1200px;
            margin: 0 auto;
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 30px;
            padding: 0 20px;
        }
        
        .category-card {
            background: #f8f9fa;
            padding: 30px;
            border-radius: 10px;
            text-align: center;
            text-decoration: none;
            color: #333;
            transition: transform 0.3s, box-shadow 0.3s;
        }
        
        .category-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
            color: #0073e6;
        }
        
        .category-icon {
            font-size: 48px;
            margin-bottom: 15px;
        }
        
        .category-card h3 {
            font-size: 18px;
            margin-bottom: 10px;
        }
        
        .category-card p {
            font-size: 14px;
            color: #666;
        }
        
        /* Features Section */
        .features {
            padding: 60px 0;
            background: #f8f9fa;
        }
        
        .features h2 {
            text-align: center;
            margin-bottom: 40px;
            font-size: 32px;
            color: #333;
        }
        
        .features-grid {
            max-width: 1200px;
            margin: 0 auto;
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 40px;
            padding: 0 20px;
        }
        
        .feature-card {
            text-align: center;
        }
        
        .feature-icon {
            font-size: 64px;
            color: #0073e6;
            margin-bottom: 20px;
        }
        
        .feature-card h3 {
            font-size: 24px;
            margin-bottom: 15px;
            color: #333;
        }
        
        .feature-card p {
            color: #666;
            line-height: 1.6;
        }
        
        /* Footer */
        .footer {
            background: #333;
            color: white;
            padding: 40px 0 20px;
            text-align: center;
        }
        
        .footer-links {
            display: flex;
            justify-content: center;
            gap: 30px;
            margin-bottom: 20px;
        }
        
        .footer-links a {
            color: white;
            text-decoration: none;
        }
        
        .footer-links a:hover {
            color: #0073e6;
        }
        
        .welcome-message {
            background: #d4edda;
            color: #155724;
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 5px;
            text-align: center;
        }
    </style>
</head>
<body>
    <!-- Header -->
    <header class="header">
        <!-- Top Navigation -->
        <div class="top-nav">
            <div class="container">
                <div class="nav-left">
                    <c:choose>
                        <c:when test="${not empty currentUser}">
                            <span>Hi <strong>${currentUser.firstName}!</strong></span>
                        </c:when>
                        <c:otherwise>
                            <span>Welcome to eBay!</span>
                        </c:otherwise>
                    </c:choose>
                    <a href="#">Daily Deals</a>
                    <a href="#">Brand Outlet</a>
                    <a href="#">Help & Contact</a>
                </div>
                <div class="nav-right">
                    <c:choose>
                        <c:when test="${not empty currentUser}">
                            <a href="#">Sell</a>
                            <a href="#">My eBay</a>
                            <a href="/eBay/user/profile">My Account</a>
                            <a href="/eBay/user/logout">Sign out</a>
                        </c:when>
                        <c:otherwise>
                            <a href="/eBay/user/register">Register</a>
                            <a href="/eBay/user/login">Sign in</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
        
        <!-- Main Navigation -->
        <div class="main-nav">
            <div class="container">
                <a href="/eBay/" class="logo">eBay</a>
                
                <div class="search-bar">
                    <input type="text" placeholder="Search for anything" id="searchInput">
                    <button type="button" onclick="performSearch()">Search</button>
                </div>
                
                <div class="user-menu">
                    <c:choose>
                        <c:when test="${not empty currentUser}">
                            <c:if test="${currentUser.userType == 'SELLER' || currentUser.userType == 'BOTH'}">
                                <a href="#" class="btn" onclick="alert('Feature coming soon!')">List an item</a>
                            </c:if>
                            <a href="#" onclick="alert('Feature coming soon!')">🛒 Cart</a>
                        </c:when>
                        <c:otherwise>
                            <a href="/eBay/user/login" class="btn btn-outline">Sign in</a>
                            <a href="/eBay/user/register" class="btn">Register</a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </header>
    
    <!-- Hero Section -->
    <section class="hero">
        <div class="container">
            <h1>Welcome to eBay Marketplace</h1>
            <p>Buy, sell, and discover amazing deals on millions of items</p>
            <div class="hero-buttons">
                <c:choose>
                    <c:when test="${not empty currentUser}">
                        <a href="#" class="btn" onclick="alert('Feature coming soon!')">Start Selling</a>
                        <a href="#" class="btn btn-outline" onclick="alert('Feature coming soon!')">Browse Categories</a>
                    </c:when>
                    <c:otherwise>
                        <a href="/eBay/user/register" class="btn">Get Started</a>
                        <a href="#categories" class="btn btn-outline">Browse Categories</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </section>
    
    <!-- Categories Section -->
    <section class="categories" id="categories">
        <div class="container">
            <h2>Shop by Category</h2>
            <div class="category-grid">
                <a href="#" class="category-card" onclick="alert('Feature coming soon!')">
                    <div class="category-icon">📱</div>
                    <h3>Electronics</h3>
                    <p>Phones, computers, and gadgets</p>
                </a>
                <a href="#" class="category-card" onclick="alert('Feature coming soon!')">
                    <div class="category-icon">👕</div>
                    <h3>Fashion</h3>
                    <p>Clothing, shoes, and accessories</p>
                </a>
                <a href="#" class="category-card" onclick="alert('Feature coming soon!')">
                    <div class="category-icon">🏠</div>
                    <h3>Home & Garden</h3>
                    <p>Furniture, decor, and tools</p>
                </a>
                <a href="#" class="category-card" onclick="alert('Feature coming soon!')">
                    <div class="category-icon">⚽</div>
                    <h3>Sports</h3>
                    <p>Sports equipment and outdoor gear</p>
                </a>
                <a href="#" class="category-card" onclick="alert('Feature coming soon!')">
                    <div class="category-icon">🚗</div>
                    <h3>Automotive</h3>
                    <p>Car parts and accessories</p>
                </a>
                <a href="#" class="category-card" onclick="alert('Feature coming soon!')">
                    <div class="category-icon">📚</div>
                    <h3>Books & Media</h3>
                    <p>Books, movies, and music</p>
                </a>
            </div>
        </div>
    </section>
    
    <!-- Features Section -->
    <section class="features">
        <div class="container">
            <h2>Why Choose eBay?</h2>
            <div class="features-grid">
                <div class="feature-card">
                    <div class="feature-icon">🔒</div>
                    <h3>Secure Trading</h3>
                    <p>Shop with confidence using our secure payment system and buyer protection.</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">🌍</div>
                    <h3>Global Marketplace</h3>
                    <p>Connect with buyers and sellers from around the world.</p>
                </div>
                <div class="feature-card">
                    <div class="feature-icon">⚡</div>
                    <h3>Fast & Easy</h3>
                    <p>List items quickly and find what you're looking for with our powerful search.</p>
                </div>
            </div>
        </div>
    </section>
    
    <!-- Footer -->
    <footer class="footer">
        <div class="container">
            <div class="footer-links">
                <a href="#">About eBay</a>
                <a href="#">Privacy Policy</a>
                <a href="#">Terms of Service</a>
                <a href="#">Help & Contact</a>
            </div>
            <p>&copy; 2024 eBay Marketplace. All rights reserved.</p>
        </div>
    </footer>
    
    <script>
        function performSearch() {
            const searchTerm = document.getElementById('searchInput').value;
            if (searchTerm.trim()) {
                alert('Search functionality coming soon! You searched for: ' + searchTerm);
            } else {
                alert('Please enter a search term');
            }
        }
        
        // Allow search on Enter key
        document.getElementById('searchInput').addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                performSearch();
            }
        });
    </script>
</body>
</html>