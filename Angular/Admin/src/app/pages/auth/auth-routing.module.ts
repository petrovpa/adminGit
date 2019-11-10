import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { LogoutComponent } from './logout/logout.component';
import { GuestGuard } from './guard/guest.guard';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { RememberComponent } from './remember/remember.component';

const ROUTES_AUTH: Routes = [
  {
    path: 'login',
    component: LoginComponent,
    data: {
      title: 'Авторизация'
    },
    canActivate: [GuestGuard]
  },
  {
    path: 'remember',
    component: RememberComponent,
    data: {
      title: 'Восстановление пароля'
    }
  },
  {
    path: 'change-password',
    component: ChangePasswordComponent,
    data: {
      title: 'Смена пароля'
    }
  },
  {
    path: 'logout',
    component: LogoutComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(ROUTES_AUTH)],
  exports: [RouterModule]
})
export class AuthRoutingModule {
}
