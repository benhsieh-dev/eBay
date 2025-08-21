<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Seller Dashboard - eBay</title>
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

        .dashboard-header {
            background: white;
            padding: 2rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 2rem;
        }

        .dashboard-title {
            font-size: 2rem;
            margin-bottom: 0.5rem;
            color: #333;
        }

        .dashboard-subtitle {
            color: #666;
            font-size: 1.1rem;
        }

        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2rem;
        }

        .stat-card {
            background: white;
            padding: 1.5rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            text-align: center;
            transition: transform 0.2s;
        }

        .stat-card:hover {
            transform: translateY(-2px);
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

        .recent-orders-table {
            width: 100%;
            border-collapse: collapse;
        }

        .recent-orders-table th,
        .recent-orders-table td {
            padding: 0.75rem;
            text-align: left;
            border-bottom: 1px solid #e9ecef;
        }

        .recent-orders-table th {
            background-color: #f8f9fa;
            font-weight: 600;
            color: #495057;
        }

        .status-badge {
            display: inline-block;
            padding: 0.25rem 0.75rem;
            border-radius: 1rem;
            font-size: 0.8rem;
            font-weight: 500;
            text-transform: uppercase;
        }

        .status-pending {
            background-color: #fff3cd;
            color: #856404;
        }

        .status-processing {
            background-color: #d1ecf1;
            color: #0c5460;
        }

        .status-shipped {
            background-color: #d4edda;
            color: #155724;
        }

        .status-completed {
            background-color: #d1ecf1;
            color: #0c5460;
        }

        .recent-products-list {
            list-style: none;
        }

        .recent-products-list li {
            padding: 0.75rem 0;
            border-bottom: 1px solid #e9ecef;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .recent-products-list li:last-child {
            border-bottom: none;
        }

        .product-name {
            font-weight: 500;
            color: #333;
        }

        .product-status {
            font-size: 0.8rem;
            color: #666;
        }

        .quick-actions {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1rem;
        }

        .action-btn {
            display: block;
            background: #0064d2;
            color: white;
            text-decoration: none;
            padding: 1rem;
            border-radius: 8px;
            text-align: center;
            font-weight: 600;
            transition: background-color 0.3s;
        }

        .action-btn:hover {
            background: #0052a3;
        }

        .action-btn.secondary {
            background: #6c757d;
        }

        .action-btn.secondary:hover {
            background: #545b62;
        }

        @media (max-width: 768px) {
            .content-grid {
                grid-template-columns: 1fr;
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
                <a href="/seller/dashboard" class="active">Dashboard</a>
                <a href="/seller/listings">My Listings</a>
                <a href="/seller/orders">Orders</a>
                <a href="/seller/inventory">Inventory</a>
                <a href="/seller/analytics">Analytics</a>
                <a href="/seller/auctions">Auctions</a>
                <a href="/product/create">List Item</a>
                <a href="/user/profile">Profile</a>
                <a href="/user/logout">Logout</a>
            </nav>
        </div>
    </header>

    <div class="container">
        <div class="dashboard-header">
            <h1 class="dashboard-title">Welcome back, ${currentUser.firstName}!</h1>
            <p class="dashboard-subtitle">Here's an overview of your seller activity</p>
        </div>

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-number">${activeListings}</div>
                <div class="stat-label">Active Listings</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">${draftListings}</div>
                <div class="stat-label">Draft Listings</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">${soldListings}</div>
                <div class="stat-label">Sold Items</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">$<fmt:formatNumber value="${totalRevenue}" type="number" maxFractionDigits="2"/></div>
                <div class="stat-label">Total Revenue</div>
            </div>
            <div class="stat-card">
                <div class="stat-number">${pendingOrders}</div>
                <div class="stat-label">Pending Orders</div>
            </div>
        </div>

        <div class="content-grid">
            <div class="card">
                <div class="card-header">Recent Orders</div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty recentOrders}">
                            <table class="recent-orders-table">
                                <thead>
                                    <tr>
                                        <th>Order #</th>
                                        <th>Buyer</th>
                                        <th>Date</th>
                                        <th>Total</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${recentOrders}" var="order" varStatus="status">
                                        <c:if test="${status.index < 5}">
                                            <tr>
                                                <td>#${order.orderId}</td>
                                                <td>${order.buyer.firstName} ${order.buyer.lastName}</td>
                                                <td><fmt:formatDate value="${order.orderDate}" pattern="MMM dd, yyyy"/></td>
                                                <td>$<fmt:formatNumber value="${order.totalAmount}" type="number" maxFractionDigits="2"/></td>
                                                <td>
                                                    <span class="status-badge status-${order.status.name().toLowerCase()}">
                                                        ${order.status.name()}
                                                    </span>
                                                </td>
                                            </tr>
                                        </c:if>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <p style="text-align: center; color: #666; padding: 2rem;">No recent orders</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <div class="card">
                <div class="card-header">Recent Products</div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty recentProducts}">
                            <ul class="recent-products-list">
                                <c:forEach items="${recentProducts}" var="product">
                                    <li>
                                        <div>
                                            <div class="product-name">${product.title}</div>
                                            <div class="product-status">${product.status.name()}</div>
                                        </div>
                                        <div>$<fmt:formatNumber value="${product.currentPrice}" type="number" maxFractionDigits="2"/></div>
                                    </li>
                                </c:forEach>
                            </ul>
                        </c:when>
                        <c:otherwise>
                            <p style="text-align: center; color: #666; padding: 2rem;">No recent products</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <div class="card">
            <div class="card-header">Quick Actions</div>
            <div class="card-body">
                <div class="quick-actions">
                    <a href="/product/create" class="action-btn">Create New Listing</a>
                    <a href="/seller/listings?status=draft" class="action-btn secondary">Manage Drafts</a>
                    <a href="/seller/orders?status=pending_shipment" class="action-btn secondary">Process Orders</a>
                    <a href="/seller/inventory" class="action-btn secondary">Update Inventory</a>
                </div>
            </div>
        </div>
    </div>
</body>
</html>