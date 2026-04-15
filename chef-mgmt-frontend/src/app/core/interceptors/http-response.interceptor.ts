import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { ToastService } from '../services/toast.service';
import { AppRoutes } from '../models/app-routes.enum';
import { API_CONFIG } from '../config/api.config';
import { USERS_ME_PATH, UserService } from '../../feature/auth/services/user.service';

const AUTH_LOGIN_PATH = `${ API_CONFIG.AUTH_URL }/v1/login`;
const AUTH_LOGOUT_PATH = `${ API_CONFIG.AUTH_URL }/v1/logout`;

export const httpResponseInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const userService = inject(UserService);
  const toastService = inject(ToastService);

  return next(req).pipe(
    catchError(error => {
      const isAuthEndpoint =
        req.url.includes(AUTH_LOGIN_PATH) || req.url.includes(AUTH_LOGOUT_PATH);
      const isSessionCheckEndpoint = req.url.includes(USERS_ME_PATH);
      const isAuthRelatedEndpoint = isAuthEndpoint || isSessionCheckEndpoint;

      if (error.status === 401) {
        if (!isAuthEndpoint) {
          userService.setCurrentUser(null);
          if (router.url !== `/${ AppRoutes.LOGIN }`) {
            router.navigate([`/${ AppRoutes.LOGIN }`]).then();
          }
        }
      } else if (error.status === 403) {
        if (!isSessionCheckEndpoint) {
          toastService.showError('Access denied.');
          router.navigate([`/${ AppRoutes.DASHBOARD }`]).then();
        }
      } else if (!isAuthRelatedEndpoint && error.error?.message) {
        toastService.showError(error.error.message);
      }
      return throwError(() => error);
    })
  );
};
