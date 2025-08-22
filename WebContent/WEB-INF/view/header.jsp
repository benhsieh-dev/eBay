<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<header class="main-header">
    <div class="top-nav">
        <div class="container">
            <div class="nav-left">
                <a href="/eBay/products">Shop</a>
                <a href="/eBay/seller/dashboard">Sell</a>
                <a href="/eBay/help">Help</a>
            </div>
            <div class="nav-right">
                <c:choose>
                    <c:when test="${currentUser != null}">
                        <div class="user-menu">
                            <div class="notification-icons">
                                <!-- Messages Notification -->
                                <a href="/eBay/messages" class="notification-link" id="messages-notification">
                                    <span class="notification-icon">💬</span>
                                    <span class="notification-badge" id="messages-badge" style="display: none;">0</span>
                                </a>
                                
                                <!-- Watchlist Notification -->
                                <a href="/eBay/watchlist" class="notification-link">
                                    <span class="notification-icon">❤️</span>
                                </a>
                                
                                <!-- Cart Notification -->
                                <a href="/eBay/cart" class="notification-link" id="cart-notification">
                                    <span class="notification-icon">🛒</span>
                                    <span class="notification-badge" id="cart-badge" style="display: none;">0</span>
                                </a>
                            </div>
                            
                            <div class="user-dropdown">
                                <button class="user-toggle" onclick="toggleUserMenu()">
                                    <span class="user-avatar">${currentUser.firstName.substring(0,1)}</span>
                                    <span class="user-name">${currentUser.firstName}</span>
                                    <span class="dropdown-arrow">▼</span>
                                </button>
                                <div class="user-dropdown-menu" id="user-dropdown-menu">
                                    <a href="/eBay/profile">My Profile</a>
                                    <a href="/eBay/orders">My Orders</a>
                                    <a href="/eBay/seller/dashboard">Seller Dashboard</a>
                                    <a href="/eBay/messages">Messages</a>
                                    <a href="/eBay/payment/tracking">Payment History</a>
                                    <hr>
                                    <a href="/eBay/logout">Sign Out</a>
                                </div>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <a href="/eBay/user/login">Sign In</a>
                        <a href="/eBay/user/register">Register</a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    
    <div class="main-nav">
        <div class="container">
            <div class="logo">
                <a href="/eBay/">
                    <img src="/eBay/assets/img/ebay-logo.png" alt="eBay" style="height: 40px;" onerror="this.outerHTML='<span style=\'font-size:24px;font-weight:bold;color:#0064d2;\'>eBay</span>'">
                </a>
            </div>
            
            <div class="search-bar">
                <form action="/eBay/products/search" method="get" class="search-form">
                    <input type="text" name="q" placeholder="Search for anything" class="search-input" value="${param.q}">
                    <select name="category" class="category-select">
                        <option value="">All Categories</option>
                        <option value="1">Electronics</option>
                        <option value="2">Fashion</option>
                        <option value="3">Home & Garden</option>
                        <option value="4">Sports</option>
                        <option value="5">Automotive</option>
                    </select>
                    <button type="submit" class="search-btn">Search</button>
                </form>
            </div>
            
            <div class="nav-actions">
                <c:if test="${currentUser != null}">
                    <a href="/eBay/seller/listings/create" class="sell-btn">List an Item</a>
                </c:if>
            </div>
        </div>
    </div>
</header>

<style>
.main-header {
    background: white;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    position: sticky;
    top: 0;
    z-index: 100;
}

.top-nav {
    background: #f7f7f7;
    border-bottom: 1px solid #e5e5e5;
    padding: 8px 0;
    font-size: 13px;
}

.main-nav {
    padding: 15px 0;
}

.container {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    align-items: center;
    padding: 0 20px;
}

.top-nav .container {
    justify-content: space-between;
    padding: 0 20px;
}

.nav-left, .nav-right {
    display: flex;
    gap: 20px;
    align-items: center;
}

.nav-left a, .nav-right a {
    color: #666;
    text-decoration: none;
    transition: color 0.2s;
}

.nav-left a:hover, .nav-right a:hover {
    color: #0064d2;
}

.user-menu {
    display: flex;
    align-items: center;
    gap: 15px;
}

.notification-icons {
    display: flex;
    gap: 15px;
    align-items: center;
}

.notification-link {
    position: relative;
    display: flex;
    align-items: center;
    text-decoration: none;
    padding: 5px;
    border-radius: 50%;
    transition: background-color 0.2s;
}

.notification-link:hover {
    background: rgba(0,100,210,0.1);
}

.notification-icon {
    font-size: 18px;
}

.notification-badge {
    position: absolute;
    top: -5px;
    right: -5px;
    background: #dc3545;
    color: white;
    border-radius: 10px;
    padding: 2px 6px;
    font-size: 10px;
    font-weight: bold;
    min-width: 16px;
    text-align: center;
}

.user-dropdown {
    position: relative;
}

.user-toggle {
    display: flex;
    align-items: center;
    gap: 8px;
    background: none;
    border: none;
    cursor: pointer;
    padding: 8px 12px;
    border-radius: 20px;
    transition: background-color 0.2s;
}

.user-toggle:hover {
    background: rgba(0,100,210,0.1);
}

