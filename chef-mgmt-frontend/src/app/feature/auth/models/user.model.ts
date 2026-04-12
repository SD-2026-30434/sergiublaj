import { Role } from '../../../core/models/role.enum';

export interface User {
  id: string;
  email: string;
  role: Role;
  chefId: string | null;
  chefName: string | null;
  chefBirthDate: string | null;
  chefRating: number;
}
