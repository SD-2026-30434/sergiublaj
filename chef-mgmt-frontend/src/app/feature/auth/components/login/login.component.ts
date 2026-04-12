import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Store } from '@ngxs/store';
import { Observable } from 'rxjs';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { AsyncPipe } from '@angular/common';
import { Login } from '../../store/auth.actions';
import { AuthState } from '../../store/auth.state';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [AsyncPipe, ReactiveFormsModule, InputTextModule, PasswordModule, ButtonModule, CardModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly store = inject(Store);

  form!: FormGroup;
  loading$: Observable<boolean> = this.store.select(AuthState.loading);
  error$: Observable<string | null> = this.store.select(AuthState.error);

  ngOnInit(): void {
    this.buildForm();
  }

  onSubmit(): void {
    if (!this.form.valid) {
      return;
    }
    const { email, password } = this.form.value;
    this.store.dispatch(new Login(email, password));
  }

  private buildForm(): void {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }
}
