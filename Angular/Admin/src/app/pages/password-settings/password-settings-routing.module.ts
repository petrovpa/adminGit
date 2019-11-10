import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PasswordSettingsComponent } from './password-settings/password-settings.component';

const ROUTES_PASSWORD_SETTINGS: Routes = [
    {
        path: '',
        component: PasswordSettingsComponent
      }
];

@NgModule({
  imports: [RouterModule.forChild(ROUTES_PASSWORD_SETTINGS)],
  exports: [RouterModule]
})
export class PasswordSettingsRoutingModule { }
