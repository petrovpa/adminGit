import { Injectable } from '@angular/core';
import { Logger } from '@frontend/logger';
import { SettingsPassword } from '@app/classes/settings-password.class';
import { RestService } from '@core/services/common';

@Injectable()
export class PasswordSettingsService {

  constructor(private logger: Logger,
              private rest: RestService) {
  }

  /**
   * Сохранить настройки
   *
   * @param {SettingsPassword} settings
   * @returns {any | any}
   */
  saveSettings(settings: SettingsPassword) {
    return this.rest.doCall('admrestgate/saveSettingPassword', {
      'PWD_MIN_LEN': settings.minLength,
      'PWD_MAX_LEN': settings.maxLength,
      'PWD_MAX_AGE_ON': settings.enableForceChange,
      'PWD_MAX_AGE': settings.expiredDays,
      'PWD_GROUPS_ADDITIONAL_ALLOWED': settings.getAdditionalString(),
      'PWD_GROUPS_COUNT': settings.minGroupNumber,
      'PWD_IDT_SYM_COUNT': settings.sameSymbolsInRow,
      'PWD_HISTORY_ON': settings.enablePasswordHistory,
      'PWD_HISTORY_COUNT': settings.previousPasswordsNumber,
      'USR_MAX_FAILED_LOGIN_ATTEMPTS': settings.maxUnsuccessfulLoginAttempts,
      'USR_MAX_INACTIVITY_TIME': settings.maxInactivityPeriod,
      'LICENSE_EXP_CNT': settings.warnLicenceExpirationDays,
      "SESSION_SIZE": settings.sessionSize
    });
  }

  /**
   * Загрузить настройки
   */
  loadSettings() {
    return this.rest.doCall('admrestgate/readSettingPassword', {}).map((res) => {
      const settings: SettingsPassword = new SettingsPassword();
      settings.minLength = res['PWD_MIN_LEN'];
      settings.maxLength = res['PWD_MAX_LEN'];
      settings.enableForceChange = res['PWD_MAX_AGE_ON'];
      settings.expiredDays = res['PWD_MAX_AGE'];
      settings.setAdditionalString(res['PWD_GROUPS_ADDITIONAL_ALLOWED']);
      settings.minGroupNumber = res['PWD_GROUPS_COUNT'];
      settings.sameSymbolsInRow = res['PWD_IDT_SYM_COUNT'];
      settings.enablePasswordHistory = res['PWD_HISTORY_ON'];
      settings.previousPasswordsNumber = res['PWD_HISTORY_COUNT'];
      settings.maxUnsuccessfulLoginAttempts = res['USR_MAX_FAILED_LOGIN_ATTEMPTS'];
      settings.maxInactivityPeriod = res['USR_MAX_INACTIVITY_TIME'];
      settings.warnLicenceExpirationDays = res['LICENSE_EXP_CNT'];
      settings.sessionSize = res['SESSION_SIZE']
      return settings;
    });
  }
}
