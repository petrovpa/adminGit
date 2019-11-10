import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { MenuListComponent } from './menu-list/menu-list.component';
import { MenuEditComponent } from './menu-edit/menu-edit.component';

const ROUTES_MENU_MANAGE: Routes = [
  {
    path: '',
    component: MenuListComponent,
    data: {
      title: 'Журнал меню'
    }
  },
  {
    path: 'edit',
    component: MenuEditComponent,
    data: {
      title: 'Редактирование меню'
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(ROUTES_MENU_MANAGE)],
  exports: [RouterModule]
})
export class MenuManageRoutingModule {
}
