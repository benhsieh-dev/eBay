<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User Reviews - eBay</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .reviews-container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 1rem;
        }
        
        .reviews-header {
            background: white;
            padding: 2rem;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            margin-bottom: 2rem;
        }
        
        .reviews-header h1 {
            color: #2c3e50;
            margin-bottom: 1rem;
        }
        
        .user-rating-summary {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1.5rem;
            margin-top: 1.5rem;
        }
        
        .rating-card {
            background: #f8f9fa;
            padding: 1.5rem;
            border-radius: 8px;
            text-align: center;
            border-left: 4px solid #3498db;
        }
        
        .rating-card h3 {
            color: #2c3e50;
            margin-bottom: 0.5rem;
            font-size: 1.1rem;
        }
        
        .rating-value {
            font-size: 2rem;
            font-weight: bold;
            color: #3498db;
            margin-bottom: 0.5rem;
        }
        
        .rating-stars {
            color: #f39c12;
            font-size: 1.2rem;
            margin-bottom: 0.5rem;
        }
        
        .rating-count {
            color: #7f8c8d;
            font-size: 0.9rem;
        }
        
        .filter-tabs {
            display: flex;
            gap: 1rem;
            margin-bottom: 2rem;
            flex-wrap: wrap;
        }
        
        .filter-tab {
            padding: 0.75rem 1.5rem;
            border: 2px solid #3498db;
            background: white;
            color: #3498db;
            text-decoration: none;
            border-radius: 25px;
            font-weight: 500;
            transition: all 0.3s ease;
        }
        
        .filter-tab:hover,
        .filter-tab.active {
            background: #3498db;
            color: white;
            transform: translateY(-2px);
        }
        
        .reviews-list {
            display: flex;
            flex-direction: column;
            gap: 1.5rem;
        }
        
        .review-card {
            background: white;
            padding: 2rem;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            transition: transform 0.3s ease;
        }
        
        .review-card:hover {
            transform: translateY(-2px);
        }
        
        .review-header {
            display: flex;
            justify-content: between;
            align-items: flex-start;
            margin-bottom: 1rem;
        }
        
        .review-rating {
            display: flex;
            align-items: center;
            gap: 1rem;
            margin-bottom: 1rem;
        }
        
        .review-stars {
            color: #f39c12;
            font-size: 1.2rem;
        }
        
        .review-rating-text {
            color: #7f8c8d;
            font-size: 0.9rem;
        }
        
        .review-type-badge {
            padding: 0.25rem 0.75rem;
            border-radius: 15px;
            font-size: 0.8rem;
            font-weight: 500;
            text-transform: uppercase;
        }
        
        .review-type-seller {
            background: #e8f5e8;
            color: #27ae60;
        }
        
        .review-type-buyer {
            background: #e8f4fd;
            color: #3498db;
        }
        
        .review-type-product {
            background: #fff3e0;
            color: #f39c12;
        }
        
        .review-meta {
            display: flex;
            justify-content: between;
            align-items: center;
            margin-bottom: 1rem;
            flex-wrap: wrap;
            gap: 1rem;
        }
        
        .reviewer-info {
            display: flex;
            align-items: center;
            gap: 1rem;
        }
        
        .reviewer-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: #3498db;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: bold;
        }
        
        .reviewer-name {
            font-weight: 500;
            color: #2c3e50;
        }
        
        .review-date {
            color: #7f8c8d;
            font-size: 0.9rem;
        }
        
        .review-title {
            font-size: 1.2rem;
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 0.75rem;
        }
        
        .review-comment {
            color: #5a6c7d;
            line-height: 1.6;
            margin-bottom: 1rem;
        }
        
        .review-detailed-ratings {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
            gap: 1rem;
            margin: 1rem 0;
            padding: 1rem;
            background: #f8f9fa;
            border-radius: 8px;
        }
        
        .detailed-rating {
            display: flex;
            justify-content: between;
            align-items: center;
        }
        
        .detailed-rating-label {
            font-size: 0.9rem;
            color: #5a6c7d;
        }
        
        .detailed-rating-stars {
            color: #f39c12;
            font-size: 0.9rem;
        }
        
        .review-helpfulness {
            display: flex;
            align-items: center;
            gap: 1rem;
            margin-top: 1rem;
            padding-top: 1rem;
            border-top: 1px solid #eee;
        }
        
        .helpfulness-question {
            color: #7f8c8d;
            font-size: 0.9rem;
        }
        
        .helpfulness-buttons {
            display: flex;
            gap: 0.5rem;
        }
        
        .helpfulness-btn {
            padding: 0.25rem 0.75rem;
            border: 1px solid #ddd;
            background: white;
            border-radius: 15px;
            cursor: pointer;
            font-size: 0.8rem;
            transition: all 0.3s ease;
        }
        
        .helpfulness-btn:hover {
            background: #f8f9fa;
        }
        
        .helpfulness-btn.voted {
            background: #3498db;
            color: white;
            border-color: #3498db;
        }
        
        .helpfulness-stats {
            margin-left: auto;
            color: #7f8c8d;
            font-size: 0.8rem;
        }
        
        .review-response {
            margin-top: 1rem;
            padding: 1rem;
            background: #f0f8ff;
            border-left: 4px solid #3498db;
            border-radius: 0 8px 8px 0;
        }
        
        .review-response-header {
            font-weight: 500;
            color: #2c3e50;
            margin-bottom: 0.5rem;
        }
        
        .review-response-text {
            color: #5a6c7d;
            line-height: 1.5;
        }
        
        .review-response-date {
            color: #7f8c8d;
            font-size: 0.8rem;
            margin-top: 0.5rem;
        }
        
        .verified-purchase {
            display: inline-flex;
            align-items: center;
            gap: 0.25rem;
            background: #e8f5e8;
            color: #27ae60;
            padding: 0.25rem 0.5rem;
            border-radius: 12px;
            font-size: 0.8rem;
            font-weight: 500;
        }
        
        .pagination {
            display: flex;
            justify-content: center;
            gap: 0.5rem;
            margin-top: 2rem;
        }
        
        .pagination a {
            padding: 0.5rem 1rem;
            border: 1px solid #ddd;
            color: #3498db;
            text-decoration: none;
            border-radius: 4px;
            transition: all 0.3s ease;
        }
        
        .pagination a:hover,
        .pagination a.current {
            background: #3498db;
            color: white;
        }
        
        .no-reviews {
            text-align: center;
            padding: 3rem;
            color: #7f8c8d;
        }
        
        .no-reviews i {
            font-size: 3rem;
            margin-bottom: 1rem;
            color: #bdc3c7;
        }
        
        @media (max-width: 768px) {
            .reviews-container {
                margin: 1rem auto;
                padding: 0 0.5rem;
            }
            
            .reviews-header {
                padding: 1rem;
            }
            
            .user-rating-summary {
                grid-template-columns: 1fr;
            }
            
            .filter-tabs {
                justify-content: center;
            }
            
            .review-card {
                padding: 1rem;
            }
            
            .review-meta {
                flex-direction: column;
                align-items: flex-start;
            }
            
            .review-detailed-ratings {
                grid-template-columns: 1fr;
            }
            
            .review-helpfulness {
                flex-direction: column;
                align-items: flex-start;
                gap: 0.5rem;
            }
        }
    </style>
