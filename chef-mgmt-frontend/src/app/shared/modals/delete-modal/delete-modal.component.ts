import { Component, EventEmitter, Input, Output } from '@angular/core';

import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { DELETE_MODAL_DEFAULTS } from './delete-modal.component.data';

@Component({
  selector: 'app-delete-modal',
  standalone: true,
  imports: [DialogModule, ButtonModule],
  templateUrl: './delete-modal.component.html'
})
export class DeleteModalComponent {
  @Input() visible = false;
  @Input() itemName = '';
  @Input() header = DELETE_MODAL_DEFAULTS.header;
  @Input() icon = DELETE_MODAL_DEFAULTS.icon;
  @Output() visibleChange = new EventEmitter<boolean>();
  @Output() confirm = new EventEmitter<void>();

  onConfirm(): void {
    this.confirm.emit();
    this.visible = false;
    this.visibleChange.emit(false);
  }

  onCancel(): void {
    this.visible = false;
    this.visibleChange.emit(false);
  }
}
