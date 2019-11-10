import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { FormControlModule } from '@core/components/form-control';
import { FormControlLabelModule } from '@core/components/form-control-label';
import { ModalDialogModule } from '@core/components/modal-dialog';

import { ChangePasswordDialogComponent } from './change-password-dialog/change-password-dialog.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    FormControlModule,
    FormControlLabelModule,
    ModalDialogModule
  ],
  declarations: [ChangePasswordDialogComponent],
  exports: [ChangePasswordDialogComponent]
})
export class ChangePasswordDialogModule { }
