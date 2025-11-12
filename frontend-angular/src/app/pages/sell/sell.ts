import {Component, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {Router} from '@angular/router';
import {ProductService} from '../../services/product';

@Component({
  selector: 'app-sell',
  standalone: true,
  imports: [CommonModule, FormsModule, HttpClientModule],
  templateUrl: './sell.html',
  styleUrls: ['./sell.css'],
})
export class Sell implements OnInit {
  item = {
    title: '',
    description: '',
    categoryId: null,
    condition: '',
    startPrice: 1,
    buyNowPrice: null,
    duration: 7,
    listingType: 'AUCTION'
  };

  categories: any[] = [];

  conditions = ['New', 'Used - Like New', 'Used - Good', 'Used - Acceptable'];
  durations = [1, 3, 5, 7, 10];
  listingTypes = [
    { value: 'AUCTION', label: 'Auction Only' },
    { value: 'BUY_NOW', label: 'Buy It Now Only' },
    { value: 'BOTH', label: 'Auction + Buy It Now' }
  ];

  selectedFiles: File[] = [];

  constructor(private api: ProductService, private router: Router) {}

  ngOnInit() {
    console.log('Loading categories...');
    this.api.getCategories().subscribe({
      next: (response: { success: any; categories: any[]; }) => {
        console.log('Categories response:', response);
        if (response.success && response.categories) {
          this.categories = response.categories;
          console.log('Categories loaded:', this.categories);
        }
      },
      error: (err: any) => console.error('Error fetching categories:', err)
    })
  }
  onFileSelected(event: any) {
    this.selectedFiles = Array.from(event.target.files);
  }

  onSubmit() {
    if (!this.item.title || !this.item.description || !this.item.categoryId || !this.item.condition || !this.item.startPrice || this.item.startPrice <= 0) {
      alert('Please fill in all required fields and ensure starting price is greater than 0.');
      return;
    }

    // Calculate auction end time (current time + duration in days)
    const now = new Date();
    const auctionEndTime = new Date(now.getTime() + (this.item.duration * 24 * 60 * 60 * 1000));

    const payload = {
      title: this.item.title,
      description: this.item.description,
      categoryId: Number(this.item.categoryId),
      condition: this.item.condition,
      startingPrice: Number(this.item.startPrice),
      buyNowPrice: this.item.buyNowPrice ? Number(this.item.buyNowPrice) : null,
      duration: this.item.duration,
      listingType: this.item.listingType,
      endTime: auctionEndTime.toISOString()
    };
    console.log('Sending payload:', payload);

    this.api.createProduct(payload).subscribe({
      next: (response) => {
        console.log('Product created - full response:', response);

        // Check if request was successful
        if (!response.success) {
          alert('Error: ' + response.error);
          return;
        }

        // Try different possible response structures
        const productId = response.product?.productId || response.productId || response.id;
        console.log('Looking for productId:', {
          'response.product?.productId': response.product?.productId,
          'response.productId': response.productId,
          'response.id': response.id,
          'final productId': productId
        });

        if (!productId) {
          console.error('Product ID not found in response', response);
          return;
        }

        // console.log('Product created successfully! Product ID:', productId);
        // alert('Listing created successfully!');
        // this.router.navigate(['/my-ebay']);

        if (this.selectedFiles.length) {
          this.uploadImage(productId);
        } else {
          this.router.navigate(['/my-ebay']);
        }
      },
      error: (err) => console.error('Error creating product:', err)
    })
  }

  uploadImage(productId: number) {
    const formData = new FormData();
    for (const file of this.selectedFiles) {
      formData.append('images', file);
    }

    this.api.uploadProductImages(productId, formData).subscribe({
      next: (response: any) => {
        console.log('Images uploaded:', response);
        console.log('Image URLs returned:', response.imageUrls);
        // ✅ Redirect after successful upload
        this.router.navigate(['/my-ebay']);
      },
      error: (err: any) => {
        console.error('Error uploading images:', err);
      }
    });
  }

}
