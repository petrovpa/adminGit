import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LayoutMainComponent } from './layout-main/layout-main.component';
import { LayoutLoginComponent } from './layout-login/layout-login.component';
import { AuthGuard } from './guard/auth.guard';
import { WelcomeComponent } from '@app/pages/welcome/welcome.component';
//import {  AutoimportOptionsComponent } from '@app/pages/autoimport-options/autoimport-options/autoimport-options.component';


export const ADMIN_ROUTES: Routes = [
  // Авторизован
  {
    path: '',
    component: LayoutMainComponent,
    data: {
      title: 'Администрирование'
    },
    children: [
      {
        path: '',
        component: WelcomeComponent,
        data: {
          title: 'Добро пожаловать'
        },
      },
      {
        path: 'user-accounts',
        loadChildren: 'app/pages/user-accounts/user-accounts.module#UserAccountsModule',
        data: {
          title: 'Журнал пользователей',
        },
      },
      {
        path: 'autoimport-options',
        loadChildren: 'app/pages/autoimport-options/autoimport-options.module#AutoimportOptionsModule',
        data: {
          title: 'Параметры автозагрузки файлов',
        },
      },
      {
        path: 'role-manage',
        loadChildren: 'app/pages/role-manage/role-manage.module#RoleManageModule',
        data: {
          title: 'Роли',
        },
      },
      {
        path: 'password-settings',
        loadChildren: 'app/pages/password-settings/password-settings.module#PasswordSettingsModule',
        data: {
          title: 'Настройки паролей'
        },
      },
      {
        path: 'menu-manage',
        loadChildren: 'app/pages/menu-manage/menu-manage.module#MenuManageModule',
        data: {
          title: 'Журнал меню'
        },
      },
      { path: '', redirectTo: '', pathMatch: 'full' }
    ],
    canActivate: [AuthGuard]
  },
  // Не авторизован
  {
    path: '',
    component: LayoutLoginComponent,
    data: {
      title: 'Пожалуйста, авторизуйтсь'
    },
    loadChildren: 'app/pages/auth/auth.module#AuthModule'
  },
];

@NgModule({
  imports: [RouterModule.forChild(ADMIN_ROUTES)],
  exports: [RouterModule]
})
export class AdminRoutingModule {
}
