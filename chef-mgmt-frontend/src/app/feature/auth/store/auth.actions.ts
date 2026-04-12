export class Login {
  static readonly type = '[Auth] Login';
  constructor(public email: string, public password: string) {}
}

export class LoginSuccess {
  static readonly type = '[Auth] Login Success';
}

export class Logout {
  static readonly type = '[Auth] Logout';
}

export class CheckSession {
  static readonly type = '[Auth] Check Session';
}
