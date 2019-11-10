import { Component, OnInit, ViewChild, Input } from '@angular/core';
import * as _ from 'lodash';

import { GroupModalDialogOptions } from '../classes/modal-dialog-group-options.class';
import { ModalDialogComponent } from '@core/components/modal-dialog';
import { AngularTree } from '@app/classes/angular-tree.class';
import { RestService } from '@core/services/common';

@Component({
  selector: 'add-modal-dialog-group',
  templateUrl: './modal-dialog-group.component.html',
  styleUrls: ['./modal-dialog-group.component.scss']
})
export class ModalDialogGroupComponent implements OnInit {

  @ViewChild('dialog') dialog: ModalDialogComponent;
  @Input() options: GroupModalDialogOptions = new GroupModalDialogOptions();

  /**
   * Группы
   */
  private _group: any;
  public get group(): any {
    return this._group;
  }

  public set group(value: any) {
    this._group = value;
  }

  /**
   * дерево
   */
  private _groupTree: AngularTree[];
  public get groupTree(): AngularTree[] {
    return this._groupTree;
  }

  public set groupTree(value: AngularTree[]) {
    this._groupTree = value;
  }

  private _defaultOptionsMap = {
    modalDialogTitle: 'Добавить'
  };

  constructor(private rest: RestService) {
  }

  ngOnInit() {
    this.getGroupsTree();
    _.forIn(this._defaultOptionsMap, this.initOptionByDefault);
  }

  /**
   * Инициализация опции по умолчанию
   * @param value {any} - значение по умолчанию
   * @param option {string} - имя опции
   */
  private initOptionByDefault = (value: any, option: string) => {
    if (_.isUndefined(this.options[option])) {
      this.options[option] = value;
    }
  };

  /**
   * Получение дерева групп
   */
  private getGroupsTree() {
    // TODO: вынести в настройки
    return this.rest.doCall('admrestgate/userGroupsTree', {})
      .subscribe(groupTree => this.groupTree = AngularTree.createNewArray(groupTree));
  }

  /**
   * Обработчик выбора группы
   * @param group {any} - выбранная группа
   */
  private selectGroup(group: any) {
    this.group = group;
  }

}
