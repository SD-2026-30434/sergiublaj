import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { UserService } from '../../feature/auth/services/user.service';
import { AppRoutes } from '../models/app-routes.enum';

export const authGuard: CanActivateFn = () => {
  const userService = inject(UserService);
  const router = inject(Router);

  if (userService.user) {
    return true;
  }

  return userService.loadCurrentUser().pipe(
    map(isAuthenticated => isAuthenticated || router.createUrlTree([`/${AppRoutes.LOGIN}`]))
  );
};
