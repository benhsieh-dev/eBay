<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Order Management - eBay Seller</title>
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

        .orders-table-container {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            overflow: hidden;
        }

        .orders-table {
            width: 100%;
            border-collapse: collapse;
        }

        .orders-table th,
        .orders-table td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid #e9ecef;
        }

        .orders-table th {
            background-color: #f8f9fa;
            font-weight: 600;
            color: #495057;
        }

        .orders-table tr:hover {
            background-color: #f8f9fa;
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
            background-color: #cce5ff;
            color: #004085;
        }

        .status-shipped {
            background-color: #d1ecf1;
            color: #0c5460;
        }

        .status-delivered {
            background-color: #d4edda;
            color: #155724;
        }

        .status-completed {
            background-color: #d4edda;
            color: #155724;
        }

        .status-cancelled {
            background-color: #f8d7da;
            color: #721c24;
        }

        .status-pending_shipment {
            background-color: #fff3cd;
            color: #856404;
        }

        .order-actions {
            display: flex;
            gap: 0.5rem;
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

        .update-form {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.5);
            z-index: 1000;
        }

        .update-form.show {
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .update-form-content {
            background: white;
            padding: 2rem;
            border-radius: 8px;
            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            max-width: 500px;
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

        @media (max-width: 768px) {
            .orders-table-container {
                overflow-x: auto;
            }
            
            .orders-table {
                min-width: 800px;
            }
            
            .filters-row {
                flex-direction: column;
                align-items: stretch;
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
                <a href="/seller/orders" class="active">Orders</a>
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
            <h1 class="page-title">Order Management</h1>
        </div>

        <div class="filters">
            <form method="GET" action="/seller/orders">
                <div class="filters-row">
                    <div class="filter-group">
                        <label for="status">Filter by Status:</label>
                        <select name="status" id="status" onchange="this.form.submit()">
                            <option value="all" ${currentStatus == 'all' ? 'selected' : ''}>All Orders</option>
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
            <c:when test="${not empty orders}">
                <div class="orders-table-container">
                    <table class="orders-table">
                        <thead>
                            <tr>
                                <th>Order #</th>
                                <th>Buyer</th>
                                <th>Items</th>
                                <th>Order Date</th>
                                <th>Total Amount</th>
                                <th>Status</th>
                                <th>Payment</th>
                                <th>Shipping</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${orders}" var="order">
                                <tr>
                                    <td>#${order.orderId}</td>
                                    <td>${order.buyer.firstName} ${order.buyer.lastName}</td>
                                    <td>
                                        <c:forEach items="${order.orderItems}" var="item" varStatus="status">
                                            ${item.product.title}${!status.last ? ', ' : ''}
                                        </c:forEach>
                                    </td>
                                    <td><fmt:formatDate value="${order.orderDate}" pattern="MMM dd, yyyy HH:mm"/></td>
                                    <td>$<fmt:formatNumber value="${order.totalAmount}" type="number" maxFractionDigits="2"/></td>
                                    <td>
                                        <span class="status-badge status-${order.status.name().toLowerCase()}">
                                            ${order.status.name()}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="status-badge status-${order.paymentStatus.name().toLowerCase()}">
                                            ${order.paymentStatus.name()}
                                        </span>
                                    </td>
                                    <td>
                                        <span class="status-badge status-${order.shippingStatus.name().toLowerCase()}">
                                            ${order.shippingStatus.name()}
                                        </span>
                                    </td>
                                    <td>
                                        <div class="order-actions">
                                            <a href="/order/details/${order.orderId}" class="action-btn btn-primary">View</a>
                                            <c:if test="${order.status.name() == 'PENDING' || order.status.name() == 'PROCESSING'}">
                                                <button class="action-btn btn-success" onclick="showUpdateForm(${order.orderId}, '${order.status.name()}', '${order.trackingNumber}')">
                                                    Update
                                                </button>
                                            </c:if>
                                            <c:if test="${order.status.name() == 'PENDING'}">
                                                <form style="display: inline;" method="POST" action="/seller/orders/${order.orderId}/update-status">
                                                    <input type="hidden" name="status" value="CANCELLED">
                                                    <button type="submit" class="action-btn btn-danger" 
                                                            onclick="return confirm('Are you sure you want to cancel this order?')">
                                                        Cancel
                                                    </button>
                                                </form>
                                            </c:if>
                                        </div>
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
                    <h2 class="empty-state-title">No orders found</h2>
                    <p class="empty-state-text">
                        <c:choose>
                            <c:when test="${currentStatus == 'all'}">
                                You don't have any orders yet. Orders will appear here when customers purchase your items.
                            </c:when>
                            <c:otherwise>
                                No orders found with status: ${currentStatus}
                            </c:otherwise>
                        </c:choose>
                    </p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Update Order Status Form Modal -->
    <div id="updateForm" class="update-form">
        <div class="update-form-content">
            <h3>Update Order Status</h3>
            <form id="statusForm" method="POST">
                <div class="form-group">
                    <label for="newStatus">New Status:</label>
                    <select name="status" id="newStatus" required>
                        <option value="PROCESSING">Processing</option>
                        <option value="SHIPPED">Shipped</option>
                        <option value="DELIVERED">Delivered</option>
                        <option value="COMPLETED">Completed</option>
                        <option value="CANCELLED">Cancelled</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="trackingNumber">Tracking Number (optional):</label>
                    <input type="text" name="trackingNumber" id="trackingNumber" 
                           placeholder="Enter tracking number if shipping">
                </div>
                <div class="form-actions">
                    <button type="button" class="action-btn btn-danger" onclick="closeUpdateForm()">Cancel</button>
                    <button type="submit" class="action-btn btn-success">Update Order</button>
                </div>
            </form>
        </div>
    </div>

    <script>
        function showUpdateForm(orderId, currentStatus, trackingNumber) {
            const form = document.getElementById('statusForm');
            const updateForm = document.getElementById('updateForm');
            const statusSelect = document.getElementById('newStatus');
            const trackingInput = document.getElementById('trackingNumber');
            
            form.action = `/seller/orders/${orderId}/update-status`;
            statusSelect.value = currentStatus;
            trackingInput.value = trackingNumber || '';
            
            updateForm.classList.add('show');
        }

        function closeUpdateForm() {
            const updateForm = document.getElementById('updateForm');
            updateForm.classList.remove('show');
        }

        // Close modal when clicking outside
        document.getElementById('updateForm').addEventListener('click', function(e) {
            if (e.target === this) {
                closeUpdateForm();
            }
        });
    </script>
</body>
</html>