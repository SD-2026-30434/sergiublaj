import { describe, it, expect, beforeEach, vi, type Mocked } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { of, Subject } from 'rxjs';
import { MessageService } from 'primeng/api';
import { OrderListComponent } from './order-list.component';
import { OrderService } from '../../services/order.service';
import { ToastService } from '../../../../core/services/toast.service';
import { Order } from '../../models/order.model';
import { OrderRequest } from '../../models/order-request.model';
import { CollectionResponse } from '../../../../shared/models/collection.model';
import { SortDirection } from '../../../../core/models/sort-direction.enum';
import { API_CONFIG } from '../../../../core/config/api.config';

describe('OrderListComponent', () => {
  let component: OrderListComponent;
  let fixture: ComponentFixture<OrderListComponent>;
  let orderService: Mocked<OrderService>;
  let toast: Mocked<ToastService>;
  let queryParams$: Subject<any>;

  const sampleOrder: Order = {
    id: 'o-1',
    chefId: 'c-1',
    itemName: 'Pizza',
    totalPrice: 25,
    orderedAt: '2026-04-26T10:00:00.000Z'
  };

  const emptyResponse: CollectionResponse<Order> = {
    elements: [], totalElements: 0, pageNumber: 0, pageSize: 20, totalPages: 0
  };

  beforeEach(async () => {
    orderService = {
      getAll: vi.fn().mockReturnValue(of(emptyResponse)),
      create: vi.fn().mockReturnValue(of(sampleOrder)),
      update: vi.fn().mockReturnValue(of(sampleOrder)),
      delete: vi.fn().mockReturnValue(of(void 0))
    } as unknown as Mocked<OrderService>;
    toast = {
      showSuccess: vi.fn(),
      showError: vi.fn()
    } as unknown as Mocked<ToastService>;
    queryParams$ = new Subject<any>();

    await TestBed.configureTestingModule({
      imports: [OrderListComponent, NoopAnimationsModule],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        provideHttpClientTesting(),
        MessageService,
        { provide: OrderService, useValue: orderService },
        { provide: ToastService, useValue: toast },
        { provide: ActivatedRoute, useValue: { queryParams: queryParams$.asObservable() } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(OrderListComponent);
    component = fixture.componentInstance;
  });

  it('given an order is passed when editOrder is called then it clones the order and opens the form', () => {
    // given
    expect(component.formVisible).toBe(false);
    expect(component.selectedOrder).toBeNull();

    // when
    component.editOrder(sampleOrder);

    // then
    expect(component.selectedOrder).toEqual(sampleOrder);
    expect(component.selectedOrder).not.toBe(sampleOrder);
    expect(component.formVisible).toBe(true);
  });

  it('given no selected order when onSave is called then the service is not called', () => {
    // given
    component.selectedOrder = null;
    const request: OrderRequest = { itemName: 'Pizza', totalPrice: 25, orderedAt: sampleOrder.orderedAt };

    // when
    component.onSave(request);

    // then
    expect(orderService.update).not.toHaveBeenCalled();
    expect(toast.showSuccess).not.toHaveBeenCalled();
  });

  it('given a selected order when onSave is called then it updates the order, shows a success toast and reloads', () => {
    // given
    component.selectedOrder = { ...sampleOrder };
    const request: OrderRequest = { itemName: 'Pasta', totalPrice: 18, orderedAt: sampleOrder.orderedAt };
    orderService.getAll.mockClear();

    // when
    component.onSave(request);

    // then
    expect(orderService.update).toHaveBeenCalledWith(sampleOrder.chefId, sampleOrder.id, request);
    expect(toast.showSuccess).toHaveBeenCalledWith('Order updated');
    expect(orderService.getAll).toHaveBeenCalledTimes(1);
  });

  it('given an order is passed when openDeleteModal is called then it stores the order and opens the modal', () => {
    // given
    expect(component.deleteModalVisible).toBe(false);

    // when
    component.openDeleteModal(sampleOrder);

    // then
    expect(component.orderToDelete).toBe(sampleOrder);
    expect(component.deleteModalVisible).toBe(true);
  });

  it('given no order to delete when onDeleteConfirm is called then the service is not called', () => {
    // given
    component.orderToDelete = null;

    // when
    component.onDeleteConfirm();

    // then
    expect(orderService.delete).not.toHaveBeenCalled();
    expect(toast.showSuccess).not.toHaveBeenCalled();
  });

  it('given an order to delete when onDeleteConfirm is called then it deletes, shows toast, clears state and reloads', () => {
    // given
    component.orderToDelete = sampleOrder;
    orderService.getAll.mockClear();

    // when
    component.onDeleteConfirm();

    // then
    expect(orderService.delete).toHaveBeenCalledWith(sampleOrder.chefId, sampleOrder.id);
    expect(toast.showSuccess).toHaveBeenCalledWith('Order deleted');
    expect(component.orderToDelete).toBeNull();
    expect(orderService.getAll).toHaveBeenCalledTimes(1);
  });

  it('given the route emits empty query params when ngOnInit subscribes then it loads data with default filter values', () => {
    // given
    const response: CollectionResponse<Order> = {
      elements: [sampleOrder], totalElements: 1, pageNumber: 0, pageSize: 20, totalPages: 1
    };
    orderService.getAll.mockReturnValue(of(response));
    fixture.detectChanges();

    // when
    queryParams$.next({});

    // then
    expect(orderService.getAll).toHaveBeenCalledWith({
      itemName: undefined,
      sortBy: undefined,
      sortDirection: SortDirection.ASC,
      pageNumber: 0,
      pageSize: API_CONFIG.PAGE_SIZE
    });
    expect(component.orders).toEqual([sampleOrder]);
    expect(component.totalElements).toBe(1);
  });

  it('given the route emits descending sort and search params when ngOnInit subscribes then it forwards them to the service', () => {
    // given
    orderService.getAll.mockReturnValue(of(emptyResponse));
    fixture.detectChanges();

    // when
    queryParams$.next({
      search: 'Pizza',
      sortField: 'totalPrice',
      sortOrder: SortDirection.DESC,
      page: '2',
      size: '10'
    });

    // then
    expect(orderService.getAll).toHaveBeenCalledWith({
      itemName: 'Pizza',
      sortBy: 'totalPrice',
      sortDirection: SortDirection.DESC,
      pageNumber: 2,
      pageSize: 10
    });
  });
});
