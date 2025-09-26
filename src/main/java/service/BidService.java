package service;

import dao.BidDAO;
import dao.ProductDAO;
import entity.Bid;
import entity.Product;
import entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Service
@Transactional
public class BidService {
    
    @Autowired
    private BidDAO bidDAO;
    
    @Autowired
    private ProductDAO productDAO;
    
    public Bid placeBid(Integer productId, Integer bidderId, BigDecimal bidAmount, Bid.BidType bidType, BigDecimal maxProxyAmount) {
        // Get the product and validate
        Product product = productDAO.findById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        
        // Validate auction is active
        if (!product.isAuctionActive()) {
            throw new RuntimeException("Auction is not active or has ended");
        }
        
        // Validate bid amount
        validateBidAmount(productId, bidAmount);
        
        // Check if bidder is not the seller
        if (product.getSeller().getUserId().equals(bidderId)) {
            throw new RuntimeException("You cannot bid on your own item");
        }
        
        // Create the bid
        Bid bid = new Bid();
        bid.setProduct(product);
        bid.setBidder(new User()); // Will be set properly in controller
        bid.getBidder().setUserId(bidderId);
        bid.setBidAmount(bidAmount);
        bid.setBidType(bidType);
        bid.setBidTime(new Timestamp(System.currentTimeMillis()));
        bid.setBidStatus(Bid.BidStatus.ACTIVE);
        
        if (bidType == Bid.BidType.PROXY && maxProxyAmount != null) {
            bid.setMaxProxyAmount(maxProxyAmount);
        }
        
        // Save the bid
        bid = bidDAO.save(bid);
        
        // Update bid statuses and process proxy bids
        processBidUpdate(productId, bid);
        
        // Update product current price
        updateProductCurrentPrice(productId);
        
        return bid;
    }
    
    public Bid placeBuyNowBid(Integer productId, Integer bidderId) {
        Product product = productDAO.findById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }
        
        if (!product.isBuyNowAvailable() || product.getBuyNowPrice() == null) {
            throw new RuntimeException("Buy It Now is not available for this item");
        }
        
        if (product.getSeller().getUserId().equals(bidderId)) {
            throw new RuntimeException("You cannot buy your own item");
        }
        
        // Create Buy Now bid
        Bid bid = new Bid();
        bid.setProduct(product);
        bid.setBidder(new User());
        bid.getBidder().setUserId(bidderId);
        bid.setBidAmount(product.getBuyNowPrice());
        bid.setBidType(Bid.BidType.BUY_NOW);
        bid.setBidTime(new Timestamp(System.currentTimeMillis()));
        bid.setBidStatus(Bid.BidStatus.WON);
        bid.setIsWinningBid(true);
        
        bid = bidDAO.save(bid);
        
        // Mark all other bids as outbid
        bidDAO.markPreviousBidsAsOutbid(productId, bid.getBidId());
        
        // End the auction immediately
        product.setStatus(Product.ProductStatus.SOLD);
        productDAO.update(product);
        
