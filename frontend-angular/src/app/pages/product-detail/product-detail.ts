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
  
  // Owner and image upload properties
  isOwner = false;
  showImageUpload = false;
  selectedFiles: File[] = [];
  isUploading = false;
  uploadError = '';
  isDragOver = false;

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
        console.log('Product loaded:', data); // Debug log
        
        // Handle nested response structure
        const productData = data.product || data;
        console.log('Actual product data:', productData); // Debug the actual product
        console.log('Product images:', productData.images); // Debug log
        console.log('Product imageUrls:', productData.imageUrls); // Check alternative field name
        console.log('Product imageUrl:', productData.imageUrl); // Check singular field name
        
        this.product = productData;
        this.bidHistory = productData.bids || productData.bidHistory || [];
        this.checkIfOwner();
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
    const fullUrl = this.productService.getFullImageUrl(imageUrl);
    console.log('Image URL conversion:', imageUrl, '->', fullUrl); // Debug log
    return fullUrl;
  }

  checkIfOwner() {
    // Show upload option only if product has no images or very few images
    // In a real app, you'd also check if the current user's ID matches the product's sellerId
    const images = this.getProductImages();
    const hasLimitedImages = !images || images.length === 0;
    this.isOwner = hasLimitedImages; // Show for products with no images
  }

  getProductImages(): string[] {
    if (!this.product) return [];
    
    // Try different possible field names for images
    if (this.product.images && this.product.images.length > 0) {
      return this.product.images;
    }
    if (this.product.imageUrls && this.product.imageUrls.length > 0) {
      return this.product.imageUrls;
    }
    if (this.product.imageUrl) {
      return [this.product.imageUrl];
    }
    
    return [];
  }

  // Image Upload Methods
  openImageUpload() {
    this.showImageUpload = true;
    this.selectedFiles = [];
    this.uploadError = '';
  }

  closeImageUpload() {
    this.showImageUpload = false;
    this.selectedFiles = [];
    this.uploadError = '';
    this.isUploading = false;
  }

  onFileSelected(event: any) {
    const files = Array.from(event.target.files) as File[];
    this.addFiles(files);
  }

  onDragOver(event: DragEvent) {
    event.preventDefault();
    this.isDragOver = true;
  }

  onDragLeave(event: DragEvent) {
    event.preventDefault();
    this.isDragOver = false;
  }

  onDrop(event: DragEvent) {
    event.preventDefault();
    this.isDragOver = false;
    const files = Array.from(event.dataTransfer?.files || []) as File[];
    this.addFiles(files);
  }

  addFiles(files: File[]) {
    const imageFiles = files.filter(file => file.type.startsWith('image/'));
    if (imageFiles.length !== files.length) {
      this.uploadError = 'Only image files are allowed';
    } else {
      this.uploadError = '';
    }
    this.selectedFiles = [...this.selectedFiles, ...imageFiles];
  }

  removeFile(index: number) {
    this.selectedFiles.splice(index, 1);
  }

  getFilePreview(file: File): string {
    // Create stable blob URLs to avoid change detection issues
    if (!(file as any)._blobUrl) {
      (file as any)._blobUrl = URL.createObjectURL(file);
    }
    return (file as any)._blobUrl;
  }

  uploadImages() {
    if (!this.product || this.selectedFiles.length === 0) return;

    this.isUploading = true;
    this.uploadError = '';

    const formData = new FormData();
    this.selectedFiles.forEach((file) => {
      formData.append(`images`, file);
    });

    // Try different possible product ID field names
    const productId = this.product.productId || this.product.id || 
                     this.route.snapshot.paramMap.get('id');
    
    if (!productId) {
      this.uploadError = 'Product ID not found';
      this.isUploading = false;
      return;
    }

    this.productService.uploadProductImages(Number(productId), formData).subscribe({
      next: (response) => {
        console.log('Upload response:', response); // Debug log
        console.log('Upload response keys:', Object.keys(response)); // Debug response structure
        if (response) {
          Object.keys(response).forEach(key => {
            console.log(`  upload ${key}:`, response[key]);
          });
        }
        alert('Images uploaded successfully!');
        this.closeImageUpload();
        
        // Add a small delay to ensure backend has processed the upload
        setTimeout(() => {
          console.log('Reloading product after upload...'); // Debug log
          this.loadProduct(); // Refresh product to show new images
        }, 1000);
      },
      error: (error) => {
        this.uploadError = error.error?.message || 'Failed to upload images. Please try again.';
        this.isUploading = false;
      }
    });
  }
}
