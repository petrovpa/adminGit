import { Mappable } from '@core/services/common';

export class AccountRequest extends Mappable {
  private _ADUSERLOGIN: string;
  private _ADUSERPRINCIPALNAME: string;
  private _USERTYPE: number;
  private _DEPARTMENTID: number;
  private _NOTINLIST: boolean;
  private _PARTICIPANTNOTINLIST: boolean;
  private _USERID: number;
  private _USERACCOUNTID: number;
  private _EMPLOYEEID: number;
  private _FIRSTNAME: string;
  private _MIDDLENAME: string;
  private _LASTNAME: string;
  private _PARTICIPANTNAME: string;
  private _PARTICIPANTEXTNAME: string;
  private _PARTICIPANTEXTID: string;
  private _PARTEMPLOYEEID: number;
  private _LOGIN: string;
  private _PASSWORD: string;
  private _PHONE1: string;
  private _EMAIL: string;
  private _ROLES: string;
  private _GROUPS: string;
  private _BLOCKED: boolean;
  private _ISCONCURRENT: number;
  private _OBJECTTYPE: number;
  private _STATUS: string;
  private _AUTHMETHOD: string;

  public get ADUSERLOGIN(): string {
    return this._ADUSERLOGIN;
  }

  public set ADUSERLOGIN(value: string) {
    this._ADUSERLOGIN = value;
  }

  public get ADUSERPRINCIPALNAME(): string {
    return this._ADUSERPRINCIPALNAME;
  }

  public set ADUSERPRINCIPALNAME(value: string) {
    this._ADUSERPRINCIPALNAME = value;
  }

  public get USERTYPE(): number {
    return this._USERTYPE;
  }

  public set USERTYPE(value: number) {
    this._USERTYPE = value;
  }

  public get DEPARTMENTID(): number {
    return this._DEPARTMENTID;
  }

  public set DEPARTMENTID(value: number) {
    this._DEPARTMENTID = value;
  }

  public get NOTINLIST(): boolean {
    return this._NOTINLIST;
  }

  public set NOTINLIST(value: boolean) {
    this._NOTINLIST = value;
  }

  public get PARTICIPANTNOTINLIST(): boolean {
    return this._PARTICIPANTNOTINLIST;
  }

  public set PARTICIPANTNOTINLIST(value: boolean) {
    this._PARTICIPANTNOTINLIST = value;
  }

  public get USERID(): number {
    return this._USERID;
  }

  public set USERID(value: number) {
    this._USERID = value;
  }

  public get EMPLOYEEID(): number {
    return this._EMPLOYEEID;
  }

  public set EMPLOYEEID(value: number) {
    this._EMPLOYEEID = value;
  }

  public get FIRSTNAME(): string {
    return this._FIRSTNAME;
  }

  public set FIRSTNAME(value: string) {
    this._FIRSTNAME = value;
  }

  public get MIDDLENAME(): string {
    return this._MIDDLENAME;
  }

  public set MIDDLENAME(value: string) {
    this._MIDDLENAME = value;
  }

  public get LASTNAME(): string {
    return this._LASTNAME;
  }

  public set LASTNAME(value: string) {
    this._LASTNAME = value;
  }

  public get PARTICIPANTNAME(): string {
    return this._PARTICIPANTNAME;
  }

  public set PARTICIPANTNAME(value: string) {
    this._PARTICIPANTNAME = value;
  }

  public get PARTICIPANTEXTNAME(): string {
    return this._PARTICIPANTEXTNAME;
  }

  public set PARTICIPANTEXTNAME(value: string) {
    this._PARTICIPANTEXTNAME = value;
  }

  public get PARTICIPANTEXTID(): string {
    return this._PARTICIPANTEXTID;
  }

  public set PARTICIPANTEXTID(value: string) {
    this._PARTICIPANTEXTID = value;
  }

  public get PARTEMPLOYEEID(): number {
    return this._PARTEMPLOYEEID;
  }

  public set PARTEMPLOYEEID(value: number) {
    this._PARTEMPLOYEEID = value;
  }

  public get LOGIN(): string {
    return this._LOGIN;
  }

  public set LOGIN(value: string) {
    this._LOGIN = value;
  }

  public get PASSWORD(): string {
    return this._PASSWORD;
  }

  public set PASSWORD(value: string) {
    this._PASSWORD = value;
  }

  public get PHONE1(): string {
    return this._PHONE1;
  }

  public set PHONE1(value: string) {
    this._PHONE1 = value;
  }

  public get EMAIL(): string {
    return this._EMAIL;
  }

  public set EMAIL(value: string) {
    this._EMAIL = value;
  }

  public get ROLES(): string {
    return this._ROLES;
  }

  public set ROLES(value: string) {
    this._ROLES = value;
  }

  public get GROUPS(): string {
    return this._GROUPS;
  }

  public set GROUPS(value: string) {
    this._GROUPS = value;
  }

  public get BLOCKED(): boolean {
    return this._BLOCKED;
  }

  public set BLOCKED(value: boolean) {
    this._BLOCKED = value;
  }

  public get ISCONCURRENT(): number {
    return this._ISCONCURRENT;
  }

  public set ISCONCURRENT(value: number) {
    this._ISCONCURRENT = value;
  }

  public get OBJECTTYPE(): number {
    return this._OBJECTTYPE;
  }

  public set OBJECTTYPE(value: number) {
    this._OBJECTTYPE = value;
  }

  get USERACCOUNTID(): number {
    return this._USERACCOUNTID;
  }

  set USERACCOUNTID(value: number) {
    this._USERACCOUNTID = value;
  }

  public get STATUS(): string {
    return this._STATUS;
  }

  public set STATUS(value: string) {
    this._STATUS = value;
  }


  public get AUTHMETHOD(): string {
    return this._AUTHMETHOD;
  }

  public set AUTHMETHOD(value: string) {
    this._AUTHMETHOD = value;
  }
}
