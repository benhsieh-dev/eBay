import { Routes } from '@angular/router';
import { MyEbay } from './pages/my-ebay/my-ebay';
import { Home } from './pages/home/home';
import { Login } from './pages/login/login';

export const routes: Routes = [
  { path: 'home', component: Home },
  { path: 'my-ebay', component: MyEbay },
  { path: 'login', component: Login },
  { path: 'angular/my-ebay', component: MyEbay },
  { path: 'angular/home', component: Home },
  { path: 'angular/login', component: Login },
  { path: '', redirectTo: '/home', pathMatch: 'full' }
];
