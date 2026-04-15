import { HttpInterceptorFn } from '@angular/common/http';
import { API_CONFIG } from '../config/api.config';

export const httpRequestInterceptor: HttpInterceptorFn = (req, next) => {
  const modifiedReq = req.clone({
    url: `${ API_CONFIG.baseUrl }${ req.url }`,
    withCredentials: true
  });

  return next(modifiedReq);
};
