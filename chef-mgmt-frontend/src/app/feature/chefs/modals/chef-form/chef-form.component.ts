import { Component, EventEmitter, inject, Input, OnChanges, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { DatePickerModule } from 'primeng/datepicker';
import { ButtonModule } from 'primeng/button';
import { Chef } from '../../models/chef.model';
import { ChefRequest } from '../../models/chef-request.model';

@Component({
  selector: 'app-chef-form',
  standalone: true,
  imports: [ReactiveFormsModule, DialogModule, InputTextModule, InputNumberModule, DatePickerModule, ButtonModule],
  templateUrl: './chef-form.component.html',
  styleUrl: './chef-form.component.scss'
})
export class ChefFormComponent implements OnInit, OnChanges {
  private readonly fb = inject(FormBuilder);

  @Input() visible = false;
  @Input() chef: Chef | null = null;
  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() save = new EventEmitter<ChefRequest>();

  form!: FormGroup;

  ngOnInit(): void {
    this.buildForm();
  }

  ngOnChanges(): void {
    if (!this.form) {
      return;
    }
    if (!this.chef) {
      this.form.reset({ rating: 0 });
      return;
    }

    this.form.patchValue({
      name: this.chef.name,
      email: this.chef.email,
      birthDate: new Date(this.chef.birthDate),
      rating: this.chef.numberOfStars
    });
  }

  onSubmit(): void {
    if (!this.form.valid) {
      return;
    }

    const value = this.form.value;
    this.save.emit({
      name: value.name,
      email: value.email,
      birthDate: value.birthDate instanceof Date ? value.birthDate.toISOString() : value.birthDate,
      rating: value.rating
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
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(30)]],
      email: ['', [Validators.required, Validators.email]],
      birthDate: [null, Validators.required],
      rating: [0, [Validators.required, Validators.min(0), Validators.max(5)]]
    });
  }
}
