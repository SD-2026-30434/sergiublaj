import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order } from '../models/order.model';
import { OrderFilter } from '../models/order-filter.model';
import { OrderRequest } from '../models/order-request.model';
import { CollectionResponse } from '../../../shared/models/collection.model';
import { buildQueryParams } from '../../../core/utils/query-params.util';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/orders/v1';
  private readonly chefOrdersUrl = '/chefs/v1';

  getAll(filter: OrderFilter = {}): Observable<CollectionResponse<Order>> {
    const params = buildQueryParams(filter);
    return this.http.get<CollectionResponse<Order>>(this.baseUrl, { params });
  }

  getAllByChefId(chefId: string, filter: OrderFilter = {}): Observable<CollectionResponse<Order>> {
    const params = buildQueryParams(filter);
    return this.http.get<CollectionResponse<Order>>(`${this.chefOrdersUrl}/${chefId}/orders`, { params });
  }

  create(chefId: string, request: OrderRequest): Observable<Order> {
    return this.http.post<Order>(`${this.chefOrdersUrl}/${chefId}/orders`, request);
  }

  update(chefId: string, orderId: string, request: OrderRequest): Observable<Order> {
    return this.http.put<Order>(`${this.chefOrdersUrl}/${chefId}/orders/${orderId}`, request);
  }

  delete(chefId: string, orderId: string): Observable<void> {
    return this.http.delete<void>(`${this.chefOrdersUrl}/${chefId}/orders/${orderId}`);
  }
}
