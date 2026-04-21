import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { ToastService } from '../services/toast.service';
import { AppRoutes } from '../models/app-routes.enum';
import { UserService } from '../../feature/auth/services/user.service';

// Auth/user endpoints handle their own errors (e.g. login showing "wrong password" inline),
// so we skip the global handling here to avoid double-toasting or redirect loops on /login itself.
const IGNORED_PATHS = ['/auth/', '/users/'];

export const httpResponseInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const userService = inject(UserService);
  const toastService = inject(ToastService);

  return next(req).pipe(
    catchError(error => {
      const isIgnored = IGNORED_PATHS.some(path => req.url.includes(path));

      if (error.status === 401 && !isIgnored) {
        // Case 1: 401 Unauthorized — session expired or never existed. Clear the cached user
        // so guards stop trusting stale state, and kick the user to the login screen.
        userService.setCurrentUser(null);
        router.navigate([`/${AppRoutes.LOGIN}`]).then();
      } else if (error.status === 403 && !isIgnored) {
        // Case 2: 403 Forbidden — authenticated but not allowed. Surface a toast and send
        // them back to the dashboard instead of leaving them on a page they can't use.
        toastService.showError('Access denied.');
        router.navigate([`/${AppRoutes.DASHBOARD}`]).then();
      } else if (!isIgnored && error.error?.message) {
        // Case 3: any other error that carries a backend message — show it as a toast so
        // users get actionable feedback (validation errors, conflicts, etc.) instead of silence.
        toastService.showError(error.error.message);
      }

      // Always rethrow: individual callers may still want to react (e.g. stop a spinner,
      // keep form state) — the interceptor handles cross-cutting UX, not flow control.
      return throwError(() => error);
    })
  );
};
