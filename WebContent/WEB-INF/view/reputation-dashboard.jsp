<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Reputation Dashboard - eBay</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/style.css">
    <style>
        .reputation-container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 0 1rem;
        }
        
        .reputation-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 2rem;
            border-radius: 12px;
            margin-bottom: 2rem;
            text-align: center;
        }
        
        .reputation-title {
            font-size: 2.5rem;
            margin-bottom: 0.5rem;
            font-weight: 300;
        }
        
        .reputation-subtitle {
            font-size: 1.1rem;
            opacity: 0.9;
        }
        
        .reputation-overview {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 2rem;
            margin-bottom: 2rem;
        }
        
        .reputation-card {
            background: white;
            padding: 2rem;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            text-align: center;
            transition: transform 0.3s ease;
        }
        
        .reputation-card:hover {
            transform: translateY(-4px);
        }
        
        .reputation-badge {
            font-size: 3rem;
            margin-bottom: 1rem;
        }
        
        .reputation-level {
            font-size: 1.5rem;
            font-weight: 600;
            margin-bottom: 0.5rem;
            color: #2c3e50;
        }
        
        .reputation-score {
            font-size: 2.5rem;
            font-weight: bold;
            margin-bottom: 0.5rem;
        }
        
        .reputation-details {
            color: #7f8c8d;
            font-size: 0.9rem;
        }
        
        .component-scores {
            background: white;
            padding: 2rem;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            margin-bottom: 2rem;
        }
        
        .component-scores h2 {
            color: #2c3e50;
            margin-bottom: 1.5rem;
            text-align: center;
        }
        
        .score-breakdown {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1.5rem;
        }
        
        .score-item {
            text-align: center;
        }
        
        .score-label {
            font-weight: 500;
            color: #34495e;
            margin-bottom: 0.5rem;
        }
        
        .score-value {
            font-size: 2rem;
            font-weight: bold;
            color: #3498db;
            margin-bottom: 0.5rem;
        }
        
        .score-bar {
            height: 8px;
            background: #ecf0f1;
            border-radius: 4px;
            overflow: hidden;
            margin-bottom: 0.5rem;
        }
        
        .score-progress {
            height: 100%;
            background: linear-gradient(90deg, #3498db, #2ecc71);
            transition: width 0.3s ease;
        }
        
        .score-description {
            font-size: 0.8rem;
            color: #7f8c8d;
        }
        
        .insights-section {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 2rem;
            margin-bottom: 2rem;
        }
        
        .insights-card {
            background: white;
            padding: 2rem;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }
        
        .insights-card h3 {
            color: #2c3e50;
            margin-bottom: 1rem;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }
        
        .insights-list {
            list-style: none;
            padding: 0;
        }
        
        .insights-list li {
            padding: 0.75rem;
            margin-bottom: 0.5rem;
            background: #f8f9fa;
            border-radius: 6px;
            border-left: 4px solid #3498db;
        }
        
        .suggestions-list li {
            border-left-color: #e74c3c;
        }
        
        .percentile-section {
            background: white;
            padding: 2rem;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            text-align: center;
            margin-bottom: 2rem;
        }
        
        .percentile-chart {
            position: relative;
            width: 200px;
            height: 200px;
            margin: 2rem auto;
        }
        
        .percentile-circle {
            width: 100%;
            height: 100%;
            border-radius: 50%;
            background: conic-gradient(#3498db 0deg, #3498db var(--percentile), #ecf0f1 var(--percentile), #ecf0f1 360deg);
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .percentile-inner {
            width: 140px;
            height: 140px;
            background: white;
            border-radius: 50%;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
        }
        
        .percentile-value {
            font-size: 2.5rem;
            font-weight: bold;
            color: #3498db;
        }
        
        .percentile-label {
            font-size: 0.9rem;
            color: #7f8c8d;
        }
        
        .action-buttons {
            display: flex;
            gap: 1rem;
            justify-content: center;
            margin-top: 2rem;
            flex-wrap: wrap;
        }
        
        .btn {
            padding: 0.75rem 2rem;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-weight: 500;
            text-decoration: none;
            display: inline-block;
            transition: all 0.3s ease;
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
        
        .reputation-tip {
            background: #e8f4fd;
            border: 1px solid #bee5eb;
            border-radius: 8px;
            padding: 1rem;
            margin-top: 2rem;
        }
        
        .reputation-tip h4 {
            color: #0c5460;
            margin-bottom: 0.5rem;
        }
        
        .reputation-tip p {
            color: #0c5460;
            margin: 0;
        }
        
        @media (max-width: 768px) {
            .reputation-container {
                margin: 1rem auto;
                padding: 0 0.5rem;
            }
            
            .reputation-header {
                padding: 1.5rem;
            }
            
            .reputation-title {
                font-size: 2rem;
            }
            
            .reputation-overview {
                grid-template-columns: 1fr;
            }
            
            .insights-section {
                grid-template-columns: 1fr;
            }
            
            .component-scores {
                padding: 1rem;
            }
            
            .score-breakdown {
                grid-template-columns: 1fr;
            }
            
            .action-buttons {
                flex-direction: column;
                align-items: center;
            }
        }
    </style>
</head>
<body>
    <div class="reputation-container">
        <!-- Header -->
        <div class="reputation-header">
            <h1 class="reputation-title">Your Reputation</h1>
            <p class="reputation-subtitle">Track your performance and build trust with buyers</p>
        </div>
        
        <!-- Reputation Overview -->
        <div class="reputation-overview">
            <div class="reputation-card">
                <div class="reputation-badge">${reputationScore.level.badge}</div>
                <div class="reputation-level" style="color: ${reputationScore.level.color}">
                    ${reputationScore.level.displayName}
                </div>
                <div class="reputation-details">Current Status</div>
            </div>
            
            <div class="reputation-card">
                <div class="reputation-score" style="color: #3498db;">
                    <fmt:formatNumber value="${reputationScore.overallScore}" maxFractionDigits="1"/>
                </div>
                <div class="reputation-level">Overall Score</div>
                <div class="reputation-details">Out of 100</div>
            </div>
            
            <div class="reputation-card">
                <div class="reputation-score" style="color: #f39c12;">
                    <fmt:formatNumber value="${reputationScore.averageRating}" maxFractionDigits="2"/>
                </div>
                <div class="reputation-level">Average Rating</div>
                <div class="reputation-details">${reputationScore.reviewCount} reviews</div>
            </div>
        </div>
        
        <!-- Component Scores -->
        <div class="component-scores">
            <h2>Performance Breakdown</h2>
            <div class="score-breakdown">
                <div class="score-item">
                    <div class="score-label">Rating Quality</div>
                    <div class="score-value">
                        <fmt:formatNumber value="${reputationScore.ratingScore}" maxFractionDigits="0"/>
                    </div>
                    <div class="score-bar">
                        <div class="score-progress" style="width: ${reputationScore.ratingScore}%"></div>
                    </div>
                    <div class="score-description">Based on customer satisfaction</div>
                </div>
                
                <div class="score-item">
                    <div class="score-label">Sales Volume</div>
                    <div class="score-value">
                        <fmt:formatNumber value="${reputationScore.volumeScore}" maxFractionDigits="0"/>
                    </div>
                    <div class="score-bar">
                        <div class="score-progress" style="width: ${reputationScore.volumeScore}%"></div>
                    </div>
                    <div class="score-description">Transaction history & volume</div>
                </div>
                
                <div class="score-item">
                    <div class="score-label">Recent Activity</div>
                    <div class="score-value">
                        <fmt:formatNumber value="${reputationScore.recencyScore}" maxFractionDigits="0"/>
                    </div>
                    <div class="score-bar">
                        <div class="score-progress" style="width: ${reputationScore.recencyScore}%"></div>
                    </div>
                    <div class="score-description">Recent engagement & activity</div>
                </div>
                
                <div class="score-item">
                    <div class="score-label">Review Quality</div>
                    <div class="score-value">
                        <fmt:formatNumber value="${reputationScore.qualityScore}" maxFractionDigits="0"/>
                    </div>
                    <div class="score-bar">
                        <div class="score-progress" style="width: ${reputationScore.qualityScore}%"></div>
                    </div>
                    <div class="score-description">Detail & helpfulness of reviews</div>
                </div>
            </div>
        </div>
        
        <!-- Percentile Ranking -->
        <div class="percentile-section">
            <h2>Performance Ranking</h2>
            <div class="percentile-chart">
                <div class="percentile-circle" style="--percentile: ${comparison.percentile * 3.6}deg">
                    <div class="percentile-inner">
                        <div class="percentile-value">${comparison.percentile}%</div>
                        <div class="percentile-label">Percentile</div>
                    </div>
                </div>
            </div>
            <p>You're performing better than <strong>${comparison.percentile}%</strong> of sellers on the platform</p>
        </div>
        
        <!-- Insights and Suggestions -->
        <div class="insights-section">
            <div class="insights-card">
                <h3>📈 Performance Insights</h3>
                <ul class="insights-list">
                    <c:forEach var="insight" items="${comparison.insights}">
                        <li>${insight}</li>
                    </c:forEach>
                </ul>
            </div>
            
            <div class="insights-card">
                <h3>💡 Improvement Suggestions</h3>
                <ul class="insights-list suggestions-list">
                    <c:forEach var="suggestion" items="${suggestions}">
                        <li>${suggestion}</li>
                    </c:forEach>
                </ul>
            </div>
        </div>
        
        <!-- Action Buttons -->
        <div class="action-buttons">
            <a href="${pageContext.request.contextPath}/reviews/my-reviews" class="btn btn-primary">
                View My Reviews
            </a>
            <a href="${pageContext.request.contextPath}/seller/dashboard" class="btn btn-secondary">
                Seller Dashboard
            </a>
            <button class="btn btn-primary" onclick="updateReputation()">
                Refresh Reputation
            </button>
        </div>
        
        <!-- Reputation Tip -->
        <div class="reputation-tip">
            <h4>💡 Pro Tip</h4>
            <p>Your reputation score is updated automatically as you receive new reviews. Maintain excellent customer service, ship quickly, and communicate clearly to keep improving your score!</p>
        </div>
    </div>
    
    <script>
        function updateReputation() {
            const userId = ${currentUser.userId};
            
            fetch('${pageContext.request.contextPath}/reputation/api/update', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: 'userId=' + userId
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    alert('Reputation updated successfully!');
                    location.reload();
                } else {
                    alert(data.message || 'Error updating reputation');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                alert('Error updating reputation');
            });
        }
        
        // Animate progress bars on load
        window.addEventListener('load', function() {
            const progressBars = document.querySelectorAll('.score-progress');
            progressBars.forEach(bar => {
                const width = bar.style.width;
                bar.style.width = '0%';
                setTimeout(() => {
                    bar.style.width = width;
                }, 500);
            });
        });
    </script>
</body>
</html>