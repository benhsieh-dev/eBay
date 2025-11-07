import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Api } from './api';

interface User {
  userId: number;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  userType: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  private isLoggedInSubject = new BehaviorSubject<boolean>(false);

  public currentUser$ = this.currentUserSubject.asObservable();
  public isLoggedIn$ = this.isLoggedInSubject.asObservable();

  constructor(private api: Api) {
    // Check if user is already logged in on service initialization
    this.checkAuthStatus();
  }

  private checkAuthStatus() {
    this.api.getCurrentUser().subscribe({
      next: (response) => {
        if (response.success && response.authenticated) {
          this.setCurrentUser(response.user);
        } else {
          this.setCurrentUser(null);
        }
      },
      error: () => {
        this.setCurrentUser(null);
      }
    });
  }

  setCurrentUser(user: User | null) {
    this.currentUserSubject.next(user);
    this.isLoggedInSubject.next(!!user);
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  isLoggedIn(): boolean {
    return this.isLoggedInSubject.value;
  }

  logout() {
    this.api.logout().subscribe({
      next: () => {
        this.setCurrentUser(null);
      },
      error: () => {
        // Even if logout fails, clear local state
        this.setCurrentUser(null);
      }
    });
  }
}