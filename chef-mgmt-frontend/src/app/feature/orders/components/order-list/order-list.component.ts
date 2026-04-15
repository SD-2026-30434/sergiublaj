import { Component, inject } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Params, RouterModule } from '@angular/router';
import { tap } from 'rxjs';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { ToolbarModule } from 'primeng/toolbar';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { AppRoutes } from '../../../../core/models/app-routes.enum';
import { Order } from '../../models/order.model';
import { OrderRequest } from '../../models/order-request.model';
import { OrderFilter } from '../../models/order-filter.model';
import { SortDirection } from '../../../../core/models/sort-direction.enum';
import { CollectionResponse } from '../../../../shared/models/collection.model';
import { OrderFormComponent } from '../../modals/order-form/order-form.component';
import { DeleteModalComponent } from '../../../../shared/modals/delete-modal/delete-modal.component';
import { BaseListComponent } from '../../../../shared/components/base-list/base-list.component';
import { ChefService } from '../../../chefs/services/chef.service';
import { OrderService } from '../../services/order.service';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [
    CurrencyPipe, DatePipe, RouterModule, TableModule, ButtonModule, ToolbarModule,
    InputTextModule, SelectModule, IconFieldModule, InputIconModule,
    ReactiveFormsModule, OrderFormComponent, DeleteModalComponent
  ],
  templateUrl: './order-list.component.html'
})
export class OrderListComponent extends BaseListComponent {
  readonly AppRoutes = AppRoutes;

  result: CollectionResponse<Order> = {
    pageNumber: 0,
    pageSize: 0,
    totalPages: 0,
    totalElements: 0,
    elements: []
  };
  private readonly chefService = inject(ChefService);
  private readonly orderService = inject(OrderService);
  private readonly toast = inject(ToastService);
  orders: Order[] = [];
  formVisible = false;
  selectedOrder: Order | null = null;
  chefFilterControl = new FormControl<string | null>(null);
  chefOptions: { label: string; value: string }[] = [];
  private readonly chefNameById = new Map<string, string>();
  deleteModalVisible = false;
  orderToDelete: Order | null = null;

  override ngOnInit(): void {
    this.chefService.getAll({ pageSize: 1000 }).subscribe(chefsResult => {
      const chefs = chefsResult?.elements ?? [];
      this.chefOptions = chefs.map(chef => ({ label: chef.name, value: chef.id }));
      this.chefNameById.clear();
      chefs.forEach(chef => this.chefNameById.set(chef.id, chef.name));
      if (this.orders.length > 0) {
        this.orders = this.orders.map(order => ({
          ...order,
          chefName: this.chefNameById.get(order.chefId) ?? order.chefName
        }));
      }
    });
    super.ngOnInit();
  }

  filterByChef(): void {
    this.updateQueryParams({ chefId: this.chefFilterControl.value || null, page: 0 });
  }

  editOrder(order: Order): void {
    this.selectedOrder = { ...order };
    this.formVisible = true;
  }

  onSave(request: OrderRequest): void {
    if (this.selectedOrder) {
      this.orderService.update(this.selectedOrder.chefId, this.selectedOrder.id, request).pipe(
        tap(() => {
          this.toast.showSuccess('Order updated');
          this.loadData();
        })
      ).subscribe();
    }
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

  protected override readCustomParams(params: Params): void {
    const chefId = params['chefId'] || null;
    this.chefFilterControl.setValue(chefId, { emitEvent: false });
  }

  protected override loadData(): void {
    const filter: OrderFilter = {
      itemName: this.search || undefined,
      chefId: this.chefFilterControl.value || undefined,
      sortBy: this.sortField || undefined,
      sortDirection: this.sortOrder === -1 ? SortDirection.DESC : SortDirection.ASC,
      pageNumber: this.page,
      pageSize: this.rows
    };
    this.orderService.getAll(filter).subscribe(result => {
      this.result = result;
      this.orders = (result?.elements ?? []).map(order => ({
        ...order,
        chefName: this.chefNameById.get(order.chefId) ?? order.chefName
      }));
    });
  }
}
