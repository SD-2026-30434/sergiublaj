import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { SortEvent } from 'primeng/api';
import { APP_CONFIG } from '../../../core/config/app.config';
import { SortDirection } from '../../../core/models/sort-direction.enum';

@Component({ template: '' })
export abstract class BaseListComponent implements OnInit {
  protected readonly router = inject(Router);
  protected readonly route = inject(ActivatedRoute);
  private readonly destroyRef = inject(DestroyRef);

  search = '';
  sortField = '';
  sortOrder = 1;
  page = 0;
  rows = APP_CONFIG.pageSize;

  ngOnInit(): void {
    this.route.queryParams
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((params) => {
        this.readBaseParams(params);
        this.readCustomParams(params);
        this.loadData();
      });
  }

  onSort(event: SortEvent): void {
    this.updateQueryParams({
      sortField: event.field,
      sortOrder: event.order === 1 ? SortDirection.ASC : SortDirection.DESC
    });
  }

  onPage(event: any): void {
    this.updateQueryParams({
      page: event.first / event.rows,
      rows: event.rows
    });
  }

  onSearch(value: string): void {
    this.updateQueryParams({ search: value || null, page: 0 });
  }

  updateQueryParams(params: Record<string, any>): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: params,
      queryParamsHandling: 'merge'
    }).then();
  }

  protected readCustomParams(_params: Params): void {}

  protected abstract loadData(): void;

  private readBaseParams(params: Params): void {
    this.search = params['search'] || '';
    this.sortField = params['sortField'] || '';
    this.sortOrder = params['sortOrder'] === SortDirection.DESC ? -1 : 1;
    this.page = Number(params['page']) || 0;
    this.rows = Number(params['rows']) || 10;
  }
}
