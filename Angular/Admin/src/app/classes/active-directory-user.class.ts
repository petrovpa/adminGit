export class ActiveDirectoryUser {
  // фамилия пользователя
  LASTNAME: string;
  // имя пользователя
  FIRSTNAME: string;
  // полное имя пользователя
  FULLNAME: string;
  // главный логин пользователя
  ADUSERPRINCIPALNAME: string;
  // логин пользователя
  ADUSERLOGIN: string;
  // требуются ли возвращать заблокированых пользователей
  ISNEEDBLOCKED: boolean;

  constructor(data: any) {
    Object.assign(this, data);
  }
}
