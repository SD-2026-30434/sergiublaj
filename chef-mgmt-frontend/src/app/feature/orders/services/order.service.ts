import { Injectable } from '@angular/core';
import { Order } from '../models/order.model';
import { OrderFilter } from '../models/order-filter.model';
import { OrderRequest } from '../models/order-request.model';
import { CollectionResponse } from '../../../shared/models/collection.model';
import { APP_CONFIG } from '../../../core/config/app.config';
import mockData from '../../../../assets/mock-data.json';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private orders: Order[] = [...mockData.orders];

  getAll(filter: OrderFilter = {}): CollectionResponse<Order> {
    let result = [...this.orders];

    if (filter.itemName) {
      result = result.filter(order => order.itemName.toLowerCase().includes(filter.itemName!.toLowerCase()));
    }
    if (filter.totalPrice != null) {
      result = result.filter(order => order.totalPrice >= filter.totalPrice!);
    }
    if (filter.chefId) {
      result = result.filter(order => order.chefId === filter.chefId);
    }

    const sortBy = filter.sortBy || 'orderedAt';
    const sortDir = filter.sortDirection === 'desc' ? -1 : 1;
    result.sort((orderA, orderB) => {
      const aVal = (orderA as any)[sortBy];
      const bVal = (orderB as any)[sortBy];
      if (aVal < bVal) {
        return -1 * sortDir;
      }
      if (aVal > bVal) {
        return 1 * sortDir;
      }
      return 0;
    });

    const pageNumber = filter.pageNumber ?? 0;
    const pageSize = filter.pageSize ?? APP_CONFIG.pageSize;
    const totalElements = result.length;
    const totalPages = Math.ceil(totalElements / pageSize);
    const start = pageNumber * pageSize;
    const elements = result.slice(start, start + pageSize);

    return { pageNumber, pageSize, totalPages, totalElements, elements };
  }

  create(chefId: string, request: OrderRequest): Order {
    const order: Order = {
      id: crypto.randomUUID(),
      itemName: request.itemName,
      totalPrice: request.totalPrice,
      orderedAt: request.orderedAt,
      chefId
    };
    this.orders.push(order);
    return order;
  }

  update(chefId: string, orderId: string, request: OrderRequest): Order | undefined {
    const index = this.orders.findIndex(order => order.id === orderId && order.chefId === chefId);
    if (index === -1) {
      return undefined;
    }
    this.orders[index] = {
      ...this.orders[index],
      itemName: request.itemName,
      totalPrice: request.totalPrice,
      orderedAt: request.orderedAt
    };
    return this.orders[index];
  }

  delete(chefId: string, orderId: string): void {
    this.orders = this.orders.filter((order) => !(order.id === orderId && order.chefId === chefId));
  }

  deleteByChefId(chefId: string): void {
    this.orders = this.orders.filter(order => order.chefId !== chefId);
  }
}
