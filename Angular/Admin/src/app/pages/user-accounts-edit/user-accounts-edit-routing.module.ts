import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserGroupComponent } from './group/group.component';
import { UserRightComponent } from './right/right.component';
import { UserRoleComponent } from './role/role.component';
import { UserAccountEditGuard } from './user-account-edit.guard';
import { UserMainComponent } from './user-main/user-main.component';
import { ActiveDirectoryComponent } from './active-directory/active-directory.component';

import {
  ROUTE_USER_ACCOUNTS,
  ROUTE_USER_ACCOUNTS_ACTIVE_DIRECTORY, ROUTE_USER_ACCOUNTS_GROUP, ROUTE_USER_ACCOUNTS_MAIN, ROUTE_USER_ACCOUNTS_RIGHT,
  ROUTE_USER_ACCOUNTS_RIGHT_PROFILE, ROUTE_USER_ACCOUNTS_RIGHT_SIMPLE, ROUTE_USER_ACCOUNTS_ROLE
} from '@app/shared/routes.const';
import { ManageRightProfileComponent, ManageRightSimpleComponent } from '@app/modules/manage-right';

export const UACCOUNTS_EDIT_ROUTES: Routes = [
  {
    path: 'main',
    component: UserMainComponent,
    data: {
      title: 'Редактировать пользователя',
      next: ROUTE_USER_ACCOUNTS_ACTIVE_DIRECTORY,
      cancel: ROUTE_USER_ACCOUNTS,
      menuClose: ['user-accounts']
    }
  },
  {
    path: 'active-directory',
    component: ActiveDirectoryComponent,
    data: {
      back: ROUTE_USER_ACCOUNTS_MAIN,
      next: ROUTE_USER_ACCOUNTS_ROLE,
      menuClose: ['user-accounts']
    },
    canActivate: [UserAccountEditGuard]
  },
  {
    path: 'role',
    component: UserRoleComponent,
    data: {
      back: ROUTE_USER_ACCOUNTS_ACTIVE_DIRECTORY,
      next: ROUTE_USER_ACCOUNTS_GROUP,
      menuClose: ['user-accounts']
    },
    canActivate: [UserAccountEditGuard]
  },
  {
    path: 'group',
    component: UserGroupComponent,
    data: {
      back: ROUTE_USER_ACCOUNTS_ROLE,
      next: ROUTE_USER_ACCOUNTS_RIGHT,
      cancel: ROUTE_USER_ACCOUNTS,
      menuClose: ['user-accounts']
    },
    canActivate: [UserAccountEditGuard]
  },
  {
    path: 'rights',
    component: UserRightComponent,
    canActivate: [UserAccountEditGuard],
    data: {
      back: ROUTE_USER_ACCOUNTS_GROUP,
      cancel: ROUTE_USER_ACCOUNTS,
      menuClose: ['user-accounts']
    }
  },
  {
    path: 'rights/simple',
    component: ManageRightSimpleComponent,
    data: {
      title: 'Пользователь. Права. Простое право',
      menuTitle: 'Пользователь. Права',
      menu: [
        { title: 'Простое право', path: ROUTE_USER_ACCOUNTS_RIGHT_SIMPLE },
      ],
      cancel: ROUTE_USER_ACCOUNTS_RIGHT,
      menuClose: ROUTE_USER_ACCOUNTS_RIGHT
    },
    canActivate: [UserAccountEditGuard]
  },
  {
    path: 'rights/profile',
    component: ManageRightProfileComponent,
    data: {
      title: 'Пользователь. Права. Профильное право',
      menuTitle: 'Пользователь. Права',
      menu: [
        { title: 'Профильное право', path: ROUTE_USER_ACCOUNTS_RIGHT_PROFILE },
      ],
      cancel: ROUTE_USER_ACCOUNTS_RIGHT,
      menuClose: ROUTE_USER_ACCOUNTS_RIGHT
    },
    canActivate: [UserAccountEditGuard]
  },
];

@NgModule({
  imports: [RouterModule.forChild(UACCOUNTS_EDIT_ROUTES)],
  exports: [RouterModule]
})
export class UserAccountsEditRoutingModule {}
