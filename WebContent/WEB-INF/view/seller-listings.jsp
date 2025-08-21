<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Listings - eBay Seller</title>
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

        .create-btn {
            background: #0064d2;
            color: white;
            text-decoration: none;
            padding: 0.75rem 1.5rem;
            border-radius: 6px;
            font-weight: 600;
            transition: background-color 0.3s;
        }

        .create-btn:hover {
            background: #0052a3;
        }

        .filters {
            background: white;
            padding: 1.5rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 2rem;
        }

        .filters-row {
            display: flex;
            gap: 1rem;
            align-items: center;
        }

        .filter-group {
            display: flex;
            flex-direction: column;
            gap: 0.5rem;
        }

        .filter-group label {
            font-size: 0.9rem;
            color: #666;
        }

        .filter-group select {
            padding: 0.5rem;
            border: 1px solid #ddd;
            border-radius: 4px;
        }

        .listings-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 2rem;
        }

        .listing-card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            overflow: hidden;
            transition: transform 0.2s;
        }

        .listing-card:hover {
            transform: translateY(-2px);
        }

        .listing-image {
            width: 100%;
            height: 200px;
            background: #f8f9fa;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #666;
            border-bottom: 1px solid #e9ecef;
        }

        .listing-content {
            padding: 1.5rem;
        }

        .listing-title {
            font-size: 1.1rem;
            font-weight: 600;
            margin-bottom: 0.5rem;
            color: #333;
        }

        .listing-price {
            font-size: 1.3rem;
            font-weight: bold;
            color: #0064d2;
            margin-bottom: 0.5rem;
        }

        .listing-details {
            font-size: 0.9rem;
            color: #666;
            margin-bottom: 1rem;
        }

        .listing-status {
            display: inline-block;
            padding: 0.25rem 0.75rem;
            border-radius: 1rem;
            font-size: 0.8rem;
            font-weight: 500;
            text-transform: uppercase;
            margin-bottom: 1rem;
        }

        .status-active {
            background-color: #d4edda;
            color: #155724;
        }

        .status-draft {
            background-color: #fff3cd;
            color: #856404;
        }

        .status-sold {
            background-color: #d1ecf1;
            color: #0c5460;
        }

        .status-ended {
            background-color: #f8d7da;
            color: #721c24;
        }

        .status-cancelled {
            background-color: #e2e3e5;
            color: #6c757d;
        }

        .listing-actions {
            display: flex;
            gap: 0.5rem;
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

        .btn-success {
            background: #28a745;
            color: white;
        }

        .btn-success:hover {
            background: #218838;
        }

        .btn-danger {
            background: #dc3545;
            color: white;
        }

        .btn-danger:hover {
            background: #c82333;
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

        .empty-state-text {
            color: #888;
        }

        @media (max-width: 768px) {
            .page-header {
                flex-direction: column;
                gap: 1rem;
                text-align: center;
            }
            
            .filters-row {
                flex-direction: column;
                align-items: stretch;
            }
            
            .listings-grid {
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
                <a href="/seller/listings" class="active">My Listings</a>
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
        <div class="page-header">
            <h1 class="page-title">My Listings</h1>
            <a href="/product/create" class="create-btn">Create New Listing</a>
        </div>

        <div class="filters">
            <form method="GET" action="/seller/listings">
                <div class="filters-row">
                    <div class="filter-group">
                        <label for="status">Filter by Status:</label>
                        <select name="status" id="status" onchange="this.form.submit()">
                            <option value="all" ${currentStatus == 'all' ? 'selected' : ''}>All Listings</option>
                            <c:forEach items="${statuses}" var="status">
                                <option value="${status.name().toLowerCase()}" 
                                        ${currentStatus == status.name().toLowerCase() ? 'selected' : ''}>
                                    ${status.name()}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </form>
        </div>

        <c:choose>
            <c:when test="${not empty products}">
                <div class="listings-grid">
                    <c:forEach items="${products}" var="product">
                        <div class="listing-card">
                            <div class="listing-image">
                                <c:choose>
                                    <c:when test="${not empty product.images}">
                                        <img src="${product.images[0].imageUrl}" alt="${product.title}" 
                                             style="width: 100%; height: 100%; object-fit: cover;">
                                    </c:when>
                                    <c:otherwise>
                                        📷 No Image
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="listing-content">
                                <h3 class="listing-title">${product.title}</h3>
                                <div class="listing-price">
                                    $<fmt:formatNumber value="${product.currentPrice}" type="number" maxFractionDigits="2"/>
                                </div>
                                <div class="listing-details">
                                    Type: ${product.listingType.name()}<br>
                                    Quantity: ${product.quantityAvailable}<br>
                                    Views: ${product.viewCount}<br>
                                    Created: <fmt:formatDate value="${product.createdDate}" pattern="MMM dd, yyyy"/>
                                </div>
                                <div class="listing-status status-${product.status.name().toLowerCase()}">
                                    ${product.status.name()}
                                </div>
                                <div class="listing-actions">
                                    <a href="/product/view/${product.productId}" class="action-btn btn-primary">View</a>
                                    <a href="/product/edit/${product.productId}" class="action-btn btn-secondary">Edit</a>
                                    <c:if test="${product.status.name() == 'DRAFT'}">
                                        <form style="display: inline;" method="POST" action="/product/activate/${product.productId}">
                                            <button type="submit" class="action-btn btn-success">Activate</button>
                                        </form>
                                    </c:if>
                                    <c:if test="${product.status.name() == 'ACTIVE'}">
                                        <form style="display: inline;" method="POST" action="/product/deactivate/${product.productId}">
                                            <button type="submit" class="action-btn btn-danger">Deactivate</button>
                                        </form>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-state-icon">📦</div>
                    <h2 class="empty-state-title">No listings found</h2>
                    <p class="empty-state-text">
                        <c:choose>
                            <c:when test="${currentStatus == 'all'}">
                                You haven't created any listings yet. Start selling by creating your first listing!
                            </c:when>
                            <c:otherwise>
                                No listings found with status: ${currentStatus}
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>