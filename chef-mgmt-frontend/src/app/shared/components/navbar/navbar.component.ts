import { Component, inject } from '@angular/core';

import { Router } from '@angular/router';
import { MenubarModule } from 'primeng/menubar';
import { MenuItem } from 'primeng/api';
import { AppRoutes } from '../../../core/models/app-routes.enum';
import { NAVBAR_MENU_ITEMS } from './navbar.component.data';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [MenubarModule],
  templateUrl: './navbar.component.html'
})
export class NavbarComponent {
  private readonly router = inject(Router);

  menuItems: MenuItem[] = NAVBAR_MENU_ITEMS;

  goHome(): void {
    this.router.navigate([AppRoutes.DASHBOARD]).then();
  }
}
