import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { User } from '../models/user.model';
import { Role } from '../../../core/models/role.enum';
import { API_CONFIG } from '../../../core/config/api.config';

@Injectable({ providedIn: 'root' })
export class UserService {
  private readonly http = inject(HttpClient);

  private readonly user$ = new BehaviorSubject<User | null>(null);
  readonly userChanges$ = this.user$.asObservable();

  get user(): User | null {
    return this.user$.value;
  }

  get userRole(): Role | null {
    return this.user?.role ?? null;
  }

  setCurrentUser(user: User | null): void {
    this.user$.next(user);
  }

  getMe(): Observable<User> {
    return this.http.get<User>(`${API_CONFIG.USERS_URL}/me`);
  }

  loadCurrentUser(): Observable<boolean> {
    return this.getMe().pipe(
      tap(user => this.setCurrentUser(user)),
      map(() => true),
      catchError(() => {
        this.setCurrentUser(null);
        return of(false);
      })
    );
  }
}
