export interface Order {
  id: string;
  itemName: string;
  totalPrice: number;
  orderedAt: string;
  chefId: string;
  chefName?: string;
}
