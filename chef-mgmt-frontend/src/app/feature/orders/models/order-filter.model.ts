export interface OrderFilter {
  itemName?: string;
  totalPrice?: number;
  chefId?: string;
  sortBy?: string;
  sortDirection?: string;
  pageNumber?: number;
  pageSize?: number;
}
