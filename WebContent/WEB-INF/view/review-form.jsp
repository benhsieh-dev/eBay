<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Write Review - eBay</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .review-form-container {
            max-width: 800px;
            margin: 2rem auto;
            padding: 2rem;
            background: white;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        
        .review-form h1 {
            color: #2c3e50;
            margin-bottom: 1.5rem;
            text-align: center;
        }
        
        .form-section {
            margin-bottom: 2rem;
            padding: 1.5rem;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            background: #fafafa;
        }
        
        .form-section h3 {
            color: #34495e;
            margin-bottom: 1rem;
            border-bottom: 2px solid #3498db;
            padding-bottom: 0.5rem;
        }
        
        .rating-section {
            display: flex;
            flex-direction: column;
            gap: 1rem;
        }
        
        .rating-group {
            display: flex;
            align-items: center;
            gap: 1rem;
        }
        
        .rating-label {
            min-width: 150px;
            font-weight: 500;
            color: #2c3e50;
        }
        
        .star-rating {
            display: flex;
            gap: 2px;
        }
        
        .star {
            font-size: 1.5rem;
            color: #ddd;
            cursor: pointer;
            transition: all 0.2s ease;
        }
        
        .star:hover,
        .star.active {
            color: #f39c12;
            transform: scale(1.1);
        }
        
        .star:hover ~ .star {
            color: #ddd;
        }
        
        .rating-text {
            margin-left: 1rem;
            font-size: 0.9rem;
            color: #7f8c8d;
        }
        
        .form-group {
            margin-bottom: 1.5rem;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 500;
            color: #2c3e50;
        }
        
        .form-group input,
        .form-group textarea {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #ddd;
            border-radius: 6px;
            font-size: 1rem;
            transition: border-color 0.3s ease;
        }
        
        .form-group input:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #3498db;
            box-shadow: 0 0 0 3px rgba(52, 152, 219, 0.1);
        }
        
        .form-group textarea {
            min-height: 120px;
            resize: vertical;
        }
        
        .button-group {
            display: flex;
            gap: 1rem;
            justify-content: center;
            margin-top: 2rem;
        }
        
        .btn {
            padding: 0.75rem 2rem;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 1rem;
            font-weight: 500;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-block;
            text-align: center;
        }
        
        .btn-primary {
            background: #3498db;
            color: white;
        }
        
        .btn-primary:hover {
            background: #2980b9;
            transform: translateY(-2px);
        }
        
        .btn-secondary {
            background: #95a5a6;
            color: white;
        }
        
        .btn-secondary:hover {
            background: #7f8c8d;
        }
        
        .review-type-info {
            background: #e8f4fd;
            padding: 1rem;
            border-radius: 6px;
            margin-bottom: 1.5rem;
            border-left: 4px solid #3498db;
        }
        
        .review-type-info h4 {
            margin: 0 0 0.5rem 0;
            color: #2c3e50;
        }
        
        .review-type-info p {
            margin: 0;
            color: #5a6c7d;
        }
        
        .character-count {
            font-size: 0.8rem;
            color: #7f8c8d;
            text-align: right;
            margin-top: 0.25rem;
        }
        
        .error-message {
            background: #ffe6e6;
            color: #c0392b;
            padding: 1rem;
            border-radius: 6px;
            margin-bottom: 1rem;
            border-left: 4px solid #e74c3c;
        }
        
        .success-message {
            background: #e8f5e8;
            color: #27ae60;
            padding: 1rem;
            border-radius: 6px;
            margin-bottom: 1rem;
            border-left: 4px solid #2ecc71;
        }
        
        @media (max-width: 768px) {
            .review-form-container {
                margin: 1rem;
                padding: 1rem;
            }
            
            .rating-group {
                flex-direction: column;
                align-items: flex-start;
                gap: 0.5rem;
            }
            
            .button-group {
                flex-direction: column;
            }
        }
    </style>
