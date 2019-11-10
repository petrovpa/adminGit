import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { TreeModule } from '@node/angular-tree-component';
import { FormControlModule } from '@core/components/form-control';
import { FormControlLabelModule } from '@core/components/form-control-label';
import { ModalDialogModule } from '@core/components/modal-dialog';

import { ModalDialogGroupComponent } from './modal-dialog-group/modal-dialog-group.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    FormControlModule,
    FormControlLabelModule,
    ModalDialogModule,
    TreeModule
  ],
  declarations: [ModalDialogGroupComponent],
  exports: [ModalDialogGroupComponent]
})
export class ModalDialogGroupModule {
}
