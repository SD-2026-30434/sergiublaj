import { Component, inject } from '@angular/core';
import { Store } from '@ngxs/store';
import { AuthState } from '../../auth/store/auth.state';
import { Role } from '../../../core/models/role.enum';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent {
  private readonly store = inject(Store);

  private readonly user = this.store.selectSnapshot(AuthState.user);
  isAdmin = this.user?.role === Role.ADMIN;
  name = this.user?.chefName ?? this.user?.email ?? '';
}
