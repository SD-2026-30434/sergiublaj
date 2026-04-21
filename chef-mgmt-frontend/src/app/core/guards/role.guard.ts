import { inject } from '@angular/core';
import { CanMatchFn, Router } from '@angular/router';
import { UserService } from '../../feature/auth/services/user.service';
import { AppRoutes } from '../models/app-routes.enum';
import { Role } from '../models/role.enum';

export const roleGuard: CanMatchFn = route => {
  const userService = inject(UserService);
  const router = inject(Router);

  const allowedRoles: Role[] = route.data?.['roles'] ?? [];
  const role = userService.userRole;

  if (!role) {
    // Case 1: no role resolved — user isn't loaded yet or the session lacks a role claim; bounce to dashboard.
    router.navigate([`/${ AppRoutes.DASHBOARD }`]).then();
    return false;
  }

  if (!allowedRoles.includes(role)) {
    // Case 2: role exists but isn't in the route's allowed list — authenticated but unauthorized; bounce to dashboard.
    router.navigate([`/${ AppRoutes.DASHBOARD }`]).then();
    return false;
  }

  // Case 3: role is present and permitted — allow the match.
  return true;
};
