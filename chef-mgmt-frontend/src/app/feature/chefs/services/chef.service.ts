import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Chef } from '../models/chef.model';
import { ChefFilter } from '../models/chef-filter.model';
import { ChefRequest } from '../models/chef-request.model';
import { CollectionResponse } from '../../../shared/models/collection.model';
import { buildQueryParams } from '../../../core/utils/query-params.util';

@Injectable({ providedIn: 'root' })
export class ChefService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/chefs/v1';

  getAll(filter: ChefFilter = {}): Observable<CollectionResponse<Chef>> {
    const params = buildQueryParams(filter);
    return this.http.get<CollectionResponse<Chef>>(this.baseUrl, { params });
  }

  getById(id: string): Observable<Chef> {
    return this.http.get<Chef>(`${this.baseUrl}/${id}`);
  }

  create(request: ChefRequest): Observable<Chef> {
    return this.http.post<Chef>(this.baseUrl, request);
  }

  update(id: string, request: ChefRequest): Observable<Chef> {
    return this.http.put<Chef>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
