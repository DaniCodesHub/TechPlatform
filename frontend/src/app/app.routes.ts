import { Routes } from '@angular/router';
import { Login } from './features/login/login';
import { Products } from './features/products/products';
import { Orders } from './features/orders/orders';
import { Users } from './features/users/users';
import { authGuard } from './core/guards/auth-guard';

export const routes: Routes = [
  {
    path: '',
    component: Login
  },
  {
    path: 'products',
    component: Products,
    canActivate: [authGuard]
  },
  {
    path: 'orders',
    component: Orders,
    canActivate: [authGuard]
  },
  {
    path: 'users',
    component: Users,
    canActivate: [authGuard]
  }
];