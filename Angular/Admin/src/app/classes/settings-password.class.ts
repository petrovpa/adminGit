export class SettingsPassword {
  // "PWD_MIN_LEN"
  minLength: number;
  // "PWD_MAX_LEN"
  maxLength: number;
  // PWD_MAX_AGE_ON
  enableForceChange: boolean;
  // PWD_MAX_AGE
  expiredDays: number;
  // always true
  capitalLetters: boolean;
  // always true
  lowercaseLetters: boolean;
  // always true
  digits: boolean;
  // "PWD_GROUPS_ADDITIONAL_ALLOWED"
  additional: boolean;
  // PWD_GROUPS_COUNT
  minGroupNumber: number;
  // PWD_IDT_SYM_COUNT
  sameSymbolsInRow: number;
  // PWD_HISTORY_ON
  enablePasswordHistory: boolean;
  // PWD_HISTORY_COUNT
  previousPasswordsNumber: number;
  // USR_MAX_FAILED_LOGIN_ATTEMPTS
  maxUnsuccessfulLoginAttempts: number;
  // USR_MAX_INACTIVITY_TIME
  maxInactivityPeriod: number;
  // LICENSE_EXP_CNT
  warnLicenceExpirationDays: number;
  // SESSION_SIZE
//   параметр вынесен в common-config (maxSessionSize)
//   sessionSize: number;

  constructor() {
    this.digits = true;
    this.capitalLetters = true;
    this.lowercaseLetters = true;
  }

  getAdditionalString() {
    return this.additional ? 'TRUE' : 'FALSE';
  }

  setAdditionalString(additional: string) {
    if (additional === 'TRUE') {
      this.additional = true;
    } else {
      this.additional = false;
    }
  }
}
