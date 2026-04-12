import { inject, Injectable } from '@angular/core';
import { State, Action, StateContext, Selector } from '@ngxs/store';
import { tap, catchError, switchMap } from 'rxjs/operators';
import { of } from 'rxjs';
import { Router } from '@angular/router';
import { User } from '../models/user.model';
import { AppRoutes } from '../../../core/models/app-routes.enum';
import { Role } from '../../../core/models/role.enum';
import { AuthService } from '../services/auth.service';
import { Login, LoginSuccess, Logout, CheckSession } from './auth.actions';

export interface AuthStateModel {
  user: User | null;
  isAuthenticated: boolean;
  loading: boolean;
  error: string | null;
  sessionChecked: boolean;
}

@State<AuthStateModel>({
  name: 'auth',
  defaults: {
    user: null,
    isAuthenticated: false,
    loading: false,
    error: null,
    sessionChecked: false
  }
})
@Injectable()
export class AuthState {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  @Selector()
  static user(state: AuthStateModel): User | null {
    return state.user;
  }

  @Selector()
  static isAuthenticated(state: AuthStateModel): boolean {
    return state.isAuthenticated;
  }

  @Selector()
  static userRole(state: AuthStateModel): Role | null {
    return state.user?.role ?? null;
  }

  @Selector()
  static chefId(state: AuthStateModel): string | null {
    return state.user?.chefId ?? null;
  }

  @Selector()
  static loading(state: AuthStateModel): boolean {
    return state.loading;
  }

  @Selector()
  static error(state: AuthStateModel): string | null {
    return state.error;
  }

  @Selector()
  static sessionChecked(state: AuthStateModel): boolean {
    return state.sessionChecked;
  }

  @Action(Login)
  login({ patchState, dispatch }: StateContext<AuthStateModel>, action: Login) {
    patchState({ loading: true, error: null });
    return this.authService.login(action.email, action.password).pipe(
      switchMap(() => {
        dispatch(new LoginSuccess());
        return this.authService.me();
      }),
      tap(user => {
        patchState({ user, isAuthenticated: true, loading: false, sessionChecked: true });
        if (user.role === Role.CHEF) {
          this.router.navigate([`/${AppRoutes.CHEFS}`, user.chefId]).then();
        } else {
          this.router.navigate([`/${AppRoutes.DASHBOARD}`]).then();
        }
      }),
      catchError(err => {
        const message = err.error?.message || 'Login failed';
        patchState({ loading: false, error: message });
        return of(null);
      })
    );
  }

  @Action(Logout)
  logout({ setState }: StateContext<AuthStateModel>) {
    const clearAndRedirect = () => {
      setState({ user: null, isAuthenticated: false, loading: false, error: null, sessionChecked: true });
      this.router.navigate([`/${AppRoutes.LOGIN}`]).then();
    };

    return this.authService.logout().pipe(
      tap(() => clearAndRedirect()),
      catchError(() => {
        clearAndRedirect();
        return of(null);
      })
    );
  }

  @Action(CheckSession)
  checkSession({ patchState }: StateContext<AuthStateModel>) {
    patchState({ loading: true });
    return this.authService.me().pipe(
      tap(user => patchState({ user, isAuthenticated: true, loading: false, sessionChecked: true })),
      catchError(() => {
        patchState({ user: null, isAuthenticated: false, loading: false, sessionChecked: true });
        return of(null);
      })
    );
  }
}
