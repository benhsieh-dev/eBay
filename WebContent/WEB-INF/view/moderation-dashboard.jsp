<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Review Moderation Dashboard - eBay</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .moderation-container {
            max-width: 1400px;
            margin: 2rem auto;
            padding: 0 1rem;
        }
        
        .moderation-header {
            background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
            color: white;
            padding: 2rem;
            border-radius: 12px;
            margin-bottom: 2rem;
            text-align: center;
        }
        
        .moderation-title {
            font-size: 2.5rem;
            margin-bottom: 0.5rem;
            font-weight: 300;
        }
        
        .moderation-subtitle {
            font-size: 1.1rem;
            opacity: 0.9;
        }
        
        .stats-overview {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2rem;
        }
        
        .stat-card {
            background: white;
            padding: 1.5rem;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            text-align: center;
            transition: transform 0.3s ease;
        }
        
        .stat-card:hover {
            transform: translateY(-2px);
        }
        
        .stat-value {
            font-size: 2.5rem;
            font-weight: bold;
            color: #e74c3c;
            margin-bottom: 0.5rem;
        }
        
        .stat-label {
            color: #7f8c8d;
            font-size: 0.9rem;
            font-weight: 500;
        }
        
        .moderation-sections {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 2rem;
            margin-bottom: 2rem;
        }
        
        .moderation-section {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            overflow: hidden;
        }
        
        .section-header {
            padding: 1.5rem;
            background: #f8f9fa;
            border-bottom: 1px solid #e9ecef;
            display: flex;
            justify-content: between;
            align-items: center;
        }
        
        .section-title {
            color: #2c3e50;
            margin: 0;
            font-size: 1.2rem;
        }
        
        .section-count {
            background: #e74c3c;
            color: white;
            padding: 0.25rem 0.75rem;
            border-radius: 15px;
            font-size: 0.8rem;
            font-weight: 500;
        }
        
        .review-list {
            max-height: 500px;
            overflow-y: auto;
        }
        
        .review-item {
            padding: 1.5rem;
            border-bottom: 1px solid #f1f2f6;
            transition: background-color 0.3s ease;
        }
        
        .review-item:hover {
            background: #f8f9fa;
        }
        
        .review-item:last-child {
            border-bottom: none;
        }
        
        .review-header {
            display: flex;
            justify-content: between;
            align-items: flex-start;
            margin-bottom: 1rem;
        }
        
        .review-info {
            flex: 1;
        }
        
        .review-id {
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 0.25rem;
        }
        
        .review-meta {
            display: flex;
            gap: 1rem;
            color: #7f8c8d;
            font-size: 0.85rem;
            margin-bottom: 0.5rem;
        }
        
        .review-rating {
            color: #f39c12;
            font-weight: 500;
        }
        
        .review-status {
            padding: 0.2rem 0.6rem;
            border-radius: 12px;
            font-size: 0.75rem;
            font-weight: 500;
            text-transform: uppercase;
        }
        
        .status-pending {
            background: #fff3cd;
            color: #856404;
        }
        
        .status-flagged {
            background: #f8d7da;
            color: #721c24;
        }
        
        .status-reported {
            background: #d1ecf1;
            color: #0c5460;
        }
        
        .review-content {
            margin-bottom: 1rem;
        }
        
        .review-title {
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 0.5rem;
        }
        
        .review-text {
            color: #5a6c7d;
            line-height: 1.5;
            margin-bottom: 0.5rem;
        }
        
        .review-flags {
            display: flex;
            gap: 0.5rem;
            flex-wrap: wrap;
            margin-bottom: 1rem;
        }
        
        .flag-badge {
            background: #e74c3c;
            color: white;
            padding: 0.2rem 0.5rem;
            border-radius: 10px;
            font-size: 0.7rem;
            font-weight: 500;
        }
        
        .moderation-actions {
            display: flex;
            gap: 0.5rem;
            flex-wrap: wrap;
        }
        
        .action-btn {
            padding: 0.4rem 0.8rem;
            border: 1px solid #ddd;
            background: white;
            color: #495057;
            border-radius: 4px;
            cursor: pointer;
            font-size: 0.8rem;
            transition: all 0.3s ease;
        }
        
        .action-btn:hover {
            background: #f8f9fa;
        }
        
        .action-btn.approve {
            border-color: #28a745;
            color: #28a745;
        }
        
        .action-btn.approve:hover {
            background: #28a745;
            color: white;
        }
        
        .action-btn.hide {
            border-color: #ffc107;
            color: #ffc107;
        }
        
        .action-btn.hide:hover {
            background: #ffc107;
            color: white;
        }
        
        .action-btn.delete {
            border-color: #dc3545;
            color: #dc3545;
        }
        
        .action-btn.delete:hover {
            background: #dc3545;
            color: white;
        }
        
        .no-reviews {
            padding: 3rem;
            text-align: center;
            color: #7f8c8d;
        }
        
        .no-reviews i {
            font-size: 2.5rem;
            margin-bottom: 1rem;
            color: #bdc3c7;
        }
        
        .moderation-modal {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            z-index: 1000;
        }
        
        .modal-content {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            background: white;
            padding: 2rem;
            border-radius: 12px;
            max-width: 500px;
            width: 90%;
        }
        
        .modal-header {
            margin-bottom: 1.5rem;
        }
        
        .modal-title {
            color: #2c3e50;
            margin-bottom: 0.5rem;
        }
        
        .form-group {
            margin-bottom: 1rem;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
            color: #2c3e50;
        }
        
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 0.5rem;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 0.9rem;
        }
        
        .form-group textarea {
            min-height: 80px;
            resize: vertical;
        }
        
        .modal-actions {
            display: flex;
            gap: 1rem;
            justify-content: flex-end;
        }
        
        .btn {
            padding: 0.5rem 1.5rem;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 0.9rem;
            font-weight: 500;
            transition: all 0.3s ease;
        }
        
        .btn-primary {
            background: #3498db;
            color: white;
        }
        
        .btn-primary:hover {
            background: #2980b9;
        }
        
        .btn-secondary {
            background: #6c757d;
            color: white;
        }
        
        .btn-secondary:hover {
            background: #5a6268;
        }
        
        @media (max-width: 768px) {
            .moderation-container {
                margin: 1rem auto;
                padding: 0 0.5rem;
            }
            
            .moderation-sections {
                grid-template-columns: 1fr;
            }
            
            .stats-overview {
                grid-template-columns: repeat(2, 1fr);
            }
            
            .review-header {
                flex-direction: column;
                align-items: flex-start;
                gap: 0.5rem;
            }
            
            .review-meta {
                flex-direction: column;
                gap: 0.25rem;
            }
            
            .moderation-actions {
                justify-content: flex-start;
            }
        }
    </style>
