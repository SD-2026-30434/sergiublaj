import { inject, Injectable } from '@angular/core';
import { Chef } from '../models/chef.model';
import { ChefFilter } from '../models/chef-filter.model';
import { ChefRequest } from '../models/chef-request.model';
import { CollectionResponse } from '../../../shared/models/collection.model';
import { OrderService } from '../../orders/services/order.service';
import { APP_CONFIG } from '../../../core/config/app.config';
import mockData from '../../../../assets/mock-data.json';

@Injectable({ providedIn: 'root' })
export class ChefService {
  private readonly orderService = inject(OrderService);

  private chefs: Chef[] = [...mockData.chefs];

  getAll(filter: ChefFilter = {}): CollectionResponse<Chef> {
    let result = [...this.chefs];

    if (filter.name) {
      result = result.filter(chef => chef.name.toLowerCase().includes(filter.name!.toLowerCase()));
    }
    if (filter.email) {
      result = result.filter(chef => chef.email.toLowerCase().includes(filter.email!.toLowerCase()));
    }
    if (filter.rating != null) {
      result = result.filter(chef => chef.numberOfStars >= filter.rating!);
    }

    const sortBy = filter.sortBy || 'name';
    const sortDir = filter.sortDirection === 'desc' ? -1 : 1;
    result.sort((chefA, chefB) => {
      const aVal = (chefA as any)[sortBy];
      const bVal = (chefB as any)[sortBy];
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

  getById(id: string): Chef | undefined {
    const found = this.chefs.find(chef => chef.id === id);
    return found
      ? { ...found, orders: this.orderService.getAll({ chefId: id, pageSize: 1000 }).elements }
      : undefined;
  }

  create(request: ChefRequest): Chef {
    const chef: Chef = {
      id: crypto.randomUUID(),
      name: request.name,
      email: request.email,
      birthDate: request.birthDate,
      numberOfStars: request.rating,
      orders: []
    };
    this.chefs.push(chef);
    return chef;
  }

  update(id: string, request: ChefRequest): Chef | undefined {
    const index = this.chefs.findIndex(chef => chef.id === id);
    if (index === -1) {
      return undefined;
    }
    this.chefs[index] = {
      ...this.chefs[index],
      name: request.name,
      email: request.email,
      birthDate: request.birthDate,
      numberOfStars: request.rating
    };
    return this.getById(id);
  }

  delete(id: string): void {
    this.chefs = this.chefs.filter(chef => chef.id !== id);
    this.orderService.deleteByChefId(id);
  }
}
