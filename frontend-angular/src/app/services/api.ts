import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

interface ApiResponse<T = any> {
  success: boolean;
  data?: T;
  error?: string;
  products?: T;
  totalCount?: number;
  watchlist?: T;
  count?: number;
  userBids?: T;
}

@Injectable({
  providedIn: 'root'
})
export class Api {
  private readonly baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // Products
  getProducts(params?: { page?: number; size?: number; category?: string }): Observable<ApiResponse> {
    let httpParams = new HttpParams();
    if (params) {
      Object.entries(params).forEach(([key, value]) => {
        if (value !== undefined) {
          httpParams = httpParams.set(key, value.toString());
        }
      });
    }
    return this.http.get<ApiResponse>(`${this.baseUrl}/products`, { 
      params: httpParams,
      withCredentials: true 
    });
  }

  getFeaturedProducts(): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.baseUrl}/products/featured`, {
      withCredentials: true
    });
  }

  getProductById(productId: number): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.baseUrl}/products/${productId}`, {
      withCredentials: true
    });
  }

  getMyListings(params: { page: number; size: number }): Observable<ApiResponse> {
    const httpParams = new HttpParams()
      .set('page', params.page.toString())
      .set('size', params.size.toString());
    
    return this.http.get<ApiResponse>(`${this.baseUrl}/products/my-listings`, { 
      params: httpParams,
      withCredentials: true 
    });
  }

  // Watchlist
  getWatchlist(): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.baseUrl}/watchlist/user`, { 
      withCredentials: true 
    });
  }

  toggleWatchlist(productId: number): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.baseUrl}/watchlist/toggle`, {
      productId: productId
    }, {
      withCredentials: true
    });
  }

  // Bids
  getMyBids(): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.baseUrl}/bids/my-bids`, { 
      withCredentials: true 
    });
  }

  placeBid(bidData: { productId: number; bidAmount: number; bidType?: string; maxProxyAmount?: number }): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.baseUrl}/bids/place`, bidData, {
      withCredentials: true
    });
  }

  buyNow(productId: number): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.baseUrl}/bids/buy-now`, {
      productId: productId
    }, {
      withCredentials: true
    });
  }

  getBidHistory(productId: number): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.baseUrl}/bids/history/${productId}`, {
      withCredentials: true
    });
  }

  // Auth
  login(credentials: { usernameOrEmail: string; password: string }): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.baseUrl}/user/login`, credentials, {
      withCredentials: true
    });
  }

  register(userData: { username: string; email: string; password: string; firstName?: string; lastName?: string }): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.baseUrl}/user/register`, userData, {
      withCredentials: true
    });
  }

  logout(): Observable<ApiResponse> {
    return this.http.post<ApiResponse>(`${this.baseUrl}/user/logout`, {}, {
      withCredentials: true
    });
  }

  getCurrentUser(): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${this.baseUrl}/user/me`, {
      withCredentials: true
    });
  }
}
