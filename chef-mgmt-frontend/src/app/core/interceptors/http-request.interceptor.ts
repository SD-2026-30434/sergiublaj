import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export const httpRequestInterceptor: HttpInterceptorFn = (req, next) => {
  const modifiedReq = req.clone({
    url: `${environment.apiUrl}${req.url}`,
    withCredentials: true
  });

  return next(modifiedReq);
};
