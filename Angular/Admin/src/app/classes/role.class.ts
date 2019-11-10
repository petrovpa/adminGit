import { Mappable } from '@core/services/common';

export class Role extends Mappable {
  private _DESCRIPTION: string;
  private _PROJECTID: number;
  private _ROLEID: number;
  private _ROLENAME: string;
  private _ROLESYSNAME: string;
  private _ROLEVIEWNAME: string;
  private _ROLEDATESTART: string;
  private _ROLEDATEEND: string;

  get DESCRIPTION(): string {
    return this._DESCRIPTION;
  }

  set DESCRIPTION(value: string) {
    this._DESCRIPTION = value;
  }

  get PROJECTID(): number {
    return this._PROJECTID;
  }

  set PROJECTID(value: number) {
    this._PROJECTID = value;
  }

  get ROLEID(): number {
    return this._ROLEID;
  }

  set ROLEID(value: number) {
    this._ROLEID = +value;
  }

  get ROLENAME(): string {
    return this._ROLENAME;
  }

  set ROLENAME(value: string) {
    this._ROLENAME = value;
  }

  get ROLESYSNAME(): string {
    return this._ROLESYSNAME;
  }

  set ROLESYSNAME(value: string) {
    this._ROLESYSNAME = value;
  }

  get ROLEVIEWNAME(): string {
    return this._ROLEVIEWNAME;
  }

  set ROLEVIEWNAME(value: string) {
    this._ROLEVIEWNAME = value;
  }

  get ROLEDATESTART(): string {
    return this._ROLEDATESTART;
  }

  set ROLEDATESTART(value: string) {
    this._ROLEDATESTART = value;
  }

  get ROLEDATEEND(): string {
    return this._ROLEDATEEND;
  }

  set ROLEDATEEND(value: string) {
    this._ROLEDATEEND = value;
  }

  constructor(data: any = {}) {
    super();
    Object.assign(this, data);
  }
}
