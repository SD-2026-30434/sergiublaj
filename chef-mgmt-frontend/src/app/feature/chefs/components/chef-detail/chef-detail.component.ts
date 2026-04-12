import { Component, inject, OnInit } from '@angular/core';
import { CurrencyPipe, DatePipe, DecimalPipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Store } from '@ngxs/store';
import { CardModule } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { TagModule } from 'primeng/tag';
import { AvatarModule } from 'primeng/avatar';
import { ToolbarModule } from 'primeng/toolbar';
import { Chef } from '../../models/chef.model';
import { ChefRequest } from '../../models/chef-request.model';
import { Order } from '../../../orders/models/order.model';
import { OrderRequest } from '../../../orders/models/order-request.model';
import { ChefState } from '../../store/chef.state';
import { LoadChef, UpdateChef } from '../../store/chef.actions';
import { CreateOrder, UpdateOrder, DeleteOrder } from '../../../orders/store/order.actions';
import { AuthState } from '../../../auth/store/auth.state';
import { AppRoutes } from '../../../../core/models/app-routes.enum';
import { Role } from '../../../../core/models/role.enum';
import { ChefFormComponent } from '../../modals/chef-form/chef-form.component';
import { OrderFormComponent } from '../../../orders/modals/order-form/order-form.component';
import { DeleteModalComponent } from '../../../../shared/modals/delete-modal/delete-modal.component';

@Component({
  selector: 'app-chef-detail',
  standalone: true,
  imports: [
    CurrencyPipe, DatePipe, DecimalPipe, CardModule, TableModule, ButtonModule, TagModule,
    ToolbarModule, AvatarModule, ChefFormComponent, OrderFormComponent, DeleteModalComponent
  ],
  templateUrl: './chef-detail.component.html',
  styleUrl: './chef-detail.component.scss'
})
export class ChefDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly store = inject(Store);

  chef: Chef | null = null;
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
    this.store.dispatch(new LoadChef(id)).subscribe(() => {
      this.chef = this.store.selectSnapshot(ChefState.selectedChef);
      this.orders = this.chef?.orders ?? [];
    });
  }

  goBack(): void {
    const target = this.store.selectSnapshot(AuthState.userRole) === Role.ADMIN ? AppRoutes.CHEFS : AppRoutes.DASHBOARD;
    this.router.navigate([`/${target}`]).then();
  }

  editChef(): void {
    this.chefFormVisible = true;
  }

  onChefSave(request: ChefRequest): void {
    if (!this.chef) {
      return;
    }
    this.store.dispatch(new UpdateChef(this.chef.id, request)).subscribe(() => {
      this.loadChef(this.chef!.id);
    });
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
    const action = this.selectedOrder
      ? new UpdateOrder(this.chef.id, this.selectedOrder.id, request)
      : new CreateOrder(this.chef.id, request);
    this.store.dispatch(action).subscribe(() => this.loadChef(this.chef!.id));
  }

  openDeleteModal(order: Order): void {
    this.orderToDelete = order;
    this.deleteModalVisible = true;
  }

  onDeleteConfirm(): void {
    if (!this.chef || !this.orderToDelete) {
      return;
    }
    this.store.dispatch(new DeleteOrder(this.chef.id, this.orderToDelete.id)).subscribe(() => {
      this.orderToDelete = null;
      this.loadChef(this.chef!.id);
    });
  }
}
