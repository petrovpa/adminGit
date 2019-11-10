import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { FormControlLabelModule } from '@core/components/form-control-label';

import { AuthRoutingModule } from './auth-routing.module';
import { LoginComponent } from './login/login.component';
import { LoginService } from './services/login.service';
import { LogoutComponent } from './logout/logout.component';
import { RememberComponent } from './remember/remember.component';
import { GuestGuard } from './guard/guest.guard';
import { ChangePasswordComponent } from './change-password/change-password.component';

console.log('Auth bundle loaded asynchronously');

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    AuthRoutingModule,
    FormControlLabelModule,
  ],
  declarations: [
    LoginComponent,
    LogoutComponent,
    RememberComponent,
    ChangePasswordComponent
  ],
  providers: [
    LoginService,
    GuestGuard
  ]
})
export class AuthModule {
}
