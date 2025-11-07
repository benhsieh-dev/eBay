import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Api } from '../../services/api';

interface Product {
  productId: number;
  title: string;
  description: string;
  currentPrice: number;
  buyNowPrice?: number;
  imageUrl?: string;
  endTime?: string;
  condition: string;
  listingType: 'AUCTION' | 'BUY_NOW' | 'AUCTION_WITH_BUY_NOW';
  status: string;
  sellerId: number;
  createdDate: string;
}

interface WatchlistItem {
  watchlistId: number;
  addedDate: string;
  product: {
    productId: number;
    name: string;
    description: string;
    currentPrice: number;
    buyItNowPrice?: number;
    status: string;
    endTime?: string;
    seller: {
      userId: number;
      username: string;
      firstName?: string;
      lastName?: string;
    };
  };
}

interface BidItem {
  bidId: number;
  bidAmount: number;
  bidTime: string;
  bidType: string;
  bidStatus: string;
  isWinningBid: boolean;
  maxProxyAmount?: number;
  product: {
    productId: number;
    title: string;
    currentPrice: number;
    status: string;
    isAuctionActive: boolean;
    timeRemaining: string;
  };
}

@Component({
  selector: 'app-my-ebay',
  imports: [CommonModule],
  templateUrl: './my-ebay.html',
  styleUrl: './my-ebay.css',
})
export class MyEbay implements OnInit {
  myListings: Product[] = [];
  watchlist: WatchlistItem[] = [];
  myBids: BidItem[] = [];
  loading = true;
  error = '';
  currentPage = 0;
  totalCount = 0;
  activeTab: 'selling' | 'buying' | 'watching' | 'bidding' = 'selling';
  
  pageSize = 10;

  constructor(
    private api: Api,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadTabData();
  }

  setActiveTab(tab: 'selling' | 'buying' | 'watching' | 'bidding') {
    this.activeTab = tab;
    this.currentPage = 0;
    this.loadTabData();
  }

  private loadTabData() {
    if (this.activeTab === 'selling') {
      this.fetchMyListings();
    } else if (this.activeTab === 'watching') {
      this.fetchWatchlist();
    } else if (this.activeTab === 'bidding') {
      this.fetchMyBids();
    }
  }

  fetchMyListings() {
    this.loading = true;
    this.api.getMyListings({
      page: this.currentPage,
      size: this.pageSize
    }).subscribe({
      next: (response) => {
        if (response.success) {
          this.myListings = response.products || [];
          this.totalCount = response.totalCount || 0;
          this.error = '';
        } else {
          this.error = response.error || 'Failed to fetch your listings';
        }
        this.loading = false;
      },
      error: (err) => {
        if (err.status === 401) {
          this.error = 'Please log in to view your listings';
          this.router.navigate(['/login']);
        } else {
          this.error = 'Failed to fetch your listings. Please try again.';
        }
        this.loading = false;
      }
    });
  }

  fetchWatchlist() {
    this.loading = true;
    this.api.getWatchlist().subscribe({
      next: (response) => {
        if (response.success) {
          this.watchlist = response.watchlist || [];
          this.totalCount = response.count || 0;
          this.error = '';
        } else {
          this.error = response.error || 'Failed to fetch your watchlist';
        }
        this.loading = false;
      },
      error: (err) => {
        if (err.status === 401) {
          this.error = 'Please log in to view your watchlist';
          this.router.navigate(['/login']);
        } else {
          this.error = 'Failed to fetch your watchlist. Please try again.';
        }
        this.loading = false;
      }
    });
  }

  fetchMyBids() {
    this.loading = true;
    this.api.getMyBids().subscribe({
      next: (response) => {
        if (response.success) {
          this.myBids = response.userBids;
          this.totalCount = response.userBids.length;
          this.error = '';
        } else {
          this.error = response.error || 'Failed to fetch your bids';
        }
        this.loading = false;
      },
      error: (err) => {
        if (err.status === 401) {
          this.error = 'Please log in to view your bids';
          this.router.navigate(['/login']);
        } else {
          this.error = 'Failed to fetch your bids. Please try again.';
        }
        this.loading = false;
      }
    });
  }

  removeFromWatchlist(productId: number) {
    this.api.toggleWatchlist(productId).subscribe({
      next: (response) => {
        if (response.success) {
          this.fetchWatchlist(); // Refresh watchlist
        } else {
          alert(response.error || 'Failed to remove from watchlist');
        }
      },
      error: (err) => {
        alert(err.error?.error || 'Failed to remove from watchlist. Please try again.');
      }
    });
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(price);
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  formatTimeRemaining(endTime: string): string {
    const now = new Date();
    const end = new Date(endTime);
    const diff = end.getTime() - now.getTime();
    
    if (diff <= 0) return 'Ended';
    
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));
    const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
    const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    
    if (days > 0) return `${days}d ${hours}h`;
    if (hours > 0) return `${hours}h ${minutes}m`;
    return `${minutes}m`;
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'ACTIVE': return '#2e7d32';
      case 'SOLD': return '#1976d2';
      case 'ENDED': return '#d32f2f';
      case 'CANCELLED': return '#757575';
      default: return '#666';
    }
  }

  getBidStatusColor(bidStatus: string, isWinning: boolean): string {
    if (isWinning) return '#2e7d32';
    switch (bidStatus) {
      case 'ACTIVE': return '#1976d2';
      case 'OUTBID': return '#f57c00';
      case 'WON': return '#2e7d32';
      case 'LOST': return '#d32f2f';
      default: return '#666';
    }
  }

  getBidStatusText(bidStatus: string, isWinning: boolean): string {
    if (isWinning && bidStatus === 'ACTIVE') return 'Winning';
    switch (bidStatus) {
      case 'ACTIVE': return 'Outbid';
      case 'OUTBID': return 'Outbid';
      case 'WON': return 'Won';
      case 'LOST': return 'Lost';
      default: return bidStatus;
    }
  }

  navigateToProduct(productId: number) {
    this.router.navigate(['/products', productId]);
  }

  navigateToSell() {
    this.router.navigate(['/sell']);
  }

  navigateToProducts() {
    this.router.navigate(['/products']);
  }

  get totalPages(): number {
    return Math.ceil(this.totalCount / this.pageSize);
  }

  previousPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadTabData();
    }
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadTabData();
    }
  }

  // Helper methods for template
  getActiveListingsCount(): number {
    return this.myListings.filter(p => p.status === 'ACTIVE').length;
  }

  getSoldListingsCount(): number {
    return this.myListings.filter(p => p.status === 'SOLD').length;
  }

  getListingTypeText(listingType: string): string {
    switch (listingType) {
      case 'BUY_NOW': return 'Buy It Now';
      case 'AUCTION': return 'Auction';
      case 'AUCTION_WITH_BUY_NOW': return 'Auction + BIN';
      default: return listingType;
    }
  }

  getSellerName(seller: { username: string; firstName?: string; lastName?: string }): string {
    if (seller.firstName && seller.lastName) {
      return `${seller.firstName} ${seller.lastName}`;
    }
    return seller.username;
  }
}
