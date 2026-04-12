import { Routes } from '@angular/router';
import { AppRoutes } from './core/models/app-routes.enum';
import { Role } from './core/models/role.enum';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  {
    path: AppRoutes.LOGIN,
    loadChildren: () => import('./feature/auth/auth.routes').then(m => m.authRoutes)
  },
  {
    path: AppRoutes.DASHBOARD,
    loadChildren: () => import('./feature/dashboard/dashboard.routes').then(m => m.dashboardRoutes),
    canActivate: [authGuard]
  },
  {
    path: AppRoutes.CHEFS,
    loadChildren: () => import('./feature/chefs/chefs.routes').then(m => m.chefsRoutes),
    canActivate: [authGuard]
  },
  {
    path: AppRoutes.ORDERS,
    loadChildren: () => import('./feature/orders/orders.routes').then(m => m.ordersRoutes),
    canActivate: [authGuard, roleGuard],
    data: {
      roles: [Role.ADMIN]
    }
  },
  {
    path: AppRoutes.ANY,
    redirectTo: AppRoutes.DASHBOARD
  }
];
