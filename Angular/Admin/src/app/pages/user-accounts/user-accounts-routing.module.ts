import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { UserAccountsComponent } from './user-accounts/user-accounts.component';

export const ROUTES_USER_ACCOUNTS: Routes = [
  {
    path: '',
    component: UserAccountsComponent,
  },
  {
    path: '',
    loadChildren: 'app/pages/user-accounts-edit/user-accounts-edit.module#UserAccountsEditModule',
    data: {
      title: 'Управление пользователем'
    },
  },
];

@NgModule({
  imports: [RouterModule.forChild(ROUTES_USER_ACCOUNTS)],
  exports: [RouterModule]
})
export class UserAccountsRoutingModule { }
