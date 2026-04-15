import { Component, computed, inject } from '@angular/core';
import { Router } from '@angular/router';
import { MenubarModule } from 'primeng/menubar';
import { ButtonModule } from 'primeng/button';
import { MenuItem } from 'primeng/api';
import { AuthService } from '../../../feature/auth/services/auth.service';
import { UserService } from '../../../feature/auth/services/user.service';
import { AppRoutes } from '../../../core/models/app-routes.enum';
import { Role } from '../../../core/models/role.enum';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [MenubarModule, ButtonModule],
  templateUrl: './navbar.component.html'
})
export class NavbarComponent {
  private readonly router = inject(Router);
  protected readonly userService = inject(UserService);
  readonly menuItems = computed<MenuItem[]>(() => {
    const user = this.userService.user();
    if (!user) {
      return [];
    }

    if (user.role === Role.ADMIN) {
      return [
        { label: 'Dashboard', icon: 'pi pi-home', routerLink: `/${AppRoutes.DASHBOARD}` },
        { label: 'Chefs', icon: 'pi pi-users', routerLink: `/${AppRoutes.CHEFS}` },
        { label: 'Orders', icon: 'pi pi-shopping-cart', routerLink: `/${AppRoutes.ORDERS}` }
      ];
    }

    return [
      { label: 'Dashboard', icon: 'pi pi-home', routerLink: `/${AppRoutes.DASHBOARD}` },
      { label: 'My Profile', icon: 'pi pi-user', routerLink: `/${ AppRoutes.CHEFS }/${ user.chefId }` }
    ];
  });
  private readonly auth = inject(AuthService);
  private readonly toast = inject(ToastService);

  goHome(): void {
    this.router.navigate([`/${AppRoutes.DASHBOARD}`]).then();
  }

  onLogout(): void {
    this.auth.logout().subscribe({
      next: () => {
        this.userService.setCurrentUser(null);
        this.toast.showSuccess('Signed out');
        this.router.navigate([`/${ AppRoutes.LOGIN }`]).then();
      },
      error: () => {
        this.userService.setCurrentUser(null);
        this.toast.showSuccess('Signed out');
        this.router.navigate([`/${ AppRoutes.LOGIN }`]).then();
      }
    });
  }
}
