import { Routes } from '@angular/router';

import { Page404Component } from './layouts/page404/page404.component';

export const MAIN_ROUTES: Routes = [
  {
    path: '',
    loadChildren: './layouts/admin/admin.module#AdminModule',
  },
  {
    path: '',
    redirectTo: '/',
    pathMatch: 'full'
  },
  // 404
  {
    path: '**',
    component: Page404Component,
    data: {
      title: 'Страница не найдена'
    }
  },
];
