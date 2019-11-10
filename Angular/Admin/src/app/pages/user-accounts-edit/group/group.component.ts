import { Component, OnInit, Injector } from '@angular/core';
import { Group } from '@app/classes/group.class';
import { NgForm } from '@angular/forms';

import * as _ from 'lodash';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/first';

import { User } from '@app/classes/user.class';
import { GroupModalDialogOptions } from '@app/components/modal-dialog-group/classes/modal-dialog-group-options.class';
import { ModalType } from '@core/components/modal-dialog';
import { ROUTE_USER_ACCOUNTS } from '@app/shared/routes.const';
import { UserBaseTableComponent } from '../base/user-base-table.component';

@Component({
  selector: 'app-user-account-group',
  templateUrl: './group.component.html',
  styleUrls: ['./group.component.scss']
})
export class UserGroupComponent extends UserBaseTableComponent implements OnInit {

  // признак загрузки списка групп пользователя
  private isGroupLoading: boolean = true;
  // список доступных групп пользователя
  private groupAvailableList: Group[];
  // список групп пользователя
  protected groupList: Group[];
  // список всех групп
  private groupListFull: Group[];
  // namespace группы
  private groupNamespace: any[] = [];
  // кнопки таблицы групп пользователя
  protected groupTableButtons: any[] = _.cloneDeep(this.tableButtons);

  // Обработчик события выбора записи из списка ролей пользователя
  protected onGroupSelect(ev) {
  }

  // опции модального окна добавления группы
  protected groupModalDialogOptions: GroupModalDialogOptions = new GroupModalDialogOptions(
    'Добавить в группу',
    'Группа'
  );

  constructor(protected injector: Injector) {
    super(injector);
  }

  ngOnInit() {
    this.initTables();

    // загружаем аккаунт
    this.userAccountId = this.session.getContext('userAccountId');
    if (this.userAccountId) {
      this.getUserAccount();
      this.getGroupListByAccountId(this.userAccountId).subscribe();
    } else {
      this.routingState.navigate(ROUTE_USER_ACCOUNTS);
    }

    // данные по пользователю
    // this.userId = this.session.getContext('userId');
    // if (!this.userId) {
    //   this.userAccountsService.getUserInfoByAccountId(this.userAccountId)
    //     .first()
    //     .subscribe((userInfo: User) => {
    //       this.userId = userInfo.USERID;
    //
    //       this.user = userInfo;
    //       this.isBlock = userInfo.STATUS === 'BLOCKED';
    //     });
    // } else {
    //
    // }
  }

  /**
   * Инициализация таблиц Группы
   */
  private initTables() {
    this.groupTableButtons[0].onClick = this.onClickAddUserToGroup;
    this.groupTableButtons[1].onClick = this.removeUserFromGroup;
    this.groupModalDialogOptions.onClickAddButton = this.onAddUserToGroupModal;
  }

  /**
   * Получение списка групп пользователя
   * @param userAccountId {number} - идентификатор аккаунта пользователя
   */
  private getGroupListByAccountId(userAccountId: number) {
    return this.userAccountsService.getGroupListByAccountId(userAccountId).map((groupList: Group[]) => {
      this.groupList = groupList;
      this.isGroupLoading = false;
    });
  }

  /**
   * Добавление пользователя в группу
   * @param groupId
   */
  addUserToGroup = (groupId: number) => {
    return this.userAccountsService.addUserToGroup(this.userId, groupId);
  };

  /**
   * Обработчик onClickAddButton для groupModalDialogOptions
   *
   * @param group {any} - выбранная группа
   * @param modalForm {NgForm} - объект модальной формы групп
   */
  onAddUserToGroupModal = (group: any, modalForm: NgForm) => {
    const groupId = group && +group.id;
    if (!_.isUndefined(groupId)) {
      if (groupId === 0) {
        this.dialogService.alert(ModalType.Error, 'Нельзя добавить пользователя в корневую группу.');
      } else {
        this.addUserToGroup(groupId).subscribe((res) => {
          this.getGroupListByAccountId(this.userAccountId)
            .subscribe(() => this.addDialog.dialog.close());
        });
      }
    }
  };

  /**
   * Обработчик события нажатия на кнопку добавления пользователя в группу (datatable)
   */
  onClickAddUserToGroup = async () => {
    this.addDialog.dialog.show();
  };

  /**
   * Удаление пользователя из группы
   * @param selectedList {any[]} - список выбранных групп
   */
  removeUserFromGroup = (selectedList: any[]) => {
    if (_.isArray(selectedList)) {
      try {
        const roleList$: Array<Observable<any>> =
          selectedList.map((group: any) => this.userAccountsService.removeUserFromGroup(this.userId, group.USERGROUPID));
        Observable.forkJoin(...roleList$)
          .first()
          .subscribe(() => {
            this.getGroupListByAccountId(this.userAccountId).subscribe();
          });
      } catch (e) {
        console.error('error in UserAccountsEditComponent UserFromGroup', e);
      }
    }
  };
}
