import { Injectable } from '@angular/core';
import { RestService } from '@core/services/common';
import { isUndefined } from '@core/shared/common';

@Injectable()
export class RightParamsService {

  constructor(private rest: RestService) {
  }

  /**
   * Получение списка в виде дерева всех подразделений
   */
  getDepartmentTree() {
    return this.rest.doCall('admrestgate/departmentTree', {});
  }

  /**
   * Полный список меню
   */
  getMenuTree() {
    return this.rest.formCall('menu/dsLoadFullMenu', {})
      .map((res) => {
          if (!res.Error) {
            return this.mapMenuTree(res.MENU);
          }

          return res;
      });
  }

  /**
   * Создание дерева меню
   * @param data
   * @returns {Array}
   */
  private mapMenuTree(data) {
    const result = [];

    // маппинг
    data.forEach((item) => {
      const menuItem = {
        id: item.MENUID,
        name: item.ITEMNAME
      };

      if (item.SUBITEMS) {
        menuItem['children'] = this.mapMenuTree(item.SUBITEMS);
      }

      result.push(menuItem);
    });

    return result;
  }
}