</head>
<body>
    <div class="reviews-container">
        <!-- Header with user rating summary -->
        <div class="reviews-header">
            <h1>User Reviews</h1>
            
            <div class="user-rating-summary">
                <div class="rating-card">
                    <h3>Seller Rating</h3>
                    <div class="rating-value">
                        <fmt:formatNumber value="${stats.sellerRating}" maxFractionDigits="1"/>
                    </div>
                    <div class="rating-stars">
                        <c:forEach begin="1" end="5" var="star">
                            <c:choose>
                                <c:when test="${star <= stats.sellerRating}">★</c:when>
                                <c:otherwise>☆</c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </div>
                    <div class="rating-count">${stats.sellerReviewCount} reviews</div>
                </div>
                
                <div class="rating-card">
                    <h3>Buyer Rating</h3>
                    <div class="rating-value">
                        <fmt:formatNumber value="${stats.buyerRating}" maxFractionDigits="1"/>
                    </div>
                    <div class="rating-stars">
                        <c:forEach begin="1" end="5" var="star">
                            <c:choose>
                                <c:when test="${star <= stats.buyerRating}">★</c:when>
                                <c:otherwise>☆</c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </div>
                    <div class="rating-count">As buyer</div>
                </div>
                
                <div class="rating-card">
                    <h3>Recent Activity</h3>
                    <div class="rating-value">${stats.recentReviewCount}</div>
                    <div class="rating-count">Reviews in last 30 days</div>
                </div>
            </div>
        </div>
        
        <!-- Filter tabs -->
        <div class="filter-tabs">
            <a href="?type=all" class="filter-tab ${empty reviewType ? 'active' : ''}">All Reviews</a>
            <a href="?type=seller_review" class="filter-tab ${reviewType == 'SELLER_REVIEW' ? 'active' : ''}">As Seller</a>
            <a href="?type=buyer_review" class="filter-tab ${reviewType == 'BUYER_REVIEW' ? 'active' : ''}">As Buyer</a>
        </div>
        
        <!-- Reviews list -->
        <div class="reviews-list">
            <c:choose>
                <c:when test="${empty reviews}">
                    <div class="no-reviews">
                        <i>📝</i>
                        <h3>No reviews found</h3>
                        <p>This user hasn't received any reviews yet.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="review" items="${reviews}">
                        <div class="review-card">
                            <div class="review-header">
                                <div class="review-meta">
                                    <div class="reviewer-info">
                                        <div class="reviewer-avatar">
                                            ${review.reviewer.firstName.substring(0,1)}${review.reviewer.lastName.substring(0,1)}
                                        </div>
                                        <div>
                                            <div class="reviewer-name">${review.reviewer.fullName}</div>
                                            <c:if test="${review.verifiedPurchase}">
                                                <span class="verified-purchase">
                                                    ✓ Verified Purchase
                                                </span>
                                            </c:if>
                                        </div>
                                    </div>
                                    <div class="review-date">
                                        <fmt:formatDate value="${review.createdAt}" pattern="MMM dd, yyyy"/>
                                    </div>
                                </div>
                                
                                <span class="review-type-badge review-type-${review.reviewType.toString().toLowerCase()}">
                                    ${review.reviewType.toString().replace('_', ' ')}
                                </span>
                            </div>
                            
                            <div class="review-rating">
                                <div class="review-stars">
                                    <c:forEach begin="1" end="5" var="star">
                                        <c:choose>
                                            <c:when test="${star <= review.rating}">★</c:when>
                                            <c:otherwise>☆</c:otherwise>
                                        </c:choose>
                                    </c:forEach>
                                </div>
                                <span class="review-rating-text">
                                    <fmt:formatNumber value="${review.rating}" maxFractionDigits="1"/> out of 5
                                </span>
                            </div>
                            
                            <c:if test="${not empty review.title}">
                                <h3 class="review-title">${review.title}</h3>
                            </c:if>
                            
                            <c:if test="${not empty review.comment}">
                                <div class="review-comment">${review.comment}</div>
                            </c:if>
                            
                            <!-- Detailed ratings for seller reviews -->
                            <c:if test="${review.reviewType == 'SELLER_REVIEW' && (not empty review.communicationRating || not empty review.shippingRating)}">
                                <div class="review-detailed-ratings">
                                    <c:if test="${not empty review.communicationRating}">
                                        <div class="detailed-rating">
                                            <span class="detailed-rating-label">Communication:</span>
                                            <span class="detailed-rating-stars">
                                                <c:forEach begin="1" end="5" var="star">
                                                    <c:choose>
                                                        <c:when test="${star <= review.communicationRating}">★</c:when>
                                                        <c:otherwise>☆</c:otherwise>
                                                    </c:choose>
                                                </c:forEach>
                                            </span>
                                        </div>
                                    </c:if>
                                    
                                    <c:if test="${not empty review.shippingRating}">
                                        <div class="detailed-rating">
                                            <span class="detailed-rating-label">Shipping:</span>
                                            <span class="detailed-rating-stars">
                                                <c:forEach begin="1" end="5" var="star">
                                                    <c:choose>
                                                        <c:when test="${star <= review.shippingRating}">★</c:when>
                                                        <c:otherwise>☆</c:otherwise>
                                                    </c:choose>
                                                </c:forEach>
                                            </span>
                                        </div>
                                    </c:if>
                                    
                                    <c:if test="${not empty review.itemDescriptionRating}">
                                        <div class="detailed-rating">
                                            <span class="detailed-rating-label">Item Description:</span>
                                            <span class="detailed-rating-stars">
                                                <c:forEach begin="1" end="5" var="star">
                                                    <c:choose>
                                                        <c:when test="${star <= review.itemDescriptionRating}">★</c:when>
                                                        <c:otherwise>☆</c:otherwise>
                                                    </c:choose>
                                                </c:forEach>
                                            </span>
                                        </div>
                                    </c:if>
                                    
                                    <c:if test="${not empty review.responseTimeRating}">
                                        <div class="detailed-rating">
                                            <span class="detailed-rating-label">Response Time:</span>
                                            <span class="detailed-rating-stars">
                                                <c:forEach begin="1" end="5" var="star">
                                                    <c:choose>
                                                        <c:when test="${star <= review.responseTimeRating}">★</c:when>
                                                        <c:otherwise>☆</c:otherwise>
                                                    </c:choose>
                                                </c:forEach>
                                            </span>
                                        </div>
                                    </c:if>
                                </div>
                            </c:if>
                            
                            <!-- Review response -->
                            <c:if test="${review.hasResponse}">
                                <div class="review-response">
                                    <div class="review-response-header">Response from seller:</div>
                                    <div class="review-response-text">${review.response}</div>
                                    <div class="review-response-date">
                                        <fmt:formatDate value="${review.responseDate}" pattern="MMM dd, yyyy"/>
                                    </div>
                                </div>
                            </c:if>
                            
                            <!-- Helpfulness voting -->
                            <div class="review-helpfulness">
                                <span class="helpfulness-question">Was this review helpful?</span>
                                <div class="helpfulness-buttons">
                                    <button class="helpfulness-btn" onclick="voteHelpfulness(${review.reviewId}, true)">
                                        👍 Yes (${review.helpfulCount})
                                    </button>
                                    <button class="helpfulness-btn" onclick="voteHelpfulness(${review.reviewId}, false)">
                                        👎 No (${review.notHelpfulCount})
                                    </button>
                                </div>
                                <div class="helpfulness-stats">
                                    <fmt:formatNumber value="${review.helpfulnessPercentage}" maxFractionDigits="0"/>% found helpful
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
        
        <!-- Pagination -->
        <c:if test="${not empty reviews}">
            <div class="pagination">
                <c:if test="${page > 0}">
                    <a href="?page=${page - 1}&type=${param.type}">&laquo; Previous</a>
                </c:if>
                
                <c:forEach begin="0" end="4" var="p">
                    <c:if test="${p <= (fn:length(reviews) / 20)}">
                        <a href="?page=${p}&type=${param.type}" 
                           class="${p == page ? 'current' : ''}">${p + 1}</a>
                    </c:if>
                </c:forEach>
                
                <c:if test="${fn:length(reviews) == 20}">
                    <a href="?page=${page + 1}&type=${param.type}">Next &raquo;</a>
                </c:if>
            </div>
        </c:if>
    </div>
    
    <script>
        function voteHelpfulness(reviewId, isHelpful) {
            const formData = new FormData();
            formData.append('helpful', isHelpful);
            
            fetch('${pageContext.request.contextPath}/reviews/api/' + reviewId + '/vote', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Reload the page to show updated counts
                    location.reload();
                } else {
                    alert(data.message || 'Error voting on review');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error voting on review');
            });
        }
        
        function flagReview(reviewId) {
            const reason = prompt('Please provide a reason for flagging this review:');
            if (!reason) return;
            
            const formData = new FormData();
            formData.append('reason', reason);
            
            fetch('${pageContext.request.contextPath}/reviews/api/' + reviewId + '/flag', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Review flagged for moderation');
                } else {
                    alert(data.message || 'Error flagging review');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error flagging review');
            });
        }
    </script>
</body>
</html>