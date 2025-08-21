<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Auction Management - eBay Seller</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f5f5f5;
            color: #333;
        }

        .header {
            background: #0064d2;
            color: white;
            padding: 1rem 0;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .header-content {
            max-width: 1200px;
            margin: 0 auto;
            padding: 0 2rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .logo {
            font-size: 1.8rem;
            font-weight: bold;
            text-decoration: none;
            color: white;
        }

        .nav {
            display: flex;
            gap: 2rem;
        }

        .nav a {
            color: white;
            text-decoration: none;
            padding: 0.5rem 1rem;
            border-radius: 4px;
            transition: background-color 0.3s;
        }

        .nav a:hover, .nav a.active {
            background-color: rgba(255,255,255,0.2);
        }

        .container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 2rem;
        }

        .page-header {
            background: white;
            padding: 2rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 2rem;
        }

        .page-title {
            font-size: 2rem;
            color: #333;
            margin-bottom: 0.5rem;
        }

        .page-subtitle {
            color: #666;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2rem;
        }

        .stat-card {
            background: white;
            padding: 1.5rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            text-align: center;
        }

        .stat-number {
            font-size: 2rem;
            font-weight: bold;
            color: #0064d2;
            margin-bottom: 0.5rem;
        }

        .stat-label {
            color: #666;
            font-size: 0.9rem;
        }

        .auctions-container {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            overflow: hidden;
        }

        .auctions-header {
            background: #f8f9fa;
            padding: 1rem 1.5rem;
            border-bottom: 1px solid #e9ecef;
            font-weight: 600;
        }

        .auction-item {
            border-bottom: 1px solid #e9ecef;
            padding: 1.5rem;
            transition: background-color 0.2s;
        }

        .auction-item:hover {
            background-color: #f8f9fa;
        }

        .auction-item:last-child {
            border-bottom: none;
        }

        .auction-content {
            display: grid;
            grid-template-columns: 100px 1fr auto;
            gap: 1.5rem;
            align-items: center;
        }

        .auction-image {
            width: 100px;
            height: 100px;
            border-radius: 4px;
            object-fit: cover;
            background-color: #f0f0f0;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #666;
            font-size: 0.8rem;
        }

        .auction-details {
            flex: 1;
        }

        .auction-title {
            font-size: 1.1rem;
            font-weight: 600;
            margin-bottom: 0.5rem;
            color: #333;
        }

        .auction-info {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 1rem;
            margin-bottom: 0.75rem;
        }

        .info-item {
            font-size: 0.9rem;
        }

        .info-label {
            color: #666;
            font-size: 0.8rem;
            display: block;
        }

        .info-value {
            font-weight: 500;
            color: #333;
        }

        .auction-status {
            display: inline-block;
            padding: 0.25rem 0.75rem;
            border-radius: 1rem;
            font-size: 0.8rem;
            font-weight: 500;
            text-transform: uppercase;
        }

        .status-active {
            background-color: #d4edda;
            color: #155724;
        }

        .status-ending-soon {
            background-color: #fff3cd;
            color: #856404;
            animation: pulse 2s infinite;
        }

        .status-ended {
            background-color: #f8d7da;
            color: #721c24;
        }

        .status-draft {
            background-color: #e2e3e5;
            color: #6c757d;
        }

        @keyframes pulse {
            0% { opacity: 1; }
            50% { opacity: 0.7; }
            100% { opacity: 1; }
        }

        .countdown {
            font-family: 'Courier New', monospace;
            font-weight: bold;
            color: #dc3545;
        }

        .countdown.ending-soon {
            color: #fd7e14;
            animation: pulse 1s infinite;
        }

        .auction-actions {
            display: flex;
            flex-direction: column;
            gap: 0.5rem;
            align-items: flex-end;
        }

        .action-btn {
            padding: 0.5rem 1rem;
            border: none;
            border-radius: 4px;
            font-size: 0.8rem;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            text-align: center;
            transition: background-color 0.3s;
            white-space: nowrap;
        }

        .btn-primary {
            background: #0064d2;
            color: white;
        }

        .btn-primary:hover {
            background: #0052a3;
        }

        .btn-secondary {
            background: #6c757d;
            color: white;
        }

        .btn-secondary:hover {
            background: #545b62;
        }

        .btn-warning {
            background: #ffc107;
            color: #212529;
        }

        .btn-warning:hover {
            background: #e0a800;
        }

        .btn-danger {
            background: #dc3545;
            color: white;
        }

        .btn-danger:hover {
            background: #c82333;
        }

        .extend-form {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.5);
            z-index: 1000;
        }

        .extend-form.show {
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .extend-form-content {
            background: white;
            padding: 2rem;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            max-width: 400px;
            width: 90%;
        }

        .form-group {
            margin-bottom: 1rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
        }

        .form-group select,
        .form-group input {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #ddd;
            border-radius: 4px;
        }

        .form-actions {
            display: flex;
            gap: 1rem;
            justify-content: flex-end;
            margin-top: 2rem;
        }

        .empty-state {
            text-align: center;
            padding: 4rem;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .empty-state-icon {
            font-size: 4rem;
            color: #ddd;
            margin-bottom: 1rem;
        }

        .empty-state-title {
            font-size: 1.5rem;
            color: #666;
            margin-bottom: 0.5rem;
        }

        .alert {
            padding: 0.75rem 1rem;
            margin-bottom: 1rem;
            border: 1px solid transparent;
            border-radius: 0.375rem;
        }

        .alert-success {
            color: #155724;
            background-color: #d4edda;
            border-color: #c3e6cb;
        }

        .alert-danger {
            color: #721c24;
            background-color: #f8d7da;
            border-color: #f5c6cb;
        }

        @media (max-width: 768px) {
            .auction-content {
                grid-template-columns: 1fr;
                gap: 1rem;
            }
            
            .auction-actions {
                align-items: stretch;
            }
            
            .stats-grid {
                grid-template-columns: 1fr;
            }
            
            .header-content {
                flex-direction: column;
                gap: 1rem;
            }
            
            .nav {
                flex-wrap: wrap;
                justify-content: center;
            }
        }
    </style>
</head>
<body>
    <header class="header">
        <div class="header-content">
            <a href="/" class="logo">eBay</a>
            <nav class="nav">
                <a href="/seller/dashboard">Dashboard</a>
                <a href="/seller/listings">My Listings</a>
                <a href="/seller/orders">Orders</a>
                <a href="/seller/inventory">Inventory</a>
                <a href="/seller/analytics">Analytics</a>
                <a href="/seller/auctions" class="active">Auctions</a>
                <a href="/product/create">List Item</a>
                <a href="/user/profile">Profile</a>
                <a href="/user/logout">Logout</a>
            </nav>
        </div>
    </header>

    <div class="container">
        <div class="page-header">
            <h1 class="page-title">Auction Management</h1>
            <p class="page-subtitle">Monitor and manage your auction listings</p>
        </div>

        <c:if test="${not empty message}">
            <div class="alert alert-success">${message}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-number">${activeAuctions}</div>
                <div class="stat-label">Active Auctions</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">${endingSoon}</div>
                <div class="stat-label">Ending Soon (24h)</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">${endedAuctions}</div>
                <div class="stat-label">Recently Ended</div>
            </div>
        </div>

        <c:choose>
            <c:when test="${not empty auctions}">
                <div class="auctions-container">
                    <div class="auctions-header">Your Auction Listings</div>
                    
                    <c:forEach items="${auctions}" var="auction">
                        <div class="auction-item">
                            <div class="auction-content">
                                <div class="auction-image">
                                    <c:choose>
                                        <c:when test="${not empty auction.images}">
                                            <img src="${auction.images[0].imageUrl}" alt="${auction.title}" 
                                                 style="width: 100%; height: 100%; object-fit: cover;">
                                        </c:when>
                                        <c:otherwise>
                                            No Image
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                
                                <div class="auction-details">
                                    <h3 class="auction-title">${auction.title}</h3>
                                    
                                    <div class="auction-info">
                                        <div class="info-item">
                                            <span class="info-label">Current Bid</span>
                                            <span class="info-value">$<fmt:formatNumber value="${auction.currentPrice}" type="number" maxFractionDigits="2"/></span>
                                        </div>
                                        <div class="info-item">
                                            <span class="info-label">Starting Price</span>
                                            <span class="info-value">$<fmt:formatNumber value="${auction.startingPrice}" type="number" maxFractionDigits="2"/></span>
                                        </div>
                                        <c:if test="${auction.reservePrice != null}">
                                            <div class="info-item">
                                                <span class="info-label">Reserve Price</span>
                                                <span class="info-value">$<fmt:formatNumber value="${auction.reservePrice}" type="number" maxFractionDigits="2"/></span>
                                            </div>
                                        </c:if>
                                        <div class="info-item">
                                            <span class="info-label">Bids</span>
                                            <span class="info-value">${auction.bids.size()}</span>
                                        </div>
                                        <div class="info-item">
                                            <span class="info-label">Watchers</span>
                                            <span class="info-value">${auction.watchCount}</span>
                                        </div>
                                        <div class="info-item">
                                            <span class="info-label">Time Remaining</span>
                                            <span class="info-value countdown ${auction.timeRemainingMillis < 86400000 ? 'ending-soon' : ''}">
                                                ${auction.formattedTimeRemaining}
                                            </span>
                                        </div>
                                    </div>
                                    
                                    <div>
                                        <c:choose>
                                            <c:when test="${auction.isAuctionActive()}">
                                                <c:choose>
                                                    <c:when test="${auction.timeRemainingMillis < 86400000}">
                                                        <span class="auction-status status-ending-soon">Ending Soon</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="auction-status status-active">Active</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:when>
                                            <c:when test="${auction.isAuctionEnded()}">
                                                <span class="auction-status status-ended">Ended</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="auction-status status-draft">Draft</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                                
                                <div class="auction-actions">
                                    <a href="/product/view/${auction.productId}" class="action-btn btn-primary">View Details</a>
                                    
                                    <c:if test="${auction.isAuctionActive()}">
                                        <button class="action-btn btn-warning" 
                                                onclick="showExtendForm(${auction.productId}, '${auction.title}')">
                                            Extend Time
                                        </button>
                                        <form style="display: inline;" method="POST" action="/seller/auctions/${auction.productId}/end">
                                            <button type="submit" class="action-btn btn-danger" 
                                                    onclick="return confirm('Are you sure you want to end this auction early?')">
                                                End Early
                                            </button>
                                        </form>
                                    </c:if>
                                    
                                    <c:if test="${auction.status.name() == 'DRAFT'}">
                                        <a href="/product/edit/${auction.productId}" class="action-btn btn-secondary">Edit</a>
                                    </c:if>
                                    
                                    <c:if test="${not empty auction.bids}">
                                        <a href="/bid/history/${auction.productId}" class="action-btn btn-secondary">View Bids</a>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-state-icon">🔨</div>
                    <h2 class="empty-state-title">No auction listings found</h2>
                    <p class="empty-state-text">
                        You haven't created any auction listings yet. 
                        Create an auction listing to start selling through bidding.
                    </p>
                    <a href="/product/create" class="action-btn btn-primary" style="margin-top: 1rem;">
                        Create Auction Listing
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Extend Auction Form Modal -->
    <div id="extendForm" class="extend-form">
        <div class="extend-form-content">
            <h3>Extend Auction Time</h3>
            <p id="auctionTitle" style="color: #666; margin-bottom: 1rem;"></p>
            
            <form id="extendAuctionForm" method="POST">
                <div class="form-group">
                    <label for="hours">Extend by (hours):</label>
                    <select name="hours" id="hours" required>
                        <option value="1">1 hour</option>
                        <option value="3">3 hours</option>
                        <option value="6">6 hours</option>
                        <option value="12">12 hours</option>
                        <option value="24" selected>24 hours</option>
                        <option value="48">48 hours</option>
                        <option value="72">72 hours</option>
                    </select>
                </div>
                
                <div class="form-actions">
                    <button type="button" class="action-btn btn-secondary" onclick="closeExtendForm()">Cancel</button>
                    <button type="submit" class="action-btn btn-warning">Extend Auction</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        function showExtendForm(productId, title) {
            const form = document.getElementById('extendAuctionForm');
            const extendForm = document.getElementById('extendForm');
            const titleElement = document.getElementById('auctionTitle');
            
            form.action = `/seller/auctions/${productId}/extend`;
            titleElement.textContent = title;
            
            extendForm.classList.add('show');
        }

        function closeExtendForm() {
            const extendForm = document.getElementById('extendForm');
            extendForm.classList.remove('show');
        }

        // Close modal when clicking outside
        document.getElementById('extendForm').addEventListener('click', function(e) {
            if (e.target === this) {
                closeExtendForm();
            }
        });

        // Update countdown timers every minute
        function updateCountdowns() {
            document.querySelectorAll('.countdown').forEach(element => {
                const auctionId = element.closest('.auction-item').getAttribute('data-auction-id');
                // In a real system, you would fetch updated data from server
                // For now, we'll just refresh the page periodically
            });
        }

        // Refresh page every 5 minutes to update auction times
        setInterval(() => {
            window.location.reload();
        }, 5 * 60 * 1000);
    </script>
</body>
</html>