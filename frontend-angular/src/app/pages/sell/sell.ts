import { Component } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {Router} from '@angular/router';
import {ProductService} from '../../services/product';

@Component({
  selector: 'app-sell',
  standalone: true,
  imports: [FormsModule, HttpClientModule],
  templateUrl: './sell.html',
  styleUrl: './sell.css',
})
export class Sell {
  item = {
    title: '',
    description: '',
    category: '',
    condition: '',
    startPrice: 0,
    buyNowPrice: null,
    duration: 7
  };

  categories = ['Electronics', 'Fashion', 'Toys', 'Collectibles'];
  conditions = ['New', 'Used - Like New', 'Used - Good', 'Used - Acceptable'];
  durations = [1, 3, 5, 7, 10];

  selectedFiles: File[] = [];

  constructor(private api: ProductService, private router: Router) {}

  onFileSelected(event: any) {
    this.selectedFiles = Array.from(event.target.files);
  }

  onSubmit() {
    if (!this.item.title || !this.item.description || !this.item.category || !this.item.condition || !this.item.startPrice) {
      alert('Please fill in all required fields.');
      return;
    }

   this.api.createProduct(this.item).subscribe({
     next: (product) => {
       console.log('Product created:', product);
       if (this.selectedFiles.length > 0) {
         this.uploadImage(product.productId);
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

        // ✅ Redirect after successful upload
        this.router.navigate(['/my-ebay']);
      },
      error: (err: any) => {
        console.error('Error uploading images:', err);
      }
    });
  }

}
