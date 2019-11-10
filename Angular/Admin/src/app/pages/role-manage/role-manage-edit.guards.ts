import { Inject, Injectable } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

import { RoutingStateService, SessionService } from '@core/services/common';

import { ROUTE_ROLE_MANAGE_LIST } from '@app/shared/routes.const';
import { RoleManageService } from './services/role-manage.service';

@Injectable()
export class RoleManageEditGuard implements CanActivate {
  constructor(private session: SessionService,
              private routingState: RoutingStateService,
              private roleManage: RoleManageService,
              @Inject(DOCUMENT) private document: any) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const roleId = this.session.getContext('roleId');

    if (!roleId) {
      // новая запись - просто блокируем преход,
      // иначе перенаправляем на список
      if (!this.roleManage.isNewRecord) {
        this.routingState.navigate(ROUTE_ROLE_MANAGE_LIST);
      }

      this.document.body.style.cursor = 'auto';

      return false;
    }

    return true;
  }
}
