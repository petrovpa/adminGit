import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { environment } from 'environments/environment';
import { LoginService } from '../services/login.service';
import { Subscription } from 'rxjs/Subscription';
import { Logger } from '@frontend/logger';

const ERRORS_LOGIN = {
  required: 'Заполнены не все обязательные поля',
  expired: 'Время сессии истекло'
};

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit, OnDestroy {
  private sub: Subscription;

  /**
   * Форма
   */
  protected loginForm: FormGroup;

  /**
   * Обработка сервиса
   * @type {boolean}
   */
  protected isServiceInProgress: boolean = false;

  constructor(private logger: Logger,
              private router: Router,
              private route: ActivatedRoute,
              public loginService: LoginService) {
    this.loginForm = new FormGroup({
      'login': new FormControl('', Validators.required),
      'password': new FormControl('', [Validators.required])
    });
  }

  ngOnInit() {
    this.sub = this.route
      .queryParams
      .subscribe(params => {
        if (params && params['error'] === 'expired') {
          this.loginService.showError = ERRORS_LOGIN['expired'];
        }
      });
  }

  /**
   * Авторизация
   */
  login() {
    this.logger.log('auth..');

    if (this.loginForm.valid) {
      this.isServiceInProgress = true;

      this.loginService.showError = null;
      this.loginService.showInfo = null;

      this.loginService.login(
        this.loginForm.get('login').value.toString(),
        this.loginForm.get('password').value.toString()
      ).finally(() => {
        this.isServiceInProgress = false;
      }).subscribe((res: any) => {
        if (res === true) {
          this.router.navigate(['']);
        } else {
          // проверка на истекший пароль?
          if (res.isNeedChangePwd) {
            this.router.navigate(['/change-password']);
          } else {
            this.loginService.showError = res.Error;
          }
        }
      }, (errorRes: any) => {
        this.loginService.showError = errorRes.Error;
        this.logger.error(errorRes.Error);
      });
    } else {
      this.loginService.showError = ERRORS_LOGIN['required'];
    }

    return false;
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }
}
