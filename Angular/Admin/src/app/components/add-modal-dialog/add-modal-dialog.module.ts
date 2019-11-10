import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { ModalDialogModule } from '@core/components/modal-dialog';
import { FormControlModule } from '@core/components/form-control';
import { FormControlLabelModule } from '@core/components/form-control-label';
import { Select2Module } from '@core/components/select2';

import { AddModalDialogComponent } from './add-modal-dialog/add-modal-dialog.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    FormControlModule,
    FormControlLabelModule,
    ModalDialogModule,
    Select2Module
  ],
  declarations: [AddModalDialogComponent],
  exports: [AddModalDialogComponent]
})
export class AddModalDialogModule { }
