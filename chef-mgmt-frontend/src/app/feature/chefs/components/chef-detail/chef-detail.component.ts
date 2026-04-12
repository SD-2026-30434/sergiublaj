import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { AppRoutes } from '../../../../core/models/app-routes.enum';
import { CardModule } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { AvatarModule } from 'primeng/avatar';
import { ToolbarModule } from 'primeng/toolbar';
import { ToastService } from '../../../../core/services/toast.service';
import { Chef } from '../../models/chef.model';
import { ChefRequest } from '../../models/chef-request.model';
import { Order } from '../../../orders/models/order.model';
import { OrderRequest } from '../../../orders/models/order-request.model';
import { ChefService } from '../../services/chef.service';
import { OrderService } from '../../../orders/services/order.service';
import { ChefFormComponent } from '../../modals/chef-form/chef-form.component';
import { OrderFormComponent } from '../../../orders/modals/order-form/order-form.component';
import { DeleteModalComponent } from '../../../../shared/modals/delete-modal/delete-modal.component';

@Component({
  selector: 'app-chef-detail',
  standalone: true,
  imports: [
    CommonModule, CardModule, TableModule, ButtonModule, TagModule,
    ToolbarModule, AvatarModule, ChefFormComponent, OrderFormComponent, DeleteModalComponent
  ],
  templateUrl: './chef-detail.component.html',
  styleUrl: './chef-detail.component.scss'
})
export class ChefDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly chefService = inject(ChefService);
  private readonly orderService = inject(OrderService);
  private readonly toastService = inject(ToastService);

  chef: Chef | undefined;
  orders: Order[] = [];
  chefFormVisible = false;
  orderFormVisible = false;
  selectedOrder: Order | null = null;
  deleteModalVisible = false;
  orderToDelete: Order | null = null;

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.loadChef(id);
  }

  loadChef(id: string): void {
    this.chef = this.chefService.getById(id);
    this.orders = this.chef ? this.chef.orders ?? [] : [];
  }

  goBack(): void {
    this.router.navigate([AppRoutes.CHEFS]).then();
  }

  editChef(): void {
    this.chefFormVisible = true;
  }

  onChefSave(request: ChefRequest): void {
    if (!this.chef) {
      return;
    }
    this.chefService.update(this.chef.id, request);
    this.loadChef(this.chef.id);
    this.toastService.showSuccess('Chef updated');
  }

  openNewOrder(): void {
    this.selectedOrder = null;
    this.orderFormVisible = true;
  }

  editOrder(order: Order): void {
    this.selectedOrder = { ...order };
    this.orderFormVisible = true;
  }

  onOrderSave(request: OrderRequest): void {
    if (!this.chef) {
      return;
    }

    if (this.selectedOrder) {
      this.orderService.update(this.chef.id, this.selectedOrder.id, request);
      this.toastService.showSuccess('Order updated');
    } else {
      this.orderService.create(this.chef.id, request);
      this.toastService.showSuccess('Order created');
    }
    this.loadChef(this.chef.id);
  }

  openDeleteModal(order: Order): void {
    this.orderToDelete = order;
    this.deleteModalVisible = true;
  }

  onDeleteConfirm(): void {
    if (!this.chef || !this.orderToDelete) {
      return;
    }
    this.orderService.delete(this.chef.id, this.orderToDelete.id);
    this.toastService.showSuccess(`${ this.orderToDelete.itemName } removed`);
    this.orderToDelete = null;
    this.loadChef(this.chef.id);
  }
}
