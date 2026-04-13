import { SortDirection } from '../../../core/models/sort-direction.enum';

export interface OrderFilter {
  itemName?: string;
  totalPrice?: number;
  chefId?: string;
  sortBy?: string;
  sortDirection?: SortDirection;
  pageNumber?: number;
  pageSize?: number;
}
