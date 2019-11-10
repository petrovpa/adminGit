import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';

import { PasswordSettingsService } from '../services/password-settings.service';
import { SettingsPassword } from '@app/classes/settings-password.class';
import { ModalDialogService, ModalType } from '@core/components/modal-dialog';

@Component({
  selector: 'app-password-settings',
  templateUrl: './password-settings.component.html',
  styleUrls: ['./password-settings.component.scss']
})
export class PasswordSettingsComponent implements OnInit {

  namespace: SettingsPassword = new SettingsPassword();

  wasValidated: boolean = false;

  isFormDisabled: boolean = false;

  constructor(private settingsService: PasswordSettingsService,
              private dialogService: ModalDialogService,
              public router: Router) {
  }

  ngOnInit() {
    this.settingsService.loadSettings().subscribe((res) => {
      if (!res) {
        //  this.showErrorDialog('Произошла ошибка сервиса');
      } else {
        if (res['Error']) {
          // this.showErrorDialog(res['Error']);
        } else {
          this.namespace = res;
        }
      }
    });
  }

  showErrorDialog(message) {
    this.dialogService.alert(ModalType.Error, message);
  }

  saveSettings(form: NgForm) {
    this.wasValidated = true;
    if (form.valid) {
      this.settingsService.saveSettings(this.namespace).subscribe((res) => {
        if (res && res['Error']) {
          //  this.showErrorDialog(res['Error']);
        } else {
          this.router.navigate(['password-settings']);
        }
      });
    } else {
      this.showErrorDialog('Проверьте введенные данные');
    }
  }

}
