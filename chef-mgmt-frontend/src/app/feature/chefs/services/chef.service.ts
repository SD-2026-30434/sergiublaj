import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_CONFIG } from '../../../core/config/api.config';
import { Chef } from '../models/chef.model';
import { ChefFilter } from '../models/chef-filter.model';
import { ChefRequest } from '../models/chef-request.model';
import { CollectionResponse } from '../../../shared/models/collection.model';
import { buildQueryParams } from '../../../core/utils/query-params.util';

@Injectable({ providedIn: 'root' })
export class ChefService {
  private readonly http = inject(HttpClient);

  getAll(filter: ChefFilter = {}): Observable<CollectionResponse<Chef>> {
    const params = buildQueryParams(filter);
    return this.http.get<CollectionResponse<Chef>>(`${ API_CONFIG.CHEFS_URL }/v1`, { params });
  }

  getById(id: string): Observable<Chef> {
    return this.http.get<Chef>(`${ API_CONFIG.CHEFS_URL }/v1/${ id }`);
  }

  create(request: ChefRequest): Observable<Chef> {
    return this.http.post<Chef>(`${ API_CONFIG.CHEFS_URL }/v1`, request);
  }

  update(id: string, request: ChefRequest): Observable<Chef> {
    return this.http.put<Chef>(`${ API_CONFIG.CHEFS_URL }/v1/${ id }`, request);
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${ API_CONFIG.CHEFS_URL }/v1/${ id }`);
  }
}
