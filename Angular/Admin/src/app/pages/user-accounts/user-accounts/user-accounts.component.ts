import { Component, ViewChild } from '@angular/core';

import { User } from '@app/classes/user.class';
import { UserAccountsService } from '@app/pages/user-accounts-edit/services/user-accounts.service';
import { ROUTE_USER_ACCOUNTS_EDIT } from '@app/shared/routes.const';
import { JOURNAL_BUTTON_ADD } from '@core/modules/journal';
import { RoutingStateService } from '@core/services/common';

@Component({
  templateUrl: './user-accounts.component.html',
  styleUrls: ['./user-accounts.component.scss'],
  selector: 'user-accounts-journal',
})
export class UserAccountsComponent {

  /**
   * Журнал
   */
  @ViewChild('journal') journal;

  /**
   * Кнопки журнала
   */
  protected journalButtons = [
    JOURNAL_BUTTON_ADD,
    {
      title: 'Блокировать',
      styleClass: 'custom-button__block',
      iconClass: 'icon-block',
      sysName: 'block',
      disabled: (selected: any) => !(selected && selected[0] && selected[0].STATUS === 'ACTIVE')
    },
    {
      title: 'Разблокировать',
      styleClass: 'custom-button__unblock',
      iconClass: 'icon-block-outline',
      sysName: 'unblock',
      disabled: (selected: any) => {
        return !(selected && selected[0] && selected[0].STATUS === 'BLOCKED');
      }
    }
  ];

  /**
   * Обработчик кнопок
   */
  protected journalButtonsAction = {
    add: () => {
      this.routingState.navigate(ROUTE_USER_ACCOUNTS_EDIT);
    },
    block: (selected: any) => {
      let user: User;
      user = selected && selected[0];
      if (user) {
        const userAccountId = user.USERACCOUNTID;
        const userId = user.USERID;
        const status = 'BLOCKED';
        this.userAccountsService.updateAccountStatus(userAccountId, userId, status).subscribe((res: any) => {
          this.journal.getData();
        });
      }
    },
    unblock: (selected: any) => {
      let user: User;
      user = selected && selected[0];
      if (user) {
        const userAccountId = user.USERACCOUNTID;
        const userId = user.USERID;
        const status = 'ACTIVE';
        this.userAccountsService.updateAccountStatus(userAccountId, userId, status).subscribe((res: any) => {
          this.journal.getData();
        });
      }
    }
  };

  constructor(private routingState: RoutingStateService,
              private userAccountsService: UserAccountsService) {
  }

  /**
   * Переход на форму редактирования данных пользователя
   * @param user {User} - пользователь для редактирования
   */
  openUserAccount(user?: User) {
    if (user) {
      this.routingState.navigate(ROUTE_USER_ACCOUNTS_EDIT, {
        userAccountId: user.USERACCOUNTID,
        userId: user.USERID
      });
    }
  }

}
