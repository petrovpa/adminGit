import { Injectable } from '@angular/core';
import isUndefined from 'lodash-es/isUndefined';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/of';

import { CreateAccountRequest } from '@app/classes/create-account-request.class';

import { RestCallExtParams, RestService } from '@core/services/common';
import { User } from '@app/classes/user.class';

@Injectable()
export class UserAccountsService {
  constructor(private rest: RestService) {
  }

  /**
   * кэш данных справочников
   * @type {{}}
   */
  private cacheData: any = {};

  public user: User = new User({});

  /**
   * Сохранение аккаунта пользователя
   * @param account
   * @returns {any | any}
   */
  saveAccount(account) {
    return this.rest.doCall('admrestgate/saveAccount', new CreateAccountRequest(account));
  }

  /**
   * Обновление статуса аккаунта пользователя
   *
   * @param {number} userAccountId
   * @param {number} userId -
   * @param {string} status - новый статус пользователя
   */
  updateAccountStatus(userAccountId: number, userId: number, status: string) {
    const params = {
      USERACCOUNTID: userAccountId,
      USERID: userId,
      STATUS: status
    };
    return this.rest.doCall('admrestgate/accountUpdateStatus', params);
  }

  /**
   * Получение информации о пользователе
   * @param userAccountId {number} - идентификатор аккаунта пользователя
   */
  getUserInfoByAccountId(userAccountId: number) {
    return this.rest.doCall('admrestgate/getUserInfoByAccountId', {
      USERACCOUNTID: userAccountId
    }).map((res) => {
      if (!res.Error) {
        this.user = new User(res);

        return this.user;
      }

      return res;
    });
  }

  /**
   * Получение списка департаментов
   */
  getDepartmentList() {
    if (this.cacheData.departmentsList) {
      return Observable.of(this.cacheData.departmentsList);
    }
    return this.rest.formCall('department/dsGetDepartmentList', {}).map(departmentsList => {
      if (departmentsList) {
        this.cacheData.departmentsList = departmentsList;
      }
      return departmentsList;
    });
  }

  /**
   * Получение списка в виде дерева всех подразделений
   * @param departmentId {number} - идентификатор департамента
   */
  getDepartmentTree(departmentId?: number) {
    const params = isUndefined(departmentId) ? {} : {
      DEPARTMENTID: departmentId
    };
    return this.rest.doCall('admrestgate/departmentTree', params);
  }

  /**
   * Смена пароля
   * @param userAccountId {number} - идентификатор аккаунта пользователя
   * @param newPassword {string} - новый пароль
   */
  changePassword(userAccountId, newPassword) {
    return this.rest.doCall('admrestgate/dsAdminUserChangePass', {
      NEWPASS: newPassword,
      USERACCOUNTID: userAccountId
    }, {
      hideLoader: true
    });
  }

  //#endregion

  //#region Роли пользователя
  /**
   * Получение списка доступных ролей по идентификатору аккаунта
   * @param userAccountId {number} - идентификатор аккаунта пользователя
   */
  getRoleAvailableListByAccountId(userAccountId: number) {
    return this.rest.doCall('admrestgate/getRoleAvailableListByAccountId', {
      USERACCOUNTID: userAccountId
    });
  }

  /**
   * Получение списка ролей пользователя
   * @param userAccountId {number} - идентификатор аккаунта пользователя
   */
  getUserRoleList(userAccountId: number) {
    return this.rest.doCall('admrestgate/roleListByAccountId', {
      USERACCOUNTID: userAccountId
    });
  }

  /**
   * Получение списка всех ролей
   * @param userAccountId {number} - идентификатор аккаунта пользователя
   */
  getRoleList(userAccountId: number) {
    return this.rest.doCall('admrestgate/roleListAbsentFromUser', {
      USERACCOUNTID: userAccountId
    });
  }

  /**
   * Добавление роли пользователю
   * @param userAccountId {number} - идентификатор аккаунта пользователя
   * @param roleId {number} - идентификатор роли
   */
  addRoleToUser(userAccountId: number, roleId: number) {
    return this.rest.doCall('admrestgate/roleUserAdd', {
      USERACCOUNTID: userAccountId,
      ROLEID: roleId
    });
  }

  /**
   * Удаление роли пользователя
   * @param userAccountId {number} - идентификатор аккаунта пользователя
   * @param roleId {number} - идентификатор роли
   */
  removeUserRole(userAccountId: number, roleId: number) {
    return this.rest.doCall('admrestgate/roleUserRemove', {
      USERACCOUNTID: userAccountId,
      ROLEID: roleId
    });
  }

  /**
   * Получение списка прав пользователя по идентификатору аккаунта
   * @param userAccountId {number} - идентификатор аккаунта пользователя
   */
  getRightListByAccountId(userAccountId: number) {
    return this.rest.doCall('rights/loadObjectRights', {
      RIGHTOWNER: 'ACCOUNT',
      OBJECTID: userAccountId
    });
  }

  /**
   * Удаление права пользователя
   * @param userAccountId
   * @param rightId {number} - идентификатор права
   */
  removeUserRight(userAccountId: number, rightId: number) {
    return this.rest.doCall('rights/removeObjectRights', {
      OBJECTID: userAccountId,
      RIGHTID: rightId,
      RIGHTOWNER: 'ACCOUNT'
    });
  }

  /**
   * Добавление пользователей в группу
   * @param userId {number} - идентификатор пользователя
   * @param groupId {number} - идентификатор группы для добавления пользователя
   */
  addUserToGroup(userId: number, groupId: number) {
    return this.rest.doCall('admrestgate/groupUserAdd', {
      USERID: userId,
      GROUPID: groupId
    });
  }

  /**
   * Удаление пользователя из группы
   * @param userId {number} - идентификатор аккаунта пользователя
   * @param groupId {number} - идентификатор группы
   */
  removeUserFromGroup(userId: number, groupId: number) {
    return this.rest.doCall('admrestgate/groupUserRemove', {
      USERID: userId,
      GROUPID: groupId
    });
  }

  /**
   * Получение списка груп пользователя по идентификатору аккаунта
   * @param userAccountId {number} - идентификатор аккаунта пользователя
   */
  getGroupListByAccountId(userAccountId: number) {
    return this.rest.doCall('admrestgate/getGroupListByAccountId', {
      USERACCOUNTID: userAccountId
    });
  }

  /**
   * Получение иерархической структуры всех групп
   */
  getGroupsTree() {
    return this.rest.doCall('admrestgate/userGroupsTree', {});
  }

  /**
   * Метод получания списка пользователей из active directory
   * @param userPrincipalNameSearch строка поиска ADUSERPRINCIPALNAME
   * @param callParams расширенные параметры вызова сервиса
   */
  getActiveDirectoryUserList(userPrincipalNameSearch: string, callParams?: RestCallExtParams) {
    return this.rest
      .doCall('active-directory/getActiveDirectoryUserList', {
        FULLNAME: `*${userPrincipalNameSearch}*`,
      }, callParams)
      .map((res) => {
        if (!res.Error) {
          res.forEach((item) => {
            item.ADUSERFULLNAME = item.ADUSERPRINCIPALNAME + ' (' + item.FULLNAME + ')';
          });

          return res;
        }

        return [];
      });
  }

  /**
   * Список типов пользователя
   * @returns {any | any}
   */
  getUserTypeList() {
    return this.rest.doCall('admrestgate/loadUserTypes', {});
  }
}
