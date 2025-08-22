<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!-- Product Reviews Section -->
<div class="product-reviews-section" id="product-reviews">
    <style>
        .product-reviews-section {
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            overflow: hidden;
        }
        
        .reviews-header {
            padding: 2rem;
            border-bottom: 1px solid #eee;
        }
        
        .reviews-title {
            color: #2c3e50;
            margin-bottom: 1.5rem;
            display: flex;
            align-items: center;
            gap: 1rem;
        }
        
        .reviews-summary {
            display: grid;
            grid-template-columns: auto 1fr auto;
            gap: 2rem;
            align-items: center;
        }
        
        .rating-overview {
            text-align: center;
        }
        
        .rating-score {
            font-size: 3rem;
            font-weight: bold;
            color: #3498db;
            margin-bottom: 0.5rem;
        }
        
        .rating-stars-large {
            color: #f39c12;
            font-size: 1.5rem;
            margin-bottom: 0.5rem;
        }
        
        .rating-count {
            color: #7f8c8d;
            font-size: 0.9rem;
        }
        
        .rating-breakdown {
            display: flex;
            flex-direction: column;
            gap: 0.5rem;
            min-width: 300px;
        }
        
        .rating-bar {
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .rating-bar-label {
            width: 60px;
            font-size: 0.9rem;
            color: #7f8c8d;
        }
        
        .rating-bar-fill {
            flex: 1;
            height: 8px;
            background: #f1f2f6;
            border-radius: 4px;
            overflow: hidden;
        }
        
        .rating-bar-progress {
            height: 100%;
            background: #f39c12;
            transition: width 0.3s ease;
        }
        
        .rating-bar-count {
            width: 40px;
            text-align: right;
            font-size: 0.8rem;
            color: #7f8c8d;
        }
        
        .write-review-btn {
            padding: 0.75rem 2rem;
            background: #3498db;
            color: white;
            border: none;
            border-radius: 6px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-block;
        }
        
        .write-review-btn:hover {
            background: #2980b9;
            transform: translateY(-2px);
        }
        
        .reviews-filters {
            padding: 1rem 2rem;
            background: #f8f9fa;
            border-bottom: 1px solid #eee;
        }
        
        .filter-buttons {
            display: flex;
            gap: 0.5rem;
            flex-wrap: wrap;
        }
        
        .filter-btn {
            padding: 0.5rem 1rem;
            border: 1px solid #ddd;
            background: white;
            color: #5a6c7d;
            border-radius: 20px;
            cursor: pointer;
            font-size: 0.9rem;
            transition: all 0.3s ease;
        }
        
        .filter-btn:hover,
        .filter-btn.active {
            background: #3498db;
            color: white;
            border-color: #3498db;
        }
        
        .reviews-list {
            padding: 0 2rem;
        }
        
        .review-item {
            padding: 1.5rem 0;
            border-bottom: 1px solid #f1f2f6;
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
        
        .reviewer-info {
            display: flex;
            align-items: center;
            gap: 0.75rem;
        }
        
        .reviewer-avatar {
            width: 36px;
            height: 36px;
            border-radius: 50%;
            background: #3498db;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: 500;
            font-size: 0.9rem;
        }
        
        .reviewer-details {
            display: flex;
            flex-direction: column;
        }
        
        .reviewer-name {
            font-weight: 500;
            color: #2c3e50;
            font-size: 0.95rem;
        }
        
        .review-date {
            color: #7f8c8d;
            font-size: 0.8rem;
        }
        
        .review-rating {
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .review-stars {
            color: #f39c12;
            font-size: 1rem;
        }
        
        .verified-badge {
            background: #e8f5e8;
            color: #27ae60;
            padding: 0.2rem 0.5rem;
            border-radius: 10px;
            font-size: 0.7rem;
            font-weight: 500;
        }
        
        .review-content {
            margin-bottom: 1rem;
        }
        
        .review-title {
            font-weight: 600;
            color: #2c3e50;
            margin-bottom: 0.5rem;
            font-size: 1.05rem;
        }
        
        .review-text {
            color: #5a6c7d;
            line-height: 1.6;
            margin-bottom: 1rem;
        }
        
        .review-text.collapsed {
            max-height: 4.8em;
            overflow: hidden;
        }
        
        .read-more-btn {
            color: #3498db;
            cursor: pointer;
            font-size: 0.9rem;
            border: none;
            background: none;
            padding: 0;
        }
        
        .review-images {
            display: flex;
            gap: 0.5rem;
            margin: 1rem 0;
            flex-wrap: wrap;
        }
        
        .review-image {
            width: 60px;
            height: 60px;
            border-radius: 6px;
            object-fit: cover;
            cursor: pointer;
            transition: transform 0.3s ease;
        }
        
        .review-image:hover {
            transform: scale(1.05);
        }
        
        .review-actions {
            display: flex;
            align-items: center;
            gap: 1rem;
            margin-top: 1rem;
        }
        
        .helpfulness-section {
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .helpfulness-text {
            color: #7f8c8d;
            font-size: 0.85rem;
        }
        
        .helpfulness-btn {
            background: none;
            border: 1px solid #ddd;
            padding: 0.25rem 0.5rem;
            border-radius: 4px;
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
        
        .show-more-reviews {
            padding: 1.5rem 2rem;
            text-align: center;
            border-top: 1px solid #eee;
        }
        
        .show-more-btn {
            padding: 0.75rem 2rem;
            border: 2px solid #3498db;
            background: white;
            color: #3498db;
            border-radius: 6px;
            cursor: pointer;
            font-weight: 500;
            transition: all 0.3s ease;
        }
        
        .show-more-btn:hover {
            background: #3498db;
            color: white;
        }
        
        .no-reviews {
            padding: 3rem 2rem;
            text-align: center;
            color: #7f8c8d;
        }
        
        .no-reviews i {
            font-size: 2.5rem;
            margin-bottom: 1rem;
            color: #bdc3c7;
        }
        
        @media (max-width: 768px) {
            .reviews-summary {
                grid-template-columns: 1fr;
                gap: 1.5rem;
                text-align: center;
            }
            
            .rating-breakdown {
                min-width: auto;
            }
            
            .reviews-header,
            .reviews-filters,
            .reviews-list {
                padding-left: 1rem;
                padding-right: 1rem;
            }
            
            .review-header {
                flex-direction: column;
                align-items: flex-start;
                gap: 0.5rem;
            }
            
            .filter-buttons {
                justify-content: center;
            }
        }
    </style>
    
    <!-- Reviews Header -->
    <div class="reviews-header">
        <h2 class="reviews-title">
            <span>Customer Reviews</span>
            <c:if test="${stats.reviewCount > 0}">
                <span>(${stats.reviewCount})</span>
            </c:if>
        </h2>
        
        <c:choose>
            <c:when test="${stats.reviewCount > 0}">
                <div class="reviews-summary">
                    <div class="rating-overview">
                        <div class="rating-score">
                            <fmt:formatNumber value="${stats.averageRating}" maxFractionDigits="1"/>
                        </div>
                        <div class="rating-stars-large">
                            <c:forEach begin="1" end="5" var="star">
                                <c:choose>
                                    <c:when test="${star <= stats.averageRating}">★</c:when>
                                    <c:otherwise>☆</c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </div>
                        <div class="rating-count">Based on ${stats.reviewCount} reviews</div>
                    </div>
                    
                    <div class="rating-breakdown">
                        <c:forEach begin="5" end="1" step="-1" var="rating">
                            <div class="rating-bar">
                                <div class="rating-bar-label">${rating} star</div>
                                <div class="rating-bar-fill">
                                    <div class="rating-bar-progress" style="width: ${ratingBreakdown[rating] * 100 / stats.reviewCount}%"></div>
                                </div>
                                <div class="rating-bar-count">${ratingBreakdown[rating] || 0}</div>
                            </div>
                        </c:forEach>
                    </div>
                    
                    <div>
                        <c:if test="${not empty currentUser}">
                            <a href="${pageContext.request.contextPath}/reviews/write/${productId}?reviewType=PRODUCT_REVIEW&productId=${productId}" 
                               class="write-review-btn">Write a Review</a>
                        </c:if>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="reviews-summary">
                    <div class="rating-overview">
                        <div class="rating-score">0.0</div>
                        <div class="rating-stars-large">☆☆☆☆☆</div>
                        <div class="rating-count">No reviews yet</div>
                    </div>
                    <div>
                        <c:if test="${not empty currentUser}">
                            <a href="${pageContext.request.contextPath}/reviews/write/new?reviewType=PRODUCT_REVIEW&productId=${productId}" 
                               class="write-review-btn">Be the First to Review</a>
                        </c:if>
                    </div>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
    
    <c:if test="${not empty reviews}">
        <!-- Reviews Filters -->
        <div class="reviews-filters">
            <div class="filter-buttons">
                <button class="filter-btn active" onclick="filterReviews('all')">All Reviews</button>
                <button class="filter-btn" onclick="filterReviews('5')">5 Stars</button>
                <button class="filter-btn" onclick="filterReviews('4')">4 Stars</button>
                <button class="filter-btn" onclick="filterReviews('3')">3 Stars</button>
                <button class="filter-btn" onclick="filterReviews('2')">2 Stars</button>
                <button class="filter-btn" onclick="filterReviews('1')">1 Star</button>
                <button class="filter-btn" onclick="filterReviews('verified')">Verified Only</button>
            </div>
        </div>
        
        <!-- Reviews List -->
        <div class="reviews-list">
            <c:forEach var="review" items="${reviews}" varStatus="status">
                <div class="review-item" data-rating="${review.rating.intValue()}" data-verified="${review.verifiedPurchase}">
                    <div class="review-header">
                        <div class="reviewer-info">
                            <div class="reviewer-avatar">
                                ${review.reviewer.firstName.substring(0,1)}${review.reviewer.lastName.substring(0,1)}
                            </div>
                            <div class="reviewer-details">
                                <div class="reviewer-name">${review.reviewer.fullName}</div>
                                <div class="review-date">
                                    <fmt:formatDate value="${review.createdAt}" pattern="MMMM dd, yyyy"/>
                                </div>
                            </div>
                            <c:if test="${review.verifiedPurchase}">
                                <span class="verified-badge">✓ Verified Purchase</span>
                            </c:if>
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
                        </div>
                    </div>
                    
                    <div class="review-content">
                        <c:if test="${not empty review.title}">
                            <div class="review-title">${review.title}</div>
                        </c:if>
                        
                        <c:if test="${not empty review.comment}">
                            <div class="review-text ${fn:length(review.comment) > 300 ? 'collapsed' : ''}" 
                                 id="review-text-${review.reviewId}">
                                ${review.comment}
                            </div>
                            <c:if test="${fn:length(review.comment) > 300}">
                                <button class="read-more-btn" onclick="toggleReadMore(${review.reviewId})">
                                    Read more
                                </button>
                            </c:if>
                        </c:if>
                        
                        <!-- Review images would go here if implemented -->
                        <c:if test="${not empty review.reviewImages}">
                            <div class="review-images">
                                <c:forEach var="image" items="${review.reviewImages}">
                                    <img src="${image.thumbnailUrl}" alt="Review image" class="review-image" 
                                         onclick="showImageModal('${image.imageUrl}')">
                                </c:forEach>
                            </div>
                        </c:if>
                    </div>
                    
                    <div class="review-actions">
                        <div class="helpfulness-section">
                            <span class="helpfulness-text">Helpful?</span>
                            <button class="helpfulness-btn" onclick="voteHelpfulness(${review.reviewId}, true)">
                                👍 ${review.helpfulCount}
                            </button>
                            <button class="helpfulness-btn" onclick="voteHelpfulness(${review.reviewId}, false)">
                                👎 ${review.notHelpfulCount}
                            </button>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
        
        <!-- Show More Reviews -->
        <c:if test="${fn:length(reviews) >= 5}">
            <div class="show-more-reviews">
                <button class="show-more-btn" onclick="loadMoreReviews()">
                    Show More Reviews
                </button>
            </div>
        </c:if>
    </c:if>
    
    <c:if test="${empty reviews}">
        <div class="no-reviews">
            <i>📝</i>
            <h3>No reviews yet</h3>
            <p>Be the first to share your experience with this product.</p>
            <c:if test="${not empty currentUser}">
                <a href="${pageContext.request.contextPath}/reviews/write/new?reviewType=PRODUCT_REVIEW&productId=${productId}" 
                   class="write-review-btn" style="margin-top: 1rem;">Write the First Review</a>
            </c:if>
        </div>
    </c:if>
</div>

<script>
    let currentFilter = 'all';
    let currentPage = 0;
    
    function filterReviews(filter) {
        currentFilter = filter;
        currentPage = 0;
        
        // Update active filter button
        document.querySelectorAll('.filter-btn').forEach(btn => btn.classList.remove('active'));
        event.target.classList.add('active');
        
        // Show/hide reviews based on filter
        const reviews = document.querySelectorAll('.review-item');
        reviews.forEach(review => {
            const rating = review.dataset.rating;
            const verified = review.dataset.verified === 'true';
            
            let show = true;
            
            if (filter !== 'all') {
                if (filter === 'verified') {
                    show = verified;
                } else {
                    show = rating === filter;
                }
            }
            
            review.style.display = show ? 'block' : 'none';
        });
    }
    
    function toggleReadMore(reviewId) {
        const textElement = document.getElementById('review-text-' + reviewId);
        const button = event.target;
        
        if (textElement.classList.contains('collapsed')) {
            textElement.classList.remove('collapsed');
            button.textContent = 'Read less';
        } else {
            textElement.classList.add('collapsed');
            button.textContent = 'Read more';
        }
    }
    
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
                // Update the button counts
                location.reload(); // Simple approach - could be improved with dynamic updates
            } else {
                alert(data.message || 'Error voting on review');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error voting on review');
        });
    }
    
    function loadMoreReviews() {
        currentPage++;
        
        fetch('${pageContext.request.contextPath}/reviews/api/product/${productId}?page=' + currentPage + '&pageSize=5')
        .then(response => response.json())
        .then(data => {
            if (data.success && data.reviews.length > 0) {
                // Add new reviews to the list
                const reviewsList = document.querySelector('.reviews-list');
                data.reviews.forEach(review => {
                    const reviewHTML = createReviewHTML(review);
                    reviewsList.insertAdjacentHTML('beforeend', reviewHTML);
                });
                
                // Hide show more button if no more reviews
                if (data.reviews.length < 5) {
                    document.querySelector('.show-more-reviews').style.display = 'none';
                }
            } else {
                document.querySelector('.show-more-reviews').style.display = 'none';
            }
        })
        .catch(error => {
            console.error('Error loading more reviews:', error);
        });
    }
    
    function createReviewHTML(review) {
        // This would create the HTML for a review item
        // Simplified implementation - in production this would be more robust
        const stars = '★'.repeat(Math.floor(review.rating)) + '☆'.repeat(5 - Math.floor(review.rating));
        
        return '<div class="review-item" data-rating="' + Math.floor(review.rating) + '" data-verified="' + review.verifiedPurchase + '">' +
               '<div class="review-header">' +
               '<div class="reviewer-info">' +
               '<div class="reviewer-avatar">' + review.reviewer.firstName.charAt(0) + review.reviewer.lastName.charAt(0) + '</div>' +
               '<div class="reviewer-details">' +
               '<div class="reviewer-name">' + review.reviewer.fullName + '</div>' +
               '<div class="review-date">' + new Date(review.createdAt).toLocaleDateString() + '</div>' +
               '</div>' +
               (review.verifiedPurchase ? '<span class="verified-badge">✓ Verified Purchase</span>' : '') +
               '</div>' +
               '<div class="review-rating"><div class="review-stars">' + stars + '</div></div>' +
               '</div>' +
               '<div class="review-content">' +
               (review.title ? '<div class="review-title">' + review.title + '</div>' : '') +
               (review.comment ? '<div class="review-text">' + review.comment + '</div>' : '') +
               '</div>' +
               '<div class="review-actions">' +
               '<div class="helpfulness-section">' +
               '<span class="helpfulness-text">Helpful?</span>' +
               '<button class="helpfulness-btn" onclick="voteHelpfulness(' + review.reviewId + ', true)">👍 ' + review.helpfulCount + '</button>' +
               '<button class="helpfulness-btn" onclick="voteHelpfulness(' + review.reviewId + ', false)">👎 ' + review.notHelpfulCount + '</button>' +
               '</div>' +
               '</div>' +
               '</div>';
    }
    
    function showImageModal(imageUrl) {
        // Simple image modal - in production this would be more sophisticated
        const modal = document.createElement('div');
        modal.style.cssText = 'position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.8); display: flex; align-items: center; justify-content: center; z-index: 1000;';
        modal.onclick = () => modal.remove();
        
        const img = document.createElement('img');
        img.src = imageUrl;
        img.style.cssText = 'max-width: 90%; max-height: 90%; border-radius: 8px;';
        
        modal.appendChild(img);
        document.body.appendChild(modal);
    }
</script>