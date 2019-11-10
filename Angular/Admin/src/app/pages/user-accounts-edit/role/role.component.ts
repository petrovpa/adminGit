import { Component, OnInit, ViewChild, Injector } from '@angular/core';

import { ModalType } from '@core/components/modal-dialog';
import * as _ from 'lodash';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/first';
import 'rxjs/add/observable/forkJoin';

import { AddModalDialogOptions } from '@app/components/add-modal-dialog/classes/add-modal-dailog-options.class';
import { Role } from '@app/classes/role.class';
import { ROUTE_USER_ACCOUNTS } from '@app/shared/routes.const';
import { UserBaseTableComponent } from '../base/user-base-table.component';

@Component({
  selector: 'app-user-account-role',
  templateUrl: './role.component.html',
  styleUrls: ['./role.component.scss']
})
export class UserRoleComponent extends UserBaseTableComponent implements OnInit {
  /**
   * Журнал
   */
  @ViewChild('roleTable') roleTable;

  // признак загрузки списка ролей пользователя
  protected isRoleLoading: boolean;
  // идентификатор выбранной роли в модальном окне добавления роли
  protected selectedRoleId: number;
  // список доступных ролей пользователя
  protected roleAvailableList: Role[];
  // список ролей пользователя
  protected roleList: Role[];
  // список всех ролей
  protected roleListFull: Role[];
  // кнопки таблицы ролей пользователя
  protected roleTableButtons: any[] = _.cloneDeep(this.tableButtons);

  // опции модального окна добавления роли
  private roleModalDialogOptions: AddModalDialogOptions = <AddModalDialogOptions>{
    modalDialogTitle: 'Добавить в роль',
    labelTitle: 'Роль',
    inputSelectNameField: 'ROLEVIEWNAME',
    inputSelectSysnameField: 'ROLEID'
  };

  constructor(protected injector: Injector) {
    super(injector);
  }

  /**
   * Инициализация
   */
  ngOnInit() {
    this.initTable();

    // загружаем аккаунт
    if (this.userAccountId) {
      this.getUserAccount();

      this.getUserRoleListByUserAccountId(this.userAccountId);
    } else {
      this.routingState.navigate(ROUTE_USER_ACCOUNTS);
    }
  }

  /**
   * Инициализация таблиц Роли
   */
  private initTable() {
    this.roleTableButtons[0].onClick = this.onClickAddRoleToUser;
    this.roleTableButtons[1].onClick = this.removeUserRole;
    this.roleModalDialogOptions.onClickAddButton = this.addRoleToUser;
  }

  /**
   * Получение списка ролей пользователя
   * @param userAccountId {number} - идентификатор аккаунта пользователя
   */
  getUserRoleListByUserAccountId(userAccountId: number) {
    this.userAccountsService.getUserRoleList(userAccountId)
      .subscribe((roleList: Role[]) => this.roleList = roleList);
  }

  /**
   * Получение списка всех ролей
   */
  getRoleList(userAccountId: number) {
    return this.userAccountsService.getRoleList(userAccountId)
      .map((roleListFull: Role[]) => this.roleListFull = roleListFull.map((role: Role) => {
        role.ROLEVIEWNAME = `${role.ROLENAME} (${role.ROLESYSNAME})`;
        return role;
      }))
      .toPromise();
  }

  /**
   * Фильтр ролей
   * @returns {Role[]}
   */
  private filterRoles() {
    return this.roleListFull.filter((item) => {
      return this.roleList.findIndex((roleItem) => {
        return roleItem.ROLEID === item.ROLEID;
      }) === -1;
    });
  }

  /**
   * Добавление роли пользователю
   * @param selectedRoleId {number} - идентификатор добавляемой роли
   */
  addRoleToUser = (selectedRoleId: number) => {
    const findRoleId = this.roleList.findIndex((item) => {
      return item.ROLEID == selectedRoleId;
    });

    if (findRoleId === -1) {
      this.userAccountsService.addRoleToUser(this.userAccountId, selectedRoleId)
        .subscribe(() => this.getUserRoleListByUserAccountId(this.userAccountId), this.addDialog.dialog.close());
    } else {
      this.dialogService.alert(ModalType.Warning, 'Данная роль уже добавлена.');
    }
  };

  /**
   * Обработчик события нажатия на кнопку добавления роли пользователю
   */
  onClickAddRoleToUser = async () => {
    this.addModalDialogOptions = this.roleModalDialogOptions;
    await this.getRoleList(this.userAccountId);
    if (this.roleListFull.length > 0) {
      const roleListFilter = this.filterRoles();

      this.roleModalDialogOptions.inputSelectItems = roleListFilter;
      this.addDialog.dialog.show();
    } else {
      this.dialogService.alert(ModalType.Error, 'Отсутствуют доступные для добавления роли.');
    }
  };

  /**
   * Удаление роли пользователя
   * @param selectedList {any[]} - список выбранных ролей
   */
  removeUserRole = (selectedList: any[]) => {
    if (_.isArray(selectedList)) {
      try {
        // FIXME: доработать формирование списка потоков
        // вероятно в случае когда сервис не возвращает result (только status)
        // this.userAccountsService.removeUserRole возвращает reject вместо resolve
        const roleList$: Array<Observable<any>> = selectedList.map(
          (role: any) => this.userAccountsService.removeUserRole(this.userAccountId, role.ROLEID)
        );
        Observable.forkJoin(roleList$)
          .first()
          .subscribe(() => this.getUserRoleListByUserAccountId(this.userAccountId));
      } catch (e) {
        console.error('error in UserAccountsEditComponent removeUserRole', e);
      }
    }
  };

  /**
   * Удаление роли пользователя
   * @param selectedList {any[]} - список выбранных ролей
   */
  // removeUserRole = async (selectedList: any[]) => {
  //   if (_.isArray(selectedList)) {
  //     try {
  //       await selectedList.forEach(async (role: any) => {
  //         await this.userAccountsService.removeUserRole(this.userAccountId, role.ROLEID).toPromise();
  //       });
  //       this.getUserRoleListByUserAccountId(this.userAccountId);
  //     } catch (e) {
  //       console.error('error in UserAccountsEditComponent removeUserRole', e);
  //     }
  //   }
  // }
}
