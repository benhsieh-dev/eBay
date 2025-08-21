<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Product - eBay</title>
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

        .nav a:hover {
            background-color: rgba(255,255,255,0.2);
        }

        .container {
            max-width: 1000px;
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

        .breadcrumb {
            color: #666;
            font-size: 0.9rem;
        }

        .breadcrumb a {
            color: #0064d2;
            text-decoration: none;
        }

        .form-container {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            overflow: hidden;
        }

        .form-section {
            padding: 2rem;
            border-bottom: 1px solid #e9ecef;
        }

        .form-section:last-child {
            border-bottom: none;
        }

        .section-title {
            font-size: 1.3rem;
            margin-bottom: 1rem;
            color: #333;
        }

        .form-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 1.5rem;
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        .form-group.full-width {
            grid-column: 1 / -1;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
            color: #333;
        }

        .form-group input,
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 1rem;
        }

        .form-group textarea {
            resize: vertical;
            min-height: 100px;
        }

        .form-group .help-text {
            font-size: 0.85rem;
            color: #666;
            margin-top: 0.25rem;
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

        .btn {
            display: inline-block;
            padding: 0.75rem 1.5rem;
            margin: 0.25rem;
            border: none;
            border-radius: 4px;
            text-decoration: none;
            text-align: center;
            cursor: pointer;
            font-size: 1rem;
            transition: background-color 0.3s;
        }

        .btn-primary {
            background: #0064d2;
            color: white;
        }

        .btn-primary:hover {
            background: #0052a3;
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

        .btn-secondary {
            background: #6c757d;
            color: white;
        }

        .btn-secondary:hover {
            background: #545b62;
        }

        .status-badge {
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

        .status-draft {
            background-color: #fff3cd;
            color: #856404;
        }

        .status-sold {
            background-color: #d1ecf1;
            color: #0c5460;
        }

        .images-section {
            margin-top: 2rem;
        }

        .images-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
            gap: 1rem;
            margin-bottom: 1rem;
        }

        .image-item {
            position: relative;
            border: 1px solid #ddd;
            border-radius: 4px;
            overflow: hidden;
        }

        .image-item img {
            width: 100%;
            height: 150px;
            object-fit: cover;
        }

        .image-actions {
            position: absolute;
            top: 0.5rem;
            right: 0.5rem;
            display: flex;
            gap: 0.25rem;
        }

        .image-btn {
            padding: 0.25rem 0.5rem;
            border: none;
            border-radius: 3px;
            font-size: 0.7rem;
            cursor: pointer;
        }

        .primary-badge {
            position: absolute;
            bottom: 0.5rem;
            left: 0.5rem;
            background: #28a745;
            color: white;
            padding: 0.25rem 0.5rem;
            border-radius: 3px;
            font-size: 0.7rem;
        }

        .add-image-form {
            background: #f8f9fa;
            padding: 1rem;
            border-radius: 4px;
            margin-top: 1rem;
        }

        .add-image-form input {
            margin-bottom: 0.5rem;
        }

        .form-actions {
            background: #f8f9fa;
            padding: 1.5rem 2rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        @media (max-width: 768px) {
            .form-grid {
                grid-template-columns: 1fr;
            }
            
            .form-actions {
                flex-direction: column;
                gap: 1rem;
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
                <a href="/product/create">List Item</a>
                <a href="/user/profile">Profile</a>
                <a href="/user/logout">Logout</a>
            </nav>
        </div>
    </header>

    <div class="container">
        <div class="page-header">
            <h1 class="page-title">Edit Product</h1>
            <div class="breadcrumb">
                <a href="/seller/dashboard">Dashboard</a> &gt; 
                <a href="/seller/listings">My Listings</a> &gt; 
                Edit Product
            </div>
        </div>

        <c:if test="${not empty message}">
            <div class="alert alert-success">${message}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <form method="POST" action="/product/update" class="form-container">
            <input type="hidden" name="productId" value="${product.productId}">
            
            <div class="form-section">
                <h2 class="section-title">
                    Basic Information
                    <span class="status-badge status-${product.status.name().toLowerCase()}" style="float: right;">
                        ${product.status.name()}
                    </span>
                </h2>
                
                <div class="form-grid">
                    <div class="form-group full-width">
                        <label for="title">Product Title *</label>
                        <input type="text" id="title" name="title" value="${product.title}" required maxlength="200">
                        <div class="help-text">Create a clear, descriptive title for your item</div>
                    </div>
                    
                    <div class="form-group full-width">
                        <label for="description">Description *</label>
                        <textarea id="description" name="description" required>${product.description}</textarea>
                        <div class="help-text">Provide detailed information about your item</div>
                    </div>
                    
                    <div class="form-group">
                        <label for="category">Category *</label>
                        <select id="category" name="category.categoryId" required>
                            <option value="">Select Category</option>
                            <c:forEach items="${categories}" var="cat">
                                <option value="${cat.categoryId}" 
                                        ${product.category.categoryId == cat.categoryId ? 'selected' : ''}>
                                    ${cat.categoryName}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="conditionType">Condition *</label>
                        <select id="conditionType" name="conditionType" required>
                            <c:forEach items="${conditions}" var="condition">
                                <option value="${condition}" 
                                        ${product.conditionType == condition ? 'selected' : ''}>
                                    ${condition.name()}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>

            <div class="form-section">
                <h2 class="section-title">Pricing & Listing</h2>
                
                <div class="form-grid">
                    <div class="form-group">
                        <label for="listingType">Listing Type *</label>
                        <select id="listingType" name="listingType" required onchange="togglePriceFields()">
                            <c:forEach items="${listingTypes}" var="type">
                                <option value="${type}" 
                                        ${product.listingType == type ? 'selected' : ''}>
                                    ${type.name()}
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="startingPrice">Starting Price *</label>
                        <input type="number" id="startingPrice" name="startingPrice" 
                               value="${product.startingPrice}" step="0.01" min="0.01" required>
                        <div class="help-text">Minimum bid for auctions, price for buy-now items</div>
                    </div>
                    
                    <div class="form-group" id="buyNowPriceGroup">
                        <label for="buyNowPrice">Buy Now Price</label>
                        <input type="number" id="buyNowPrice" name="buyNowPrice" 
                               value="${product.buyNowPrice}" step="0.01" min="0.01">
                        <div class="help-text">Optional fixed price for immediate purchase</div>
                    </div>
                    
                    <div class="form-group" id="reservePriceGroup">
                        <label for="reservePrice">Reserve Price</label>
                        <input type="number" id="reservePrice" name="reservePrice" 
                               value="${product.reservePrice}" step="0.01" min="0.01">
                        <div class="help-text">Minimum acceptable selling price (hidden from buyers)</div>
                    </div>
                </div>
            </div>

            <div class="form-section">
                <h2 class="section-title">Inventory & Shipping</h2>
                
                <div class="form-grid">
                    <div class="form-group">
                        <label for="quantityAvailable">Quantity Available</label>
                        <input type="number" id="quantityAvailable" name="quantityAvailable" 
                               value="${product.quantityAvailable}" min="0">
                        <div class="help-text">Number of items in stock</div>
                    </div>
                    
                    <div class="form-group">
                        <label for="itemLocation">Item Location</label>
                        <input type="text" id="itemLocation" name="itemLocation" 
                               value="${product.itemLocation}" maxlength="100">
                        <div class="help-text">City, State or general location</div>
                    </div>
                    
                    <div class="form-group">
                        <label for="shippingCost">Shipping Cost</label>
                        <input type="number" id="shippingCost" name="shippingCost" 
                               value="${product.shippingCost}" step="0.01" min="0">
                    </div>
                    
                    <div class="form-group">
                        <label for="shippingMethod">Shipping Method</label>
                        <input type="text" id="shippingMethod" name="shippingMethod" 
                               value="${product.shippingMethod}" maxlength="100">
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="returnPolicy">Return Policy</label>
                    <textarea id="returnPolicy" name="returnPolicy">${product.returnPolicy}</textarea>
                    <div class="help-text">Describe your return and refund policy</div>
                </div>
            </div>

            <div class="form-section" id="auctionSection" style="display: none;">
                <h2 class="section-title">Auction Settings</h2>
                
                <div class="form-grid">
                    <div class="form-group">
                        <label for="auctionStartTime">Start Time</label>
                        <input type="datetime-local" id="auctionStartTime" name="auctionStartTime" 
                               value="<fmt:formatDate value='${product.auctionStartTime}' pattern='yyyy-MM-dd\'T\'HH:mm'/>">
                    </div>
                    
                    <div class="form-group">
                        <label for="auctionEndTime">End Time *</label>
                        <input type="datetime-local" id="auctionEndTime" name="auctionEndTime" 
                               value="<fmt:formatDate value='${product.auctionEndTime}' pattern='yyyy-MM-dd\'T\'HH:mm'/>">
                    </div>
                </div>
            </div>

            <div class="form-actions">
                <div>
                    <a href="/seller/listings" class="btn btn-secondary">Cancel</a>
                    <button type="submit" class="btn btn-primary">Update Product</button>
                </div>
                <div>
                    <c:if test="${product.status.name() == 'DRAFT'}">
                        <a href="/product/activate/${product.productId}" class="btn btn-success" 
                           onclick="return confirm('Activate this product? It will become visible to buyers.')">
                            Activate Product
                        </a>
                    </c:if>
                    <c:if test="${product.status.name() == 'ACTIVE'}">
                        <a href="/product/deactivate/${product.productId}" class="btn btn-danger" 
                           onclick="return confirm('Deactivate this product? It will be hidden from buyers.')">
                            Deactivate Product
                        </a>
                    </c:if>
                </div>
            </div>
        </form>

        <!-- Product Images Section -->
        <div class="form-container images-section">
            <div class="form-section">
                <h2 class="section-title">Product Images</h2>
                
                <c:choose>
                    <c:when test="${not empty images}">
                        <div class="images-grid">
                            <c:forEach items="${images}" var="image">
                                <div class="image-item">
                                    <img src="${image.imageUrl}" alt="${image.altText}">
                                    <div class="image-actions">
                                        <c:if test="${!image.isPrimary}">
                                            <button class="image-btn btn-success" 
                                                    onclick="setPrimaryImage(${image.imageId})">
                                                Primary
                                            </button>
                                        </c:if>
                                        <button class="image-btn btn-danger" 
                                                onclick="deleteImage(${image.imageId})">
                                            Delete
                                        </button>
                                    </div>
                                    <c:if test="${image.isPrimary}">
                                        <div class="primary-badge">Primary</div>
                                    </c:if>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p style="color: #666; text-align: center; padding: 2rem;">
                            No images uploaded yet. Add at least one image to activate your listing.
                        </p>
                    </c:otherwise>
                </c:choose>
                
                <div class="add-image-form">
                    <h3>Add New Image</h3>
                    <input type="url" id="imageUrl" placeholder="Enter image URL" required>
                    <input type="text" id="altText" placeholder="Image description (optional)">
                    <label>
                        <input type="checkbox" id="isPrimary"> Set as primary image
                    </label>
                    <button type="button" class="btn btn-primary" onclick="addImage()">Add Image</button>
                </div>
            </div>
        </div>
    </div>

    <script>
        function togglePriceFields() {
            const listingType = document.getElementById('listingType').value;
            const buyNowGroup = document.getElementById('buyNowPriceGroup');
            const reserveGroup = document.getElementById('reservePriceGroup');
            const auctionSection = document.getElementById('auctionSection');
            
            if (listingType === 'AUCTION' || listingType === 'BOTH') {
                auctionSection.style.display = 'block';
                reserveGroup.style.display = 'block';
            } else {
                auctionSection.style.display = 'none';
                reserveGroup.style.display = 'none';
            }
            
            if (listingType === 'BUY_NOW' || listingType === 'BOTH') {
                buyNowGroup.style.display = 'block';
            } else {
                buyNowGroup.style.display = 'none';
            }
        }

        function addImage() {
            const imageUrl = document.getElementById('imageUrl').value;
            const altText = document.getElementById('altText').value;
            const isPrimary = document.getElementById('isPrimary').checked;
            
            if (!imageUrl) {
                alert('Please enter an image URL');
                return;
            }
            
            fetch('/product/add-image', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `productId=${${product.productId}}&imageUrl=${encodeURIComponent(imageUrl)}&altText=${encodeURIComponent(altText)}&isPrimary=${isPrimary}`
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    location.reload();
                } else {
                    alert(data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred while adding the image');
            });
        }

        function deleteImage(imageId) {
            if (!confirm('Are you sure you want to delete this image?')) {
                return;
            }
            
            fetch(`/product/delete-image/${imageId}`, {
                method: 'POST'
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    location.reload();
                } else {
                    alert(data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred while deleting the image');
            });
        }

        function setPrimaryImage(imageId) {
            fetch(`/product/set-primary-image/${imageId}`, {
                method: 'POST'
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    location.reload();
                } else {
                    alert(data.message);
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('An error occurred while setting primary image');
            });
        }

        // Initialize form on page load
        document.addEventListener('DOMContentLoaded', function() {
            togglePriceFields();
        });
    </script>
</body>
</html>