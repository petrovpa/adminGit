import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { JournalModule } from '@core/modules/journal';
import { FormControlModule } from '@core/components/form-control';
import { FormControlLabelModule } from '@core/components/form-control-label';
import { DatatableModule } from '@core/components/datatable';
import { CustomButtonGroupModule } from '@core/components/custom-button-group';
import { ModalDialogModule } from '@core/components/modal-dialog';
import { DebugModule } from '@core/components/debug';

import { RoleManageRoutingModule } from './role-manage-routing.module';
import { RoleManageComponent } from './role-manage/role-manage.component';
import { RoleManageEditComponent } from './role-manage-edit/role-manage-edit.component';
import { RoleManageRightsComponent } from './role-manage-rights/role-manage-rights.component';
import { RoleManageEditGuard } from './role-manage-edit.guards';
import { RoleManageService } from './services/role-manage.service';
import { ManageRightModule } from '@app/modules/manage-right/manage-right.module';

@NgModule({
  imports: [
    CommonModule,
    RoleManageRoutingModule,
    FormControlModule,
    FormControlLabelModule,
    JournalModule,
    CustomButtonGroupModule,
    DatatableModule,
    ModalDialogModule,
    DebugModule,
    ManageRightModule
  ],
  declarations: [
    RoleManageComponent,
    RoleManageEditComponent,
    RoleManageRightsComponent
  ],
  providers: [
    RoleManageEditGuard,
    RoleManageService
  ]
})
export class RoleManageModule {
}
