import { Routes } from '@angular/router';
import { AppRoutes } from '../../core/models/app-routes.enum';

export const chefsRoutes: Routes = [
  {
    path: AppRoutes.EMPTY,
    loadComponent: () => import('./components/chef-list/chef-list.component').then(m => m.ChefListComponent)
  },
  {
    path: AppRoutes.ID,
    loadComponent: () => import('./components/chef-detail/chef-detail.component').then(m => m.ChefDetailComponent)
  },
  {
    path: AppRoutes.ANY,
    redirectTo: AppRoutes.EMPTY
  }
];
