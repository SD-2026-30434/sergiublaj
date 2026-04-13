import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Params, RouterModule } from '@angular/router';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { ToolbarModule } from 'primeng/toolbar';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { AppRoutes } from '../../../../core/models/app-routes.enum';
import { ToastService } from '../../../../core/services/toast.service';
import { Order } from '../../models/order.model';
import { OrderRequest } from '../../models/order-request.model';
import { OrderFilter } from '../../models/order-filter.model';
import { Chef } from '../../../chefs/models/chef.model';
import { OrderService } from '../../services/order.service';
import { ChefService } from '../../../chefs/services/chef.service';
import { CollectionResponse } from '../../../../shared/models/collection.model';
import { OrderFormComponent } from '../../modals/order-form/order-form.component';
import { DeleteModalComponent } from '../../../../shared/modals/delete-modal/delete-modal.component';
import { BaseListComponent } from '../../../../shared/components/base-list/base-list.component';
import { SortDirection } from '../../../../core/models/sort-direction.enum';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [
    CommonModule, RouterModule, TableModule, ButtonModule, ToolbarModule,
    InputTextModule, SelectModule, IconFieldModule, InputIconModule,
    ReactiveFormsModule, OrderFormComponent, DeleteModalComponent
  ],
  templateUrl: './order-list.component.html',
  styleUrl: './order-list.component.scss'
})
export class OrderListComponent extends BaseListComponent {
  readonly AppRoutes = AppRoutes;

  private readonly orderService = inject(OrderService);
  private readonly chefService = inject(ChefService);
  private readonly toastService = inject(ToastService);

  result!: CollectionResponse<Order>;
  orders: Order[] = [];
  formVisible = false;
  selectedOrder: Order | null = null;
  chefFilterControl = new FormControl<string | null>(null);
  chefOptions: { label: string; value: string }[] = [];
  deleteModalVisible = false;
  orderToDelete: Order | null = null;
  private chefsMap = new Map<string, Chef>();

  override ngOnInit(): void {
    const chefsResult = this.chefService.getAll({ pageSize: 1000 });
    chefsResult.elements.forEach(chef => this.chefsMap.set(chef.id, chef));
    this.chefOptions = chefsResult.elements.map(chef => ({ label: chef.name, value: chef.id }));
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
      this.orderService.update(this.selectedOrder.chefId, this.selectedOrder.id, request);
      this.toastService.showSuccess('Order updated');
      this.loadData();
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
    this.orderService.delete(this.orderToDelete.chefId, this.orderToDelete.id);
    this.toastService.showSuccess(`${this.orderToDelete.itemName} removed`);
    this.orderToDelete = null;
    this.loadData();
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
    this.result = this.orderService.getAll(filter);
    this.orders = this.result.elements.map(order => ({
      ...order,
      chefName: this.chefsMap.get(order.chefId)?.name ?? 'Unknown'
    }));
  }
}
