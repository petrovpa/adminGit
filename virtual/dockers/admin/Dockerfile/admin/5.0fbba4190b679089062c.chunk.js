webpackJsonp([5],{"29kO":function(e,n,s){var t=s("7B3k");e.exports="string"==typeof t?t:t.toString()},"3GpI":function(e,n){e.exports='<form #f="ngForm" [ngClass]="{\'was-validated\': wasValidated}"\n      class="page-form-edit password-settings-form"\n      novalidate autocomplete="off"\n      (submit)="saveSettings(f)">\n  <fieldset>\n    <legend>Длина пароля</legend>\n\n    <div class="row">\n      <div class="col-sm-6 col-12">\n        <form-control-label title="Минимальная(символов)" [required]="true">\n          <input-number name="minLength" placeholder="" [(ngModel)]="namespace.minLength" [required]="true"\n                        [disabled]="isFormDisabled"></input-number>\n        </form-control-label>\n      </div>\n      <div class="col-sm-6 col-12">\n        <form-control-label title="Максимальная(символов)" [required]="true">\n          <input-number name="maxLength" placeholder="" [(ngModel)]="namespace.maxLength" [required]="true"\n                        [disabled]="isFormDisabled"></input-number>\n        </form-control-label>\n      </div>\n    </div>\n  </fieldset>\n\n  <fieldset>\n    <legend>Принудительная смена пароля</legend>\n\n    <div class="row">\n      <div class="col-sm-6 col-12 margin-top-check">\n        <checkbox label="Включить принудительную смену" name="enableForceChange"\n                  [(ngModel)]="namespace.enableForceChange" [disabled]="isFormDisabled"></checkbox>\n      </div>\n      <div class="col-sm-6 col-12">\n        <form-control-label title="Смена пароля через(дней)" [required]="true">\n          <input-number name="expiredDays" [(ngModel)]="namespace.expiredDays" placeholder=""\n                        [disabled]="isFormDisabled && !namespace.enableForceChange"></input-number>\n        </form-control-label>\n      </div>\n    </div>\n\n    <legend>Разрешенные группы символов</legend>\n\n    <div class="row">\n      <div class="col-sm-6 col-12">\n        <checkbox label="Заглавные" name="capitalLetters" [(ngModel)]="namespace.capitalLetters"\n                  [disabled]="true"></checkbox>\n      </div>\n      <div class="col-sm-6 col-12">\n        <checkbox label="Строчные" name="lowercaseLetters" [(ngModel)]="namespace.lowercaseLetters"\n                  [disabled]="true"></checkbox>\n      </div>\n    </div>\n\n    <div class="row">\n      <div class="col-sm-6 col-12">\n        <checkbox label="Цифры" name="digits" [(ngModel)]="namespace.digits" [disabled]="true"></checkbox>\n      </div>\n      <div class="col-sm-6 col-12">\n        <checkbox label="Дополнительные" name="additional" [(ngModel)]="namespace.additional"\n                  [disabled]="isFormDisabled"></checkbox>\n      </div>\n    </div>\n\n    <div class="row">\n      <div class="col-sm-6 col-12">\n        <form-control-label title="Минимальное количество групп в пароле" [required]="true">\n          \x3c!--<input-select name="minGroupNumber" [items]="minGroupNumbers" [(ngModel)]="namespace.minGroupNumber" [required]="true" [nameField]="minGroupNumberName"\n            [sysnameField]="minGroupNumberSysname"></input-select>--\x3e\n          <input-number name="minGroupNumber" [(ngModel)]="namespace.minGroupNumber" placeholder=""\n                        [disabled]="isFormDisabled"></input-number>\n        </form-control-label>\n      </div>\n      <div class="col-sm-6 col-12">\n        <form-control-label title="Количество одинаковых символов подряд" [required]="true">\n          <input-number name="sameSymbolsInRow" [(ngModel)]="namespace.sameSymbolsInRow" placeholder=""\n                        [disabled]="isFormDisabled"></input-number>\n        </form-control-label>\n      </div>\n    </div>\n\n    <legend>Контроль истории паролей</legend>\n\n    <div class="row">\n      <div class="col-sm-6 col-12 margin-top-check">\n        <checkbox label="Включить контроль истории паролей учетных записей" name="enablePasswordHistory"\n                  [(ngModel)]="namespace.enablePasswordHistory"\n                  [disabled]="isFormDisabled"></checkbox>\n      </div>\n      <div class="col-sm-6 col-12">\n        <form-control-label title="Количество предыдущих паролей" [required]="true">\n          <input-number name="previousPasswordsNumber" [(ngModel)]="namespace.previousPasswordsNumber" placeholder=""\n                        [disabled]="isFormDisabled && !namespace.enablePasswordHistory"></input-number>\n        </form-control-label>\n      </div>\n    </div>\n  </fieldset>\n\n  <fieldset>\n    <legend>Настройка политики учетных записей</legend>\n\n    <div class="row">\n      <div class="col-sm-6 col-12">\n        <form-control-label title="Максимальное количество неудачных попыток входа" [required]="true">\n          <input-number name="maxUnsuccessfulLoginAttempts" placeholder=""\n                        [(ngModel)]="namespace.maxUnsuccessfulLoginAttempts" [required]="true"\n                        [disabled]="isFormDisabled"></input-number>\n        </form-control-label>\n      </div>\n      <div class="col-sm-6 col-12">\n        <form-control-label title="Максимальный период неактивности пользователя (дней)" [required]="true">\n          <input-number name="maxInactivityPeriod" placeholder="" [(ngModel)]="namespace.maxInactivityPeriod"\n                        [required]="true" [disabled]="isFormDisabled"></input-number>\n        </form-control-label>\n      </div>\n      <div class="col-sm-6 col-12">\n        <form-control-label title="Максимальное период неактивности пользователя внутри сеанса (минут)" [required]="true">\n          <input-number name="sessionSize" placeholder="" [(ngModel)]="namespace.sessionSize"\n                        [required]="true" [disabled]="isFormDisabled"></input-number>\n        </form-control-label>\n      </div>\n    </div>\n  </fieldset>\n\n  \x3c!--<fieldset>--\x3e\n  \x3c!--<legend>Предупреждения о сроках окончания лицензии</legend>--\x3e\n\n  \x3c!--<div class="row">--\x3e\n  \x3c!--<div class="col-sm-6 col-12">--\x3e\n  \x3c!--<form-control-label title="Предупреждения о сроках окончания лицензии за(дней)" [required]="true">--\x3e\n  \x3c!--<input-number name="warnLicenceExpirationDays" placeholder=""--\x3e\n  \x3c!--[(ngModel)]="namespace.warnLicenceExpirationDays" [required]="true"--\x3e\n  \x3c!--[disabled]="isFormDisabled"></input-number>--\x3e\n  \x3c!--</form-control-label>--\x3e\n  \x3c!--</div>--\x3e\n  \x3c!--</div>--\x3e\n  \x3c!--</fieldset>--\x3e\n\n  <div class="page__btn">\n    <div class="row">\n      <div class="col-sm-6 col-12">\n        <button class="btn btn-link btn-rounded btn-block"\n                (click)="router.navigate([\'/\'])">Закрыть\n        </button>\n      </div>\n      <div class="col-sm-6 col-12">\n        <button type="submit"\n                class="btn btn-primary btn-rounded btn-block">Сохранить\n        </button>\n      </div>\n    </div>\n  </div>\n</form>\n'},"7B3k":function(e,n,s){(e.exports=s("lcwS")(!0)).push([e.i,".margin-top-check {\n  margin-top: 2rem; }\n","",{version:3,sources:["/home/sambucus/fastwork/hgbiv/bsflifeprod/Angular/Admin/src/app/pages/password-settings/password-settings/password-settings.component.scss"],names:[],mappings:"AAAA;EACE,iBAAgB,EACjB",file:"password-settings.component.scss",sourcesContent:[".margin-top-check {\n  margin-top: 2rem;\n}\n"],sourceRoot:""}])},"8+bp":function(e,n,s){"use strict";Object.defineProperty(n,"__esModule",{value:!0});var t=s("vCxL"),o=s("LBxa"),i=s("FhS4"),r=s("x0/j"),a=s("kLaQ"),l=s("jWmD"),d=s("weID"),c=s("pvhM"),m=s("BTAH"),u=function(){return t.__decorate([o.NgModule({imports:[i.CommonModule,r.FormsModule,l.FormControlModule,a.FormControlLabelModule,c.PasswordSettingsRoutingModule],providers:[m.PasswordSettingsService],declarations:[d.PasswordSettingsComponent]})],function(){})}();n.PasswordSettingsModule=u},BTAH:function(e,n,s){"use strict";Object.defineProperty(n,"__esModule",{value:!0});var t=s("vCxL"),o=s("LBxa"),i=s("8E6H"),r=s("r7fO"),a=s("+laO"),l=function(){function e(e,n){this.logger=e,this.rest=n}return e.prototype.saveSettings=function(e){return this.rest.doCall("admrestgate/saveSettingPassword",{PWD_MIN_LEN:e.minLength,PWD_MAX_LEN:e.maxLength,PWD_MAX_AGE_ON:e.enableForceChange,PWD_MAX_AGE:e.expiredDays,PWD_GROUPS_ADDITIONAL_ALLOWED:e.getAdditionalString(),PWD_GROUPS_COUNT:e.minGroupNumber,PWD_IDT_SYM_COUNT:e.sameSymbolsInRow,PWD_HISTORY_ON:e.enablePasswordHistory,PWD_HISTORY_COUNT:e.previousPasswordsNumber,USR_MAX_FAILED_LOGIN_ATTEMPTS:e.maxUnsuccessfulLoginAttempts,USR_MAX_INACTIVITY_TIME:e.maxInactivityPeriod,LICENSE_EXP_CNT:e.warnLicenceExpirationDays,SESSION_SIZE:e.sessionSize})},e.prototype.loadSettings=function(){return this.rest.doCall("admrestgate/readSettingPassword",{}).map(function(e){var n=new r.SettingsPassword;return n.minLength=e.PWD_MIN_LEN,n.maxLength=e.PWD_MAX_LEN,n.enableForceChange=e.PWD_MAX_AGE_ON,n.expiredDays=e.PWD_MAX_AGE,n.setAdditionalString(e.PWD_GROUPS_ADDITIONAL_ALLOWED),n.minGroupNumber=e.PWD_GROUPS_COUNT,n.sameSymbolsInRow=e.PWD_IDT_SYM_COUNT,n.enablePasswordHistory=e.PWD_HISTORY_ON,n.previousPasswordsNumber=e.PWD_HISTORY_COUNT,n.maxUnsuccessfulLoginAttempts=e.USR_MAX_FAILED_LOGIN_ATTEMPTS,n.maxInactivityPeriod=e.USR_MAX_INACTIVITY_TIME,n.warnLicenceExpirationDays=e.LICENSE_EXP_CNT,n.sessionSize=e.SESSION_SIZE,n})},t.__decorate([o.Injectable(),t.__metadata("design:paramtypes",[i.Logger,a.RestService])],e)}();n.PasswordSettingsService=l},pvhM:function(e,n,s){"use strict";Object.defineProperty(n,"__esModule",{value:!0});var t=s("vCxL"),o=s("LBxa"),i=s("UllE"),r=[{path:"",component:s("weID").PasswordSettingsComponent}],a=function(){return t.__decorate([o.NgModule({imports:[i.RouterModule.forChild(r)],exports:[i.RouterModule]})],function(){})}();n.PasswordSettingsRoutingModule=a},r7fO:function(e,n,s){"use strict";Object.defineProperty(n,"__esModule",{value:!0});var t=function(){function e(){this.digits=!0,this.capitalLetters=!0,this.lowercaseLetters=!0}return e.prototype.getAdditionalString=function(){return this.additional?"TRUE":"FALSE"},e.prototype.setAdditionalString=function(e){this.additional="TRUE"===e},e}();n.SettingsPassword=t},weID:function(e,n,s){"use strict";Object.defineProperty(n,"__esModule",{value:!0});var t=s("vCxL"),o=s("LBxa"),i=s("UllE"),r=s("BTAH"),a=s("r7fO"),l=s("uz/q"),d=function(){function e(e,n,s){this.settingsService=e,this.dialogService=n,this.router=s,this.namespace=new a.SettingsPassword,this.wasValidated=!1,this.isFormDisabled=!1}return e.prototype.ngOnInit=function(){var e=this;this.settingsService.loadSettings().subscribe(function(n){n&&(n.Error||(e.namespace=n))})},e.prototype.showErrorDialog=function(e){this.dialogService.alert(l.ModalType.Error,e)},e.prototype.saveSettings=function(e){var n=this;this.wasValidated=!0,e.valid?this.settingsService.saveSettings(this.namespace).subscribe(function(e){e&&e.Error||n.router.navigate(["password-settings"])}):this.showErrorDialog("Проверьте введенные данные")},t.__decorate([o.Component({selector:"app-password-settings",template:s("3GpI"),styles:[s("29kO")]}),t.__metadata("design:paramtypes",[r.PasswordSettingsService,l.ModalDialogService,i.Router])],e)}();n.PasswordSettingsComponent=d}});