</head>
<body>
    <div class="moderation-container">
        <!-- Header -->
        <div class="moderation-header">
            <h1 class="moderation-title">Review Moderation</h1>
            <p class="moderation-subtitle">Monitor and manage review content quality</p>
        </div>
        
        <!-- Statistics Overview -->
        <div class="stats-overview">
            <div class="stat-card">
                <div class="stat-value">${stats.pendingCount}</div>
                <div class="stat-label">Pending Reviews</div>
            </div>
            <div class="stat-card">
                <div class="stat-value">${stats.flaggedCount}</div>
                <div class="stat-label">Flagged Reviews</div>
            </div>
            <div class="stat-card">
                <div class="stat-value">${stats.totalRequiringAttention}</div>
                <div class="stat-label">Require Attention</div>
            </div>
            <div class="stat-card">
                <div class="stat-value">
                    <fmt:formatNumber value="${stats.approvalRate}" maxFractionDigits="0"/>%
                </div>
                <div class="stat-label">Approval Rate</div>
            </div>
        </div>
        
        <!-- Moderation Sections -->
        <div class="moderation-sections">
            <!-- Pending Reviews -->
            <div class="moderation-section">
                <div class="section-header">
                    <h2 class="section-title">Pending Reviews</h2>
                    <span class="section-count">${fn:length(pendingReviews)}</span>
                </div>
                <div class="review-list">
                    <c:choose>
                        <c:when test="${empty pendingReviews}">
                            <div class="no-reviews">
                                <i>✅</i>
                                <h3>All caught up!</h3>
                                <p>No pending reviews to moderate</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="review" items="${pendingReviews}">
                                <div class="review-item">
                                    <div class="review-header">
                                        <div class="review-info">
                                            <div class="review-id">Review #${review.reviewId}</div>
                                            <div class="review-meta">
                                                <span>By: ${review.reviewer.fullName}</span>
                                                <span class="review-rating">★ ${review.rating}</span>
                                                <span><fmt:formatDate value="${review.createdAt}" pattern="MMM dd, yyyy"/></span>
                                            </div>
                                        </div>
                                        <span class="review-status status-pending">Pending</span>
                                    </div>
                                    
                                    <div class="review-content">
                                        <c:if test="${not empty review.title}">
                                            <div class="review-title">${review.title}</div>
                                        </c:if>
                                        <c:if test="${not empty review.comment}">
                                            <div class="review-text">${review.comment}</div>
                                        </c:if>
                                    </div>
                                    
                                    <div class="moderation-actions">
                                        <button class="action-btn approve" onclick="moderateReview(${review.reviewId}, 'APPROVE')">
                                            Approve
                                        </button>
                                        <button class="action-btn hide" onclick="moderateReview(${review.reviewId}, 'HIDE')">
                                            Hide
                                        </button>
                                        <button class="action-btn delete" onclick="moderateReview(${review.reviewId}, 'DELETE')">
                                            Delete
                                        </button>
                                        <button class="action-btn" onclick="showModerationModal(${review.reviewId})">
                                            More Actions
                                        </button>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            
            <!-- Flagged Reviews -->
            <div class="moderation-section">
                <div class="section-header">
                    <h2 class="section-title">Flagged Reviews</h2>
                    <span class="section-count">${fn:length(flaggedReviews)}</span>
                </div>
                <div class="review-list">
                    <c:choose>
                        <c:when test="${empty flaggedReviews}">
                            <div class="no-reviews">
                                <i>🎯</i>
                                <h3>No flagged reviews</h3>
                                <p>All reviews are clean</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="review" items="${flaggedReviews}">
                                <div class="review-item">
                                    <div class="review-header">
                                        <div class="review-info">
                                            <div class="review-id">Review #${review.reviewId}</div>
                                            <div class="review-meta">
                                                <span>By: ${review.reviewer.fullName}</span>
                                                <span class="review-rating">★ ${review.rating}</span>
                                                <span><fmt:formatDate value="${review.createdAt}" pattern="MMM dd, yyyy"/></span>
                                            </div>
                                        </div>
                                        <span class="review-status status-flagged">Flagged</span>
                                    </div>
                                    
                                    <c:if test="${not empty review.flaggedReason}">
                                        <div class="review-flags">
                                            <span class="flag-badge">${review.flaggedReason}</span>
                                        </div>
                                    </c:if>
                                    
                                    <div class="review-content">
                                        <c:if test="${not empty review.title}">
                                            <div class="review-title">${review.title}</div>
                                        </c:if>
                                        <c:if test="${not empty review.comment}">
                                            <div class="review-text">${review.comment}</div>
                                        </c:if>
                                    </div>
                                    
                                    <div class="moderation-actions">
                                        <button class="action-btn approve" onclick="moderateReview(${review.reviewId}, 'APPROVE')">
                                            Approve
                                        </button>
                                        <button class="action-btn hide" onclick="moderateReview(${review.reviewId}, 'HIDE')">
                                            Hide
                                        </button>
                                        <button class="action-btn delete" onclick="moderateReview(${review.reviewId}, 'DELETE')">
                                            Delete
                                        </button>
                                        <button class="action-btn" onclick="showModerationModal(${review.reviewId})">
                                            More Actions
                                        </button>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Moderation Modal -->
    <div id="moderationModal" class="moderation-modal">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title">Moderate Review</h3>
                <p>Choose an action and provide a reason</p>
            </div>
            
            <form id="moderationForm">
                <input type="hidden" id="modalReviewId" value="">
                
                <div class="form-group">
                    <label for="moderationAction">Action:</label>
                    <select id="moderationAction" required>
                        <option value="">Select action...</option>
                        <c:forEach var="action" items="${moderationActions}">
                            <option value="${action}">${action.displayName}</option>
                        </c:forEach>
                    </select>
                </div>
                
                <div class="form-group">
                    <label for="moderationReason">Reason:</label>
                    <textarea id="moderationReason" placeholder="Explain the reason for this action..." required></textarea>
                </div>
                
                <div class="modal-actions">
                    <button type="button" class="btn btn-secondary" onclick="closeModerationModal()">Cancel</button>
                    <button type="submit" class="btn btn-primary">Apply Action</button>
                </div>
            </form>
        </div>
    </div>
    
    <script>
        function moderateReview(reviewId, action) {
            if (!confirm('Are you sure you want to ' + action.toLowerCase() + ' this review?')) {
                return;
            }
            
            const formData = new FormData();
            formData.append('reviewId', reviewId);
            formData.append('action', action);
            formData.append('reason', 'Quick action: ' + action);
            
            fetch('${pageContext.request.contextPath}/moderation/api/moderate', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Review moderated successfully');
                    location.reload();
                } else {
                    alert(data.message || 'Error moderating review');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error moderating review');
            });
        }
        
        function showModerationModal(reviewId) {
            document.getElementById('modalReviewId').value = reviewId;
            document.getElementById('moderationModal').style.display = 'block';
        }
        
        function closeModerationModal() {
            document.getElementById('moderationModal').style.display = 'none';
            document.getElementById('moderationForm').reset();
        }
        
        // Modal form submission
        document.getElementById('moderationForm').addEventListener('submit', function(e) {
            e.preventDefault();
            
            const reviewId = document.getElementById('modalReviewId').value;
            const action = document.getElementById('moderationAction').value;
            const reason = document.getElementById('moderationReason').value;
            
            const formData = new FormData();
            formData.append('reviewId', reviewId);
            formData.append('action', action);
            formData.append('reason', reason);
            
            fetch('${pageContext.request.contextPath}/moderation/api/moderate', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Review moderated successfully');
                    closeModerationModal();
                    location.reload();
                } else {
                    alert(data.message || 'Error moderating review');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error moderating review');
            });
        });
        
        // Close modal when clicking outside
        window.addEventListener('click', function(e) {
            const modal = document.getElementById('moderationModal');
            if (e.target === modal) {
                closeModerationModal();
            }
        });
        
        // Auto-refresh every 30 seconds
        setInterval(function() {
            fetch('${pageContext.request.contextPath}/moderation/api/stats')
            .then(response => response.json())
            .then(data => {
                if (data.success && (data.pendingCount > 0 || data.flaggedCount > 0)) {
                    // Show notification badge or update counts
                    console.log('New items requiring moderation:', data.totalRequiringAttention);
                }
            })
            .catch(error => console.error('Error checking for updates:', error));
        }, 30000);
    </script>
</body>
</html>