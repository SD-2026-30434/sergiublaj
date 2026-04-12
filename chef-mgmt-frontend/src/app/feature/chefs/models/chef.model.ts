import { Order } from '../../orders/models/order.model';

export interface Chef {
  id: string;
  name: string;
  email: string;
  birthDate: string;
  numberOfStars: number;
  orders?: Order[];
}
