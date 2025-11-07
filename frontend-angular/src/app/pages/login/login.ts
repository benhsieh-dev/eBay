import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Api } from '../../services/api';
import { UserService } from '../../services/user.service';

interface LoginFormData {
  usernameOrEmail: string;
  password: string;
}

@Component({
  selector: 'app-login',
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  formData: LoginFormData = {
    usernameOrEmail: '',
    password: ''
  };
  
  error = '';
  loading = false;

  constructor(
    private api: Api,
    private router: Router,
    private userService: UserService
  ) {}

  onSubmit() {
    this.loading = true;
    this.error = '';

    this.api.login({
      usernameOrEmail: this.formData.usernameOrEmail,
      password: this.formData.password
    }).subscribe({
      next: (response) => {
        if (response.success) {
          // Update user service with logged-in user
          this.userService.setCurrentUser(response.user);
          console.log('Login successful:', response);
          this.router.navigate(['/home']);
        } else {
          this.error = response.error || 'Login failed';
        }
        this.loading = false;
      },
      error: (err) => {
        console.error('Login error:', err);
        this.error = err.error?.error || err.error?.message || 'Login failed. Please try again.';
        this.loading = false;
      }
    });
  }

  handleDemoLogin(username: string) {
    this.formData = {
      usernameOrEmail: username,
      password: 'demo123'
    };
    this.error = '';
  }
}