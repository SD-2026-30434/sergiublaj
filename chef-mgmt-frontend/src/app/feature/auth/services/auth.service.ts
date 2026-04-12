import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';

export const AUTH_ENDPOINTS = {
  LOGIN: '/auth/v1/login',
  LOGOUT: '/auth/v1/logout',
  ME: '/users/v1/me'
};

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  login(email: string, password: string): Observable<void> {
    return this.http.post<void>(AUTH_ENDPOINTS.LOGIN, { email, password });
  }

  logout(): Observable<void> {
    return this.http.post<void>(AUTH_ENDPOINTS.LOGOUT, {});
  }

  me(): Observable<User> {
    return this.http.get<User>(AUTH_ENDPOINTS.ME);
  }
}
