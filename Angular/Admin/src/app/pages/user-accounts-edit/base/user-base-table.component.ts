import { Component, Injector, ViewChild } from '@angular/core';

import { AddModalDialogComponent } from '@app/components/add-modal-dialog/add-modal-dialog/add-modal-dialog.component';
import { AddModalDialogOptions } from '@app/components/add-modal-dialog/classes/add-modal-dailog-options.class';
import { UserBaseAccountComponent } from './user-base-account.component';

@Component({
  selector: 'user-base-table',
  template: ''
})
export class UserBaseTableComponent extends UserBaseAccountComponent {

  @ViewChild('addDialog') protected addDialog: AddModalDialogComponent;
  protected addModalDialogOptions: AddModalDialogOptions = new AddModalDialogOptions();

  protected tableButtons: any[] = [
    { title: 'Добавить', styleClass: 'custom-button__add', iconClass: 'icon-add' },
    { title: 'Удалить', styleClass: 'custom-button__delete', iconClass: 'icon-delete', selectedRequired: true }
  ];

  constructor(protected injector: Injector) {
    super(injector);
  }
}
