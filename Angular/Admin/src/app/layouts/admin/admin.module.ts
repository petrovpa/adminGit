import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { AlertModule } from '@core/components/alert';
import { ModalDialogModule } from '@core/components/modal-dialog';
import { MenuModule } from '@core/components/menu';
import { SidebarMenuModule } from '@core/components/sidebar-menu';

import { LoginService } from '@app/pages/auth/services/login.service';
import { WelcomeComponent } from '@app/pages/welcome/welcome.component';

import { AuthGuard } from './guard/auth.guard';

import { LayoutLoginComponent } from './layout-login/layout-login.component';
import { LayoutMainComponent } from './layout-main/layout-main.component';
import { AppFooterComponent } from './app-footer/app-footer.component';
import { AppHeaderComponent } from './app-header/app-header.component';
import { AppLoaderComponent } from './app-loader/app-loader.component';
import { MenuService } from '@app/services/menu.service';
import { AdminRoutingModule } from './admin-routing.module';

console.log('Admin layout bundle loaded asynchronously');

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    AdminRoutingModule,
    HttpClientModule,
    AlertModule.forRoot(),
    MenuModule.forRoot(),
    ModalDialogModule.forRoot(),
    SidebarMenuModule.forRoot()
  ],
  declarations: [
    LayoutLoginComponent,
    LayoutMainComponent,
    AppFooterComponent,
    AppHeaderComponent,
    AppLoaderComponent,
    WelcomeComponent,
    WelcomeComponent
  ],
  providers: [
    LoginService,
    MenuService,
    AuthGuard
  ]
})

export class AdminModule { }
