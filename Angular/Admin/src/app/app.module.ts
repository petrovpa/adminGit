import { NgModule, TemplateRef, LOCALE_ID } from '@angular/core';
import { registerLocaleData } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule, PreloadAllModules } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoggerModule } from '@frontend/logger';
import { Ng2Webstorage } from 'ngx-webstorage';

// Cтили
import '../styles/styles.scss';

import { environment } from 'environments/environment';
import { MAIN_ROUTES } from './app.routes';

// Языковая поддержка
import localeRu from '@angular/common/locales/ru';

// Модули
import { CommonServiceModule, RestType } from '@core/services/common';

import { AppComponent } from './app.component';
import { Page404Component } from './layouts/page404/page404.component';
import { NavigationService } from '@app/services/navigation.service';
import { AdminModule } from '@app/layouts/admin/admin.module';
import { CacheService } from '@core/services/cache';

registerLocaleData(localeRu);

// let LOG_LEVEL = Level.LOG;
// if (!isDevMode()) {
//   LOG_LEVEL = Level.ERROR;
// }

@NgModule({
  bootstrap: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule,
    AdminModule,
    LoggerModule.forRoot(environment.logLevel),
    CommonServiceModule.forRoot({
      restType: RestType.JSON,
      restPath: environment.restPath,
      showError: true
    }),
    Ng2Webstorage.forRoot({
      prefix: 'admin',
      separator: '.',
      caseSensitive: true
    }),
    RouterModule.forRoot(MAIN_ROUTES, {
      useHash: false,
      // будут медленные переходы между страницами - расскомментить!
      // TODO: добавить custom https://alligator.io/angular/preloading/ или показ загрузки
      preloadingStrategy: PreloadAllModules
    })
  ],
  declarations: [
    AppComponent,
    Page404Component,
  ],
  providers: [
    environment.ENV_PROVIDERS,
    {
      provide: LOCALE_ID,
      useValue: 'ru'
    },
    TemplateRef,
    CacheService,
    NavigationService
  ]
})
export class AppModule {
}
