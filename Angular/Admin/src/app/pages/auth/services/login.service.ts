import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import { Logger } from '@frontend/logger';
import { EventsService, RestService, SessionService } from '@core/services/common';
import { StringHelper } from '@core/shared/common';

@Injectable()
export class LoginService {
  private loggedIn = false;

  /**
   * Редирект на страницу
   *
   * @type {string}
   * @private
   */
  private _redirectURL = '';
  get redirectURL(): string {
    return this._redirectURL;
  }

  set redirectURL(value: string) {
    this._redirectURL = value;
  }

  /**
   * Ошибки
   * @type {null}
   */
  showError: string = null;
  showInfo: string = null;
  /**
   * Пароль истек
   * @type {boolean}
   */
  passwordExpiredSession: string = '';

  constructor(private logger: Logger,
              private restService: RestService,
              private sessionService: SessionService,
              private eventsService: EventsService,
              private router: Router) {
    this.loggedIn = this.isLoggedIn();
  }

  /**
   * Авторизация
   *
   * @param {string} login
   * @param {string} password
   */
  public login(login: string, password: string) {
    const params = {
      login: login,
      password: password
    };

    return this.restService.formCall('login/dsLogin', params, {
      hideLoader: true,
      showError: false,
      getAllData: true
    }).map((res: any) => {
      this.logger.log('get result from dsLogin', res);

      const result = res.Result;

      if (result && !result.Error && this.sessionService.sessionId) {
        this.setUserName(result);

        this.sessionService.roleList = result.ROLELIST;

        // событие об успешном входе
        this.eventsService.publish('login');

        return true;
      }

      // проверка на истекший пароль?
      if (result.isNeedChangePwd) {
        this.passwordExpiredSession = result.SESSIONID;
      }

      return result;
    });
  }

  /**
   * Смена пароля
   * @param oldPassword
   * @param newPassword
   * @param confirmPassword
   */
  public tryChange(oldPassword, newPassword, confirmPassword) {
    return this.restService.formCall('pass/dsB2BUserChangePass', {
      NEWPASS: newPassword,
      OLDPASS: oldPassword,
      sessionid: this.passwordExpiredSession
    }, {
      hideLoader: true,
      showError: false
    });
  }

  /**
   * Восстановление пароля
   *
   * @param {string} email
   * @param {string} login
   */
  public restorePassword(email: string, login: string) {
    const params = {
      LOGIN: login,
      EMAIL: email
    };

    return this.restService.formCall('pass/dsResetPassword', params, {
      showError: false
    });
  }

  /**
   * Имя пользователя
   *
   * @param res
   */
  private setUserName(res) {
    this.sessionService.userFIO = (
      StringHelper.toString(res.LASTNAME) + ' '
      + StringHelper.toString(res.FIRSTNAME) + ' '
      + StringHelper.toString(res.MIDDLENAME)
    ).trim();

    if (!this.sessionService.userFIO) this.sessionService.userFIO = null;
  }

  /**
   * Выход
   */
  public logout(): void {
    this.sessionService.sessionId = null;
    this.sessionService.userFIO = null;
    this.sessionService.roleList = null;
    this.loggedIn = false;
    // событие об выходе
    this.eventsService.publish('logout');
  }

  /**
   * Авторизован ли?
   *
   * @returns {any}
   */
  public isLoggedIn(): any {
    return !!this.sessionService.sessionId;
  }

  /**
   * Запрет доступа: окончание сесиии
   *
   * @param error
   */
  public unauthorizedAccess(error: any): void {
    this.logout();
    this.router.navigate(['/login']);
    // посылаем событие о необходимости повторно авторизоваться
    this.eventsService.publish('login:unauthorized access');
  }
}
