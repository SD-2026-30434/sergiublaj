import { Routes } from '@angular/router';
import { AppRoutes } from '../../core/models/app-routes.enum';

export const ordersRoutes: Routes = [
  {
    path: AppRoutes.EMPTY,
    loadComponent: () => import('./components/order-list/order-list.component').then(m => m.OrderListComponent)
  },
  {
    path: AppRoutes.ANY,
    redirectTo: AppRoutes.DASHBOARD
  }
];
