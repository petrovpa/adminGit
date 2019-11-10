import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TreeModule } from 'angular-tree-component';

import { FormControlModule } from '@core/components/form-control';
import { FormControlLabelModule } from '@core/components/form-control-label';
import { DatatableModule } from '@core/components/datatable';
import { DropdownListModule } from '@core/components/dropdown-list/dropdown-list.module';
import { ChangePasswordDialogModule } from '@app/components/change-password-dialog/change-password-dialog.module';
import { AddModalDialogModule } from '@app/components/add-modal-dialog/add-modal-dialog.module';
import { ModalDialogGroupModule } from '@app/components/modal-dialog-group/modal-dialog-group.module';
import { SelectTreeModule } from '@core/components/select-tree';
import { Select2Module } from '@core/components/select2';
import { AlertModule } from '@core/components/alert';
import { CustomButtonGroupModule } from '@core/components/custom-button-group';
import { JournalModule } from '@core/modules/journal';

import { UserMainComponent } from './user-main/user-main.component';
import { UserAccountsEditRoutingModule } from './user-accounts-edit-routing.module';
import { UserRoleComponent } from './role/role.component';
import { UserGroupComponent } from './group/group.component';
import { UserRightComponent } from './right/right.component';
import { UserAccountEditGuard } from './user-account-edit.guard';
import { ActiveDirectoryComponent } from './active-directory/active-directory.component';
import { UserAccountsService } from './services/user-accounts.service';

import { ManageRightModule } from '@app/modules/manage-right';
import { UserShortInfoModule } from '@app/components/user-short-info/user-short-info.module';

console.log('Acc edit bundle loaded asynchronously');

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UserAccountsEditRoutingModule,
    FormControlModule,
    FormControlLabelModule,
    DatatableModule,
    DropdownListModule,
    CustomButtonGroupModule,
    AddModalDialogModule,
    ChangePasswordDialogModule,
    ModalDialogGroupModule,
    SelectTreeModule,
    TreeModule,
    Select2Module,
    AlertModule,
    ManageRightModule,
    JournalModule,
    UserShortInfoModule
  ],
  declarations: [
    UserMainComponent,
    UserRoleComponent,
    UserGroupComponent,
    UserRightComponent,
    ActiveDirectoryComponent
  ],
  providers: [
    UserAccountEditGuard,
    UserAccountsService
  ]
})
export class UserAccountsEditModule { }
