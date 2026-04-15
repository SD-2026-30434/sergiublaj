import { Component, inject, OnInit, signal } from '@angular/core';
import { CurrencyPipe, DatePipe, DecimalPipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { catchError, finalize, tap } from 'rxjs/operators';
import { of } from 'rxjs';
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
import { AppRoutes } from '../../../../core/models/app-routes.enum';
import { Role } from '../../../../core/models/role.enum';
import { ChefFormComponent } from '../../modals/chef-form/chef-form.component';
import { OrderFormComponent } from '../../../orders/modals/order-form/order-form.component';
import { DeleteModalComponent } from '../../../../shared/modals/delete-modal/delete-modal.component';
import { ChefService } from '../../services/chef.service';
import { OrderService } from '../../../orders/services/order.service';
import { UserService } from '../../../auth/services/user.service';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-chef-detail',
  standalone: true,
  imports: [
    CurrencyPipe, DatePipe, DecimalPipe, CardModule, TableModule, ButtonModule, TagModule,
    ToolbarModule, AvatarModule, ChefFormComponent, OrderFormComponent, DeleteModalComponent
  ],
  templateUrl: './chef-detail.component.html'
})
export class ChefDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly chefService = inject(ChefService);
  private readonly orderService = inject(OrderService);
  private readonly userService = inject(UserService);
  private readonly toast = inject(ToastService);

  protected readonly loading = signal(true);

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
    this.loading.set(true);
    this.chefService.getById(id).pipe(
      tap(chef => {
        this.chef = chef;
        this.orders = chef?.orders ?? [];
      }),
      catchError(() => {
        this.chef = null;
        this.orders = [];
        return of(null);
      }),
      finalize(() => this.loading.set(false))
    ).subscribe();
  }

  goBack(): void {
    const target = this.userService.userRole() === Role.ADMIN ? AppRoutes.CHEFS : AppRoutes.DASHBOARD;
    this.router.navigate([`/${target}`]).then();
  }

  editChef(): void {
    this.chefFormVisible = true;
  }

  onChefSave(request: ChefRequest): void {
    if (!this.chef) {
      return;
    }
    this.chefService.update(this.chef.id, request).pipe(
      tap(() => {
        this.toast.showSuccess('Chef updated');
        this.loadChef(this.chef!.id);
      })
    ).subscribe();
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
    const isUpdate = !!this.selectedOrder;
    const orderFunction = isUpdate
      ? this.orderService.update(this.chef.id, this.selectedOrder!.id, request)
      : this.orderService.create(this.chef.id, request);
    orderFunction.pipe(
      tap(() => {
        this.toast.showSuccess(isUpdate ? 'Order updated' : 'Order created');
        this.loadChef(this.chef!.id);
      })
    ).subscribe();
  }

  openDeleteModal(order: Order): void {
    this.orderToDelete = order;
    this.deleteModalVisible = true;
  }

  onDeleteConfirm(): void {
    if (!this.chef || !this.orderToDelete) {
      return;
    }
    this.orderService.delete(this.chef.id, this.orderToDelete.id).pipe(
      tap(() => {
        this.toast.showSuccess('Order deleted');
        this.orderToDelete = null;
        this.loadChef(this.chef!.id);
      })
    ).subscribe();
  }
}