.user-avatar {
    width: 24px;
    height: 24px;
    border-radius: 50%;
    background: linear-gradient(45deg, #0064d2, #004494);
    color: white;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    font-weight: bold;
}

.user-name {
    font-size: 13px;
    color: #333;
}

.dropdown-arrow {
    font-size: 10px;
    color: #666;
    transition: transform 0.2s;
}

.user-dropdown.open .dropdown-arrow {
    transform: rotate(180deg);
}

.user-dropdown-menu {
    position: absolute;
    top: 100%;
    right: 0;
    background: white;
    border: 1px solid #e5e5e5;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    padding: 8px 0;
    min-width: 180px;
    display: none;
    z-index: 1000;
}

.user-dropdown.open .user-dropdown-menu {
    display: block;
}

.user-dropdown-menu a {
    display: block;
    padding: 10px 16px;
    color: #333;
    text-decoration: none;
    font-size: 14px;
    transition: background-color 0.2s;
}

.user-dropdown-menu a:hover {
    background: #f8f9fa;
    color: #0064d2;
}

.user-dropdown-menu hr {
    border: none;
    border-top: 1px solid #e5e5e5;
    margin: 8px 0;
}

.logo a {
    text-decoration: none;
}

.search-bar {
    flex: 1;
    max-width: 600px;
    margin: 0 30px;
}

.search-form {
    display: flex;
    border: 2px solid #0064d2;
    border-radius: 4px;
    overflow: hidden;
}

.search-input {
    flex: 1;
    padding: 12px 15px;
    border: none;
    outline: none;
    font-size: 16px;
}

.category-select {
    padding: 12px 15px;
    border: none;
    border-left: 1px solid #e5e5e5;
    background: #f8f9fa;
    outline: none;
    cursor: pointer;
}

.search-btn {
    padding: 12px 20px;
    background: #0064d2;
    color: white;
    border: none;
    cursor: pointer;
    font-weight: bold;
    transition: background-color 0.2s;
}

.search-btn:hover {
    background: #004494;
}

.sell-btn {
    background: #3498db;
    color: white;
    padding: 10px 20px;
    border-radius: 6px;
    text-decoration: none;
    font-weight: bold;
    transition: background-color 0.2s;
}

.sell-btn:hover {
    background: #2980b9;
    text-decoration: none;
    color: white;
}

@media (max-width: 768px) {
    .container {
        flex-direction: column;
        gap: 15px;
    }
    
    .top-nav .container {
        flex-direction: row;
        gap: 0;
    }
    
    .search-bar {
        margin: 0;
        max-width: none;
        width: 100%;
    }
    
    .nav-left, .nav-right {
        gap: 10px;
    }
    
    .user-name {
        display: none;
    }
}
</style>

<script>
// User dropdown functionality
function toggleUserMenu() {
    const dropdown = document.querySelector('.user-dropdown');
    dropdown.classList.toggle('open');
}

// Close dropdown when clicking outside
document.addEventListener('click', function(event) {
    const dropdown = document.querySelector('.user-dropdown');
    const toggle = document.querySelector('.user-toggle');
    
    if (!dropdown.contains(event.target)) {
        dropdown.classList.remove('open');
    }
});

// Load notification counts
document.addEventListener('DOMContentLoaded', function() {
    loadNotificationCounts();
    
    // Refresh notifications every 30 seconds
    setInterval(loadNotificationCounts, 30000);
});

function loadNotificationCounts() {
    // Load messages count
    fetch('/eBay/messages/api/stats')
        .then(response => response.json())
        .then(data => {
            if (data.success && data.unreadMessageCount > 0) {
                const messagesBadge = document.getElementById('messages-badge');
                messagesBadge.textContent = data.unreadMessageCount > 99 ? '99+' : data.unreadMessageCount;
                messagesBadge.style.display = 'block';
            } else {
                document.getElementById('messages-badge').style.display = 'none';
            }
        })
        .catch(error => {
            console.error('Error loading message count:', error);
        });
    
    // Load cart count (if cart API exists)
    fetch('/eBay/cart/api/count')
        .then(response => response.json())
        .then(data => {
            if (data.success && data.count > 0) {
                const cartBadge = document.getElementById('cart-badge');
                cartBadge.textContent = data.count > 99 ? '99+' : data.count;
                cartBadge.style.display = 'block';
            } else {
                document.getElementById('cart-badge').style.display = 'none';
            }
        })
        .catch(error => {
            console.error('Error loading cart count:', error);
        });
}

// Real-time notifications via WebSocket (if enabled)
if (typeof SockJS !== 'undefined' && typeof Stomp !== 'undefined') {
    const socket = new SockJS('/eBay/websocket');
    const stompClient = Stomp.over(socket);
    
    stompClient.connect({}, function(frame) {
        // Subscribe to user-specific notifications
        stompClient.subscribe('/user/queue/notifications', function(message) {
            const notification = JSON.parse(message.body);
            
            if (notification.type === 'new_message') {
                loadNotificationCounts();
                showNotificationToast('New message received');
            }
        });
    });
}

function showNotificationToast(message) {
    // Simple notification toast
    const toast = document.createElement('div');
    toast.className = 'notification-toast';
    toast.textContent = message;
    toast.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: #333;
        color: white;
        padding: 12px 20px;
        border-radius: 6px;
        z-index: 10000;
        font-size: 14px;
    `;
    
    document.body.appendChild(toast);
    
    setTimeout(() => {
        toast.remove();
    }, 3000);
}
</script>