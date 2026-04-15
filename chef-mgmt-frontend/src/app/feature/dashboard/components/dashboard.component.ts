import { Component, computed, inject } from '@angular/core';
import { UserService } from '../../auth/services/user.service';
import { Role } from '../../../core/models/role.enum';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent {
  private readonly userService = inject(UserService);

  protected readonly isAdmin = computed(() => this.userService.user()?.role === Role.ADMIN);
  protected readonly name = computed(() => {
    const u = this.userService.user();
    return u?.chefName ?? u?.email ?? '';
  });
}
