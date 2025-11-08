import { RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule} from '@angular/common';
import {UserService} from '../../services/user.service';
import {Component} from '@angular/core';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-navbar',
  imports: [RouterLink, RouterLinkActive, CommonModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class NavbarComponent {
  isLoggedIn$: Observable<boolean>;
  currentUser$: Observable<any>;

  constructor(private userService: UserService) {
    this.isLoggedIn$ = this.userService.isLoggedIn$;
    this.currentUser$ = this.userService.currentUser$;
  }

  logout() {
    this.userService.logout();
  }
}
