import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AutoimportOptionsComponent } from './autoimport-options/autoimport-options.component';

const ROUTES_AUTOIMPORT_SETTING: Routes = [
  {
    path: '',
    component: AutoimportOptionsComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(ROUTES_AUTOIMPORT_SETTING)],
  exports: [RouterModule]
})
export class AutoimportOptionsRoutingModule { }
