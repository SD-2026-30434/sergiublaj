import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { UserService } from '../../feature/auth/services/user.service';
import { AppRoutes } from '../models/app-routes.enum';
import { Role } from '../models/role.enum';

export const roleGuard: CanActivateFn = route => {
  const userService = inject(UserService);
  const router = inject(Router);

  const allowedRoles: Role[] = route.data?.['roles'] ?? [];
  const role = userService.userRole;

  if (!role || !allowedRoles.includes(role)) {
    router.navigate([`/${ AppRoutes.DASHBOARD }`]).then();
    return false;
  }

  return true;
};
