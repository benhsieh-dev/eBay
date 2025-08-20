<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile - eBay Marketplace</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
    <style>
        .profile-container {
            max-width: 800px;
            margin: 30px auto;
            background: white;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        
        .profile-header {
            background: linear-gradient(135deg, #0073e6, #005bb5);
            color: white;
            padding: 30px;
            text-align: center;
        }
        
        .profile-avatar {
            width: 80px;
            height: 80px;
            border-radius: 50%;
            background: white;
            color: #0073e6;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 36px;
            font-weight: bold;
            margin: 0 auto 15px;
        }
        
        .profile-content {
            padding: 30px;
        }
        
        .info-section {
            margin-bottom: 30px;
        }
        
        .info-section h3 {
            color: #333;
            border-bottom: 2px solid #0073e6;
            padding-bottom: 10px;
            margin-bottom: 20px;
        }
        
        .info-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
        }
        
        .info-item {
            display: flex;
            flex-direction: column;
        }
        
        .info-label {
            font-weight: bold;
            color: #666;
            font-size: 14px;
            margin-bottom: 5px;
        }
        
        .info-value {
            color: #333;
            font-size: 16px;
        }
        
        .btn {
            background: #0073e6;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 5px;
            text-decoration: none;
            display: inline-block;
            margin-right: 10px;
            margin-bottom: 10px;
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
        
        .btn-danger {
            background: #dc3545;
        }
        
        .btn-danger:hover {
            background: #c82333;
        }
        
        .status-badge {
            padding: 5px 10px;
            border-radius: 15px;
            font-size: 12px;
            font-weight: bold;
            text-transform: uppercase;
        }
        
        .status-active {
            background: #d4edda;
            color: #155724;
        }
        
        .status-pending {
            background: #fff3cd;
            color: #856404;
        }
        
        .status-suspended {
            background: #f8d7da;
            color: #721c24;
        }
        
        .rating-display {
            display: flex;
            align-items: center;
            gap: 10px;
        }
        
        .stars {
            color: #ffc107;
            font-size: 18px;
        }
        
        .rating-text {
            color: #666;
            font-size: 14px;
        }
        
        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
        }
        
        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 15px;
            margin-top: 20px;
        }
        
        .stat-card {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            text-align: center;
        }
        
        .stat-number {
            font-size: 24px;
            font-weight: bold;
            color: #0073e6;
        }
        
        .stat-label {
            font-size: 14px;
            color: #666;
            margin-top: 5px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="profile-container">
            <div class="profile-header">
                <div class="profile-avatar">
                    ${user.firstName.substring(0,1)}${user.lastName.substring(0,1)}
                </div>
                <h1>${user.firstName} ${user.lastName}</h1>
                <p>@${user.username}</p>
                <span class="status-badge status-${user.accountStatus.toString().toLowerCase()}">
                    ${user.accountStatus}
                </span>
            </div>
            
            <div class="profile-content">
                <c:if test="${not empty message}">
                    <div class="alert alert-success">
                        ${message}
                    </div>
                </c:if>
                
                <!-- Account Information -->
                <div class="info-section">
                    <h3>Account Information</h3>
                    <div class="info-grid">
                        <div class="info-item">
                            <span class="info-label">Username</span>
                            <span class="info-value">${user.username}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Email</span>
                            <span class="info-value">
                                ${user.email}
                                <c:if test="${!user.emailVerified}">
                                    <span style="color: #dc3545; font-size: 12px;">(Not Verified)</span>
                                </c:if>
                            </span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Account Type</span>
                            <span class="info-value">${user.userType}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Member Since</span>
                            <span class="info-value">
                                <fmt:formatDate value="${user.registrationDate}" pattern="MMM dd, yyyy" />
                            </span>
                        </div>
                    </div>
                </div>
                
                <!-- Contact Information -->
                <div class="info-section">
                    <h3>Contact Information</h3>
                    <div class="info-grid">
                        <div class="info-item">
                            <span class="info-label">Phone</span>
                            <span class="info-value">${user.phone != null ? user.phone : 'Not provided'}</span>
                        </div>
                        <div class="info-item">
                            <span class="info-label">Address</span>
                            <span class="info-value">
                                <c:choose>
                                    <c:when test="${user.addressLine1 != null}">
                                        ${user.addressLine1}<br>
                                        <c:if test="${user.addressLine2 != null}">
                                            ${user.addressLine2}<br>
                                        </c:if>
                                        ${user.city}, ${user.state} ${user.zipCode}<br>
                                        ${user.country}
                                    </c:when>
                                    <c:otherwise>
                                        Not provided
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>
                </div>
                
                <!-- Ratings and Statistics -->
                <c:if test="${user.userType == 'SELLER' || user.userType == 'BOTH'}">
                    <div class="info-section">
                        <h3>Seller Rating & Statistics</h3>
                        <div class="rating-display">
                            <div class="stars">
                                <c:forEach begin="1" end="5" var="i">
                                    <c:choose>
                                        <c:when test="${i <= user.sellerRating}">★</c:when>
                                        <c:otherwise>☆</c:otherwise>
                                    </c:choose>
                                </c:forEach>
                            </div>
                            <span class="rating-text">
                                <fmt:formatNumber value="${user.sellerRating}" pattern="#.#"/> out of 5.0
                            </span>
                        </div>
                        
                        <div class="stats-grid">
                            <div class="stat-card">
                                <div class="stat-number">${user.totalSalesCount}</div>
                                <div class="stat-label">Total Sales</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-number">${user.totalPurchaseCount}</div>
                                <div class="stat-label">Total Purchases</div>
                            </div>
                            <div class="stat-card">
                                <div class="stat-number">
                                    <fmt:formatNumber value="${user.buyerRating}" pattern="#.#"/>
                                </div>
                                <div class="stat-label">Buyer Rating</div>
                            </div>
                        </div>
                    </div>
                </c:if>
                
                <!-- Action Buttons -->
                <div class="info-section">
                    <h3>Account Actions</h3>
                    <a href="/eBay/user/edit" class="btn">Edit Profile</a>
                    <a href="/eBay/user/change-password" class="btn btn-secondary">Change Password</a>
                    <a href="/eBay/" class="btn btn-secondary">Back to Home</a>
                    <a href="/eBay/user/logout" class="btn btn-danger" 
                       onclick="return confirm('Are you sure you want to logout?')">Logout</a>
                </div>
                
                <!-- Quick Links -->
                <div class="info-section">
                    <h3>Quick Links</h3>
                    <c:if test="${user.userType == 'SELLER' || user.userType == 'BOTH'}">
                        <a href="#" class="btn" onclick="alert('Feature coming soon!')">My Listings</a>
                        <a href="#" class="btn" onclick="alert('Feature coming soon!')">Create New Listing</a>
                        <a href="#" class="btn" onclick="alert('Feature coming soon!')">Sales History</a>
                    </c:if>
                    <c:if test="${user.userType == 'BUYER' || user.userType == 'BOTH'}">
                        <a href="#" class="btn btn-secondary" onclick="alert('Feature coming soon!')">My Bids</a>
                        <a href="#" class="btn btn-secondary" onclick="alert('Feature coming soon!')">Watchlist</a>
                        <a href="#" class="btn btn-secondary" onclick="alert('Feature coming soon!')">Purchase History</a>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</body>
</html>