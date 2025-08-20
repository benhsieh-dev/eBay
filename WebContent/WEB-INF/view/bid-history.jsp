<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Bids - eBay Marketplace</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
    <style>
        .bids-container {
            max-width: 1200px;
            margin: 30px auto;
            padding: 0 20px;
        }
        
        .section {
            background: white;
            border-radius: 10px;
            padding: 25px;
            margin-bottom: 30px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        
        .section h2 {
            color: #333;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 2px solid #0073e6;
        }
        
        .bids-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 15px;
        }
        
        .bids-table th,
        .bids-table td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #eee;
        }
        
        .bids-table th {
            background: #f8f9fa;
            font-weight: bold;
            color: #333;
        }
        
        .bids-table tr:hover {
            background: #f8f9fa;
        }
        
        .bid-status {
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: bold;
            text-transform: uppercase;
        }
        
        .status-winning {
            background: #d4edda;
            color: #155724;
        }
        
        .status-outbid {
            background: #f8d7da;
            color: #721c24;
        }
        
        .status-won {
            background: #d1ecf1;
            color: #0c5460;
        }
        
        .status-active {
            background: #fff3cd;
            color: #856404;
        }
        
        .product-link {
            color: #0073e6;
            text-decoration: none;
            font-weight: bold;
        }
        
        .product-link:hover {
            text-decoration: underline;
        }
        
        .bid-amount {
            font-weight: bold;
            color: #e53238;
        }
        
        .no-bids {
            text-align: center;
            padding: 40px 20px;
            color: #666;
        }
        
        .breadcrumb {
            margin-bottom: 20px;
            font-size: 14px;
            color: #666;
        }
        
        .breadcrumb a {
            color: #0073e6;
            text-decoration: none;
        }
        
        .summary-stats {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
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
            color: #666;
            font-size: 14px;
            margin-top: 5px;
        }
    </style>
