import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { ToastService } from '../services/toast.service';
import { AppRoutes } from '../models/app-routes.enum';
import { UserService } from '../../feature/auth/services/user.service';

const IGNORED_PATHS = ['/auth/', '/users/'];

export const httpResponseInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const userService = inject(UserService);
  const toastService = inject(ToastService);

  return next(req).pipe(
    catchError(error => {
      const isIgnored = IGNORED_PATHS.some(path => req.url.includes(path));

      if (error.status === 401 && !isIgnored) {
        userService.setCurrentUser(null);
        router.navigate([`/${AppRoutes.LOGIN}`]).then();
      } else if (error.status === 403 && !isIgnored) {
        toastService.showError('Access denied.');
        router.navigate([`/${AppRoutes.DASHBOARD}`]).then();
      } else if (!isIgnored && error.error?.message) {
        toastService.showError(error.error.message);
      }

      return throwError(() => error);
    })
  );
};
