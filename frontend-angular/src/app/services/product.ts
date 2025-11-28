import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private baseUrl = '/api/products';
  private backendBaseUrl = '';

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

  // getProductById(productId: number): Observable<any> {
  //   return this.http.get<any>(`${this.baseUrl}/${productId}`, {withCredentials: true});
  // }

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

  getFullImageUrl(relativeUrl: string): string {
    if (relativeUrl && relativeUrl.startsWith('/api/')) {
      return this.backendBaseUrl + relativeUrl;
    }
    return relativeUrl;
  }

  getProductById(id: string) {
    return this.http.get(`${this.baseUrl}/${id}`, {withCredentials: true});
  }

  buyProduct(productId: number) {
    return this.http.post(`${this.baseUrl}/${productId}/buy`, {}, {withCredentials: true});
  }

  placeBid(productId: number, bidAmount: number) {
    return this.http.post('/api/bids/place', {
      productId: productId,
      amount: bidAmount
    }, { withCredentials: true });
  }
}
