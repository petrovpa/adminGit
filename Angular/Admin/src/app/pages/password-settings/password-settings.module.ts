import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { FormControlLabelModule } from '@core/components/form-control-label';
import { FormControlModule } from '@core/components/form-control';

import { PasswordSettingsComponent } from './password-settings/password-settings.component';
import { PasswordSettingsRoutingModule } from './password-settings-routing.module';
import { PasswordSettingsService } from './services/password-settings.service';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    FormControlModule,
    FormControlLabelModule,
    PasswordSettingsRoutingModule
  ],
  providers: [
    PasswordSettingsService
  ],
  declarations: [PasswordSettingsComponent]
})
export class PasswordSettingsModule {
}
