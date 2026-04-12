import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngxs/store';
import { catchError, throwError } from 'rxjs';
import { ToastService } from '../services/toast.service';
import { AppRoutes } from '../models/app-routes.enum';
import { Logout } from '../../feature/auth/store/auth.actions';
import { AUTH_ENDPOINTS } from '../../feature/auth/services/auth.service';

export const httpResponseInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const store = inject(Store);
  const toastService = inject(ToastService);

  return next(req).pipe(
    catchError(error => {
      const isAuthEndpoint = req.url.includes(AUTH_ENDPOINTS.LOGIN) || req.url.includes(AUTH_ENDPOINTS.LOGOUT);
      if (error.status === 401 && !isAuthEndpoint) {
        store.dispatch(new Logout());
      } else if (error.status === 403) {
        toastService.showError('Access denied.');
        router.navigate([`/${AppRoutes.DASHBOARD}`]).then();
      } else if (error.error?.message) {
        toastService.showError(error.error.message);
      }
      return throwError(() => error);
    })
  );
};
