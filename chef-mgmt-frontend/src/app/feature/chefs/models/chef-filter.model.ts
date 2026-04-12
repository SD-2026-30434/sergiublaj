import { SortDirection } from '../../../core/models/sort-direction.enum';

export interface ChefFilter {
  name?: string;
  email?: string;
  rating?: number;
  sortBy?: string;
  sortDirection?: SortDirection;
  pageNumber?: number;
  pageSize?: number;
}
