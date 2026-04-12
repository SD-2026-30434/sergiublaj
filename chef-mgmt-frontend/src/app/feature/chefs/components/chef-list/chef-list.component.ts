import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AppRoutes } from '../../../../core/models/app-routes.enum';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { ToolbarModule } from 'primeng/toolbar';
import { InputTextModule } from 'primeng/inputtext';
import { TagModule } from 'primeng/tag';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { ToastService } from '../../../../core/services/toast.service';
import { Chef } from '../../models/chef.model';
import { ChefRequest } from '../../models/chef-request.model';
import { ChefFilter } from '../../models/chef-filter.model';
import { ChefService } from '../../services/chef.service';
import { CollectionResponse } from '../../../../shared/models/collection.model';
import { ChefFormComponent } from '../../modals/chef-form/chef-form.component';
import { DeleteModalComponent } from '../../../../shared/modals/delete-modal/delete-modal.component';
import { BaseListComponent } from '../../../../shared/components/base-list/base-list.component';

@Component({
  selector: 'app-chef-list',
  standalone: true,
  imports: [
    CommonModule, TableModule, ButtonModule, ToolbarModule,
    InputTextModule, TagModule, IconFieldModule, InputIconModule,
    ChefFormComponent, DeleteModalComponent
  ],
  templateUrl: './chef-list.component.html',
  styleUrl: './chef-list.component.scss'
})
export class ChefListComponent extends BaseListComponent {
  private readonly chefService = inject(ChefService);
  private readonly toastService = inject(ToastService);

  result!: CollectionResponse<Chef>;
  chefs: Chef[] = [];
  formVisible = false;
  selectedChef: Chef | null = null;
  deleteModalVisible = false;
  chefToDelete: Chef | null = null;

  openNewChef(): void {
    this.selectedChef = null;
    this.formVisible = true;
  }

  editChef(chef: Chef): void {
    this.selectedChef = { ...chef };
    this.formVisible = true;
  }

  viewChef(chef: Chef): void {
    this.router.navigate([AppRoutes.CHEFS, chef.id]).then();
  }

  onSave(request: ChefRequest): void {
    if (this.selectedChef) {
      this.chefService.update(this.selectedChef.id, request);
      this.toastService.showSuccess('Chef updated');
    } else {
      this.chefService.create(request);
      this.toastService.showSuccess('Chef created');
    }
    this.loadData();
  }

  openDeleteModal(chef: Chef): void {
    this.chefToDelete = chef;
    this.deleteModalVisible = true;
  }

  onDeleteConfirm(): void {
    if (!this.chefToDelete) {
      return;
    }
    this.chefService.delete(this.chefToDelete.id);
    this.toastService.showSuccess(`${ this.chefToDelete.name } removed`);
    this.chefToDelete = null;
    this.loadData();
  }

  protected override loadData(): void {
    const filter: ChefFilter = {
      name: this.search || undefined,
      sortBy: this.sortField || undefined,
      sortDirection: this.sortOrder === -1 ? 'desc' : 'asc',
      pageNumber: this.page,
      pageSize: this.rows
    };
    this.result = this.chefService.getAll(filter);
    this.chefs = this.result.elements;
  }
}