</head>
<body>
    <div class="bids-container">
        <!-- Breadcrumb -->
        <div class="breadcrumb">
            <a href="/eBay/">Home</a> &gt; 
            <a href="/eBay/user/profile">My Profile</a> &gt; 
            My Bids
        </div>
        
        <!-- Summary Statistics -->
        <div class="summary-stats">
            <div class="stat-card">
                <div class="stat-number">${activeBids.size()}</div>
                <div class="stat-label">Active Bids</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">${userBids.size()}</div>
                <div class="stat-label">Total Bids</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">
                    <c:set var="winningCount" value="0"/>
                    <c:forEach items="${activeBids}" var="bid">
                        <c:if test="${bid.bidStatus == 'WINNING'}">
                            <c:set var="winningCount" value="${winningCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    ${winningCount}
                </div>
                <div class="stat-label">Currently Winning</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">
                    <c:set var="wonCount" value="0"/>
                    <c:forEach items="${userBids}" var="bid">
                        <c:if test="${bid.bidStatus == 'WON'}">
                            <c:set var="wonCount" value="${wonCount + 1}"/>
                        </c:if>
                    </c:forEach>
                    ${wonCount}
                </div>
                <div class="stat-label">Auctions Won</div>
            </div>
        </div>
        
        <!-- Active Bids -->
        <div class="section">
            <h2>Active Bids</h2>
            
            <c:choose>
                <c:when test="${not empty activeBids}">
                    <table class="bids-table">
                        <thead>
                            <tr>
                                <th>Item</th>
                                <th>My Bid</th>
                                <th>Current Price</th>
                                <th>Status</th>
                                <th>Time Left</th>
                                <th>Bid Date</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${activeBids}" var="bid">
                                <tr>
                                    <td>
                                        <a href="/eBay/product/view/${bid.product.productId}" class="product-link">
                                            ${bid.product.title}
                                        </a>
                                    </td>
                                    <td class="bid-amount">
                                        $<fmt:formatNumber value="${bid.bidAmount}" pattern="#,##0.00"/>
                                    </td>
                                    <td>
                                        $<fmt:formatNumber value="${bid.product.currentPrice}" pattern="#,##0.00"/>
                                    </td>
                                    <td>
                                        <span class="bid-status status-${bid.bidStatus.toString().toLowerCase()}">
                                            <c:choose>
                                                <c:when test="${bid.bidStatus == 'WINNING'}">Winning!</c:when>
                                                <c:when test="${bid.bidStatus == 'OUTBID'}">Outbid</c:when>
                                                <c:when test="${bid.bidStatus == 'ACTIVE'}">Active</c:when>
                                                <c:otherwise>${bid.bidStatus}</c:otherwise>
                                            </c:choose>
                                        </span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${bid.product.auctionActive}">
                                                ${bid.product.formattedTimeRemaining}
                                            </c:when>
                                            <c:otherwise>
                                                <span style="color: #dc3545;">Ended</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${bid.bidTime}" pattern="MMM dd, HH:mm"/>
                                    </td>
                                    <td>
                                        <a href="/eBay/product/view/${bid.product.productId}" 
                                           style="color: #0073e6; text-decoration: none; font-size: 14px;">
                                            View Item
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div class="no-bids">
                        <h3>No active bids</h3>
                        <p>You don't have any active bids right now.</p>
                        <p><a href="/eBay/product/search">Browse auctions</a> to start bidding!</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
        
        <!-- All Bid History -->
        <div class="section">
            <h2>Complete Bid History</h2>
            
            <c:choose>
                <c:when test="${not empty userBids}">
                    <table class="bids-table">
                        <thead>
                            <tr>
                                <th>Item</th>
                                <th>My Bid</th>
                                <th>Final Price</th>
                                <th>Status</th>
                                <th>Bid Date</th>
                                <th>Bid Type</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${userBids}" var="bid">
                                <tr>
                                    <td>
                                        <a href="/eBay/product/view/${bid.product.productId}" class="product-link">
                                            ${bid.product.title}
                                        </a>
                                    </td>
                                    <td class="bid-amount">
                                        $<fmt:formatNumber value="${bid.bidAmount}" pattern="#,##0.00"/>
                                    </td>
                                    <td>
                                        $<fmt:formatNumber value="${bid.product.currentPrice}" pattern="#,##0.00"/>
                                    </td>
                                    <td>
                                        <span class="bid-status status-${bid.bidStatus.toString().toLowerCase()}">
                                            <c:choose>
                                                <c:when test="${bid.bidStatus == 'WINNING'}">Winning!</c:when>
                                                <c:when test="${bid.bidStatus == 'WON'}">Won</c:when>
                                                <c:when test="${bid.bidStatus == 'OUTBID'}">Outbid</c:when>
                                                <c:when test="${bid.bidStatus == 'ACTIVE'}">Active</c:when>
                                                <c:otherwise>${bid.bidStatus}</c:otherwise>
                                            </c:choose>
                                        </span>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${bid.bidTime}" pattern="MMM dd, yyyy HH:mm"/>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${bid.bidType == 'REGULAR'}">Regular</c:when>
                                            <c:when test="${bid.bidType == 'PROXY'}">Auto Bid</c:when>
                                            <c:when test="${bid.bidType == 'BUY_NOW'}">Buy Now</c:when>
                                            <c:otherwise>${bid.bidType}</c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <a href="/eBay/product/view/${bid.product.productId}" 
                                           style="color: #0073e6; text-decoration: none; font-size: 14px;">
                                            View Item
                                        </a>
                                        <c:if test="${bid.bidStatus == 'WON'}">
                                            <br>
                                            <a href="#" onclick="alert('Checkout feature coming soon!')" 
                                               style="color: #28a745; text-decoration: none; font-size: 14px;">
                                                Checkout
                                            </a>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div class="no-bids">
                        <h3>No bid history</h3>
                        <p>You haven't placed any bids yet.</p>
                        <p><a href="/eBay/product/search">Start browsing</a> to find items to bid on!</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</body>
</html>