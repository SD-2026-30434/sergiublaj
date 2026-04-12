import { OrderFilter } from '../models/order-filter.model';
import { OrderRequest } from '../models/order-request.model';

export class LoadOrders {
  static readonly type = '[Order] Load Orders';
  constructor(public filter: OrderFilter = {}) {}
}

export class LoadChefOrders {
  static readonly type = '[Order] Load Chef Orders';
  constructor(public chefId: string, public filter: OrderFilter = {}) {}
}

export class CreateOrder {
  static readonly type = '[Order] Create Order';
  constructor(public chefId: string, public request: OrderRequest) {}
}

export class UpdateOrder {
  static readonly type = '[Order] Update Order';
  constructor(public chefId: string, public orderId: string, public request: OrderRequest) {}
}

export class DeleteOrder {
  static readonly type = '[Order] Delete Order';
  constructor(public chefId: string, public orderId: string) {}
}
