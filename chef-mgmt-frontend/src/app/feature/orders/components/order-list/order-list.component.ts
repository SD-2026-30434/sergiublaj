import { Component, inject } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Params, RouterModule } from '@angular/router';
import { Store } from '@ngxs/store';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { ToolbarModule } from 'primeng/toolbar';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { AppRoutes } from '../../../../core/models/app-routes.enum';
import { Order } from '../../models/order.model';
import { OrderRequest } from '../../models/order-request.model';
import { OrderFilter } from '../../models/order-filter.model';
import { SortDirection } from '../../../../core/models/sort-direction.enum';
import { ChefState } from '../../../chefs/store/chef.state';
import { OrderState } from '../../store/order.state';
import { LoadChefs } from '../../../chefs/store/chef.actions';
import { LoadOrders, UpdateOrder, DeleteOrder } from '../../store/order.actions';
import { CollectionResponse } from '../../../../shared/models/collection.model';
import { OrderFormComponent } from '../../modals/order-form/order-form.component';
import { DeleteModalComponent } from '../../../../shared/modals/delete-modal/delete-modal.component';
import { BaseListComponent } from '../../../../shared/components/base-list/base-list.component';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [
    CurrencyPipe, DatePipe, RouterModule, TableModule, ButtonModule, ToolbarModule,
    InputTextModule, SelectModule, IconFieldModule, InputIconModule,
    ReactiveFormsModule, OrderFormComponent, DeleteModalComponent
  ],
  templateUrl: './order-list.component.html',
  styleUrl: './order-list.component.scss'
})
export class OrderListComponent extends BaseListComponent {
  readonly AppRoutes = AppRoutes;

  private readonly store = inject(Store);

  result!: CollectionResponse<Order>;
  orders: Order[] = [];
  formVisible = false;
  selectedOrder: Order | null = null;
  chefFilterControl = new FormControl<string | null>(null);
  chefOptions: { label: string; value: string }[] = [];
  deleteModalVisible = false;
  orderToDelete: Order | null = null;

  override ngOnInit(): void {
    this.store.dispatch(new LoadChefs({ pageSize: 1000 })).subscribe(() => {
      const chefsResult = this.store.selectSnapshot(ChefState.chefs);
      this.chefOptions = (chefsResult?.elements ?? []).map(chef => ({ label: chef.name, value: chef.id }));
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
      this.store.dispatch(new UpdateOrder(this.selectedOrder.chefId, this.selectedOrder.id, request))
        .subscribe(() => this.loadData());
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
    this.store.dispatch(new DeleteOrder(this.orderToDelete.chefId, this.orderToDelete.id))
      .subscribe(() => {
        this.orderToDelete = null;
        this.loadData();
      });
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
    this.store.dispatch(new LoadOrders(filter)).subscribe(() => {
      this.result = this.store.selectSnapshot(OrderState.orders)!;
      this.orders = this.result?.elements ?? [];
    });
  }
}
