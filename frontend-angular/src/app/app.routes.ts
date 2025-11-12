import { Routes } from '@angular/router';
import { MyEbay } from './pages/my-ebay/my-ebay';
import { Home } from './pages/home/home';
import { Products} from './pages/products/products';
import { Login } from './pages/login/login';
import { Sell } from './pages/sell/sell';
import {ProductDetail} from './pages/product-detail/product-detail';

export const routes: Routes = [
  { path: 'home', component: Home },
  { path: 'sell', component: Sell },
  { path: 'my-ebay', component: MyEbay },
  { path: 'login', component: Login },
  { path: 'angular/my-ebay', component: MyEbay },
  { path: 'angular/home', component: Home },
  { path: 'products', component: Products },
  { path: 'products/:id', component: ProductDetail },
  { path: 'angular/login', component: Login },
  { path: '', redirectTo: '/home', pathMatch: 'full' }
];
