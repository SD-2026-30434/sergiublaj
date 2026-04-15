import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { User } from '../models/user.model';

export const USERS_ME_PATH = '/users/v1/me';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);

  private readonly _user = signal<User | null>(null);
  readonly user = this._user.asReadonly();
  readonly userRole = computed(() => this._user()?.role ?? null);

  setCurrentUser(user: User | null): void {
    this._user.set(user);
  }

  getMe(): Observable<User> {
    return this.http.get<User>(USERS_ME_PATH);
  }

  loadCurrentUser(): Observable<boolean> {
    return this.getMe().pipe(
      tap(u => this.setCurrentUser(u)),
      map(() => true),
      catchError(() => {
        this.setCurrentUser(null);
        return of(false);
      })
    );
  }
}
