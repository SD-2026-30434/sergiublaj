import { inject } from '@angular/core';
import { CanMatchFn, Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { UserService } from '../../feature/auth/services/user.service';
import { AppRoutes } from '../models/app-routes.enum';

export const authGuard: CanMatchFn = () => {
  const userService = inject(UserService);
  const router = inject(Router);

  if (userService.user) {
    // Case 1: user already cached in memory — skip the network call and allow the route.
    return true;
  }

  return userService.loadCurrentUser().pipe(
    map(isAuthenticated => {
      if (isAuthenticated) {
        // Case 2: no cached user, but the session cookie is still valid — backend rehydrates the user, allow.
        return true;
      }
      // Case 3: no cached user and the session is gone — redirect to login via UrlTree.
      return router.createUrlTree([`/${AppRoutes.LOGIN}`]);
    })
  );
};
