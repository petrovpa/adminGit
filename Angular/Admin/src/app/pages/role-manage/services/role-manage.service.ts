import { Injectable } from '@angular/core';
import { RestService } from '@core/services/common';
import { Role } from '@app/classes/role.class';
import { number } from '@core/directives/validators/src/number';

@Injectable()
export class RoleManageService {
  /**
   * Текущая роль
   */
  public role: Role;
  /**
   * Флаг создание новой роли
   * @type {boolean}
   */
  public isNewRecord: boolean = false;

  constructor(private rest: RestService) {
  }

  /**
   * Загрузка роли
   */
  roleLoad(roleId: number) {
    return this.rest.doCall('admrestgate/loadRoleById', {
      ROLEID: roleId
    }).map((res) => {
      if (!res.Error) {
        this.role = new Role(res);

        return this.role;
      }

      return res;
    });
  }

  /**
   * Создание/обновление роли
   */
  roleSave(role: Role) {
    return this.rest.doCall('admrestgate/saveRole', role);
  }

  /**
   * Удаление роли
   */
  roleRemove(roleId: number) {
    return this.rest.doCall('admrestgate/deleteRoleById', {
      ROLEID: roleId
    });
  }

  /**
   * Удаление права
   * @param roleId
   * @param {number} rightId
   * @returns {any | any}
   */
  rightRemove(roleId: number, rightId: number) {
    return this.rest.doCall('rights/removeObjectRights', {
      OBJECTID: roleId,
      RIGHTID: rightId,
      RIGHTOWNER: 'ROLE'
    });
  }
}
