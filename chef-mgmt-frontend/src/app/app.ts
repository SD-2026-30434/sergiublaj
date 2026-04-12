import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Store } from '@ngxs/store';
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { ToastModule } from 'primeng/toast';
import { CheckSession } from './feature/auth/store/auth.actions';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, ToastModule],
  templateUrl: './app.html'
})
export class App implements OnInit {
  private readonly store = inject(Store);

  ngOnInit(): void {
    this.store.dispatch(new CheckSession());
  }
}
