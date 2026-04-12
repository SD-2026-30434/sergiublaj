import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Store } from '@ngxs/store';
import { map, take } from 'rxjs/operators';
import { AuthState } from '../../feature/auth/store/auth.state';
import { AppRoutes } from '../models/app-routes.enum';
import { Role } from '../models/role.enum';

export const roleGuard: CanActivateFn = route => {
  const store = inject(Store);
  const router = inject(Router);

  const allowedRoles: Role[] = route.data?.['roles'] ?? [];

  return store.select(AuthState.userRole).pipe(
    take(1),
    map(userRole => {
      if (!userRole || !allowedRoles.includes(userRole)) {
        router.navigate([`/${AppRoutes.DASHBOARD}`]).then();
        return false;
      }
      return true;
    })
  );
};
