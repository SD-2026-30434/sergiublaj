import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CardModule } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { ChefService } from '../../chefs/services/chef.service';
import { OrderService } from '../../orders/services/order.service';
import { AppRoutes } from '../../../core/models/app-routes.enum';
import { Order } from '../../orders/models/order.model';
import { Chef } from '../../chefs/models/chef.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, CardModule, TableModule],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {
  private readonly chefService = inject(ChefService);
  private readonly orderService = inject(OrderService);

  readonly AppRoutes = AppRoutes;

  totalChefs = 0;
  totalOrders = 0;
  avgRating = 0;
  recentOrders: Order[] = [];
  private chefsMap = new Map<string, Chef>();

  ngOnInit(): void {
    const chefsResult = this.chefService.getAll({ pageSize: 1000 });
    this.totalChefs = chefsResult.totalElements;
    chefsResult.elements.forEach(chef => this.chefsMap.set(chef.id, chef));

    if (this.totalChefs > 0) {
      this.avgRating = chefsResult.elements.reduce((sum, chef) => {
        return sum + chef.numberOfStars;
      }, 0) / this.totalChefs;
    }

    const ordersResult = this.orderService.getAll({ sortBy: 'orderedAt', sortDirection: 'desc', pageSize: 5 });
    this.totalOrders = ordersResult.totalElements;
    this.recentOrders = ordersResult.elements.map(order => ({
      ...order,
      chefName: this.chefsMap.get(order.chefId)?.name ?? 'Unknown'
    }));
  }

}
