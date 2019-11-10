import { CoreUseraccount } from './core-useraccount.class';

export class User extends CoreUseraccount {
  ACCSTATUS: string;
  DEPARTMENTID: number;
  DEPARTMENTSHORTNAME: string;
  EMAIL: string;
  EMPLOYEEID: number;
  PHONE1: string;
  PHONE2: string;
  POSITION: string;
  USERNAME: string;
  FIRSTNAME: string;
  LASTNAME: string;
  MIDDLENAME: string;
  RETPASSWORD: string;
  USERTYPE: string;

  get fullname() {
    const name = [];

    if (this.LASTNAME) {
      name.push(this.LASTNAME);
    }

    if (this.FIRSTNAME) {
      name.push(this.FIRSTNAME);
    }

    if (this.MIDDLENAME) {
      name.push(this.MIDDLENAME);
    }

    return name.join(' ');
  }

  constructor(data: any) {
    super();
    Object.assign(this, data);
  }
}
