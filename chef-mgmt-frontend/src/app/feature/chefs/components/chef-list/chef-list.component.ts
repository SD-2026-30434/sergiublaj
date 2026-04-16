import { Component, inject } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { tap } from 'rxjs';
import { AppRoutes } from '../../../../core/models/app-routes.enum';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { ToolbarModule } from 'primeng/toolbar';
import { InputTextModule } from 'primeng/inputtext';
import { TagModule } from 'primeng/tag';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { Chef } from '../../models/chef.model';
import { ChefRequest } from '../../models/chef-request.model';
import { ChefFilter } from '../../models/chef-filter.model';
import { SortDirection } from '../../../../core/models/sort-direction.enum';
import { ChefFormComponent } from '../../modals/chef-form/chef-form.component';
import { DeleteModalComponent } from '../../../../shared/modals/delete-modal/delete-modal.component';
import { BaseListComponent } from '../../../../shared/components/base-list/base-list.component';
import { ChefService } from '../../services/chef.service';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-chef-list',
  standalone: true,
  imports: [
    DatePipe, DecimalPipe, TableModule, ButtonModule, ToolbarModule,
    InputTextModule, TagModule, IconFieldModule, InputIconModule,
    ChefFormComponent, DeleteModalComponent
  ],
  templateUrl: './chef-list.component.html'
})
export class ChefListComponent extends BaseListComponent {
  private readonly chefService = inject(ChefService);
  private readonly toast = inject(ToastService);

  totalElements = 0;
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
    this.router.navigate([`/${AppRoutes.CHEFS}`, chef.id]).then();
  }

  onSave(request: ChefRequest): void {
    const isUpdate = !!this.selectedChef;
    const chefFunction = isUpdate
      ? this.chefService.update(this.selectedChef!.id, request)
      : this.chefService.create(request);
    chefFunction.pipe(
      tap(() => {
        this.toast.showSuccess(isUpdate ? 'Chef updated' : 'Chef created');
        this.loadData();
      })
    ).subscribe();
  }

  openDeleteModal(chef: Chef): void {
    this.chefToDelete = chef;
    this.deleteModalVisible = true;
  }

  onDeleteConfirm(): void {
    if (!this.chefToDelete) {
      return;
    }
    this.chefService.delete(this.chefToDelete.id).pipe(
      tap(() => {
        this.toast.showSuccess('Chef deleted');
        this.chefToDelete = null;
        this.loadData();
      })
    ).subscribe();
  }

  protected override loadData(): void {
    const filter: ChefFilter = {
      name: this.search || undefined,
      sortBy: this.sortField || undefined,
      sortDirection: this.sortOrder === -1 ? SortDirection.DESC : SortDirection.ASC,
      pageNumber: this.page,
      pageSize: this.size
    };
    this.chefService.getAll(filter).pipe(
      tap(result => {
        this.totalElements = result.totalElements;
        this.chefs = result.elements;
      })
    ).subscribe();
  }
}
