<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${product.title} - eBay Marketplace</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
    <style>
        .product-container {
            max-width: 1200px;
            margin: 20px auto;
            display: grid;
            grid-template-columns: 1fr 400px;
            gap: 30px;
            padding: 0 20px;
        }
        
        .product-main {
            background: white;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .product-sidebar {
            background: white;
            border-radius: 10px;
            padding: 25px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            height: fit-content;
            position: sticky;
            top: 20px;
        }
        
        .product-images {
            position: relative;
            height: 400px;
            background: #f8f9fa;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 72px;
            color: #ccc;
        }
        
        .product-info {
            padding: 30px;
        }
        
        .product-title {
            font-size: 28px;
            font-weight: bold;
            color: #333;
            margin-bottom: 15px;
            line-height: 1.3;
        }
        
        .product-condition {
            background: #e9ecef;
            color: #495057;
            padding: 5px 12px;
            border-radius: 15px;
            font-size: 14px;
            display: inline-block;
            margin-bottom: 20px;
        }
        
        .product-description {
            color: #666;
            line-height: 1.6;
            margin-bottom: 30px;
        }
        
        .product-details {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .detail-item {
            padding: 15px;
            background: #f8f9fa;
            border-radius: 5px;
        }
        
        .detail-label {
            font-weight: bold;
            color: #333;
            margin-bottom: 5px;
        }
        
        .detail-value {
            color: #666;
        }
        
        /* Auction/Bidding Sidebar */
        .price-section {
            text-align: center;
            margin-bottom: 25px;
        }
        
        .current-price {
            font-size: 32px;
            font-weight: bold;
            color: #e53238;
            margin-bottom: 5px;
        }
        
        .price-label {
            color: #666;
            font-size: 14px;
        }
        
        .auction-status {
            background: #fff3cd;
            border: 1px solid #ffeaa7;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            text-align: center;
        }
        
        .auction-status.ended {
            background: #f8d7da;
            border-color: #f5c6cb;
            color: #721c24;
        }
        
        .auction-status.winning {
            background: #d4edda;
            border-color: #c3e6cb;
            color: #155724;
        }
        
        .time-remaining {
            font-size: 18px;
            font-weight: bold;
            color: #dc3545;
            margin-bottom: 5px;
        }
        
        .bid-count {
            color: #666;
            font-size: 14px;
        }
        
        .bidding-section {
            margin-bottom: 25px;
        }
        
        .bid-form {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 5px;
            margin-bottom: 15px;
        }
        
        .bid-input {
            width: 100%;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 5px;
            font-size: 16px;
            margin-bottom: 10px;
            text-align: center;
        }
        
        .bid-input:focus {
            outline: none;
            border-color: #0073e6;
        }
        
        .min-bid-info {
            font-size: 12px;
            color: #666;
            text-align: center;
            margin-bottom: 15px;
        }
        
        .btn {
            width: 100%;
            background: #0073e6;
            color: white;
            border: none;
            padding: 15px;
            border-radius: 5px;
            font-size: 16px;
            font-weight: bold;
            cursor: pointer;
            transition: background 0.3s;
            margin-bottom: 10px;
        }
        
        .btn:hover {
            background: #005bb5;
        }
        
        .btn:disabled {
            background: #ccc;
            cursor: not-allowed;
        }
        
        .btn-buy-now {
            background: #28a745;
        }
        
        .btn-buy-now:hover {
            background: #218838;
        }
        
        .btn-secondary {
            background: #6c757d;
        }
        
        .btn-secondary:hover {
            background: #545b62;
        }
        
        .seller-info {
            border-top: 1px solid #eee;
            padding-top: 20px;
            text-align: center;
        }
        
        .seller-info h4 {
            margin-bottom: 10px;
            color: #333;
        }
        
        .seller-rating {
            color: #ffc107;
            margin-bottom: 10px;
        }
        
        .alert {
            padding: 12px;
            border-radius: 5px;
            margin-bottom: 15px;
            text-align: center;
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
        
        .alert-info {
            background: #d1ecf1;
            color: #0c5460;
            border: 1px solid #bee5eb;
        }
        
        .breadcrumb {
            max-width: 1200px;
            margin: 0 auto 20px;
            padding: 0 20px;
            font-size: 14px;
            color: #666;
        }
        
        .breadcrumb a {
            color: #0073e6;
            text-decoration: none;
        }
        
        .bid-history-link {
            text-align: center;
            margin-top: 15px;
        }
        
        .bid-history-link a {
            color: #0073e6;
            text-decoration: none;
            font-size: 14px;
        }
        
        .owner-actions {
            background: #e9ecef;
            padding: 15px;
            border-radius: 5px;
            text-align: center;
        }
        
        .owner-actions h4 {
            margin-bottom: 15px;
            color: #495057;
        }
        
        .proxy-bid-option {
            margin-top: 15px;
            padding-top: 15px;
            border-top: 1px solid #ddd;
        }
        
        .proxy-bid-option label {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 14px;
            color: #666;
        }
        
        .proxy-max-input {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 3px;
            margin-top: 10px;
            display: none;
        }
        
        @media (max-width: 768px) {
            .product-container {
                grid-template-columns: 1fr;
                gap: 20px;
            }
            
            .product-sidebar {
                position: static;
            }
        }
    </style>
</head>
<body>
    <!-- Breadcrumb -->
    <div class="breadcrumb">
        <a href="/eBay/">Home</a> &gt; 
        <a href="/eBay/product/category/${product.category.categoryId}">${product.category.categoryName}</a> &gt; 
        ${product.title}
    </div>
    
    <div class="product-container">
        <!-- Main Product Info -->
        <div class="product-main">
            <!-- Product Images -->
            <div class="product-images">
                <c:choose>
                    <c:when test="${not empty images}">
                        <!-- TODO: Implement image carousel -->
                        🖼️
                    </c:when>
                    <c:otherwise>
                        📦
                    </c:otherwise>
                </c:choose>
            </div>
            
            <!-- Product Information -->
            <div class="product-info">
                <h1 class="product-title">${product.title}</h1>
                
                <span class="product-condition">
                    <c:choose>
                        <c:when test="${product.conditionType == 'NEW'}">New</c:when>
                        <c:when test="${product.conditionType == 'LIKE_NEW'}">Like New</c:when>
                        <c:when test="${product.conditionType == 'VERY_GOOD'}">Very Good</c:when>
                        <c:when test="${product.conditionType == 'GOOD'}">Good</c:when>
                        <c:when test="${product.conditionType == 'ACCEPTABLE'}">Acceptable</c:when>
                        <c:when test="${product.conditionType == 'FOR_PARTS'}">For Parts/Not Working</c:when>
                        <c:otherwise>${product.conditionType}</c:otherwise>
                    </c:choose>
                </span>
                
                <div class="product-description">
                    ${product.description}
                </div>
                
                <div class="product-details">
                    <div class="detail-item">
                        <div class="detail-label">Seller</div>
                        <div class="detail-value">${product.seller.username}</div>
                    </div>
                    
                    <div class="detail-item">
                        <div class="detail-label">Category</div>
                        <div class="detail-value">${product.category.categoryName}</div>
                    </div>
                    
                    <div class="detail-item">
                        <div class="detail-label">Listing Type</div>
                        <div class="detail-value">
                            <c:choose>
                                <c:when test="${product.listingType == 'BUY_NOW'}">Buy It Now</c:when>
                                <c:when test="${product.listingType == 'AUCTION'}">Auction</c:when>
                                <c:when test="${product.listingType == 'BOTH'}">Auction with Buy It Now</c:when>
                                <c:otherwise>${product.listingType}</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    
                    <div class="detail-item">
                        <div class="detail-label">Quantity</div>
                        <div class="detail-value">${product.quantityAvailable}</div>
                    </div>
                    
                    <c:if test="${product.shippingCost > 0}">
                        <div class="detail-item">
                            <div class="detail-label">Shipping</div>
                            <div class="detail-value">$<fmt:formatNumber value="${product.shippingCost}" pattern="#,##0.00"/></div>
                        </div>
                    </c:if>
                    
                    <c:if test="${not empty product.itemLocation}">
                        <div class="detail-item">
                            <div class="detail-label">Item Location</div>
                            <div class="detail-value">${product.itemLocation}</div>
                        </div>
                    </c:if>
                </div>
                
                <c:if test="${not empty product.returnPolicy}">
                    <div class="detail-item">
                        <div class="detail-label">Return Policy</div>
                        <div class="detail-value">${product.returnPolicy}</div>
                    </div>
                </c:if>
            </div>
        </div>
        
        <!-- Sidebar - Bidding/Purchase -->
        <div class="product-sidebar">
            <!-- Owner Actions -->
            <c:if test="${isOwner}">
                <div class="owner-actions">
                    <h4>Your Listing</h4>
                    <a href="/eBay/product/edit/${product.productId}" class="btn btn-secondary">Edit Listing</a>
                    <c:if test="${product.status == 'DRAFT'}">
                        <button onclick="activateProduct()" class="btn">Activate Listing</button>
                    </c:if>
                </div>
            </c:if>
            
            <c:if test="${!isOwner}">
                <!-- Price Section -->
                <div class="price-section">
                    <div class="current-price" id="currentPrice">
                        $<fmt:formatNumber value="${product.currentPrice}" pattern="#,##0.00"/>
                    </div>
                    <div class="price-label">
                        <c:choose>
                            <c:when test="${product.listingType == 'AUCTION' || product.listingType == 'BOTH'}">
                                Current bid
                            </c:when>
                            <c:otherwise>
                                Buy It Now price
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
                
                <!-- Auction Status -->
                <c:if test="${product.listingType == 'AUCTION' || product.listingType == 'BOTH'}">
                    <div class="auction-status" id="auctionStatus">
                        <div class="time-remaining" id="timeRemaining">${product.formattedTimeRemaining}</div>
                        <div class="bid-count" id="bidCount">
                            <span id="bidCountNumber">0</span> bids
                        </div>
                    </div>
                </c:if>
                
                <!-- Alert Messages -->
                <div id="alertContainer"></div>
                
                <!-- Bidding Section -->
                <c:if test="${(product.listingType == 'AUCTION' || product.listingType == 'BOTH') && product.auctionActive}">
                    <div class="bidding-section" id="biddingSection">
                        <div class="bid-form">
                            <input type="number" id="bidAmount" class="bid-input" 
                                   placeholder="Enter your bid" step="0.01" min="0">
                            <div class="min-bid-info" id="minBidInfo">
                                Minimum bid: $<span id="minBidAmount">0.00</span>
                            </div>
                            
                            <!-- Proxy Bidding Option -->
                            <div class="proxy-bid-option">
                                <label>
                                    <input type="checkbox" id="useProxyBid"> 
                                    Use automatic bidding
                                </label>
                                <input type="number" id="maxProxyAmount" class="proxy-max-input" 
                                       placeholder="Maximum amount" step="0.01">
                            </div>
                            
                            <button onclick="placeBid()" class="btn" id="bidButton">
                                Place Bid
                            </button>
                        </div>
                    </div>
                </c:if>
                
                <!-- Buy Now Section -->
                <c:if test="${product.buyNowPrice != null && (product.listingType == 'BUY_NOW' || product.listingType == 'BOTH')}">
                    <div id="buyNowSection">
                        <button onclick="buyNow()" class="btn btn-buy-now" id="buyNowButton">
                            Buy It Now - $<fmt:formatNumber value="${product.buyNowPrice}" pattern="#,##0.00"/>
                        </button>
                    </div>
                </c:if>
                
                <!-- Watchlist -->
                <button onclick="toggleWatchlist()" class="btn btn-secondary" id="watchlistButton">
                    Add to Watchlist
                </button>
                
                <!-- Bid History Link -->
                <c:if test="${product.listingType == 'AUCTION' || product.listingType == 'BOTH'}">
                    <div class="bid-history-link">
                        <a href="/eBay/bid/history-page/${product.productId}">View bid history</a>
                    </div>
                </c:if>
                
                <!-- Seller Information -->
                <div class="seller-info">
                    <h4>Seller Information</h4>
                    <div><strong>${product.seller.fullName}</strong></div>
                    <div class="seller-rating">
                        <c:forEach begin="1" end="5" var="i">
                            <c:choose>
                                <c:when test="${i <= product.seller.sellerRating}">★</c:when>
                                <c:otherwise>☆</c:otherwise>
                            </c:choose>
                        </c:forEach>
                        (<fmt:formatNumber value="${product.seller.sellerRating}" pattern="#.#"/>)
                    </div>
                    <div style="font-size: 14px; color: #666;">
                        ${product.seller.totalSalesCount} items sold
                    </div>
                </div>
            </c:if>
        </div>
    </div>
    
    <script>
        let refreshInterval;
        const productId = ${product.productId};
        const isOwner = ${isOwner};
        
        // Initialize page
        document.addEventListener('DOMContentLoaded', function() {
            if (!isOwner) {
                updateAuctionData();
                // Refresh every 30 seconds for auctions
                <c:if test="${product.listingType == 'AUCTION' || product.listingType == 'BOTH'}">
                    refreshInterval = setInterval(updateAuctionData, 30000);
                </c:if>
            }
            
            // Initialize proxy bidding toggle
            document.getElementById('useProxyBid')?.addEventListener('change', function() {
                const maxInput = document.getElementById('maxProxyAmount');
                maxInput.style.display = this.checked ? 'block' : 'none';
                if (this.checked) {
                    maxInput.focus();
                }
            });
        });
        
        function updateAuctionData() {
            fetch('/eBay/bid/refresh/' + productId)
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // Update price
                        document.getElementById('currentPrice').textContent = 
                            '$' + parseFloat(data.currentPrice).toLocaleString('en-US', {minimumFractionDigits: 2});
                        
                        // Update bid count
                        if (document.getElementById('bidCountNumber')) {
                            document.getElementById('bidCountNumber').textContent = data.bidCount || 0;
                        }
                        
                        // Update minimum bid
                        if (document.getElementById('minBidAmount')) {
                            document.getElementById('minBidAmount').textContent = 
                                parseFloat(data.minNextBid).toLocaleString('en-US', {minimumFractionDigits: 2});
                            document.getElementById('bidAmount').min = data.minNextBid;
                        }
                        
                        // Update time remaining
                        if (document.getElementById('timeRemaining')) {
                            document.getElementById('timeRemaining').textContent = data.timeRemaining;
                        }
                        
                        // Update auction status
                        const auctionStatus = document.getElementById('auctionStatus');
                        if (auctionStatus) {
                            auctionStatus.className = 'auction-status';
                            if (data.isEnded) {
                                auctionStatus.classList.add('ended');
                                document.getElementById('timeRemaining').textContent = 'Auction ended';
                                disableBidding();
                            } else if (data.userIsWinning) {
                                auctionStatus.classList.add('winning');
                            }
                        }
                        
                        // Disable bidding if auction ended
                        if (data.isEnded || !data.isActive) {
                            disableBidding();
                        }
                    }
                })
                .catch(error => console.error('Error updating auction data:', error));
        }
        
        function placeBid() {
            const bidAmount = document.getElementById('bidAmount').value;
            const useProxy = document.getElementById('useProxyBid').checked;
            const maxProxyAmount = useProxy ? document.getElementById('maxProxyAmount').value : null;
            
            if (!bidAmount || parseFloat(bidAmount) <= 0) {
                showAlert('Please enter a valid bid amount', 'error');
                return;
            }
            
            if (useProxy && (!maxProxyAmount || parseFloat(maxProxyAmount) < parseFloat(bidAmount))) {
                showAlert('Maximum proxy amount must be greater than your bid', 'error');
                return;
            }
            
            const formData = new FormData();
            formData.append('productId', productId);
            formData.append('bidAmount', bidAmount);
            formData.append('bidType', useProxy ? 'PROXY' : 'REGULAR');
            if (useProxy) {
                formData.append('maxProxyAmount', maxProxyAmount);
            }
            
            fetch('/eBay/bid/place', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    showAlert(data.message, 'success');
                    document.getElementById('bidAmount').value = '';
                    document.getElementById('maxProxyAmount').value = '';
                    document.getElementById('useProxyBid').checked = false;
                    document.getElementById('maxProxyAmount').style.display = 'none';
                    updateAuctionData();
                } else {
                    showAlert(data.message, 'error');
                }
            })
            .catch(error => {
                showAlert('Error placing bid. Please try again.', 'error');
                console.error('Error:', error);
            });
        }
        
        function buyNow() {
            if (!confirm('Are you sure you want to buy this item now?')) {
                return;
            }
            
            const formData = new FormData();
            formData.append('productId', productId);
            
            fetch('/eBay/bid/buy-now', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    showAlert(data.message, 'success');
                    if (data.redirect) {
                        setTimeout(() => window.location.href = data.redirect, 2000);
                    }
                } else {
                    showAlert(data.message, 'error');
                }
            })
            .catch(error => {
                showAlert('Error processing purchase. Please try again.', 'error');
                console.error('Error:', error);
            });
        }
        
        function toggleWatchlist() {
            // TODO: Implement watchlist functionality
            showAlert('Watchlist feature coming soon!', 'info');
        }
        
        function activateProduct() {
            if (!confirm('Are you sure you want to activate this listing?')) {
                return;
            }
            
            fetch('/eBay/product/activate/' + productId, {
                method: 'POST'
            })
            .then(response => {
                if (response.ok) {
                    location.reload();
                } else {
                    showAlert('Error activating product', 'error');
                }
            })
            .catch(error => {
                showAlert('Error activating product', 'error');
                console.error('Error:', error);
            });
        }
        
        function disableBidding() {
            const bidButton = document.getElementById('bidButton');
            const buyNowButton = document.getElementById('buyNowButton');
            const bidInput = document.getElementById('bidAmount');
            
            if (bidButton) {
                bidButton.disabled = true;
                bidButton.textContent = 'Auction Ended';
            }
            
            if (buyNowButton) {
                buyNowButton.disabled = true;
                buyNowButton.textContent = 'No longer available';
            }
            
            if (bidInput) {
                bidInput.disabled = true;
            }
            
            if (refreshInterval) {
                clearInterval(refreshInterval);
            }
        }
        
        function showAlert(message, type) {
            const alertContainer = document.getElementById('alertContainer');
            const alert = document.createElement('div');
            alert.className = `alert alert-${type}`;
            alert.textContent = message;
            
            alertContainer.innerHTML = '';
            alertContainer.appendChild(alert);
            
            // Auto-remove after 5 seconds
            setTimeout(() => {
                if (alert.parentNode) {
                    alert.parentNode.removeChild(alert);
                }
            }, 5000);
        }
        
        // Cleanup on page unload
        window.addEventListener('beforeunload', function() {
            if (refreshInterval) {
                clearInterval(refreshInterval);
            }
        });
    </script>
</body>
</html>