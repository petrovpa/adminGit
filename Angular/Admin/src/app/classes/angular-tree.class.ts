import { Helper } from '@app/classes/helper.class';
import { Mappable } from '@core/services/common';

export class AngularTree extends Mappable {

  private _id: number;
  public get id(): number {
    return this._id;
  }

  public set id(value: number) {
    this._id = value;
  }

  private _name: string;
  public get name(): string {
    return this._name;
  }

  public set name(value: string) {
    this._name = value;
  }

  private _children: AngularTree;
  public get children(): AngularTree {
    return this._children;
  }

  public set children(value: AngularTree) {
    this._children = value;
  }

  constructor(data: any) {
    super();
    Object.assign(this, data);
  }

  static createNewArray(data: any[]) {
    return Helper.recursiveToLowCaseObjectKey(data);
  }
}
