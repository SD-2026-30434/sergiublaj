import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_CONFIG } from '../../../core/config/api.config';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  login(email: string, password: string): Observable<void> {
    return this.http.post<void>(`${ API_CONFIG.AUTH_URL }/v1/login`, { email, password });
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${ API_CONFIG.AUTH_URL }/v1/logout`, {});
  }
}
