<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Listing - eBay Marketplace</title>
    <link rel="stylesheet" href="<c:url value='/assets/css/style.css'/>">
    <style>
        .listing-container {
            max-width: 800px;
            margin: 30px auto;
            padding: 30px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 0 20px rgba(0,0,0,0.1);
        }
        
        .form-section {
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 1px solid #eee;
        }
        
        .form-section:last-child {
            border-bottom: none;
        }
        
        .form-section h3 {
            color: #0073e6;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 2px solid #0073e6;
        }
        
        .form-group {
            margin-bottom: 20px;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
            color: #333;
        }
        
        .form-group input, .form-group select, .form-group textarea {
            width: 100%;
            padding: 12px;
            border: 2px solid #ddd;
            border-radius: 5px;
            font-size: 16px;
            transition: border-color 0.3s;
        }
        
        .form-group input:focus, .form-group select:focus, .form-group textarea:focus {
            outline: none;
            border-color: #0073e6;
        }
        
        .form-group textarea {
            resize: vertical;
            min-height: 120px;
        }
        
        .form-row {
            display: flex;
            gap: 20px;
        }
        
        .form-row .form-group {
            flex: 1;
        }
        
        .btn {
            background: #0073e6;
            color: white;
            padding: 12px 30px;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            transition: background 0.3s;
            margin-right: 15px;
        }
        
        .btn:hover {
            background: #005bb5;
        }
        
        .btn-secondary {
            background: #6c757d;
        }
        
        .btn-secondary:hover {
            background: #545b62;
        }
        
        .alert {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 5px;
        }
        
        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .auction-fields {
            display: none;
            padding: 20px;
            background: #f8f9fa;
            border-radius: 5px;
            margin-top: 15px;
        }
        
        .auction-fields.show {
            display: block;
        }
        
        .help-text {
            font-size: 14px;
            color: #666;
            margin-top: 5px;
        }
        
        .required {
            color: #dc3545;
        }
        
        .header-nav {
            background: #f8f9fa;
            padding: 15px 0;
            margin-bottom: 30px;
        }
        
        .header-nav .container {
            max-width: 1200px;
            margin: 0 auto;
            display: flex;
            align-items: center;
            gap: 20px;
            padding: 0 20px;
        }
        
        .breadcrumb {
            color: #666;
            font-size: 14px;
        }
        
        .breadcrumb a {
            color: #0073e6;
            text-decoration: none;
        }
        
        .breadcrumb a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <!-- Header Navigation -->
    <div class="header-nav">
        <div class="container">
            <div class="breadcrumb">
                <a href="/eBay/">Home</a> &gt; 
                <a href="/eBay/user/profile">My Account</a> &gt; 
                Create Listing
            </div>
        </div>
    </div>
    
    <div class="container">
        <div class="listing-container">
            <h1 style="text-align: center; margin-bottom: 30px; color: #333;">Create New Listing</h1>
            
            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    ${error}
                </div>
            </c:if>
            
            <c:if test="${not empty message}">
                <div class="alert alert-success">
                    ${message}
                </div>
            </c:if>
            
            <form:form method="POST" action="/eBay/product/create" modelAttribute="product">
                
                <!-- Basic Information -->
                <div class="form-section">
                    <h3>Basic Information</h3>
                    
                    <div class="form-group">
                        <label for="title">Title <span class="required">*</span></label>
                        <form:input path="title" id="title" required="true" maxlength="200" 
                                   placeholder="Enter a descriptive title for your item" />
                        <div class="help-text">Be specific and include key details like brand, model, size, etc.</div>
                    </div>
                    
                    <div class="form-group">
                        <label for="description">Description <span class="required">*</span></label>
                        <form:textarea path="description" id="description" required="true" 
                                      placeholder="Describe your item in detail..." />
                        <div class="help-text">Include condition details, features, and any defects or wear.</div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="category">Category <span class="required">*</span></label>
                            <form:select path="category.categoryId" id="category" required="true">
                                <form:option value="">Select a category</form:option>
                                <form:options items="${categories}" itemValue="categoryId" itemLabel="categoryName" />
                            </form:select>
                        </div>
                        
                        <div class="form-group">
                            <label for="conditionType">Condition <span class="required">*</span></label>
                            <form:select path="conditionType" id="conditionType" required="true">
                                <form:option value="">Select condition</form:option>
                                <form:option value="NEW">New</form:option>
                                <form:option value="LIKE_NEW">Like New</form:option>
                                <form:option value="VERY_GOOD">Very Good</form:option>
                                <form:option value="GOOD">Good</form:option>
                                <form:option value="ACCEPTABLE">Acceptable</form:option>
                                <form:option value="FOR_PARTS">For Parts/Not Working</form:option>
                            </form:select>
                        </div>
                    </div>
                </div>
                
                <!-- Pricing & Listing Type -->
                <div class="form-section">
                    <h3>Pricing & Listing Type</h3>
                    
                    <div class="form-group">
                        <label for="listingType">Listing Type <span class="required">*</span></label>
                        <form:select path="listingType" id="listingType" required="true" onchange="toggleAuctionFields()">
                            <form:option value="">Select listing type</form:option>
                            <form:option value="BUY_NOW">Buy It Now</form:option>
                            <form:option value="AUCTION">Auction</form:option>
                            <form:option value="BOTH">Auction with Buy It Now</form:option>
                        </form:select>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="startingPrice">Starting Price <span class="required">*</span></label>
                            <form:input path="startingPrice" type="number" step="0.01" min="0.01" 
                                       id="startingPrice" required="true" placeholder="0.00" />
                            <div class="help-text">Minimum bid amount for auctions, or fixed price for Buy It Now</div>
                        </div>
                        
                        <div class="form-group">
                            <label for="buyNowPrice">Buy It Now Price</label>
                            <form:input path="buyNowPrice" type="number" step="0.01" min="0.01" 
                                       id="buyNowPrice" placeholder="0.00" />
                            <div class="help-text">Optional for auctions, required for Buy It Now listings</div>
                        </div>
                    </div>
                    
                    <div class="auction-fields" id="auctionFields">
                        <h4 style="margin-bottom: 15px;">Auction Settings</h4>
                        
                        <div class="form-row">
                            <div class="form-group">
                                <label for="auctionEndTime">Auction End Date & Time</label>
                                <form:input path="auctionEndTime" type="datetime-local" id="auctionEndTime" />
                                <div class="help-text">When should this auction end?</div>
                            </div>
                            
                            <div class="form-group">
                                <label for="reservePrice">Reserve Price (Optional)</label>
                                <form:input path="reservePrice" type="number" step="0.01" min="0.01" 
                                           id="reservePrice" placeholder="0.00" />
                                <div class="help-text">Minimum price you'll accept (hidden from bidders)</div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Shipping & Details -->
                <div class="form-section">
                    <h3>Shipping & Additional Details</h3>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="shippingCost">Shipping Cost</label>
                            <form:input path="shippingCost" type="number" step="0.01" min="0" 
                                       id="shippingCost" placeholder="0.00" />
                            <div class="help-text">Enter 0 for free shipping</div>
                        </div>
                        
                        <div class="form-group">
                            <label for="quantityAvailable">Quantity Available</label>
                            <form:input path="quantityAvailable" type="number" min="1" 
                                       id="quantityAvailable" value="1" />
                            <div class="help-text">How many are you selling?</div>
                        </div>
                    </div>
                    
                    <div class="form-row">
                        <div class="form-group">
                            <label for="shippingMethod">Shipping Method</label>
                            <form:input path="shippingMethod" id="shippingMethod" 
                                       placeholder="e.g., Standard, Express, Local Pickup" />
                        </div>
                        
                        <div class="form-group">
                            <label for="itemLocation">Item Location</label>
                            <form:input path="itemLocation" id="itemLocation" 
                                       placeholder="City, State or ZIP code" />
                        </div>
                    </div>
                    
                    <div class="form-group">
                        <label for="returnPolicy">Return Policy</label>
                        <form:textarea path="returnPolicy" id="returnPolicy" rows="3"
                                      placeholder="Describe your return policy (e.g., 30-day returns accepted, buyer pays return shipping)" />
                    </div>
                </div>
                
                <!-- Action Buttons -->
                <div style="text-align: center; margin-top: 30px;">
                    <button type="submit" class="btn">Save as Draft</button>
                    <a href="/eBay/user/profile" class="btn btn-secondary" style="text-decoration: none;">Cancel</a>
                </div>
                
                <div class="help-text" style="text-align: center; margin-top: 15px;">
                    <strong>Note:</strong> Your listing will be saved as a draft. You'll need to add images and activate it before it goes live.
                </div>
            </form:form>
        </div>
    </div>
    
    <script>
        function toggleAuctionFields() {
            const listingType = document.getElementById('listingType').value;
            const auctionFields = document.getElementById('auctionFields');
            const auctionEndTime = document.getElementById('auctionEndTime');
            
            if (listingType === 'AUCTION' || listingType === 'BOTH') {
                auctionFields.classList.add('show');
                auctionEndTime.required = true;
                
                // Set default end time to 7 days from now
                if (!auctionEndTime.value) {
                    const now = new Date();
                    now.setDate(now.getDate() + 7);
                    const isoString = now.toISOString().slice(0, 16);
                    auctionEndTime.value = isoString;
                }
            } else {
                auctionFields.classList.remove('show');
                auctionEndTime.required = false;
            }
        }
        
        // Initialize on page load
        document.addEventListener('DOMContentLoaded', function() {
            toggleAuctionFields();
        });
        
        // Update Buy It Now price validation based on listing type
        document.getElementById('listingType').addEventListener('change', function() {
            const buyNowPrice = document.getElementById('buyNowPrice');
            
            if (this.value === 'BUY_NOW') {
                buyNowPrice.required = true;
            } else {
                buyNowPrice.required = false;
            }
        });
    </script>
</body>
</html>