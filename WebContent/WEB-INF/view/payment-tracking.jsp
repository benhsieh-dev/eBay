<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Payment Tracking - eBay</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .tracking-container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .tracking-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 2px solid #e5e5e5;
        }
        
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 30px;
        }
        
        .stat-card {
            background: white;
            border-radius: 8px;
            padding: 20px;
            text-align: center;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
            border: 1px solid #e9ecef;
        }
        
        .stat-value {
            font-size: 32px;
            font-weight: bold;
            margin-bottom: 8px;
        }
        
        .stat-label {
            color: #666;
            font-size: 14px;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        
        .stat-card.total .stat-value { color: #0066cc; }
        .stat-card.completed .stat-value { color: #28a745; }
        .stat-card.failed .stat-value { color: #dc3545; }
        .stat-card.pending .stat-value { color: #ffc107; }
        
        .filters {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            border: 1px solid #dee2e6;
        }
        
        .filter-row {
            display: flex;
            gap: 15px;
            align-items: end;
            flex-wrap: wrap;
        }
        
        .filter-group {
            flex: 1;
            min-width: 200px;
        }
        
        .filter-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: 600;
            color: #333;
        }
        
        .filter-group input,
        .filter-group select {
            width: 100%;
            padding: 8px 12px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 14px;
        }
        
        .filter-btn {
            background: #007bff;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
            height: fit-content;
        }
        
        .filter-btn:hover {
            background: #0056b3;
        }
        
        .payments-table {
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0,0,0,0.1);
        }
        
        .payments-table table {
            width: 100%;
            border-collapse: collapse;
        }
        
        .payments-table th,
        .payments-table td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #dee2e6;
        }
        
        .payments-table th {
            background: #f8f9fa;
            font-weight: 600;
            color: #333;
            position: sticky;
            top: 0;
            z-index: 10;
        }
        
        .payments-table tbody tr:hover {
            background: #f5f5f5;
        }
        
        .status-badge {
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            text-transform: uppercase;
        }
        
        .status-completed { background: #d4edda; color: #155724; }
        .status-pending { background: #fff3cd; color: #856404; }
        .status-failed { background: #f8d7da; color: #721c24; }
        .status-refunded { background: #d1ecf1; color: #0c5460; }
        
        .payment-method {
            display: inline-flex;
            align-items: center;
            gap: 5px;
        }
        
        .payment-method i {
            font-size: 16px;
        }
        
        .amount {
            font-weight: 600;
            color: #333;
        }
        
        .amount.negative {
            color: #dc3545;
        }
        
        .action-btn {
            padding: 6px 12px;
            background: #6c757d;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 12px;
            margin: 0 2px;
        }
        
        .action-btn:hover {
            background: #5a6268;
        }
        
        .action-btn.refund {
            background: #ffc107;
            color: #000;
        }
        
        .action-btn.refund:hover {
            background: #e0a800;
        }
        
        .action-btn.details {
            background: #17a2b8;
        }
        
        .action-btn.details:hover {
            background: #138496;
        }
        
        .no-payments {
            text-align: center;
            padding: 40px;
            color: #666;
        }
        
        .no-payments i {
            font-size: 48px;
            margin-bottom: 15px;
            color: #ccc;
        }
        
        .pagination {
            display: flex;
            justify-content: center;
            margin-top: 20px;
        }
        
        .pagination a,
        .pagination span {
            padding: 8px 12px;
            margin: 0 2px;
            text-decoration: none;
            border: 1px solid #dee2e6;
            color: #007bff;
        }
        
        .pagination .current {
            background: #007bff;
            color: white;
        }
        
        .pagination a:hover {
            background: #e9ecef;
        }
        
        .export-btn {
            background: #28a745;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 14px;
        }
        
        .export-btn:hover {
            background: #218838;
        }
        
        @media (max-width: 768px) {
            .tracking-header {
                flex-direction: column;
                gap: 15px;
            }
            
            .filter-row {
                flex-direction: column;
            }
            
            .filter-group {
                min-width: 100%;
            }
            
            .stats-grid {
                grid-template-columns: repeat(2, 1fr);
            }
            
            .payments-table {
                overflow-x: auto;
            }
            
            .payments-table table {
                min-width: 800px;
            }
        }
    </style>
</head>
<body>
    <jsp:include page="header.jsp"/>
    
    <div class="tracking-container">
        <div class="tracking-header">
            <div>
                <h1>Payment Tracking</h1>
                <p>
                    <c:choose>
                        <c:when test="${param.view == 'seller'}">
                            Manage your sales and payments received
                        </c:when>
                        <c:otherwise>
                            Track your purchases and payment history
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>
            <div>
                <button class="export-btn" onclick="exportPayments()">
                    📊 Export Data
                </button>
            </div>
        </div>
        
        <!-- Statistics Cards -->
        <div class="stats-grid">
            <div class="stat-card total">
                <div class="stat-value">${trackingInfo.totalPayments}</div>
                <div class="stat-label">Total Payments</div>
            </div>
            <div class="stat-card completed">
                <div class="stat-value">${trackingInfo.completedPayments}</div>
                <div class="stat-label">Completed</div>
            </div>
            <div class="stat-card failed">
                <div class="stat-value">${trackingInfo.failedPayments}</div>
                <div class="stat-label">Failed</div>
            </div>
            <div class="stat-card pending">
                <div class="stat-value">${trackingInfo.pendingPayments}</div>
                <div class="stat-label">Pending</div>
            </div>
            <div class="stat-card">
                <div class="stat-value">$<fmt:formatNumber value="${trackingInfo.totalAmount}" type="number" maxFractionDigits="2"/></div>
                <div class="stat-label">Total Amount</div>
            </div>
            <div class="stat-card">
                <div class="stat-value"><fmt:formatNumber value="${trackingInfo.successRate}" type="number" maxFractionDigits="1"/>%</div>
                <div class="stat-label">Success Rate</div>
            </div>
        </div>
        
        <!-- Filters -->
        <div class="filters">
            <form method="get" id="filter-form">
                <input type="hidden" name="view" value="${param.view}">
                <div class="filter-row">
                    <div class="filter-group">
                        <label for="status">Status</label>
                        <select name="status" id="status">
                            <option value="">All Statuses</option>
                            <option value="COMPLETED" ${param.status == 'COMPLETED' ? 'selected' : ''}>Completed</option>
                            <option value="PENDING" ${param.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                            <option value="FAILED" ${param.status == 'FAILED' ? 'selected' : ''}>Failed</option>
                            <option value="REFUNDED" ${param.status == 'REFUNDED' ? 'selected' : ''}>Refunded</option>
                        </select>
                    </div>
                    <div class="filter-group">
                        <label for="method">Payment Method</label>
                        <select name="method" id="method">
                            <option value="">All Methods</option>
                            <option value="STRIPE" ${param.method == 'STRIPE' ? 'selected' : ''}>Credit Card (Stripe)</option>
                            <option value="PAYPAL" ${param.method == 'PAYPAL' ? 'selected' : ''}>PayPal</option>
                            <option value="CREDIT_CARD" ${param.method == 'CREDIT_CARD' ? 'selected' : ''}>Credit Card</option>
                        </select>
                    </div>
                    <div class="filter-group">
                        <label for="dateFrom">Date From</label>
                        <input type="date" name="dateFrom" id="dateFrom" value="${param.dateFrom}">
                    </div>
                    <div class="filter-group">
                        <label for="dateTo">Date To</label>
                        <input type="date" name="dateTo" id="dateTo" value="${param.dateTo}">
                    </div>
                    <button type="submit" class="filter-btn">Filter</button>
                </div>
            </form>
        </div>
        
        <!-- Payments Table -->
        <div class="payments-table">
            <table>
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Order ID</th>
                        <th>Amount</th>
                        <th>Method</th>
                        <th>Status</th>
                        <th>
                            <c:choose>
                                <c:when test="${param.view == 'seller'}">Buyer</c:when>
                                <c:otherwise>Seller</c:otherwise>
                            </c:choose>
                        </th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty trackingInfo.payments}">
                            <c:forEach var="payment" items="${trackingInfo.payments}">
                                <tr>
                                    <td>
                                        <fmt:formatDate value="${payment.paymentDate}" pattern="MMM dd, yyyy"/>
                                        <br>
                                        <small style="color: #666;">
                                            <fmt:formatDate value="${payment.paymentDate}" pattern="HH:mm"/>
                                        </small>
                                    </td>
                                    <td>
                                        <a href="/eBay/order/details/${payment.order.orderId}" style="color: #007bff; text-decoration: none;">
                                            #${payment.order.orderId}
                                        </a>
                                    </td>
                                    <td class="amount">
                                        $<fmt:formatNumber value="${payment.paymentAmount}" type="number" maxFractionDigits="2"/>
                                        <c:if test="${payment.processingFee > 0}">
                                            <br>
                                            <small style="color: #666;">
                                                Fee: $<fmt:formatNumber value="${payment.processingFee}" type="number" maxFractionDigits="2"/>
                                            </small>
                                        </c:if>
                                    </td>
                                    <td>
                                        <div class="payment-method">
                                            <c:choose>
                                                <c:when test="${payment.paymentMethod == 'PAYPAL'}">
                                                    🔵 PayPal
                                                </c:when>
                                                <c:when test="${payment.paymentMethod == 'STRIPE'}">
                                                    💳 Stripe
                                                </c:when>
                                                <c:otherwise>
                                                    💳 ${payment.paymentMethod}
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </td>
                                    <td>
                                        <span class="status-badge status-${payment.paymentStatus.toString().toLowerCase()}">
                                            ${payment.paymentStatus}
                                        </span>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${param.view == 'seller'}">
                                                ${payment.order.buyer.firstName} ${payment.order.buyer.lastName}
                                                <br>
                                                <small style="color: #666;">${payment.order.buyer.email}</small>
                                            </c:when>
                                            <c:otherwise>
                                                ${payment.order.seller.firstName} ${payment.order.seller.lastName}
                                                <br>
                                                <small style="color: #666;">${payment.order.seller.email}</small>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <button class="action-btn details" onclick="viewPaymentDetails(${payment.paymentId})">
                                            Details
                                        </button>
                                        <c:if test="${payment.paymentStatus == 'COMPLETED' && payment.canBeRefunded() && param.view == 'seller'}">
                                            <button class="action-btn refund" onclick="initiateRefund(${payment.paymentId}, ${payment.paymentAmount})">
                                                Refund
                                            </button>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="7" class="no-payments">
                                    <div>
                                        <i>💳</i>
                                        <h3>No payments found</h3>
                                        <p>No payments match your current filters.</p>
                                    </div>
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
        
        <!-- Pagination would go here -->
        <div class="pagination">
            <a href="#">‹ Previous</a>
            <span class="current">1</span>
            <a href="#">2</a>
            <a href="#">3</a>
            <a href="#">Next ›</a>
        </div>
    </div>
    
    <jsp:include page="footer.jsp"/>
    
    <script>
        function viewPaymentDetails(paymentId) {
            window.location.href = '/eBay/payment/details/' + paymentId;
        }
        
        function initiateRefund(paymentId, amount) {
            const reason = prompt('Please enter a reason for the refund:');
            if (reason) {
                const refundAmount = prompt('Enter refund amount (max: $' + amount + '):', amount);
                if (refundAmount && !isNaN(refundAmount) && refundAmount <= amount) {
                    if (confirm('Are you sure you want to refund $' + refundAmount + '?')) {
                        fetch('/eBay/payment/refund', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded',
                            },
                            body: 'paymentId=' + paymentId + '&refundAmount=' + refundAmount + '&reason=' + encodeURIComponent(reason)
                        })
                        .then(response => response.json())
                        .then(data => {
                            if (data.success) {
                                alert('Refund initiated successfully!');
                                location.reload();
                            } else {
                                alert('Refund failed: ' + data.message);
                            }
                        })
                        .catch(error => {
                            alert('Error processing refund: ' + error.message);
                        });
                    }
                }
            }
        }
        
        function exportPayments() {
            // Build export URL with current filters
            const form = document.getElementById('filter-form');
            const formData = new FormData(form);
            const params = new URLSearchParams(formData);
            params.append('export', 'csv');
            
            window.location.href = '/eBay/payment/export?' + params.toString();
        }
        
        // Auto-refresh pending payments every 30 seconds
        if (document.querySelector('.status-pending')) {
            setInterval(() => {
                location.reload();
            }, 30000);
        }
    </script>
</body>
</html>