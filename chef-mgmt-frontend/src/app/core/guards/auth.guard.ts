import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { of } from 'rxjs';
import { UserService } from '../../feature/auth/services/user.service';
import { AppRoutes } from '../models/app-routes.enum';

export const authGuard: CanActivateFn = () => {
  const userService = inject(UserService);
  const router = inject(Router);

  if (userService.user()) {
    return of(true);
  }

  return userService.loadCurrentUser().pipe(
    map(ok => {
      if (!ok) {
        router.navigate([`/${AppRoutes.LOGIN}`]).then();
        return false;
      }
      return true;
    })
  );
};
