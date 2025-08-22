<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!-- Reputation Badge Component -->
<!-- Usage: Include this file and pass userId as a parameter -->

<div class="reputation-badge-component" data-user-id="${param.userId}">
    <style>
        .reputation-badge-component {
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            padding: 0.4rem 0.8rem;
            border-radius: 20px;
            font-size: 0.85rem;
            font-weight: 500;
            background: #f8f9fa;
            border: 1px solid #e9ecef;
            transition: all 0.3s ease;
            cursor: pointer;
        }
        
        .reputation-badge-component:hover {
            transform: translateY(-1px);
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }
        
        .reputation-icon {
            font-size: 1rem;
        }
        
        .reputation-info {
            display: flex;
            flex-direction: column;
            align-items: flex-start;
        }
        
        .reputation-level {
            font-weight: 600;
            margin-bottom: 0.1rem;
        }
        
        .reputation-rating {
            display: flex;
            align-items: center;
            gap: 0.25rem;
            font-size: 0.75rem;
            color: #6c757d;
        }
        
        .reputation-stars {
            color: #ffc107;
        }
        
        .reputation-loading {
            color: #6c757d;
            font-style: italic;
        }
        
        .reputation-error {
            color: #dc3545;
            font-size: 0.75rem;
        }
        
        /* Reputation level specific styles */
        .reputation-top-rated {
            background: linear-gradient(135deg, #ffd700, #ffed4e);
            color: #7c5504;
            border-color: #ffc107;
        }
        
        .reputation-excellent {
            background: linear-gradient(135deg, #32cd32, #98fb98);
            color: #155724;
            border-color: #28a745;
        }
        
        .reputation-good {
            background: linear-gradient(135deg, #ffa500, #ffcc80);
            color: #7d4e00;
            border-color: #fd7e14;
        }
        
        .reputation-fair {
            background: linear-gradient(135deg, #ff6347, #ff9999);
            color: #721c24;
            border-color: #dc3545;
        }
        
        .reputation-poor {
            background: linear-gradient(135deg, #dc143c, #ff7f7f);
            color: #721c24;
            border-color: #dc3545;
        }
        
        .reputation-new-user {
            background: linear-gradient(135deg, #6c757d, #adb5bd);
            color: #495057;
            border-color: #6c757d;
        }
    </style>
    
    <div class="reputation-content">
        <div class="reputation-loading">Loading...</div>
    </div>
</div>

<script>
// Load reputation badge data via AJAX
document.addEventListener('DOMContentLoaded', function() {
    const badges = document.querySelectorAll('.reputation-badge-component');
    
    badges.forEach(badge => {
        const userId = badge.dataset.userId;
        if (!userId) return;
        
        loadReputationBadge(badge, userId);
    });
});

function loadReputationBadge(badgeElement, userId) {
    fetch('${pageContext.request.contextPath}/reputation/api/badge/' + userId)
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            renderReputationBadge(badgeElement, data.badge);
        } else {
            renderReputationError(badgeElement);
        }
    })
    .catch(error => {
        console.error('Error loading reputation badge:', error);
        renderReputationError(badgeElement);
    });
}

function renderReputationBadge(badgeElement, badge) {
    // Apply reputation level styling
    const levelClass = 'reputation-' + badge.level.toLowerCase().replace(/ /g, '-');
    badgeElement.classList.add(levelClass);
    
    // Update content
    const content = badgeElement.querySelector('.reputation-content');
    content.innerHTML = `
        <div class="reputation-icon">${badge.badge}</div>
        <div class="reputation-info">
            <div class="reputation-level">${badge.level}</div>
            <div class="reputation-rating">
                <span class="reputation-stars">★</span>
                <span>${parseFloat(badge.rating).toFixed(1)}</span>
                <span>(${badge.reviewCount})</span>
            </div>
        </div>
    `;
    
    // Add click handler to show full reputation profile
    badgeElement.addEventListener('click', function() {
        window.open('${pageContext.request.contextPath}/reputation/profile/' + badge.userId, '_blank');
    });
}

function renderReputationError(badgeElement) {
    const content = badgeElement.querySelector('.reputation-content');
    content.innerHTML = '<div class="reputation-error">Unable to load</div>';
    badgeElement.style.cursor = 'default';
}
</script>