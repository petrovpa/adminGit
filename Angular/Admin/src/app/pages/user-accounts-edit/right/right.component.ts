import { Component, Injector, OnInit, ViewChild } from '@angular/core';
import * as _ from 'lodash';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/first';

import { ModalType } from '@core/components/modal-dialog';
import { JOURNAL_BUTTON_REMOVE } from '@core/modules/journal';
import { ICustomButton } from '@core/components/custom-button-group';

import { Right } from '@app/classes/right.class';
import {
  ROUTE_USER_ACCOUNTS, ROUTE_USER_ACCOUNTS_RIGHT_PROFILE,
  ROUTE_USER_ACCOUNTS_RIGHT_SIMPLE
} from '@app/shared/routes.const';
import { UserBaseTableComponent } from '../base/user-base-table.component';

@Component({
  selector: 'app-user-account-right',
  templateUrl: './right.component.html',
  styleUrls: ['./right.component.scss']
})
export class UserRightComponent extends UserBaseTableComponent implements OnInit {
  /**
   * Журнал
   */
  @ViewChild('journal') journal;

  // признак загрузки списка групп пользователя
  protected isRightLoading: boolean;
  // список прав пользователя
  protected rightList: Right[];

  /**
   * Кнопки журнала
   */
  protected journalButtons: ICustomButton[] = [
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
      this.routingState.navigate(ROUTE_USER_ACCOUNTS_RIGHT_SIMPLE, {
        userAccountId: this.userAccountId,
        userId: this.userId,
        rightObjectId: this.userAccountId,
        rightOwner: 'ACCOUNT'
      });
    },
    add_right: () => {
      this.routingState.navigate(ROUTE_USER_ACCOUNTS_RIGHT_PROFILE, {
        userAccountId: this.userAccountId,
        userId: this.userId,
        rightObjectId: this.userAccountId,
        rightOwner: 'ACCOUNT'
      });
    },
    remove: (selected: any) => {
      const right: Right = selected[0];

      this.dialogService.confirm(ModalType.Warning, 'Вы действительно хотите удалить выбранное право?')
        .subscribe(() => {
            this.rightRemove(right.RIGHTID);
          },
          (err: any) => {
          }
        );
    }
  };

  constructor(protected injector: Injector) {
    super(injector);
  }

  ngOnInit() {
    // this.initCustomButtons();

    // загружаем аккаунт
    if (this.userAccountId) {
      this.getUserAccount();

      this.getRightListByAccountId(this.userAccountId).subscribe();
    } else {
      this.routingState.navigate(ROUTE_USER_ACCOUNTS);
    }
  }

  /**
   *  Инициализация кнопок
   */
  // initCustomButtons() {
  //   // пока что все едино, далее возможно разделить на группы кнопок
  //   const buttonGroups = this.journalButtons;
  //   // добавляем обработчики
  //   for (const button of buttonGroups) {
  //     if (this.journalButtonsAction.hasOwnProperty(button.sysName)) {
  //       button.onClick = this.journalButtonsAction[button.sysName];
  //     }
  //   }
  // }

  /**
   * Получение списка профильных прав пользователя
   * @param userAccountId {number} - идентификатор аккаунта пользователя
   */
  private getRightListByAccountId(userAccountId: number) {
    return this.userAccountsService.getRightListByAccountId(userAccountId)
      .map((rightList) => {
        rightList.forEach((item) => {
          // TODO: доработать сервис
          item.RIGHTTYPESTR = item.RIGHTTYPE === 1 ? 'Простое право' : 'Профильное право';
        });

        this.rightList = rightList;
      });
  }

  /**
   * удаление права
   */
  protected rightRemove(rightId) {
    this.userAccountsService.removeUserRight(this.userAccountId, rightId)
      .subscribe((res) => {
        this.journal.getData();
      });
  }

  /**
   * Просмотр права
   */
  protected viewRight(right) {
    const params = {
      userAccountId: this.userAccountId,
      userId: this.userId,
      rightObjectId: this.userAccountId,
      rightOwner: 'ACCOUNT',
      rightId: right.RIGHTID,
      rightName: right.RIGHTNAME,
      rightSysName: right.RIGHTSYSNAME,
    };

    if (right.RIGHTTYPE == 1) {
      this.routingState.navigate(ROUTE_USER_ACCOUNTS_RIGHT_SIMPLE, params);
    } else {
      this.routingState.navigate(ROUTE_USER_ACCOUNTS_RIGHT_PROFILE, params);
    }
  }
}
