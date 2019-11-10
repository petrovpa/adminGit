import { Mappable } from '@core/services/common';

export class Right extends Mappable {
  private _RIGHTID: number;
  private _RIGHTNAME: string;
  private _RIGHTSYSNAME: string;
  private _ROLEID: number;
  private _RIGHTTYPE: number;
  private _RIGHTTYPESTR: string;
  private _RIGHTOWNER: string;

  get RIGHTID(): number {
    return this._RIGHTID;
  }

  set RIGHTID(value: number) {
    this._RIGHTID = value;
  }

  get RIGHTNAME(): string {
    return this._RIGHTNAME;
  }

  set RIGHTNAME(value: string) {
    this._RIGHTNAME = value;
  }

  get RIGHTSYSNAME(): string {
    return this._RIGHTSYSNAME;
  }

  set RIGHTSYSNAME(value: string) {
    this._RIGHTSYSNAME = value;
  }

  get ROLEID(): number {
    return this._ROLEID;
  }

  set ROLEID(value: number) {
    this._ROLEID = value;
  }

  get RIGHTTYPE(): number {
    return this._RIGHTTYPE;
  }

  set RIGHTTYPE(value: number) {
    this._RIGHTTYPE = value;
  }

  get RIGHTTYPESTR(): string {
    return this._RIGHTTYPESTR;
  }

  set RIGHTTYPESTR(value: string) {
    this._RIGHTTYPESTR = value;
  }

  get RIGHTOWNER(): string {
    return this._RIGHTOWNER;
  }

  set RIGHTOWNER(value: string) {
    this._RIGHTOWNER = value;
  }

  constructor(data: any = {}) {
    super();
    Object.assign(this, data);
  }
}
