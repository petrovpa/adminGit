import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { LoginService } from '../services/login.service';
import { Logger } from '@frontend/logger';

const REMEMBER_ERRORS_REQUIRED = 'Заполнены не все обязательные поля';
const REMEMBER_MESSAGE_INFO = 'Пароль был выслан на указанный вами почтовый адрес';

@Component({
  selector: 'app-remember',
  templateUrl: './remember.component.html',
  styleUrls: ['./remember.component.scss']
})
export class RememberComponent implements OnInit {

  /**
   * Обработка сервиса
   * @type {boolean}
   */
  protected isServiceInProgress: boolean = false;

  /**
   * форма
   */
  rememberForm: FormGroup;

  constructor(private logger: Logger,
              private router: Router,
              public loginService: LoginService) {
    this.rememberForm = new FormGroup({
      'email': new FormControl('', Validators.required),
      'login': new FormControl('', [Validators.required])
    });
  }

  ngOnInit() {
  }

  /**
   * Отправка формы
   */
  remember() {
    if (this.rememberForm.valid) {
      if (!this.isServiceInProgress) {
        this.isServiceInProgress = true;
        this.loginService.showError = null;
        this.loginService.showInfo = null;

        this.loginService.restorePassword(
          this.rememberForm.get('email').value.toString(),
          this.rememberForm.get('login').value.toString()
        ).finally(() => {
          this.isServiceInProgress = false;
        }).subscribe((res: any) => {
          if (!res.Error) {
            this.loginService.showInfo = REMEMBER_MESSAGE_INFO;
            this.router.navigate(['/login']);
          } else {
            this.loginService.showError = res.Error;
          }
        });
      }
    } else {
      this.loginService.showError = REMEMBER_ERRORS_REQUIRED;
    }
  }

}
