import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { OrderService } from './order.service';
import { Order } from '../models/order.model';
import { OrderFilter } from '../models/order-filter.model';
import { OrderRequest } from '../models/order-request.model';
import { CollectionResponse } from '../../../shared/models/collection.model';
import { API_CONFIG } from '../../../core/config/api.config';
import { SortDirection } from '../../../core/models/sort-direction.enum';

describe('OrderService', () => {
  let service: OrderService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        OrderService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(OrderService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('given no filter when getAll is called then it sends GET to orders URL with no params', () => {
    // given
    const expectedResponse: CollectionResponse<Order> = {
      elements: [], totalElements: 0, pageNumber: 0, pageSize: 20, totalPages: 0
    };

    // when
    let actual: CollectionResponse<Order> | undefined;
    service.getAll().subscribe(res => actual = res);
    const req = httpMock.expectOne(r => r.url === API_CONFIG.ORDERS_URL);
    req.flush(expectedResponse);

    // then
    expect(req.request.method).toBe('GET');
    expect(req.request.params.keys().length).toBe(0);
    expect(actual).toEqual(expectedResponse);
  });

  it('given a populated filter when getAll is called then it forwards non-empty values as query params', () => {
    // given
    const filter: OrderFilter = {
      itemName: 'Pizza',
      sortBy: 'itemName',
      sortDirection: SortDirection.DESC,
      pageNumber: 2,
      pageSize: 10
    };

    // when
    service.getAll(filter).subscribe();
    const req = httpMock.expectOne(r => r.url === API_CONFIG.ORDERS_URL);
    req.flush({ elements: [], totalElements: 0, pageNumber: 0, pageSize: 20, totalPages: 0 });

    // then
    expect(req.request.params.get('itemName')).toBe('Pizza');
    expect(req.request.params.get('sortBy')).toBe('itemName');
    expect(req.request.params.get('sortDirection')).toBe(SortDirection.DESC);
    expect(req.request.params.get('pageNumber')).toBe('2');
    expect(req.request.params.get('pageSize')).toBe('10');
  });

  it('given a chefId and an order request when create is called then it POSTs to the chef orders URL with the body', () => {
    // given
    const chefId = 'chef-1';
    const request: OrderRequest = { itemName: 'Soup', totalPrice: 12.5, orderedAt: '2026-04-26T10:00:00.000Z' };
    const created: Order = { id: 'o-1', chefId, ...request };

    // when
    let actual: Order | undefined;
    service.create(chefId, request).subscribe(res => actual = res);
    const req = httpMock.expectOne(`${API_CONFIG.CHEFS_URL}/${chefId}/orders`);
    req.flush(created);

    // then
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    expect(actual).toEqual(created);
  });

  it('given a chefId, orderId and an order request when update is called then it PUTs to the order URL with the body', () => {
    // given
    const chefId = 'chef-2';
    const orderId = 'order-9';
    const request: OrderRequest = { itemName: 'Salad', totalPrice: 8, orderedAt: '2026-04-26T11:00:00.000Z' };
    const updated: Order = { id: orderId, chefId, ...request };

    // when
    let actual: Order | undefined;
    service.update(chefId, orderId, request).subscribe(res => actual = res);
    const req = httpMock.expectOne(`${API_CONFIG.CHEFS_URL}/${chefId}/orders/${orderId}`);
    req.flush(updated);

    // then
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(request);
    expect(actual).toEqual(updated);
  });

  it('given a chefId and orderId when delete is called then it sends DELETE to the order URL', () => {
    // given
    const chefId = 'chef-3';
    const orderId = 'order-7';

    // when
    let completed = false;
    service.delete(chefId, orderId).subscribe(() => completed = true);
    const req = httpMock.expectOne(`${API_CONFIG.CHEFS_URL}/${chefId}/orders/${orderId}`);
    req.flush(null);

    // then
    expect(req.request.method).toBe('DELETE');
    expect(completed).toBe(true);
  });
});
