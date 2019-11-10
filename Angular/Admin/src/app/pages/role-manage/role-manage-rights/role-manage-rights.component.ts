import { Component, Injector, OnInit, ViewChild } from '@angular/core';

import { ModalType } from '@core/components/modal-dialog';

import { Right } from '@app/classes/right.class';

import { ROUTE_ROLE_MANAGE_RIGHTS_SIMPLE, ROUTE_ROLE_MANAGE_RIGHTS_PROFILE } from '@app/shared/routes.const';
import { JOURNAL_BUTTON_REMOVE } from '@core/modules/journal';
import { RoleManageBaseComponent } from '@app/pages/role-manage/base/role-manage-base.component';

@Component({
  selector: 'app-role-manage-rights',
  templateUrl: './role-manage-rights.component.html',
  styleUrls: ['./role-manage-rights.component.scss']
})
export class RoleManageRightsComponent extends RoleManageBaseComponent implements OnInit {
  /**
   * Журнал
   */
  @ViewChild('journal') journal;

  /**
   * Кнопки журнала
   */
  protected journalButtons = [
    {
      title: 'Добавить простое право',
      styleClass: 'custom-button__add',
      iconClass: 'icon-add',
      sysName: 'add',
    },
    {
      title: 'Добавить профильное право',
      styleClass: 'custom-button__add-right-profile',
      iconClass: 'icon-plus-bold',
      sysName: 'add_right',
    },
    JOURNAL_BUTTON_REMOVE
  ];

  /**
   * Обработчик кнопок
   */
  protected journalButtonsAction = {
    add: () => {
      const roleId = this.session.getContext('roleId');

      this.routingState.navigate(ROUTE_ROLE_MANAGE_RIGHTS_SIMPLE, {
        roleId: roleId,
        rightObjectId: roleId,
        rightOwner: 'ROLE'
      });
    },
    add_right: () => {
      const roleId = this.session.getContext('roleId');

      this.routingState.navigate(ROUTE_ROLE_MANAGE_RIGHTS_PROFILE, {
        roleId: roleId,
        rightObjectId: roleId,
        rightOwner: 'ROLE'
      });
    },
    remove: (selected: any) => {
      const right: Right = selected[0];

      this.dialogService.confirm(ModalType.Warning, 'Вы действительно хотите удалить выбранное право у роли?')
        .subscribe(() => {
            this.rightRemove(right.RIGHTID);
          },
          (err: any) => {
          }
        );
    }
  };

  protected roleId: number;

  constructor(protected injector: Injector) {
    super(injector);
  }

  /**
   * Инициализация данных
   */
  protected initPage() {
    this.roleId = this.session.getContext('roleId');
  }

  /**
   * удаление права
   */
  protected rightRemove(rightId) {
    this.roleManage.rightRemove(this.role.ROLEID, rightId)
      .subscribe((res) => {
        this.journal.getData();
      });
  }

  /**
   * Просмотр права
   */
  protected viewRight(right: Right) {
    const params = {
      roleId: this.role.ROLEID,
      rightObjectId: this.role.ROLEID,
      rightOwner: 'ROLE',
      rightId: right.RIGHTID,
      rightName: right.RIGHTNAME,
      rightSysName: right.RIGHTSYSNAME,
    };

    if (right.RIGHTTYPE == 1) {
      this.routingState.navigate(ROUTE_ROLE_MANAGE_RIGHTS_SIMPLE, params);
    } else {
      this.routingState.navigate(ROUTE_ROLE_MANAGE_RIGHTS_PROFILE, params);
    }
  }
}
