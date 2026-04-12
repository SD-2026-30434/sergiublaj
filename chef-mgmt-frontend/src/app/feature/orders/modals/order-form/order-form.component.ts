import { Component, EventEmitter, inject, Input, OnChanges, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { DatePickerModule } from 'primeng/datepicker';
import { ButtonModule } from 'primeng/button';
import { Order } from '../../models/order.model';
import { OrderRequest } from '../../models/order-request.model';

@Component({
  selector: 'app-order-form',
  standalone: true,
  imports: [ReactiveFormsModule, DialogModule, InputTextModule, InputNumberModule, DatePickerModule, ButtonModule],
  templateUrl: './order-form.component.html',
  styleUrl: './order-form.component.scss'
})
export class OrderFormComponent implements OnInit, OnChanges {
  private readonly fb = inject(FormBuilder);

  @Input() visible = false;
  @Input() order: Order | null = null;
  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() save = new EventEmitter<OrderRequest>();

  form!: FormGroup;

  ngOnInit(): void {
    this.buildForm();
  }

  ngOnChanges(): void {
    if (!this.form) {
      return;
    }
    if (!this.order) {
      this.form.reset();
      return;
    }

    this.form.patchValue({
      itemName: this.order.itemName,
      totalPrice: this.order.totalPrice,
      orderedAt: new Date(this.order.orderedAt)
    });
  }

  onSubmit(): void {
    if (!this.form.valid) {
      return;
    }

    const value = this.form.value;
    this.save.emit({
      itemName: value.itemName,
      totalPrice: value.totalPrice,
      orderedAt: value.orderedAt instanceof Date ? value.orderedAt.toISOString() : value.orderedAt
    });
    this.visible = false;
    this.visibleChange.emit(false);
  }

  onCancel(): void {
    this.visible = false;
    this.visibleChange.emit(false);
  }

  private buildForm(): void {
    this.form = this.fb.group({
      itemName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(60)]],
      totalPrice: [null, [Validators.required, Validators.min(0.01)]],
      orderedAt: [null, Validators.required]
    });
  }
}
