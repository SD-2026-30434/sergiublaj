import { ChefFilter } from '../models/chef-filter.model';
import { ChefRequest } from '../models/chef-request.model';

export class LoadChefs {
  static readonly type = '[Chef] Load Chefs';
  constructor(public filter: ChefFilter = {}) {}
}

export class LoadChef {
  static readonly type = '[Chef] Load Chef';
  constructor(public id: string) {}
}

export class CreateChef {
  static readonly type = '[Chef] Create Chef';
  constructor(public request: ChefRequest) {}
}

export class UpdateChef {
  static readonly type = '[Chef] Update Chef';
  constructor(public id: string, public request: ChefRequest) {}
}

export class DeleteChef {
  static readonly type = '[Chef] Delete Chef';
  constructor(public id: string) {}
}
