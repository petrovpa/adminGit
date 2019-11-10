import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MenuManageRoutingModule } from './menu-manage-routing.module';
import { MenuListComponent } from './menu-list/menu-list.component';
import { MenuEditComponent } from './menu-edit/menu-edit.component';

@NgModule({
  imports: [
    CommonModule,
    MenuManageRoutingModule
  ],
  declarations: [MenuListComponent, MenuEditComponent]
})
export class MenuManageModule { }
