import { Component, Injector, OnInit } from '@angular/core';

import { BaseFormComponent } from '@core/shared/base-form.component';
import { ModalType } from '@core/components/modal-dialog';
import { MESSAGE_ERROR_SAVE } from '@core/shared/consts/messages.const';
import { isFunction } from '@core/shared/common';
import { ROUTE_ROLE_MANAGE_LIST } from '@app/shared/routes.const';

import { RoleManageService } from '../services/role-manage.service';
import { Role } from '@app/classes/role.class';

@Component({
  selector: 'app-role-manage-edit',
  templateUrl: './role-manage-edit.component.html',
  styleUrls: ['./role-manage-edit.component.scss']
})
export class RoleManageEditComponent extends BaseFormComponent implements OnInit {
  /**
   * Текущая роль
   */
  protected role: Role = new Role();
  /**
   * Редактирование
   */
  protected isEdit: boolean = false;
  /**
   * Фильтры
   */
  protected filterSysName = /([^A-Za-z0-9])/;

  constructor(protected injector: Injector,
              private roleManage: RoleManageService) {
    super(injector);
  }

  /**
   * Инициализация
   */
  ngOnInit() {
    const roleId = this.session.getContext('roleId');

    // это не создание?
    if (roleId) {
      this.isEdit = true;

      // роль ранее загружена?
      if (!this.role || this.role.ROLEID !== roleId) {
        this.loadRole(roleId);
      } else {
        this.routingState.navigate(ROUTE_ROLE_MANAGE_LIST);
      }
    } else {
      // создаем новый объект
      this.role = new Role();
      this.roleManage.role = this.role;
      // создание новой записи
      this.roleManage.isNewRecord = true;
    }
  }

  /**
   * Загружаем роль
   * @param roleId
   */
  private loadRole(roleId: number) {
    this.roleManage.roleLoad(roleId)
      .subscribe((res) => {
        if (!res.Error) {
          this.role = res;
        } else {
          this.isFormDisabled = true;
        }
      });
  }

  /**
   * Отправка формы
   */
  submitPage(f, callback) {
    this.roleManage.roleSave(this.role)
      .finally(() => {
        this.isServiceInProgress = false;
      })
      .subscribe((res) => {
        if (!res.Error) {
          this.role.ROLEID = res.ROLEID;
          this.isEdit = true;
          this.session.context = { roleId: res.ROLEID };
          if (isFunction(callback)) {
            callback();
          }

        } else {
          this.dialogService.alert(ModalType.Error, MESSAGE_ERROR_SAVE);
        }
      });
  }
}
