import { HttpParams } from '@angular/common/http';

export function buildQueryParams(filter: Record<string, any>): HttpParams {
  let params = new HttpParams();
  for (const [key, value] of Object.entries(filter)) {
    if (value != null && value !== '') {
      params = params.set(key, value);
    }
  }
  return params;
}
