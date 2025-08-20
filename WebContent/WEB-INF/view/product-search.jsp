<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Search Results - eBay Marketplace</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
    <style>
        .search-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .search-header {
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            margin-bottom: 30px;
        }
        
        .search-bar {
            display: flex;
            gap: 15px;
            margin-bottom: 20px;
        }
        
        .search-bar input {
            flex: 1;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 5px;
            font-size: 16px;
        }
        
        .search-bar button {
            background: #0073e6;
            color: white;
            border: none;
            padding: 12px 20px;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
        }
        
        .filters {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 15px;
        }
        
        .filter-group {
            display: flex;
            flex-direction: column;
        }
        
        .filter-group label {
            font-weight: bold;
            margin-bottom: 5px;
            color: #333;
        }
        
        .filter-group select, .filter-group input {
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 3px;
        }
        
        .results-summary {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 5px;
            margin-bottom: 20px;
            text-align: center;
        }
        
        .products-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        
        .product-card {
            background: white;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            transition: transform 0.3s, box-shadow 0.3s;
            text-decoration: none;
            color: inherit;
        }
        
        .product-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 5px 20px rgba(0,0,0,0.15);
            text-decoration: none;
            color: inherit;
        }
        
        .product-image {
            width: 100%;
            height: 200px;
            background: #f8f9fa;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 48px;
            color: #ccc;
        }
        
        .product-info {
            padding: 15px;
        }
        
        .product-title {
            font-weight: bold;
            margin-bottom: 5px;
            color: #333;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
        
        .product-price {
            font-size: 18px;
            font-weight: bold;
            color: #0073e6;
            margin-bottom: 5px;
        }
        
        .product-condition {
            font-size: 14px;
            color: #666;
            margin-bottom: 5px;
        }
        
        .product-listing-type {
            font-size: 12px;
            background: #e9ecef;
            color: #495057;
            padding: 3px 8px;
            border-radius: 12px;
            display: inline-block;
        }
        
        .auction-info {
            font-size: 12px;
            color: #dc3545;
            margin-top: 5px;
        }
        
        .no-results {
            text-align: center;
            padding: 60px 20px;
            color: #666;
        }
        
        .no-results h3 {
            margin-bottom: 15px;
            color: #333;
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
        
        .breadcrumb a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="search-container">
        <!-- Breadcrumb -->
        <div class="breadcrumb">
            <a href="/eBay/">Home</a> &gt; Search Results
            <c:if test="${not empty searchTerm}">
                for "${searchTerm}"
            </c:if>
        </div>
        
        <!-- Search Header -->
        <div class="search-header">
            <h2 style="margin-bottom: 20px;">Search Products</h2>
            
            <form method="GET" action="/eBay/product/search">
                <div class="search-bar">
                    <input type="text" name="q" value="${searchTerm}" 
                           placeholder="Search for anything..." />
                    <button type="submit">Search</button>
                </div>
                
                <div class="filters">
                    <div class="filter-group">
                        <label>Category</label>
                        <select name="category">
                            <option value="">All Categories</option>
                            <c:forEach items="${categories}" var="category">
                                <option value="${category.categoryId}" 
                                        ${category.categoryId == selectedCategory ? 'selected' : ''}>
                                    ${category.categoryName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="filter-group">
                        <label>Min Price</label>
                        <input type="number" name="minPrice" value="${minPrice}" 
                               step="0.01" min="0" placeholder="Min" />
                    </div>
                    
                    <div class="filter-group">
                        <label>Max Price</label>
                        <input type="number" name="maxPrice" value="${maxPrice}" 
                               step="0.01" min="0" placeholder="Max" />
                    </div>
                    
                    <div class="filter-group">
                        <label>Condition</label>
                        <select name="condition">
                            <option value="">Any Condition</option>
                            <c:forEach items="${conditions}" var="condition">
                                <option value="${condition}" 
                                        ${condition == selectedCondition ? 'selected' : ''}>
                                    <c:choose>
                                        <c:when test="${condition == 'NEW'}">New</c:when>
                                        <c:when test="${condition == 'LIKE_NEW'}">Like New</c:when>
                                        <c:when test="${condition == 'VERY_GOOD'}">Very Good</c:when>
                                        <c:when test="${condition == 'GOOD'}">Good</c:when>
                                        <c:when test="${condition == 'ACCEPTABLE'}">Acceptable</c:when>
                                        <c:when test="${condition == 'FOR_PARTS'}">For Parts</c:when>
                                        <c:otherwise>${condition}</c:otherwise>
                                    </c:choose>
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="filter-group">
                        <label>Listing Type</label>
                        <select name="listingType">
                            <option value="">All Types</option>
                            <c:forEach items="${listingTypes}" var="type">
                                <option value="${type}" 
                                        ${type == selectedListingType ? 'selected' : ''}>
                                    <c:choose>
                                        <c:when test="${type == 'BUY_NOW'}">Buy It Now</c:when>
                                        <c:when test="${type == 'AUCTION'}">Auction</c:when>
                                        <c:when test="${type == 'BOTH'}">Auction + Buy Now</c:when>
                                        <c:otherwise>${type}</c:otherwise>
                                    </c:choose>
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </form>
        </div>
        
        <!-- Results Summary -->
        <div class="results-summary">
            <c:choose>
                <c:when test="${not empty products}">
                    <strong>${products.size()} results found</strong>
                    <c:if test="${not empty searchTerm}">
                        for "<em>${searchTerm}</em>"
                    </c:if>
                </c:when>
                <c:otherwise>
                    <strong>No results found</strong>
                    <c:if test="${not empty searchTerm}">
                        for "<em>${searchTerm}</em>"
                    </c:if>
                </c:otherwise>
            </c:choose>
        </div>
        
        <!-- Results -->
        <c:choose>
            <c:when test="${not empty products}">
                <div class="products-grid">
                    <c:forEach items="${products}" var="product">
                        <a href="/eBay/product/view/${product.productId}" class="product-card">
                            <div class="product-image">
                                📦
                            </div>
                            <div class="product-info">
                                <div class="product-title">${product.title}</div>
                                <div class="product-price">
                                    $<fmt:formatNumber value="${product.currentPrice}" pattern="#,##0.00"/>
                                    <c:if test="${product.shippingCost > 0}">
                                        + $<fmt:formatNumber value="${product.shippingCost}" pattern="#,##0.00"/> shipping
                                    </c:if>
                                </div>
                                <div class="product-condition">
                                    <c:choose>
                                        <c:when test="${product.conditionType == 'NEW'}">New</c:when>
                                        <c:when test="${product.conditionType == 'LIKE_NEW'}">Like New</c:when>
                                        <c:when test="${product.conditionType == 'VERY_GOOD'}">Very Good</c:when>
                                        <c:when test="${product.conditionType == 'GOOD'}">Good</c:when>
                                        <c:when test="${product.conditionType == 'ACCEPTABLE'}">Acceptable</c:when>
                                        <c:when test="${product.conditionType == 'FOR_PARTS'}">For Parts</c:when>
                                        <c:otherwise>${product.conditionType}</c:otherwise>
                                    </c:choose>
                                </div>
                                <span class="product-listing-type">
                                    <c:choose>
                                        <c:when test="${product.listingType == 'BUY_NOW'}">Buy It Now</c:when>
                                        <c:when test="${product.listingType == 'AUCTION'}">Auction</c:when>
                                        <c:when test="${product.listingType == 'BOTH'}">Auction + Buy Now</c:when>
                                        <c:otherwise>${product.listingType}</c:otherwise>
                                    </c:choose>
                                </span>
                                <c:if test="${product.listingType == 'AUCTION' || product.listingType == 'BOTH'}">
                                    <c:if test="${product.auctionActive}">
                                        <div class="auction-info">
                                            ⏰ ${product.formattedTimeRemaining} left
                                        </div>
                                    </c:if>
                                </c:if>
                            </div>
                        </a>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="no-results">
                    <h3>No products found</h3>
                    <p>Try adjusting your search terms or filters</p>
                    <p><a href="/eBay/">← Back to Home</a></p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>