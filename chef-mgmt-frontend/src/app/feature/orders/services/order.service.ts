import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_CONFIG } from '../../../core/config/api.config';
import { Order } from '../models/order.model';
import { OrderFilter } from '../models/order-filter.model';
import { OrderRequest } from '../models/order-request.model';
import { CollectionResponse } from '../../../shared/models/collection.model';
import { buildQueryParams } from '../../../core/utils/query-params.util';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private readonly http = inject(HttpClient);

  getAll(filter: OrderFilter = {}): Observable<CollectionResponse<Order>> {
    const params = buildQueryParams(filter);
    return this.http.get<CollectionResponse<Order>>(`${ API_CONFIG.ORDERS_URL }/v1`, { params });
  }

  getAllByChefId(chefId: string, filter: OrderFilter = {}): Observable<CollectionResponse<Order>> {
    const params = buildQueryParams(filter);
    return this.http.get<CollectionResponse<Order>>(
      `${ API_CONFIG.CHEFS_URL }/v1/${ chefId }/orders`,
      { params }
    );
  }

  create(chefId: string, request: OrderRequest): Observable<Order> {
    return this.http.post<Order>(`${ API_CONFIG.CHEFS_URL }/v1/${ chefId }/orders`, request);
  }

  update(chefId: string, orderId: string, request: OrderRequest): Observable<Order> {
    return this.http.put<Order>(`${ API_CONFIG.CHEFS_URL }/v1/${ chefId }/orders/${ orderId }`, request);
  }

  delete(chefId: string, orderId: string): Observable<void> {
    return this.http.delete<void>(`${ API_CONFIG.CHEFS_URL }/v1/${ chefId }/orders/${ orderId }`);
  }
}
