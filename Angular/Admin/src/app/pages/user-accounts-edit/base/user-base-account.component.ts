import { Component, Injector } from '@angular/core';

import { UserAccountsService } from '../services/user-accounts.service';
import { BaseFormComponent } from '@core/shared/base-form.component';
import { CacheService } from '@core/services/cache';
import { Router } from '@angular/router';
import { MenuService, CACHE_MENU } from '@core/components/sidebar-menu';
import { User } from '@app/classes/user.class';
import { ROUTE_USER_ACCOUNTS } from '@app/shared/routes.const';

export class UserBaseAccountComponent extends BaseFormComponent {
  /**
   * Идентификатор аккаунта
   */
  protected userAccountId: number;
  protected userId: number;
  /**
   * Сервисы
   */
  protected userAccountsService: UserAccountsService;
  protected cache: CacheService;
  protected menuService: MenuService;
  protected router: Router;

  // пользователь блокирован
  protected isBlock: boolean = false;

  // модель страницы редактирования данных пользователя
  protected user: User;

  /**
   * Кнопка продолжить
   */
  protected isNextSubMenu: boolean = false;

  constructor(protected injector: Injector) {
    super(injector);
    this.userAccountsService = this.injector.get(UserAccountsService);
    this.cache = this.injector.get(CacheService);
    this.menuService = this.injector.get(MenuService);
    this.router = this.injector.get(Router);

    // данные контекста
    this.userAccountId = this.session.getContext('userAccountId');
    this.userId = this.session.getContext('userId');

    this.user = this.userAccountsService.user;

    // проверяем доступность пунктов
    this.menuService.loadB2BMenu('menu/dsB2BMenuBrowseEx')
      .subscribe((res: any) => {
        if (!res.Error) {

          const findAccountMenu = res.find((item) => {
            return item.path === '/user-accounts';
          });

          const urlRoute = this.router.url.split('#');
          const url = urlRoute[0];
          // флаг того что вернулись назад
          const isBack = urlRoute[1] === 'isBack';

          let findSubMenu = -1;
          if (findAccountMenu && findAccountMenu.submenu) {
            findSubMenu = findAccountMenu.submenu.findIndex((item) => {
              return item.path === url;
            });
          }

          // определяем следущий роут
          let urlNext = this.getRouteData('next');
          if (urlNext) {
            urlNext = '/' + urlNext.join('/');
            const findUrlNext = findAccountMenu.submenu.findIndex((item) => {
              return item.path === urlNext;
            });

            this.isNextSubMenu = findUrlNext !== -1;
          }

          if (findSubMenu === -1) {
            if (isBack) {
              this.navigateBack();
            } else {
              this.navigateNext();
            }
          }
        }
      });
  }

  /**
   * Загрузка аккаунта
   */
  protected getUserAccount() {
    if (!this.user || !this.user.USERID || this.user.USERACCOUNTID !== this.userAccountId) {
      this.userAccountsService.getUserInfoByAccountId(this.userAccountId)
        .subscribe((userInfo) => {
          if (!userInfo.Error) {
            this.user = userInfo;
            this.isBlock = userInfo.STATUS === 'BLOCKED';
          }
        });
    } else {
      this.routingState.navigate(ROUTE_USER_ACCOUNTS);
    }
  }
}
