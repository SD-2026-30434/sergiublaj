import { inject, Injectable } from '@angular/core';
import { State, Action, StateContext, Selector } from '@ngxs/store';
import { tap, catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { Order } from '../models/order.model';
import { CollectionResponse } from '../../../shared/models/collection.model';
import { OrderService } from '../services/order.service';
import { ToastService } from '../../../core/services/toast.service';
import { LoadOrders, LoadChefOrders, CreateOrder, UpdateOrder, DeleteOrder } from './order.actions';

export interface OrderStateModel {
  orders: CollectionResponse<Order> | null;
  loading: boolean;
  error: string | null;
}

@State<OrderStateModel>({
  name: 'order',
  defaults: {
    orders: null,
    loading: false,
    error: null
  }
})
@Injectable()
export class OrderState {
  private readonly orderService = inject(OrderService);
  private readonly toastService = inject(ToastService);

  @Selector()
  static orders(state: OrderStateModel): CollectionResponse<Order> | null {
    return state.orders;
  }

  @Selector()
  static loading(state: OrderStateModel): boolean {
    return state.loading;
  }

  @Action(LoadOrders)
  loadOrders({ patchState }: StateContext<OrderStateModel>, action: LoadOrders) {
    patchState({ loading: true });
    return this.orderService.getAll(action.filter).pipe(
      tap(orders => patchState({ orders, loading: false })),
      catchError(err => {
        patchState({ loading: false, error: err.error?.message });
        return of(null);
      })
    );
  }

  @Action(LoadChefOrders)
  loadChefOrders({ patchState }: StateContext<OrderStateModel>, action: LoadChefOrders) {
    patchState({ loading: true });
    return this.orderService.getAllByChefId(action.chefId, action.filter).pipe(
      tap(orders => patchState({ orders, loading: false })),
      catchError(err => {
        patchState({ loading: false, error: err.error?.message });
        return of(null);
      })
    );
  }

  @Action(CreateOrder)
  createOrder(_: StateContext<OrderStateModel>, action: CreateOrder) {
    return this.orderService.create(action.chefId, action.request).pipe(
      tap(() => this.toastService.showSuccess('Order created')),
      catchError(() => of(null))
    );
  }

  @Action(UpdateOrder)
  updateOrder(_: StateContext<OrderStateModel>, action: UpdateOrder) {
    return this.orderService.update(action.chefId, action.orderId, action.request).pipe(
      tap(() => this.toastService.showSuccess('Order updated')),
      catchError(() => of(null))
    );
  }

  @Action(DeleteOrder)
  deleteOrder(_: StateContext<OrderStateModel>, action: DeleteOrder) {
    return this.orderService.delete(action.chefId, action.orderId).pipe(
      tap(() => this.toastService.showSuccess('Order deleted')),
      catchError(() => of(null))
    );
  }
}
