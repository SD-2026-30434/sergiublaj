import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MenubarModule } from 'primeng/menubar';
import { ButtonModule } from 'primeng/button';
import { MenuItem } from 'primeng/api';
import { catchError, EMPTY, finalize, tap } from 'rxjs';
import { AuthService } from '../../../feature/auth/services/auth.service';
import { User } from '../../../feature/auth/models/user.model';
import { UserService } from '../../../feature/auth/services/user.service';
import { AppRoutes } from '../../../core/models/app-routes.enum';
import { Role } from '../../../core/models/role.enum';
import { ToastService } from '../../../core/services/toast.service';
import { ADMIN_MENU_ITEMS, CHEF_MENU_ITEMS } from './navbar.component.data';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [MenubarModule, ButtonModule],
  templateUrl: './navbar.component.html'
})
export class NavbarComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly toast = inject(ToastService);
  private readonly router = inject(Router);
  private readonly userService = inject(UserService);
  private readonly destroyRef = inject(DestroyRef);

  loggedUser: User | null = null;
  menuItems: MenuItem[] = [];

  ngOnInit(): void {
    this.userService.userChanges$.pipe(
      tap(user => {
        this.loggedUser = user;
        this.menuItems = this.getMenuItems();
      }),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe();
  }

  goHome(): void {
    this.router.navigate([`/${AppRoutes.DASHBOARD}`]).then();
  }

  onLogout(): void {
    this.auth.logout().pipe(
      catchError(() => EMPTY),
      finalize(() => {
        this.userService.setCurrentUser(null);
        this.toast.showSuccess('Signed out');
        this.router.navigate([`/${AppRoutes.LOGIN}`]).then();
      })
    ).subscribe();
  }

  private getMenuItems(): MenuItem[] {
    if (!this.loggedUser) {
      return [];
    }

    return this.loggedUser.role === Role.ADMIN
      ? ADMIN_MENU_ITEMS
      : [
        ...CHEF_MENU_ITEMS,
        { label: 'My Profile', icon: 'pi pi-user', routerLink: `/${AppRoutes.CHEFS}/${this.loggedUser.chefId}` }
      ];
  }
}
