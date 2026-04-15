import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { catchError, finalize, switchMap, tap } from 'rxjs/operators';
import { of } from 'rxjs';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { AppRoutes } from '../../../../core/models/app-routes.enum';
import { Role } from '../../../../core/models/role.enum';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, InputTextModule, PasswordModule, ButtonModule, CardModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  protected readonly loading = signal(false);
  protected readonly error = signal<string | null>(null);
  private readonly auth = inject(AuthService);
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);
  private readonly toast = inject(ToastService);

  form!: FormGroup;

  ngOnInit(): void {
    this.buildForm();
  }

  onSubmit(): void {
    if (!this.form.valid) {
      return;
    }
    const { email, password } = this.form.value;
    this.loading.set(true);
    this.error.set(null);
    this.auth.login(email, password).pipe(
      switchMap(() => this.userService.getMe()),
      tap(user => {
        this.userService.setCurrentUser(user);
        this.toast.showSuccess('Signed in successfully');
        if (user.role === Role.CHEF) {
          this.router.navigate([`/${ AppRoutes.CHEFS }`, user.chefId]).then();
        } else {
          this.router.navigate([`/${ AppRoutes.DASHBOARD }`]).then();
        }
      }),
      catchError((err: { error?: { message?: string } }) => {
        this.error.set(err.error?.message ?? 'Login failed');
        return of(null);
      }),
      finalize(() => this.loading.set(false))
    ).subscribe();
  }

  private buildForm(): void {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }
}
