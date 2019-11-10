import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FormControlModule } from '@core/components/form-control';
import { Select2Module } from '@core/components/select2';
import { ManageRightService } from '@app/modules/manage-right/services/manage-right.service';
import { ManageRightSimpleComponent } from './manage-right-simple/manage-right-simple.component';
import { ManageRightProfileComponent } from './manage-right-profile/manage-right-profile.component';
import { RightParamsModule } from '@app/modules/right-params/right-params.module';
import { CustomButtonGroupModule } from '@core/components/custom-button-group';
import { DatatableModule } from '@core/components/datatable';
import { ModalDialogModule } from '@core/components/modal-dialog';
import { DebugModule } from '@core/components/debug';
import { FormControlLabelModule } from '@core/components/form-control-label';

const DECLARATION_RIGHT = [
  ManageRightSimpleComponent,
  ManageRightProfileComponent
];

@NgModule({
  imports: [
    CommonModule,
    FormControlModule,
    FormControlLabelModule,
    Select2Module,
    RightParamsModule,
    CustomButtonGroupModule,
    DatatableModule,
    ModalDialogModule,
    DebugModule,
  ],
  declarations: DECLARATION_RIGHT,
  exports: DECLARATION_RIGHT,
  providers: [
    ManageRightService
  ]
})
export class ManageRightModule { }
