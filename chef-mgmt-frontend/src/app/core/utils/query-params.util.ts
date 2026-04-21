import { HttpParams } from '@angular/common/http';

// Turns a plain filter object (e.g. { name: 'Ana', role: '', page: 1, city: null })
// into an HttpParams instance suitable for an HttpClient `params` option.
//
// null/undefined/'' values are dropped on purpose: the backend treats a missing key
// and an empty string differently (e.g. ?role= would filter for "no role" instead of
// "any role"). `0` and `false` are kept because they're meaningful filter values.
export const buildQueryParams = (filter: Record<string, any>): HttpParams =>
  Object.entries(filter)
    .filter(([, value]) => value != null && value !== '')
    .reduce((params, [key, value]) => params.set(key, value), new HttpParams());
