import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import {
  ROUTE_ROLE_MANAGE_MAIN, ROUTE_ROLE_MANAGE_LIST, ROUTE_ROLE_MANAGE_RIGHTS, ROUTE_ROLE_MANAGE_RIGHTS_PROFILE,
  ROUTE_ROLE_MANAGE_RIGHTS_SIMPLE
} from '@app/shared/routes.const';

import { RoleManageComponent } from './role-manage/role-manage.component';
import { RoleManageEditComponent } from './role-manage-edit/role-manage-edit.component';
import { RoleManageRightsComponent } from './role-manage-rights/role-manage-rights.component';
import { RoleManageEditGuard } from './role-manage-edit.guards';
import { ManageRightProfileComponent, ManageRightSimpleComponent } from '@app/modules/manage-right';

const ROUTES_ROLE_MANAGE: Routes = [
  {
    path: '',
    component: RoleManageComponent,
    data: {
      title: 'Роли'
    }
  },
  {
    path: 'main',
    component: RoleManageEditComponent,
    data: {
      title: 'Роли. Общая информация',
      next: ROUTE_ROLE_MANAGE_RIGHTS,
      cancel: ROUTE_ROLE_MANAGE_LIST,
      menuClose: ['role-manage']
    }
  },
  {
    path: 'rights',
    component: RoleManageRightsComponent,
    data: {
      title: 'Роли. Права',
      back: ROUTE_ROLE_MANAGE_MAIN,
      menuClose: ['role-manage']
    },
    canActivate: [RoleManageEditGuard]
  },
  {
    path: 'rights/simple',
    component: ManageRightSimpleComponent,
    data: {
      title: 'Роли. Права. Простое право',
      menuTitle: 'Роли. Права',
      menu: [
        { title: 'Простое право', path: ROUTE_ROLE_MANAGE_RIGHTS_SIMPLE },
      ],
      cancel: ROUTE_ROLE_MANAGE_RIGHTS,
      menuClose: ['role-manage', 'rights']
    },
    canActivate: [RoleManageEditGuard]
  },
  {
    path: 'rights/profile',
    component: ManageRightProfileComponent,
    data: {
      title: 'Роли. Права. Профильное право',
      menuTitle: 'Роли. Права',
      menu: [
        { title: 'Профильное право', path: ROUTE_ROLE_MANAGE_RIGHTS_PROFILE },
      ],
      cancel: ROUTE_ROLE_MANAGE_RIGHTS,
      menuClose: ['role-manage', 'rights']
    },
    canActivate: [RoleManageEditGuard]
  },
];

@NgModule({
  imports: [RouterModule.forChild(ROUTES_ROLE_MANAGE)],
  exports: [RouterModule]
})
export class RoleManageRoutingModule {
}
