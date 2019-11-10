import { Injectable } from '@angular/core';
import { RestService } from '@core/services/common';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import * as _ from 'lodash';
import { Logger } from '@frontend/logger';

@Injectable()
export class MenuService {
  /**
   * Все меню
   * @type {Array}
   */
  public allMenu = [];
  /**
   * Текущее меню
   * @type {Array}
   */
  public currentMenu = [];

  constructor(private rest: RestService,
              private logger: Logger) {
  }

  /**
   * Метод для загруззки меню
   */
  public loadB2BMenu() {
    return this.rest.formCall('menu/dsB2BMenuBrowseEx', {})
      .map(res => {
        if (!res.Error) {
          // мапим дерево
          this.allMenu = this.mapTree(res.MENU);

          this.logger.log('allMenu', this.allMenu);
          return this.allMenu;
        }

        return res;
      });

    // FIXME: for debug
    // return Observable.of({
    //   MENU: [
    //     { id: 1, title: 'Журнал пользователей', path: '/user-accounts', },
    //     { id: 2, title: 'Настройки паролей', path: '/password-settings', },
    //     { id: 3, title: 'Журнал ролей', path: '/role-manage', },
    //
    //     { id: 4, title: 'Общая информация', path: '/user-accounts/main', parentId: 1 },
    //     { id: 5, title: 'Связь с AD', path: '/user-accounts/active-directory', parentId: 1 },
    //     { id: 6, title: 'Роли', path: '/user-accounts/role', parentId: 1 },
    //     { id: 7, title: 'Группы', path: '/user-accounts/group', parentId: 1 },
    //     { id: 8, title: 'Профильные права', path: '/user-accounts/profile-right', parentId: 1 },
    //
    //     { id: 9, title: 'Общая информация', path: '/role-manage/main', parentId: 3 },
    //     { id: 10, title: 'Права', path: '/role-manage/rights', parentId: 3 }
    //   ],
    //   Error: null
    // }).map(res => {
    //   if (!res.Error) {
    //     this.allMenu = this.buildTree(res.MENU);
    //
    //     this.logger.log('allMenu', this.allMenu);
    //     return this.allMenu;
    //   }
    //
    //   return res;
    // });
  }

  /**
   * Преобразуем данные
   * @param data
   */
  private mapTree(data) {
    const result = [];

    // маппинг
    data.forEach((item) => {
      const menuItem = {
        id: item.MENUID,
        title: item.ITEMNAME,
        path: item.URL,
        level: item.LEVEL,
        styleClass: item.IMG
      };

      if (item.SUBITEMS) {
        menuItem['submenu'] = this.mapTree(item.SUBITEMS);
      }

      result.push(menuItem);
    });

    return result;
  }

  /**
   * Формируем дерево
   * @param data
   * @param parent
   */
  private buildTree(data, parent = null) {
    const idName = 'id';
    const parentName = 'parentId';
    const childrenName = 'submenu';

    const result = [];

    let children;

    if (!parent) {
      children = _.filter(data, (value) => {
        return !value[parentName];
      });
    } else {
      children = _.filter(data, (value) => {
        return value[parentName] === parent[idName];
      });
    }

    if (!_.isEmpty(children)) {
      _.each(children, (child) => {
        if (child != null) {
          result.push(child);

          const ownChildren = this.buildTree(data, child);
          if (!_.isEmpty(ownChildren)) {
            child.hasChild = true;
            child[childrenName] = ownChildren;
          } else {
            child.hasChild = false;
          }
        }
      });
    }

    return result;
  }
}
