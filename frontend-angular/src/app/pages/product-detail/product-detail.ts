import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ProductService} from '../../services/product';
import {BidService} from '../../services/bid.service';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './product-detail.html',
  styleUrl: './product-detail.css',
})
export class ProductDetail implements OnInit {
  productId!: number;
  product: any = null;
  bidHistory: any[] = [];
  loading = true;
  error: string | null = null;

  bidAmount: number | null = null;
  selectedImageIndex = 0;

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private bidService: BidService
  ) {};

  ngOnInit() {
    this.loadProduct();
  }

  loadProduct() {
    const productId = this.route.snapshot.paramMap.get('id');
    if (!productId) {
      this.error = 'Product ID is missing';
      return;
    }
    this.loading = true;
    this.productService.getProductById(productId).subscribe({
      next: (data: any) => {
        this.product = data;
        this.bidHistory = data.bids || data.bidHistory || [];
        this.loading = false;
      },
      error: (err: any) => {
        this.error = err.error?.message || err.message || 'Failed to load product';
        this.loading = false;
      }
    })
  }

  placeBid() {
    if (!this.bidAmount || this.bidAmount <= 0) {
      this.error = 'Please enter a valid bid amount';
      return;
    }
    
    if (!this.product) {
      this.error = 'Product not loaded';
      return;
    }

    const productId = this.product.id || this.product.productId;

    this.bidService.placeBid(productId, this.bidAmount).subscribe({
      next: (response: any) => {
        this.bidHistory.unshift(response);
        this.bidAmount = null;
        this.loadProduct();
      },
      error: (err: any) => {
        this.error = 'Failed to place bid: ' + (err.error?.message || err.message || 'Unknown error');
      }
    })
  }

  buyItNow() {
    if (!this.product) {
      this.error = 'Product not loaded';
      return;
    }

    const productId = this.product.id || this.product.productId;

    this.productService.buyProduct(productId).subscribe({
      next: () => {
        alert('Product purchased successfully!');
        this.loadProduct();
      },
      error: (err: any) => {
        this.error = 'Failed to purchase product: ' + (err.error?.message || err.message || 'Unknown error');
      }
    })
  }

  selectImage(index: number) {
    this.selectedImageIndex = index;
  }

  getImageUrl(imageUrl: string): string {
    return this.productService.getFullImageUrl(imageUrl);
  }
}
