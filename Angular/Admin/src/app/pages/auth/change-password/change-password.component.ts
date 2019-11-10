import { Component, OnInit } from '@angular/core';
import { LoginService } from '@app/pages/auth/services/login.service';
import { Router } from '@angular/router';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Logger } from '@frontend/logger';
import { PasswordValidation } from '../directives/password.validator';
import { SessionService } from '@core/services/common';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['../login/login.component.scss', './change-password.component.scss']
})
export class ChangePasswordComponent implements OnInit {
  /**
   * Форма
   */
  passwordExpiredForm: FormGroup;

  /**
   * Обработка сервиса
   * @type {boolean}
   */
  protected wasValidated: boolean = false;
  protected isServiceInProgress: boolean = false;

  constructor(public loginService: LoginService,
              private logger: Logger,
              private session: SessionService,
              private router: Router,
              fb: FormBuilder) {

    this.passwordExpiredForm = fb.group({
      'oldPassword': new FormControl('', Validators.required),
      'newPassword': new FormControl('', [Validators.required]),
      'confirmPassword': new FormControl('', [Validators.required])
    }, {
      validator: PasswordValidation.MatchPassword
    });
  }

  ngOnInit() {
    if (!this.loginService.passwordExpiredSession) {
      this.goToLogin();
    }
  }

  /**
   * Смена пароля
   */
  tryChangePassword() {
    this.logger.log('try change..');
    this.wasValidated = true;
    this.loginService.showError = '';

    if (this.passwordExpiredForm.valid) {
      this.isServiceInProgress = true;

      this.loginService.tryChange(
        this.passwordExpiredForm.get('oldPassword').value.toString(),
        this.passwordExpiredForm.get('newPassword').value.toString(),
        this.passwordExpiredForm.get('confirmPassword').value.toString()
      ).finally(() => {
        this.isServiceInProgress = false;
      }).subscribe((res: any) => {
        if (!res.Error) {
          this.session.sessionId = null;
          this.loginService.showInfo = 'Пароль изменен. Для входа используйте новый пароль.';
          this.loginService.passwordExpiredSession = null;
          this.goToLogin();
        } else {
          this.loginService.showError = res.Error;
        }
      });
    } else {
      const controls = this.passwordExpiredForm.controls,
            confirmPassword = controls.confirmPassword,
            newPassword = controls.newPassword,
            oldPassword = controls.oldPassword;

      if (oldPassword.errors || newPassword.errors || (confirmPassword.errors && confirmPassword.errors.required)) {
        this.loginService.showError += 'Заполнены не все обязательные поля.<br>';
      }

      if (confirmPassword.errors && confirmPassword.errors.MatchPassword) {
        this.loginService.showError += 'Неверное подтверждение пароля.';
      }
    }
  }

  /**
   * Отмена смены пароля
   */
  cancel() {
    this.loginService.passwordExpiredSession = null;
    this.goToLogin();
    return false;
  }

  /**
   * Перенаправление на авторизацию
   */
  private goToLogin() {
    this.router.navigate(['/login']);
  }
}
