import { MenuItem } from 'primeng/api';
import { AppRoutes } from '../../../core/models/app-routes.enum';

export const NAVBAR_MENU_ITEMS: MenuItem[] = [
  {
    label: 'Dashboard',
    icon: 'pi pi-home',
    routerLink: AppRoutes.DASHBOARD
  },
  {
    label: 'Chefs',
    icon: 'pi pi-users',
    routerLink: AppRoutes.CHEFS
  },
  {
    label: 'Orders',
    icon: 'pi pi-shopping-cart',
    routerLink: AppRoutes.ORDERS
  }
];
