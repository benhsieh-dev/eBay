import { Component } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { ProductService } from '../../services/product';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-search',
  imports: [
    CommonModule,
    FormsModule,
    CurrencyPipe
  ],
  templateUrl: './search.html',
  styleUrl: './search.css',
})
export class Search {

  searchTerm = '';

  results: any[] = [];
  totalCount = 0;

  constructor(private productService: ProductService) {}

  onSearch() {
    if (!this.searchTerm) return;

    this.productService.searchProducts(this.searchTerm).subscribe(res => {
      if (res.success) {
        this.results = res.products;
        this.totalCount = res.totalCount;
      }
    });
  }

}
