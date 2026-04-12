import { Component, inject } from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { Router } from '@angular/router';
import { Store } from '@ngxs/store';
import { Observable } from 'rxjs';
import { MenubarModule } from 'primeng/menubar';
import { ButtonModule } from 'primeng/button';
import { MenuItem } from 'primeng/api';
import { AuthState } from '../../../feature/auth/store/auth.state';
import { Logout } from '../../../feature/auth/store/auth.actions';
import { User } from '../../../feature/auth/models/user.model';
import { AppRoutes } from '../../../core/models/app-routes.enum';
import { Role } from '../../../core/models/role.enum';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [AsyncPipe, MenubarModule, ButtonModule],
  templateUrl: './navbar.component.html'
})
export class NavbarComponent {
  private readonly router = inject(Router);
  private readonly store = inject(Store);

  user$: Observable<User | null> = this.store.select(AuthState.user);

  get menuItems(): MenuItem[] {
    const user = this.store.selectSnapshot(AuthState.user);
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
      { label: 'My Profile', icon: 'pi pi-user', routerLink: `/${AppRoutes.CHEFS}/${user.chefId}` },
    ];
  }

  goHome(): void {
    this.router.navigate([`/${AppRoutes.DASHBOARD}`]).then();
  }

  onLogout(): void {
    this.store.dispatch(new Logout());
  }
}
