import { Component, OnInit, Inject, ViewChild, Input } from '@angular/core';
import { ChangePasswordDialogOptions } from '../classes/change-password-dialog-options.class';
import { ModalDialogComponent } from '@core/components/modal-dialog';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'change-password-dialog',
  templateUrl: './change-password-dialog.component.html',
  styleUrls: ['./change-password-dialog.component.scss']
})
export class ChangePasswordDialogComponent implements OnInit {
  @ViewChild('dialog') dialog: ModalDialogComponent;

  @Input() options: ChangePasswordDialogOptions = <ChangePasswordDialogOptions> {};

  protected currentPassword: any = '';

  protected newPassword: any = '';

  protected retPassword: any = '';

  protected isEqualPassword: boolean = false;

  protected repeatErrorMessage = 'Введённые пароли не совпадают';

  wasValidate: boolean = false;

  isServiceInProgress: boolean = false;

  serviceErrorMessage = null;

  constructor() {
  }

  ngOnInit() {
  }

  /**
   *
   */
  private onPasswordChanged() {
    if (this.newPassword === this.retPassword) {
      this.isEqualPassword = true;
    } else {
      this.isEqualPassword = false;
    }
  }

  /**
   *
   * @param event
   */
  private onCheckRegExpPassword(event: any) {
    if (event) {
      this.repeatErrorMessage = event.errorMessage;
    }
  }

  /**
   * Отправка формы
   */
  submit(f: NgForm) {
    this.wasValidate = true;

    if (f.valid && this.isEqualPassword) {
      this.options.onClickConfirmButton(this.currentPassword, this.newPassword, this.retPassword, f);
      this.wasValidate = false;
    }

    return false;
  }

  clear() {
    this.serviceErrorMessage = null;
    this.wasValidate = false;
    this.currentPassword = null;
    this.newPassword = null;
    this.retPassword = null;
  }
}
