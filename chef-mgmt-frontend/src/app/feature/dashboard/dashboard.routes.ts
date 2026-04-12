import { Routes } from '@angular/router';
import { AppRoutes } from '../../core/models/app-routes.enum';

export const dashboardRoutes: Routes = [
  {
    path: AppRoutes.EMPTY,
    loadComponent: () => import('./components/dashboard.component').then(m => m.DashboardComponent)
  },
  {
    path: AppRoutes.ANY,
    redirectTo: AppRoutes.EMPTY
  }
];
