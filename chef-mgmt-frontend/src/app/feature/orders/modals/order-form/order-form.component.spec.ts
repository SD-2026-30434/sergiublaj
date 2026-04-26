import { describe, it, expect, beforeEach, vi } from 'vitest';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { SimpleChange } from '@angular/core';
import { OrderFormComponent } from './order-form.component';
import { Order } from '../../models/order.model';

describe('OrderFormComponent', () => {
  let component: OrderFormComponent;
  let fixture: ComponentFixture<OrderFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderFormComponent, NoopAnimationsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(OrderFormComponent);
    component = fixture.componentInstance;
  });

  it('given component is initialized when ngOnInit runs then form is built with required controls and validators', () => {
    // given
    expect(component.form).toBeUndefined();

    // when
    component.ngOnInit();

    // then
    expect(component.form).toBeDefined();
    expect(component.form.get('itemName')).toBeTruthy();
    expect(component.form.get('totalPrice')).toBeTruthy();
    expect(component.form.get('orderedAt')).toBeTruthy();
    expect(component.form.valid).toBe(false);
  });

  it('given form is not yet built when ngOnChanges fires then it returns without errors', () => {
    // given
    expect(component.form).toBeUndefined();

    // when
    const call = () => component.ngOnChanges({
      order: new SimpleChange(null, { id: '1' } as Order, true)
    });

    // then
    expect(call).not.toThrow();
  });

  it('given an order input is set when ngOnChanges fires then form is patched with the order values', () => {
    // given
    component.ngOnInit();
    const order: Order = {
      id: 'o-1',
      chefId: 'c-1',
      itemName: 'Pizza',
      totalPrice: 25,
      orderedAt: '2026-04-26T10:00:00.000Z'
    };
    component.order = order;

    // when
    component.ngOnChanges({ order: new SimpleChange(null, order, true) });

    // then
    expect(component.form.value.itemName).toBe('Pizza');
    expect(component.form.value.totalPrice).toBe(25);
    expect(component.form.value.orderedAt).toEqual(new Date(order.orderedAt));
  });

  it('given the order input becomes null when ngOnChanges fires then the form is reset', () => {
    // given
    component.ngOnInit();
    component.form.patchValue({ itemName: 'Pizza', totalPrice: 25, orderedAt: new Date() });
    component.order = null;

    // when
    component.ngOnChanges({ order: new SimpleChange({ id: 'o-1' } as Order, null, false) });

    // then
    expect(component.form.value.itemName).toBeNull();
    expect(component.form.value.totalPrice).toBeNull();
    expect(component.form.value.orderedAt).toBeNull();
  });

  it('given changes do not include order when ngOnChanges fires then the form values are untouched', () => {
    // given
    component.ngOnInit();
    component.form.patchValue({ itemName: 'Pizza', totalPrice: 25, orderedAt: new Date() });

    // when
    component.ngOnChanges({ visible: new SimpleChange(false, true, false) });

    // then
    expect(component.form.value.itemName).toBe('Pizza');
    expect(component.form.value.totalPrice).toBe(25);
  });

  it('given the form is invalid when onSubmit is called then save is not emitted and the dialog stays open', () => {
    // given
    component.ngOnInit();
    component.visible = true;
    const saveSpy = vi.spyOn(component.save, 'emit');
    const visibleSpy = vi.spyOn(component.visibleChange, 'emit');

    // when
    component.onSubmit();

    // then
    expect(saveSpy).not.toHaveBeenCalled();
    expect(visibleSpy).not.toHaveBeenCalled();
    expect(component.visible).toBe(true);
  });

  it('given the form is valid with a Date orderedAt when onSubmit is called then save is emitted with ISO string and dialog closes', () => {
    // given
    component.ngOnInit();
    component.visible = true;
    const orderedAt = new Date('2026-04-26T10:00:00.000Z');
    component.form.setValue({ itemName: 'Pizza', totalPrice: 25, orderedAt });
    const saveSpy = vi.spyOn(component.save, 'emit');
    const visibleSpy = vi.spyOn(component.visibleChange, 'emit');

    // when
    component.onSubmit();

    // then
    expect(saveSpy).toHaveBeenCalledWith({
      itemName: 'Pizza',
      totalPrice: 25,
      orderedAt: orderedAt.toISOString()
    });
    expect(component.visible).toBe(false);
    expect(visibleSpy).toHaveBeenCalledWith(false);
  });

  it('given orderedAt is already a string when onSubmit is called then it is forwarded as-is', () => {
    // given
    component.ngOnInit();
    const orderedAt = '2026-04-26T10:00:00.000Z';
    component.form.setValue({ itemName: 'Pizza', totalPrice: 25, orderedAt });
    const saveSpy = vi.spyOn(component.save, 'emit');

    // when
    component.onSubmit();

    // then
    expect(saveSpy).toHaveBeenCalledWith({
      itemName: 'Pizza',
      totalPrice: 25,
      orderedAt
    });
  });

  it('given the dialog is visible when onCancel is called then the dialog is closed and visibleChange is emitted', () => {
    // given
    component.ngOnInit();
    component.visible = true;
    const visibleSpy = vi.spyOn(component.visibleChange, 'emit');

    // when
    component.onCancel();

    // then
    expect(component.visible).toBe(false);
    expect(visibleSpy).toHaveBeenCalledWith(false);
  });
});
