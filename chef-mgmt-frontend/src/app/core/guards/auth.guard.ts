import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Store } from '@ngxs/store';
import { AuthState } from '../../feature/auth/store/auth.state';
import { CheckSession } from '../../feature/auth/store/auth.actions';
import { AppRoutes } from '../models/app-routes.enum';
import { filter, map, switchMap, take, tap } from 'rxjs/operators';

export const authGuard: CanActivateFn = () => {
  const store = inject(Store);
  const router = inject(Router);

  return store.select(AuthState.sessionChecked).pipe(
    tap(checked => !checked && store.dispatch(new CheckSession())),
    filter(checked => checked),
    take(1),
    switchMap(() => store.select(AuthState.isAuthenticated).pipe(take(1))),
    map(isAuthenticated => {
      if (!isAuthenticated) {
        router.navigate([`/${AppRoutes.LOGIN}`]).then();
        return false;
      }
      return true;
    })
  );
};
