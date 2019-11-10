import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TreeModule } from 'angular-tree-component';

import { FormControlModule } from '@core/components/form-control';

import { RightParamsMenuBaseComponent } from './base/right-params-menu-base.component';
import { RightParamsDepartamentComponent } from './right-params-departament/right-params-departament.component';
import { RightParamsMenuComponent } from './right-params-menu/right-params-menu.component';
import { RightParamsService } from './services/right-params.service';

const DECLARATION_RIGHT_PARAMS = [
  RightParamsMenuBaseComponent,
  RightParamsDepartamentComponent,
  RightParamsMenuComponent
];

@NgModule({
  imports: [
    CommonModule,
    TreeModule,
    FormControlModule
  ],
  declarations: DECLARATION_RIGHT_PARAMS,
  exports: DECLARATION_RIGHT_PARAMS,
  providers: [
    RightParamsService
  ]
})
export class RightParamsModule { }
