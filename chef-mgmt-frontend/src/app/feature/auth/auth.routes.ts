import { Routes } from '@angular/router';
import { AppRoutes } from '../../core/models/app-routes.enum';

export const authRoutes: Routes = [
  {
    path: AppRoutes.EMPTY,
    loadComponent: () => import('./components/login/login.component').then(m => m.LoginComponent)
  }
];
