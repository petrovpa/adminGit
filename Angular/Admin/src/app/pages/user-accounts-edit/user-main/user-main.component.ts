import { Component, Injector, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/first';
import * as _ from 'lodash';

import { AngularTree } from '@app/classes/angular-tree.class';
import { Department } from '@app/classes/department.class';
import { User } from '@app/classes/user.class';

import { ModalType } from '@core/components/modal-dialog';
import {
  ChangePasswordDialogComponent,
} from '@app/components/change-password-dialog/change-password-dialog/change-password-dialog.component';
import { ChangePasswordDialogOptions } from '@app/components/change-password-dialog/classes/change-password-dialog-options.class';
import { UserBaseAccountComponent } from '../base/user-base-account.component';

@Component({
  selector: 'app-user-account-user-main',
  templateUrl: './user-main.component.html',
  styleUrls: ['./user-main.component.scss']
})
export class UserMainComponent extends UserBaseAccountComponent implements OnInit {
  @ViewChild('changeDialog') protected changeDialog: ChangePasswordDialogComponent;
  // дерево подразделений
  protected departmentTree: any;
  // опции для компонента выбора департамента
  protected departamentOptions: any = {
    /**
     * Обработчик выбора департамента
     * @param dep {any} - выбранный департамернт из дерева
     */
    onActivate: (dep: any) => {
      this.departmentItem.next(dep);
    }
  };
  // список подразделений
  protected departmentList: Department[] = [];
  // список типов пользователя
  protected userTypesList = [];
  // выбранное подразделение
  protected departmentItem: BehaviorSubject<any> = new BehaviorSubject({});
  // список методов авторизации пользователя
  protected authMethodList = [
    {
      'idAuth': '0',
      'nameAuthMethod': 'Логин/Пароль'
    },
    {
      'idAuth': '2',
      'nameAuthMethod': 'СУД-ИР'
    },
    {
      'idAuth': '3',
      'nameAuthMethod': 'LDAP'
    }
  ];

  // опции модального окна смены пароля
  protected changePasswordDialogOptions: ChangePasswordDialogOptions = <ChangePasswordDialogOptions>{
    modalDialogTitle: 'Смена пароля',
    labelTitle: 'Смена пароля'
  };

  filterLastName = /(^[^А-Яа-яЁё])|([^А-Яа-яЁё\s\-\’\`\'])/;
  filterName = /(^[^А-Яа-яЁё])|([^А-Яа-яЁё\s\-\’\`\'])/;

  // пользователь блокирован
  protected isBlock: boolean = false;

  protected errorMessage;

  constructor(protected injector: Injector) {
    super(injector);
  }

  ngOnInit() {
    if (this.userAccountId) {
      this.getUserInfoByAccountId(this.userAccountId);
    } else {
      this.user = new User({});
      this.isBlock = false;
    }

    this.changePasswordDialogOptions.onClickConfirmButton = this.onConfirmChangePassword;
    this.loadDepartments();
    this.loadUserTypes();
  }

  /**
   * Получение списка отделений
   */
  loadDepartments() {
    this.userAccountsService.getDepartmentTree()
      .subscribe(departmentTree => (this.departmentTree = AngularTree.createNewArray(departmentTree)));

    this.userAccountsService.getDepartmentList()
      .subscribe(departmentList => {
        this.departmentList = departmentList;
        this.setDepartment();
      });
  }

  /**
   * Получение списка типов пользователя
   */
  loadUserTypes() {
    this.userAccountsService.getUserTypeList()
      .subscribe((res) => {
        this.userTypesList = res;
      });
  }

  /**
   * Получение данных пользователя
   * @param userAccountId {number} - идентификатор аккаунта пользователя
   */
  getUserInfoByAccountId(userAccountId: number) {
    this.userAccountsService.getUserInfoByAccountId(userAccountId).subscribe(userInfo => {
      this.user = userInfo;
      this.isBlock = userInfo.STATUS === 'BLOCKED';

      this.setDepartment();
    });
  }

  /**
   *
   */
  private setDepartment() {
    if (this.departmentList) {
      const department = this.departmentList.find((dep: any) => dep.hid === this.user.DEPARTMENTID);
      // устанавливам департамент пользователя
      this.departmentItem.next(department);
    }
  }

  /**
   * Создание или обновление пользователя
   */
  createOrUpdateUser(event, f: NgForm) {
    this.wasValidated = true;
    if (f.valid) {

      let departmentId: number;
      const departmentItem = this.departmentItem.getValue();
      if (departmentItem) {
        departmentId = _.isUndefined(departmentItem.id) ? +departmentItem.hid : +departmentItem.id;
      }

      if (!_.isUndefined(departmentId)) {
        this.user.DEPARTMENTID = departmentId;

        if (!this.user.ISCONCURRENT) {
          this.user.ISCONCURRENT = 1;
        }
        const params = {
          ...this.user
        };
        params.USERACCOUNTID = this.userAccountId;

        this.userAccountsService.saveAccount(params).subscribe(res => {
          if (!res.Error) {
            this.errorMessage = null;
            this.userAccountId = res.USERACCOUNTID;
            this.userId = res.USERID;
            this.session.context = {
              userAccountId: this.userAccountId,
              userId: this.userId
            };
          } else {
            this.errorMessage = res.Error;
          }
        });
      } else {
        this.logger.warn('Отутствует DEPARTMENTID');
      }
    } else {
      this.dialogService.alert(ModalType.Warning, 'Проверьте введенные данные');
    }
  }

  /**
   * Обработчик события нажатия на кнопку смены пароля
   */
  onChangePassword = async () => {
    this.changeDialog.dialog.show();
  };

  /**
   * Подтверждение смены пароля
   */
  onConfirmChangePassword = (currentPassword, newPassword, retPassword, f: NgForm) => {
    const modal = this.changeDialog;

    if (f.valid) {
      modal.isServiceInProgress = true;

      this.userAccountsService.changePassword(this.userAccountId, newPassword)
        .finally(() => {
          modal.isServiceInProgress = false;
        })
        .subscribe((res) => {
          if (!res.Error) {
            modal.dialog.close();
            modal.clear();
          } else {
            modal.serviceErrorMessage = res.Error;
          }
        });
    } else {
      this.dialogService.alert(ModalType.Error, 'Заполнены не все поля');
    }
  };
}
