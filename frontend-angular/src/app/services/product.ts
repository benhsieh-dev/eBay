import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  // how not to hardcode baseUrl
  private baseUrl = 'http://localhost:8080/api/products';

  constructor(private http: HttpClient) {}

  searchProducts(query: string, page = 0, size = 20) {
    return this.http.get<any>(`${this.baseUrl}/search`, {
      params: {query, page, size}
    });
  }
}