</head>
<body>
    <div class="review-form-container">
        <form class="review-form" id="reviewForm">
            <h1>Write Review</h1>
            
            <div id="messageContainer"></div>
            
            <div class="review-type-info">
                <h4>
                    <c:choose>
                        <c:when test="${reviewType == 'SELLER_REVIEW'}">Review Seller</c:when>
                        <c:when test="${reviewType == 'BUYER_REVIEW'}">Review Buyer</c:when>
                        <c:when test="${reviewType == 'PRODUCT_REVIEW'}">Review Product</c:when>
                        <c:otherwise>Review Transaction</c:otherwise>
                    </c:choose>
                </h4>
                <p>
                    <c:choose>
                        <c:when test="${reviewType == 'SELLER_REVIEW'}">
                            Share your experience with this seller. Your feedback helps other buyers make informed decisions.
                        </c:when>
                        <c:when test="${reviewType == 'BUYER_REVIEW'}">
                            Rate this buyer's communication and payment promptness.
                        </c:when>
                        <c:when test="${reviewType == 'PRODUCT_REVIEW'}">
                            Review the product quality, description accuracy, and overall satisfaction.
                        </c:when>
                        <c:otherwise>
                            Provide feedback about the overall transaction experience.
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>
            
            <!-- Overall Rating -->
            <div class="form-section">
                <h3>Overall Rating</h3>
                <div class="rating-group">
                    <span class="rating-label">Overall Experience:</span>
                    <div class="star-rating" data-rating="overall">
                        <span class="star" data-value="1">★</span>
                        <span class="star" data-value="2">★</span>
                        <span class="star" data-value="3">★</span>
                        <span class="star" data-value="4">★</span>
                        <span class="star" data-value="5">★</span>
                    </div>
                    <span class="rating-text" id="overallRatingText">Click to rate</span>
                </div>
            </div>
            
            <!-- Detailed Ratings for Seller Reviews -->
            <c:if test="${reviewType == 'SELLER_REVIEW'}">
                <div class="form-section">
                    <h3>Detailed Ratings</h3>
                    <div class="rating-section">
                        <div class="rating-group">
                            <span class="rating-label">Communication:</span>
                            <div class="star-rating" data-rating="communication">
                                <span class="star" data-value="1">★</span>
                                <span class="star" data-value="2">★</span>
                                <span class="star" data-value="3">★</span>
                                <span class="star" data-value="4">★</span>
                                <span class="star" data-value="5">★</span>
                            </div>
                            <span class="rating-text" id="communicationRatingText">Rate communication</span>
                        </div>
                        
                        <div class="rating-group">
                            <span class="rating-label">Shipping Speed:</span>
                            <div class="star-rating" data-rating="shipping">
                                <span class="star" data-value="1">★</span>
                                <span class="star" data-value="2">★</span>
                                <span class="star" data-value="3">★</span>
                                <span class="star" data-value="4">★</span>
                                <span class="star" data-value="5">★</span>
                            </div>
                            <span class="rating-text" id="shippingRatingText">Rate shipping</span>
                        </div>
                        
                        <div class="rating-group">
                            <span class="rating-label">Item Description:</span>
                            <div class="star-rating" data-rating="description">
                                <span class="star" data-value="1">★</span>
                                <span class="star" data-value="2">★</span>
                                <span class="star" data-value="3">★</span>
                                <span class="star" data-value="4">★</span>
                                <span class="star" data-value="5">★</span>
                            </div>
                            <span class="rating-text" id="descriptionRatingText">Rate accuracy</span>
                        </div>
                        
                        <div class="rating-group">
                            <span class="rating-label">Response Time:</span>
                            <div class="star-rating" data-rating="response">
                                <span class="star" data-value="1">★</span>
                                <span class="star" data-value="2">★</span>
                                <span class="star" data-value="3">★</span>
                                <span class="star" data-value="4">★</span>
                                <span class="star" data-value="5">★</span>
                            </div>
                            <span class="rating-text" id="responseRatingText">Rate responsiveness</span>
                        </div>
                    </div>
                </div>
            </c:if>
            
            <!-- Review Text -->
            <div class="form-section">
                <h3>Review Details</h3>
                
                <div class="form-group">
                    <label for="reviewTitle">Review Title</label>
                    <input type="text" id="reviewTitle" name="title" placeholder="Brief summary of your experience" maxlength="200">
                    <div class="character-count">
                        <span id="titleCount">0</span>/200 characters
                    </div>
                </div>
                
                <div class="form-group">
                    <label for="reviewComment">Review Comment</label>
                    <textarea id="reviewComment" name="comment" placeholder="Share details about your experience..." maxlength="2000"></textarea>
                    <div class="character-count">
                        <span id="commentCount">0</span>/2000 characters
                    </div>
                </div>
            </div>
            
            <!-- Hidden form fields -->
            <input type="hidden" id="orderId" value="${orderId}">
            <input type="hidden" id="reviewType" value="${reviewType}">
            <input type="hidden" id="productId" value="${productId}">
            <input type="hidden" id="overallRating" value="">
            <input type="hidden" id="communicationRating" value="">
            <input type="hidden" id="shippingRating" value="">
            <input type="hidden" id="descriptionRating" value="">
            <input type="hidden" id="responseRating" value="">
            
            <div class="button-group">
                <button type="submit" class="btn btn-primary">Submit Review</button>
                <a href="${pageContext.request.contextPath}/orders/my-orders" class="btn btn-secondary">Cancel</a>
            </div>
        </form>
    </div>
    
    <script>
        // Rating functionality
        const ratings = {
            overall: 0,
            communication: 0,
            shipping: 0,
            description: 0,
            response: 0
        };
        
        const ratingTexts = {
            1: 'Poor',
            2: 'Fair', 
            3: 'Good',
            4: 'Very Good',
            5: 'Excellent'
        };
        
        // Initialize star rating listeners
        document.querySelectorAll('.star-rating').forEach(ratingGroup => {
            const ratingType = ratingGroup.dataset.rating;
            const stars = ratingGroup.querySelectorAll('.star');
            
            stars.forEach(star => {
                star.addEventListener('click', function() {
                    const value = parseInt(this.dataset.value);
                    ratings[ratingType] = value;
                    
                    // Update visual state
                    stars.forEach((s, index) => {
                        if (index < value) {
                            s.classList.add('active');
                        } else {
                            s.classList.remove('active');
                        }
                    });
                    
                    // Update text
                    const textElement = document.getElementById(ratingType + 'RatingText');
                    if (textElement) {
                        textElement.textContent = ratingTexts[value] || 'Click to rate';
                    }
                    
                    // Update hidden field
                    const hiddenField = document.getElementById(ratingType + 'Rating');
                    if (hiddenField) {
                        hiddenField.value = value;
                    }
                });
                
                star.addEventListener('mouseenter', function() {
                    const value = parseInt(this.dataset.value);
                    stars.forEach((s, index) => {
                        if (index < value) {
                            s.style.color = '#f39c12';
                        } else {
                            s.style.color = '#ddd';
                        }
                    });
                });
            });
            
            ratingGroup.addEventListener('mouseleave', function() {
                const currentRating = ratings[ratingType];
                stars.forEach((s, index) => {
                    if (index < currentRating) {
                        s.style.color = '#f39c12';
                    } else {
                        s.style.color = '#ddd';
                    }
                });
            });
        });
        
        // Character count functionality
        function updateCharacterCount(inputId, counterId, maxLength) {
            const input = document.getElementById(inputId);
            const counter = document.getElementById(counterId);
            
            input.addEventListener('input', function() {
                const length = this.value.length;
                counter.textContent = length;
                
                if (length > maxLength * 0.9) {
                    counter.style.color = '#e74c3c';
                } else if (length > maxLength * 0.7) {
                    counter.style.color = '#f39c12';
                } else {
                    counter.style.color = '#7f8c8d';
                }
            });
        }
        
        updateCharacterCount('reviewTitle', 'titleCount', 200);
        updateCharacterCount('reviewComment', 'commentCount', 2000);
        
        // Form submission
        document.getElementById('reviewForm').addEventListener('submit', function(e) {
            e.preventDefault();
            
            if (ratings.overall === 0) {
                showMessage('Please provide an overall rating', 'error');
                return;
            }
            
            const title = document.getElementById('reviewTitle').value.trim();
            const comment = document.getElementById('reviewComment').value.trim();
            
            if (!title || !comment) {
                showMessage('Please provide both a title and comment', 'error');
                return;
            }
            
            submitReview();
        });
        
        function submitReview() {
            const formData = new FormData();
            formData.append('revieweeId', getRevieweeId()); // This would be determined from the order
            formData.append('orderId', document.getElementById('orderId').value);
            formData.append('reviewType', document.getElementById('reviewType').value);
            formData.append('overallRating', ratings.overall);
            formData.append('title', document.getElementById('reviewTitle').value);
            formData.append('comment', document.getElementById('reviewComment').value);
            
            if (document.getElementById('productId').value) {
                formData.append('productId', document.getElementById('productId').value);
            }
            
            // Add detailed ratings if available
            if (ratings.communication > 0) formData.append('communicationRating', ratings.communication);
            if (ratings.shipping > 0) formData.append('shippingRating', ratings.shipping);
            if (ratings.description > 0) formData.append('itemDescriptionRating', ratings.description);
            if (ratings.response > 0) formData.append('responseTimeRating', ratings.response);
            
            const endpoint = (ratings.communication > 0 || ratings.shipping > 0 || 
                            ratings.description > 0 || ratings.response > 0) 
                            ? '/reviews/api/create-detailed' 
                            : '/reviews/api/create';
            
            fetch('${pageContext.request.contextPath}' + endpoint, {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    showMessage('Review submitted successfully!', 'success');
                    setTimeout(() => {
                        window.location.href = '${pageContext.request.contextPath}/orders/my-orders';
                    }, 2000);
                } else {
                    showMessage(data.message || 'Error submitting review', 'error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showMessage('Error submitting review. Please try again.', 'error');
            });
        }
        
        function showMessage(message, type) {
            const container = document.getElementById('messageContainer');
            const messageClass = type === 'error' ? 'error-message' : 'success-message';
            
            container.innerHTML = '<div class="' + messageClass + '">' + message + '</div>';
            
            container.scrollIntoView({ behavior: 'smooth', block: 'center' });
        }
        
        function getRevieweeId() {
            // This would be passed from the server based on the order and review type
            // For now, return a placeholder - in real implementation this would be dynamic
            return 1; // TODO: Implement proper reviewee ID detection
        }
    </script>
</body>
</html>