import { HttpInterceptorFn } from '@angular/common/http';
import { API_CONFIG } from '../config/api.config';

export const httpRequestInterceptor: HttpInterceptorFn = (req, next) => {
  // Rewrite every outgoing request so services can pass relative paths (e.g. '/chefs')
  // and stay oblivious to the backend host — the BASE_URL is injected here in one place.
  // `withCredentials: true` is required so the session cookie rides along on cross-origin
  // calls; without it the backend sees anonymous requests and every guarded endpoint 401s.
  const modifiedReq = req.clone({
    url: `${API_CONFIG.BASE_URL}${req.url}`,
    withCredentials: true
  });

  return next(modifiedReq);
};
