<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inventory Management - eBay Seller</title>
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

        .inventory-table-container {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            overflow: hidden;
        }

        .inventory-table {
            width: 100%;
            border-collapse: collapse;
        }

        .inventory-table th,
        .inventory-table td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid #e9ecef;
        }

        .inventory-table th {
            background-color: #f8f9fa;
            font-weight: 600;
            color: #495057;
            position: sticky;
            top: 0;
            z-index: 10;
        }

        .inventory-table tr:hover {
            background-color: #f8f9fa;
        }

        .product-info {
            display: flex;
            align-items: center;
            gap: 1rem;
        }

        .product-image {
            width: 60px;
            height: 60px;
            border-radius: 4px;
            object-fit: cover;
            background-color: #f8f9fa;
        }

        .product-details {
            flex: 1;
        }

        .product-title {
            font-weight: 600;
            margin-bottom: 0.25rem;
        }

        .product-sku {
            font-size: 0.9rem;
            color: #666;
        }

        .quantity-cell {
            position: relative;
        }

        .quantity-display {
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .quantity-input {
            width: 80px;
            padding: 0.5rem;
            border: 1px solid #ddd;
            border-radius: 4px;
            text-align: center;
        }

        .quantity-actions {
            display: flex;
            gap: 0.25rem;
        }

        .quantity-btn {
            padding: 0.25rem 0.5rem;
            border: none;
            border-radius: 4px;
            background: #0064d2;
            color: white;
            cursor: pointer;
            font-size: 0.8rem;
            transition: background-color 0.3s;
        }

        .quantity-btn:hover {
            background: #0052a3;
        }

        .quantity-btn.save {
            background: #28a745;
        }

        .quantity-btn.save:hover {
            background: #218838;
        }

        .quantity-btn.cancel {
            background: #6c757d;
        }

        .quantity-btn.cancel:hover {
            background: #545b62;
        }

        .stock-status {
            display: inline-block;
            padding: 0.25rem 0.75rem;
            border-radius: 1rem;
            font-size: 0.8rem;
            font-weight: 500;
        }

        .stock-in-stock {
            background-color: #d4edda;
            color: #155724;
        }

        .stock-low-stock {
            background-color: #fff3cd;
            color: #856404;
        }

        .stock-out-of-stock {
            background-color: #f8d7da;
            color: #721c24;
        }

        .actions-cell {
            white-space: nowrap;
        }

        .action-btn {
            padding: 0.25rem 0.75rem;
            border: none;
            border-radius: 4px;
            font-size: 0.8rem;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            text-align: center;
            transition: background-color 0.3s;
            margin-right: 0.25rem;
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

        .bulk-actions {
            background: white;
            padding: 1rem 1.5rem;
            border-bottom: 1px solid #e9ecef;
            display: flex;
            align-items: center;
            gap: 1rem;
        }

        .bulk-actions select {
            padding: 0.5rem;
            border: 1px solid #ddd;
            border-radius: 4px;
        }

        .apply-btn {
            background: #0064d2;
            color: white;
            border: none;
            padding: 0.5rem 1rem;
            border-radius: 4px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .apply-btn:hover {
            background: #0052a3;
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
            .inventory-table-container {
                overflow-x: auto;
            }
            
            .inventory-table {
                min-width: 800px;
            }
            
            .bulk-actions {
                flex-direction: column;
                align-items: stretch;
                gap: 0.5rem;
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
                <a href="/seller/inventory" class="active">Inventory</a>
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
            <h1 class="page-title">Inventory Management</h1>
            <p class="page-subtitle">Manage stock levels for your active buy-now listings</p>
        </div>

        <div id="alerts"></div>

        <c:choose>
            <c:when test="${not empty products}">
                <div class="inventory-table-container">
                    <div class="bulk-actions">
                        <label for="bulkAction">Bulk Actions:</label>
                        <select id="bulkAction">
                            <option value="">Select Action</option>
                            <option value="increase">Increase Stock by 10</option>
                            <option value="decrease">Decrease Stock by 10</option>
                            <option value="zero">Set Stock to 0</option>
                        </select>
                        <button class="apply-btn" onclick="applyBulkAction()">Apply to Selected</button>
                        <span style="margin-left: auto; font-size: 0.9rem; color: #666;">
                            Select items using checkboxes
                        </span>
                    </div>
                    
                    <table class="inventory-table">
                        <thead>
                            <tr>
                                <th>
                                    <input type="checkbox" id="selectAll" onchange="toggleSelectAll()">
                                </th>
                                <th>Product</th>
                                <th>Current Price</th>
                                <th>Current Stock</th>
                                <th>Stock Status</th>
                                <th>Last Updated</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${products}" var="product">
                                <tr data-product-id="${product.productId}">
                                    <td>
                                        <input type="checkbox" class="product-checkbox" value="${product.productId}">
                                    </td>
                                    <td>
                                        <div class="product-info">
                                            <c:choose>
                                                <c:when test="${not empty product.images}">
                                                    <img src="${product.images[0].imageUrl}" alt="${product.title}" class="product-image">
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="product-image" style="display: flex; align-items: center; justify-content: center; background: #f0f0f0; color: #666; font-size: 0.8rem;">
                                                        No Image
                                                    </div>
                                                </c:otherwise>
                                            </c:choose>
                                            <div class="product-details">
                                                <div class="product-title">${product.title}</div>
                                                <div class="product-sku">ID: ${product.productId}</div>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        $<fmt:formatNumber value="${product.currentPrice}" type="number" maxFractionDigits="2"/>
                                    </td>
                                    <td>
                                        <div class="quantity-cell">
                                            <div class="quantity-display" id="display-${product.productId}">
                                                <span class="quantity-value">${product.quantityAvailable}</span>
                                                <button class="quantity-btn" onclick="editQuantity(${product.productId}, ${product.quantityAvailable})">
                                                    Edit
                                                </button>
                                            </div>
                                            <div class="quantity-display" id="edit-${product.productId}" style="display: none;">
                                                <input type="number" class="quantity-input" id="input-${product.productId}" 
                                                       value="${product.quantityAvailable}" min="0">
                                                <div class="quantity-actions">
                                                    <button class="quantity-btn save" onclick="saveQuantity(${product.productId})">
                                                        Save
                                                    </button>
                                                    <button class="quantity-btn cancel" onclick="cancelEdit(${product.productId})">
                                                        Cancel
                                                    </button>
                                                </div>
                                            </div>
                                        </div>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${product.quantityAvailable == 0}">
                                                <span class="stock-status stock-out-of-stock">Out of Stock</span>
                                            </c:when>
                                            <c:when test="${product.quantityAvailable <= 5}">
                                                <span class="stock-status stock-low-stock">Low Stock</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="stock-status stock-in-stock">In Stock</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${product.updatedDate}" pattern="MMM dd, yyyy"/>
                                    </td>
                                    <td class="actions-cell">
                                        <a href="/product/view/${product.productId}" class="action-btn btn-primary">View</a>
                                        <a href="/product/edit/${product.productId}" class="action-btn btn-secondary">Edit</a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <div class="empty-state-icon">📦</div>
                    <h2 class="empty-state-title">No inventory items found</h2>
                    <p class="empty-state-text">
                        You don't have any active buy-now listings that require inventory management.
                        Create some fixed-price listings to start managing your inventory.
                    </p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <script>
        function editQuantity(productId, currentQuantity) {
            document.getElementById(`display-${productId}`).style.display = 'none';
            document.getElementById(`edit-${productId}`).style.display = 'flex';
            document.getElementById(`input-${productId}`).focus();
        }

        function cancelEdit(productId) {
            document.getElementById(`display-${productId}`).style.display = 'flex';
            document.getElementById(`edit-${productId}`).style.display = 'none';
        }

        function saveQuantity(productId) {
            const newQuantity = document.getElementById(`input-${productId}`).value;
            
            if (newQuantity < 0) {
                showAlert('Quantity cannot be negative', 'danger');
                return;
            }

            fetch('/seller/inventory/update-quantity', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `productId=${productId}&quantity=${newQuantity}`
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Update the display
                    document.querySelector(`#display-${productId} .quantity-value`).textContent = newQuantity;
                    updateStockStatus(productId, parseInt(newQuantity));
                    cancelEdit(productId);
                    showAlert(data.message, 'success');
                } else {
                    showAlert(data.message, 'danger');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showAlert('An error occurred while updating quantity', 'danger');
            });
        }

        function updateStockStatus(productId, quantity) {
            const row = document.querySelector(`tr[data-product-id="${productId}"]`);
            const statusCell = row.querySelector('.stock-status');
            
            statusCell.className = 'stock-status';
            
            if (quantity === 0) {
                statusCell.classList.add('stock-out-of-stock');
                statusCell.textContent = 'Out of Stock';
            } else if (quantity <= 5) {
                statusCell.classList.add('stock-low-stock');
                statusCell.textContent = 'Low Stock';
            } else {
                statusCell.classList.add('stock-in-stock');
                statusCell.textContent = 'In Stock';
            }
        }

        function toggleSelectAll() {
            const selectAll = document.getElementById('selectAll');
            const checkboxes = document.querySelectorAll('.product-checkbox');
            
            checkboxes.forEach(checkbox => {
                checkbox.checked = selectAll.checked;
            });
        }

        function applyBulkAction() {
            const action = document.getElementById('bulkAction').value;
            const selectedProducts = Array.from(document.querySelectorAll('.product-checkbox:checked'))
                                          .map(cb => cb.value);
            
            if (!action) {
                showAlert('Please select an action', 'danger');
                return;
            }
            
            if (selectedProducts.length === 0) {
                showAlert('Please select at least one product', 'danger');
                return;
            }
            
            if (!confirm(`Apply "${action}" to ${selectedProducts.length} selected products?`)) {
                return;
            }
            
            selectedProducts.forEach(productId => {
                const currentQuantity = parseInt(document.querySelector(`#display-${productId} .quantity-value`).textContent);
                let newQuantity;
                
                switch (action) {
                    case 'increase':
                        newQuantity = currentQuantity + 10;
                        break;
                    case 'decrease':
                        newQuantity = Math.max(0, currentQuantity - 10);
                        break;
                    case 'zero':
                        newQuantity = 0;
                        break;
                    default:
                        return;
                }
                
                // Update via AJAX
                fetch('/seller/inventory/update-quantity', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded',
                    },
                    body: `productId=${productId}&quantity=${newQuantity}`
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        document.querySelector(`#display-${productId} .quantity-value`).textContent = newQuantity;
                        document.getElementById(`input-${productId}`).value = newQuantity;
                        updateStockStatus(productId, newQuantity);
                    }
                });
            });
            
            showAlert(`Bulk action applied to ${selectedProducts.length} products`, 'success');
            
            // Clear selections
            document.getElementById('selectAll').checked = false;
            document.querySelectorAll('.product-checkbox').forEach(cb => cb.checked = false);
        }

        function showAlert(message, type) {
            const alertsContainer = document.getElementById('alerts');
            const alert = document.createElement('div');
            alert.className = `alert alert-${type}`;
            alert.textContent = message;
            
            alertsContainer.appendChild(alert);
            
            // Auto-remove after 5 seconds
            setTimeout(() => {
                alert.remove();
            }, 5000);
        }
    </script>
</body>
</html>