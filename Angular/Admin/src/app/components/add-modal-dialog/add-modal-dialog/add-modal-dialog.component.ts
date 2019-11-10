import { Component, OnInit, Input, ViewChild } from '@angular/core';
import * as _ from 'lodash';

import { ModalDialogComponent } from '@core/components/modal-dialog';
import { AddModalDialogOptions } from '../classes/add-modal-dailog-options.class';

@Component({
  selector: 'add-modal-dialog',
  templateUrl: './add-modal-dialog.component.html',
  styleUrls: ['./add-modal-dialog.component.scss']
})

export class AddModalDialogComponent implements OnInit {
  @ViewChild('dialog') dialog: ModalDialogComponent;
  @Input() options: AddModalDialogOptions = <AddModalDialogOptions> {};

  protected selectedItemModel;

  private _defaultOptionsMap = {
    modalDialogTitle: 'Добавить'
  };

  constructor() {
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

  ngOnInit() {
    _.forIn(this._defaultOptionsMap, this.initOptionByDefault);
  }
}