        return bid;
    }
    
    public List<Bid> getBidHistory(Integer productId) {
        return bidDAO.findBidHistoryForProduct(productId);
    }
    
    public Bid getHighestBid(Integer productId) {
        return bidDAO.findHighestBidForProduct(productId);
    }
    
    public Bid getWinningBid(Integer productId) {
        return bidDAO.findWinningBidForProduct(productId);
    }
    
    public List<Bid> getUserBids(Integer userId) {
        return bidDAO.findByBidderId(userId);
    }
    
    public List<Bid> getUserActiveBids(Integer userId) {
        return bidDAO.findActiveBidsByBidderId(userId);
    }
    
    public BigDecimal getMinimumBidAmount(Integer productId) {
        return bidDAO.getMinimumBidAmount(productId);
    }
    
    public Long getBidCount(Integer productId) {
        return bidDAO.getBidCountForProduct(productId);
    }
    
    public boolean hasUserBid(Integer userId, Integer productId) {
        return bidDAO.hasUserBidOnProduct(userId, productId);
    }
    
    public Bid getUserLastBid(Integer userId, Integer productId) {
        return bidDAO.findUserLastBidOnProduct(userId, productId);
    }
    
    /**
     * Process expired auctions and determine winners
     */
    public void processExpiredAuctions() {
        List<Product> expiredAuctions = productDAO.findExpiredAuctions();
        
        for (Product product : expiredAuctions) {
            Bid winningBid = bidDAO.findHighestBidForProduct(product.getProductId());
            
            if (winningBid != null) {
                // Check if reserve price is met (if set)
                if (product.getReservePrice() == null || 
                    winningBid.getBidAmount().compareTo(product.getReservePrice()) >= 0) {
                    
                    // Mark bid as won
                    bidDAO.markBidAsWon(winningBid.getBidId());
                    
                    // Mark product as sold
                    product.setStatus(Product.ProductStatus.SOLD);
                    
                    // TODO: Create order when order system is implemented
                } else {
                    // Reserve not met, auction ends without sale
                    product.setStatus(Product.ProductStatus.ENDED);
                }
            } else {
                // No bids, auction ends
                product.setStatus(Product.ProductStatus.ENDED);
            }
            
            productDAO.update(product);
        }
    }
    
    /**
     * Get bid statistics for a product
     */
    public BidStatistics getBidStatistics(Integer productId) {
        List<Object[]> stats = bidDAO.getBidStatistics(productId);
        if (stats.isEmpty()) {
            return new BidStatistics(0L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        
        Object[] result = stats.get(0);
        return new BidStatistics(
            (Long) result[0],
            (BigDecimal) result[1],
            (BigDecimal) result[2],
            (BigDecimal) result[3]
        );
    }
    
    /**
     * Auto-bid using proxy bidding system
     */
    public void processProxyBids(Integer productId) {
        Bid currentHighBid = bidDAO.findHighestBidForProduct(productId);
        BigDecimal currentAmount = currentHighBid != null ? currentHighBid.getBidAmount() : BigDecimal.ZERO;
        
        List<Bid> proxyBids = bidDAO.findProxyBidsToExecute(productId, currentAmount);
        
        for (Bid proxyBid : proxyBids) {
            // Calculate next bid amount
            BigDecimal nextBidAmount = getMinimumBidAmount(productId);
            
            // Don't exceed proxy maximum
            if (nextBidAmount.compareTo(proxyBid.getMaxProxyAmount()) <= 0) {
                // Create new bid at the calculated amount
                Bid newBid = new Bid();
                newBid.setProduct(proxyBid.getProduct());
                newBid.setBidder(proxyBid.getBidder());
                newBid.setBidAmount(nextBidAmount);
                newBid.setBidType(Bid.BidType.PROXY);
                newBid.setBidTime(new Timestamp(System.currentTimeMillis()));
                newBid.setBidStatus(Bid.BidStatus.ACTIVE);
                
                bidDAO.save(newBid);
                
                // Update winning status
                processBidUpdate(productId, newBid);
            }
        }
    }
    
    /**
     * Validate bid amount
     */
    private void validateBidAmount(Integer productId, BigDecimal bidAmount) {
        if (bidAmount == null || bidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Bid amount must be greater than 0");
        }
        
        BigDecimal minimumBid = getMinimumBidAmount(productId);
        if (bidAmount.compareTo(minimumBid) < 0) {
            throw new RuntimeException("Bid must be at least $" + minimumBid);
        }
    }
    
    /**
     * Update bid statuses after a new bid
     */
    private void processBidUpdate(Integer productId, Bid newBid) {
        // Mark all previous bids as outbid
        bidDAO.markPreviousBidsAsOutbid(productId, newBid.getBidId());
        
        // Mark new bid as winning
        bidDAO.markBidAsWinning(newBid.getBidId());
        
        // Process any proxy bids that might now be triggered
        processProxyBids(productId);
    }
    
    /**
     * Update product current price based on highest bid
     */
    private void updateProductCurrentPrice(Integer productId) {
        Bid highestBid = bidDAO.findHighestBidForProduct(productId);
        Product product = productDAO.findById(productId);
        
        if (highestBid != null && product != null) {
            product.setCurrentPrice(highestBid.getBidAmount());
            productDAO.update(product);
        }
    }
    
    /**
     * Bid statistics helper class
     */
    public static class BidStatistics {
        private final Long bidCount;
        private final BigDecimal minBid;
        private final BigDecimal maxBid;
        private final BigDecimal avgBid;
        
        public BidStatistics(Long bidCount, BigDecimal minBid, BigDecimal maxBid, BigDecimal avgBid) {
            this.bidCount = bidCount;
            this.minBid = minBid;
            this.maxBid = maxBid;
            this.avgBid = avgBid;
        }
        
        public Long getBidCount() { return bidCount; }
        public BigDecimal getMinBid() { return minBid; }
        public BigDecimal getMaxBid() { return maxBid; }
        public BigDecimal getAvgBid() { return avgBid; }
    }
}