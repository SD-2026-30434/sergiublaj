import { Component, inject, OnInit } from '@angular/core';
import { UserService } from '../../auth/services/user.service';
import { Role } from '../../../core/models/role.enum';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  private readonly userService = inject(UserService);

  displayName = '';
  isAdmin = false;

  ngOnInit(): void {
    this.getUserInfo();
  }

  private getUserInfo(): void {
    const user = this.userService.user;
    this.displayName = user?.chefName ?? user?.email ?? '';
    this.isAdmin = user?.role === Role.ADMIN;
  }
}
