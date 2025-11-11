import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private baseUrl = 'http://localhost:8080/api/products';

  constructor(private http: HttpClient) {}

  searchProducts(query: string, page = 0, size = 20) {
    return this.http.get<any>(`${this.baseUrl}/search`, {
      params: {query, page, size}
    });
  }

  getMyListings(page: number = 0, size: number = 20) {
    return this.http.get<any>(`${this.baseUrl}/my-listings?page=${page}&size=${size}`,
      {withCredentials: true});
  }

  getProductById(productId: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/${productId}`, {withCredentials: true});
  }

  createProduct(product: any) {
    return this.http.post<any>(`${this.baseUrl}`, product, {withCredentials: true});
  }

  updateProduct(productId: number, formData: FormData): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/${productId}`, formData, {
      withCredentials: true
    })
  }

  uploadProductImages(productId: number, formData: FormData){
    return this.http.post<any>(
      `${this.baseUrl}/${productId}/images`, formData, {
      withCredentials: true
    });
  }

  getCategories(): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/categories`);
  }

}
