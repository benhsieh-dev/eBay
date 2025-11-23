import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BidService {
  private baseUrl = 'http://localhost:8080/api/bids';

  constructor(private http: HttpClient) {}

  placeBid(productId: number, bidAmount: number): Observable<any> {
    return this.http.post(`${this.baseUrl}/place`, {
      productId: productId,
      amount: bidAmount
    }, { withCredentials: true });
  }

  getBidHistory(productId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/history/${productId}`, { withCredentials: true });
  }
}