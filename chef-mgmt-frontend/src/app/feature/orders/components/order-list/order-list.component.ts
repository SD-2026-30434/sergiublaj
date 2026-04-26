import { Component, inject } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { finalize, tap } from 'rxjs';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { AppRoutes } from '../../../../core/models/app-routes.enum';
import { Order } from '../../models/order.model';
import { OrderRequest } from '../../models/order-request.model';
import { OrderFilter } from '../../models/order-filter.model';
import { SortDirection } from '../../../../core/models/sort-direction.enum';
import { OrderFormComponent } from '../../modals/order-form/order-form.component';
import { DeleteModalComponent } from '../../../../shared/modals/delete-modal/delete-modal.component';
import { BaseListComponent } from '../../../../shared/components/base-list/base-list.component';
import { OrderService } from '../../services/order.service';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [
    CurrencyPipe, DatePipe, RouterModule, TableModule, ButtonModule,
    InputTextModule, IconFieldModule, InputIconModule,
    OrderFormComponent, DeleteModalComponent
  ],
  templateUrl: './order-list.component.html'
})
export class OrderListComponent extends BaseListComponent {
  private readonly orderService = inject(OrderService);
  private readonly toast = inject(ToastService);

  readonly AppRoutes = AppRoutes;

  totalElements = 0;
  orders: Order[] = [];
  formVisible = false;
  formLoading = false;
  selectedOrder: Order | null = null;
  deleteModalVisible = false;
  orderToDelete: Order | null = null;

  editOrder(order: Order): void {
    this.selectedOrder = { ...order };
    this.formVisible = true;
  }

  onSave(request: OrderRequest): void {
    if (!this.selectedOrder) {
      return;
    }

    this.formLoading = true;
    this.orderService.update(this.selectedOrder.chefId, this.selectedOrder.id, request).pipe(
      tap(() => {
        this.toast.showSuccess('Order updated');
        this.formVisible = false;
        this.loadData();
      }),
      finalize(() => this.formLoading = false)
    ).subscribe();
  }

  openDeleteModal(order: Order): void {
    this.orderToDelete = order;
    this.deleteModalVisible = true;
  }

  onDeleteConfirm(): void {
    if (!this.orderToDelete) {
      return;
    }
    this.orderService.delete(this.orderToDelete.chefId, this.orderToDelete.id).pipe(
      tap(() => {
        this.toast.showSuccess('Order deleted');
        this.orderToDelete = null;
        this.loadData();
      })
    ).subscribe();
  }

  protected override loadData(): void {
    const filter: OrderFilter = {
      itemName: this.search || undefined,
      sortBy: this.sortField || undefined,
      sortDirection: this.sortOrder === -1 ? SortDirection.DESC : SortDirection.ASC,
      pageNumber: this.page,
      pageSize: this.size
    };
    this.orderService.getAll(filter).pipe(
      tap(result => {
        this.totalElements = result.totalElements;
        this.orders = result.elements;
      })
    ).subscribe();
  }
}
