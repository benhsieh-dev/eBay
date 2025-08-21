<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sales Analytics - eBay Seller</title>
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
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .page-title {
            font-size: 2rem;
            color: #333;
        }

        .date-selector {
            display: flex;
            align-items: center;
            gap: 1rem;
        }

        .date-selector select {
            padding: 0.5rem;
            border: 1px solid #ddd;
            border-radius: 4px;
            background: white;
        }

        .date-range {
            font-size: 0.9rem;
            color: #666;
        }

        .metrics-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2rem;
        }

        .metric-card {
            background: white;
            padding: 1.5rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            text-align: center;
        }

        .metric-value {
            font-size: 2rem;
            font-weight: bold;
            color: #0064d2;
            margin-bottom: 0.5rem;
        }

        .metric-label {
            color: #666;
            font-size: 0.9rem;
        }

        .content-grid {
            display: grid;
            grid-template-columns: 2fr 1fr;
            gap: 2rem;
            margin-bottom: 2rem;
        }

        .card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            overflow: hidden;
        }

        .card-header {
            background: #f8f9fa;
            padding: 1rem 1.5rem;
            border-bottom: 1px solid #e9ecef;
            font-weight: 600;
        }

        .card-body {
            padding: 1.5rem;
        }

        .chart-placeholder {
            height: 300px;
            background: #f8f9fa;
            border: 2px dashed #dee2e6;
            border-radius: 4px;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #6c757d;
            font-size: 1.1rem;
        }

        .top-products-list {
            list-style: none;
        }

        .top-products-list li {
            padding: 0.75rem 0;
            border-bottom: 1px solid #e9ecef;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .top-products-list li:last-child {
            border-bottom: none;
        }

        .product-rank {
            background: #0064d2;
            color: white;
            width: 24px;
            height: 24px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 0.8rem;
            font-weight: bold;
            margin-right: 0.75rem;
        }

        .product-info {
            flex: 1;
        }

        .product-name {
            font-weight: 500;
            margin-bottom: 0.25rem;
        }

        .product-price {
            font-size: 0.9rem;
            color: #666;
        }

        .product-sales {
            color: #28a745;
            font-weight: 600;
        }

        .summary-stats {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1rem;
        }

        .summary-item {
            text-align: center;
            padding: 1rem;
            background: #f8f9fa;
            border-radius: 6px;
        }

        .summary-value {
            font-size: 1.5rem;
            font-weight: bold;
            color: #0064d2;
            margin-bottom: 0.25rem;
        }

        .summary-label {
            font-size: 0.9rem;
            color: #666;
        }

        .trends {
            display: flex;
            align-items: center;
            gap: 0.5rem;
            font-size: 0.9rem;
            margin-top: 0.5rem;
        }

        .trend-up {
            color: #28a745;
        }

        .trend-down {
            color: #dc3545;
        }

        .trend-neutral {
            color: #6c757d;
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

        @media (max-width: 768px) {
            .content-grid {
                grid-template-columns: 1fr;
            }
            
            .metrics-grid {
                grid-template-columns: 1fr;
            }
            
            .page-header {
                flex-direction: column;
                gap: 1rem;
                text-align: center;
            }
            
            .summary-stats {
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
                <a href="/seller/analytics" class="active">Analytics</a>
                <a href="/seller/auctions">Auctions</a>
                <a href="/product/create">List Item</a>
                <a href="/user/profile">Profile</a>
                <a href="/user/logout">Logout</a>
            </nav>
        </div>
    </header>

    <div class="container">
        <div class="page-header">
            <h1 class="page-title">Sales Analytics</h1>
            <div class="date-selector">
                <label for="dayRange">Time Period:</label>
                <select id="dayRange" onchange="changePeriod()">
                    <option value="7" ${selectedDays == 7 ? 'selected' : ''}>Last 7 Days</option>
                    <option value="30" ${selectedDays == 30 ? 'selected' : ''}>Last 30 Days</option>
                    <option value="90" ${selectedDays == 90 ? 'selected' : ''}>Last 90 Days</option>
                    <option value="365" ${selectedDays == 365 ? 'selected' : ''}>Last Year</option>
                </select>
                <div class="date-range">
                    <fmt:formatDate value="${startDate}" pattern="MMM dd, yyyy"/> - 
                    <fmt:formatDate value="${endDate}" pattern="MMM dd, yyyy"/>
                </div>
            </div>
        </div>

        <c:choose>
            <c:when test="${totalOrders > 0}">
                <div class="metrics-grid">
                    <div class="metric-card">
                        <div class="metric-value">$<fmt:formatNumber value="${totalRevenue}" type="number" maxFractionDigits="2"/></div>
                        <div class="metric-label">Total Revenue</div>
                        <div class="trends trend-up">📈 Revenue from completed orders</div>
                    </div>
                    <div class="metric-card">
                        <div class="metric-value">${totalOrders}</div>
                        <div class="metric-label">Total Orders</div>
                        <div class="trends trend-neutral">📊 All orders in period</div>
                    </div>
                    <div class="metric-card">
                        <div class="metric-value">${totalItemsSold}</div>
                        <div class="metric-label">Items Sold</div>
                        <div class="trends trend-up">📦 Individual units sold</div>
                    </div>
                    <div class="metric-card">
                        <div class="metric-value">$<fmt:formatNumber value="${averageOrderValue}" type="number" maxFractionDigits="2"/></div>
                        <div class="metric-label">Average Order Value</div>
                        <div class="trends trend-neutral">💰 Revenue per order</div>
                    </div>
                </div>

                <div class="content-grid">
                    <div class="card">
                        <div class="card-header">Sales Trends</div>
                        <div class="card-body">
                            <div class="chart-placeholder">
                                📊 Sales chart visualization would appear here<br>
                                <small style="color: #999; margin-top: 0.5rem; display: block;">
                                    In a production system, this would show a line/bar chart of daily sales
                                </small>
                            </div>
                        </div>
                    </div>

                    <div class="card">
                        <div class="card-header">Performance Summary</div>
                        <div class="card-body">
                            <div class="summary-stats">
                                <div class="summary-item">
                                    <div class="summary-value">${selectedDays}</div>
                                    <div class="summary-label">Days Analyzed</div>
                                </div>
                                <div class="summary-item">
                                    <div class="summary-value">
                                        <c:choose>
                                            <c:when test="${selectedDays > 0}">
                                                <fmt:formatNumber value="${totalRevenue / selectedDays}" type="number" maxFractionDigits="0"/>
                                            </c:when>
                                            <c:otherwise>0</c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="summary-label">Daily Avg Revenue</div>
                                </div>
                                <div class="summary-item">
                                    <div class="summary-value">
                                        <c:choose>
                                            <c:when test="${selectedDays > 0}">
                                                <fmt:formatNumber value="${totalOrders / selectedDays}" type="number" maxFractionDigits="1"/>
                                            </c:when>
                                            <c:otherwise>0</c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="summary-label">Orders per Day</div>
                                </div>
                                <div class="summary-item">
                                    <div class="summary-value">
                                        <c:choose>
                                            <c:when name="${selectedDays > 0}">
                                                <fmt:formatNumber value="${totalItemsSold / selectedDays}" type="number" maxFractionDigits="1"/>
                                            </c:when>
                                            <c:otherwise>0</c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="summary-label">Items per Day</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <c:if test="${not empty topProducts}">
                    <div class="card">
                        <div class="card-header">Top Selling Products</div>
                        <div class="card-body">
                            <ul class="top-products-list">
                                <c:forEach items="${topProducts}" var="entry" varStatus="status">
                                    <li>
                                        <div style="display: flex; align-items: center;">
                                            <div class="product-rank">${status.index + 1}</div>
                                            <div class="product-info">
                                                <div class="product-name">${entry.key.title}</div>
                                                <div class="product-price">
                                                    $<fmt:formatNumber value="${entry.key.currentPrice}" type="number" maxFractionDigits="2"/>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="product-sales">${entry.value} sold</div>
                                    </li>
                                </c:forEach>
                            </ul>
                        </div>
                    </div>
                </c:if>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-state-icon">📊</div>
                    <h2 class="empty-state-title">No sales data available</h2>
                    <p class="empty-state-text">
                        You don't have any sales in the selected time period. 
                        Try selecting a longer time range or start promoting your listings to generate sales.
                    </p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <script>
        function changePeriod() {
            const days = document.getElementById('dayRange').value;
            window.location.href = `/seller/analytics?days=${days}`;
        }
    </script>
</body>
</html>