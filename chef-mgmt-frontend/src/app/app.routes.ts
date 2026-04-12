import { Routes } from '@angular/router';
import { AppRoutes } from './core/models/app-routes.enum';

export const routes: Routes = [
  {
    path: AppRoutes.DASHBOARD,
    loadChildren: () => import('./feature/dashboard/dashboard.routes').then(m => m.dashboardRoutes)
  },
  {
    path: AppRoutes.CHEFS,
    loadChildren: () => import('./feature/chefs/chefs.routes').then(m => m.chefsRoutes)
  },
  {
    path: AppRoutes.ORDERS,
    loadChildren: () => import('./feature/orders/orders.routes').then(m => m.ordersRoutes)
  },
  {
    path: AppRoutes.ANY,
    redirectTo: AppRoutes.DASHBOARD
  }
];
