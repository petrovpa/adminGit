import { Component, OnInit, ViewChild } from '@angular/core';
import { Logger } from '@frontend/logger';

import { RoutingStateService } from '@core/services/common';
import { ModalDialogService, ModalType } from '@core/components/modal-dialog';

import { Role } from '@app/classes/role.class';
import { RoleManageService } from '../services/role-manage.service';
import { ROUTE_ROLE_MANAGE_MAIN } from '@app/shared/routes.const';
import { JOURNAL_BUTTON_ADD, JOURNAL_BUTTON_REMOVE } from '@core/modules/journal';

@Component({
  selector: 'app-role-manage',
  templateUrl: './role-manage.component.html',
  styleUrls: ['./role-manage.component.scss']
})
export class RoleManageComponent implements OnInit {
  /**
   * Журнал
   */
  @ViewChild('journal') journal;

  /**
   * Кнопки журнала
   */
  protected journalButtons = [
    JOURNAL_BUTTON_ADD,
    JOURNAL_BUTTON_REMOVE
  ];

  /**
   * Обработчик кнопок
   */
  protected journalButtonsAction = {
    add: () => {
      this.routingState.navigate(ROUTE_ROLE_MANAGE_MAIN);
    },
    remove: (selected: any) => {
      const role: Role = selected[0];

      this.dialogService.confirm(ModalType.Warning, 'Вы действительно хотите удалить выбранную роль?')
        .subscribe(() => {
            this.roleRemove(role);
          },
          (err: any) => {
          }
        );
    }
  };

  constructor(private routingState: RoutingStateService,
              private dialogService: ModalDialogService,
              private roleManageService: RoleManageService,
              private logger: Logger) {
  }

  ngOnInit() {
  }

  /**
   * Переход на форму редактирования данных роли
   */
  protected viewRole(role?: Role) {
    this.routingState.navigate(ROUTE_ROLE_MANAGE_MAIN, {
      roleId: role.ROLEID
    });
  }

  /**
   * удаление роли
   */
  protected roleRemove(role: Role) {
    this.roleManageService.roleRemove(role.ROLEID)
      .subscribe((res) => {
        this.journal.getData();
      });
  }
}
