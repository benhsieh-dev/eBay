import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Api } from '../../services/api';

interface Product {
  productId: number;
  title: string;
  currentPrice: number;
  imageUrl?: string;
  description: string;
  category?: {
    categoryName: string;
  };
}

@Component({
  selector: 'app-home',
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {
  featuredProducts: Product[] = [];
  loading = true;
  error: string | null = null;

  constructor(private api: Api) {}

  ngOnInit() {
    this.fetchFeaturedProducts();
  }

  private fetchFeaturedProducts() {
    this.api.getFeaturedProducts().subscribe({
      next: (response) => {
        if (response.success) {
          this.featuredProducts = response.products || [];
        } else {
          throw new Error(response.error || 'Failed to fetch featured products');
        }
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching featured products:', err);
        this.error = 'Failed to load featured products';
        // Set some mock data for now
        this.featuredProducts = [
          {
            productId: 1,
            title: 'iPhone 14 Pro',
            currentPrice: 999,
            description: 'Latest iPhone with advanced camera system',
            category: { categoryName: 'Electronics' }
          },
          {
            productId: 2,
            title: 'Nike Air Jordan 1',
            currentPrice: 179,
            description: 'Classic basketball sneakers',
            category: { categoryName: 'Fashion' }
          },
          {
            productId: 3,
            title: 'MacBook Pro M3',
            currentPrice: 1999,
            description: 'Powerful laptop for professionals',
            category: { categoryName: 'Electronics' }
          }
        ];
        this.loading = false;
      }
    });
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD'
    }).format(price);
  }
}
