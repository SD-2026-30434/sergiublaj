import { inject, Injectable } from '@angular/core';
import { State, Action, StateContext, Selector } from '@ngxs/store';
import { tap, catchError } from 'rxjs/operators';
import { of } from 'rxjs';
import { Chef } from '../models/chef.model';
import { CollectionResponse } from '../../../shared/models/collection.model';
import { ChefService } from '../services/chef.service';
import { ToastService } from '../../../core/services/toast.service';
import { LoadChefs, LoadChef, CreateChef, UpdateChef, DeleteChef } from './chef.actions';

export interface ChefStateModel {
  chefs: CollectionResponse<Chef> | null;
  selectedChef: Chef | null;
  loading: boolean;
  error: string | null;
}

@State<ChefStateModel>({
  name: 'chef',
  defaults: {
    chefs: null,
    selectedChef: null,
    loading: false,
    error: null
  }
})
@Injectable()
export class ChefState {
  private readonly chefService = inject(ChefService);
  private readonly toastService = inject(ToastService);

  @Selector()
  static chefs(state: ChefStateModel): CollectionResponse<Chef> | null {
    return state.chefs;
  }

  @Selector()
  static selectedChef(state: ChefStateModel): Chef | null {
    return state.selectedChef;
  }

  @Selector()
  static loading(state: ChefStateModel): boolean {
    return state.loading;
  }

  @Action(LoadChefs)
  loadChefs({ patchState }: StateContext<ChefStateModel>, action: LoadChefs) {
    patchState({ loading: true });
    return this.chefService.getAll(action.filter).pipe(
      tap(chefs => patchState({ chefs, loading: false })),
      catchError(err => {
        patchState({ loading: false, error: err.error?.message });
        return of(null);
      })
    );
  }

  @Action(LoadChef)
  loadChef({ patchState }: StateContext<ChefStateModel>, action: LoadChef) {
    patchState({ loading: true, selectedChef: null });
    return this.chefService.getById(action.id).pipe(
      tap(chef => patchState({ selectedChef: chef, loading: false })),
      catchError(err => {
        patchState({ loading: false, error: err.error?.message });
        return of(null);
      })
    );
  }

  @Action(CreateChef)
  createChef(_: StateContext<ChefStateModel>, action: CreateChef) {
    return this.chefService.create(action.request).pipe(
      tap(() => this.toastService.showSuccess('Chef created')),
      catchError(() => of(null))
    );
  }

  @Action(UpdateChef)
  updateChef(_: StateContext<ChefStateModel>, action: UpdateChef) {
    return this.chefService.update(action.id, action.request).pipe(
      tap(() => this.toastService.showSuccess('Chef updated')),
      catchError(() => of(null))
    );
  }

  @Action(DeleteChef)
  deleteChef(_: StateContext<ChefStateModel>, action: DeleteChef) {
    return this.chefService.delete(action.id).pipe(
      tap(() => this.toastService.showSuccess('Chef deleted')),
      catchError(() => of(null))
    );
  }
}
