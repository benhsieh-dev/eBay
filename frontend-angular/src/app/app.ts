import { Component, signal, OnInit } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import {CommonModule} from '@angular/common';
import { UserService } from './services/user.service';
import { Observable } from 'rxjs';
import {Search} from './components/search/search';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, CommonModule, Search],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('frontend-angular');

  isLoggedIn$: Observable<boolean>;
  currentUser$: Observable<any>;

  constructor(private userService: UserService) {
    this.isLoggedIn$ = this.userService.isLoggedIn$;
    this.currentUser$ = this.userService.currentUser$;
  }

  ngOnInit() {
    // User service automatically checks auth status on initialization
  }

  logout() {
    this.userService.logout();
  }
}
