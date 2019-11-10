import { Component, Injector, OnInit } from '@angular/core';
import { BaseFormComponent } from '@core/shared/base-form.component';
import { RoleManageService } from '../services/role-manage.service';
import { Role } from '@app/classes/role.class';
import { ROUTE_ROLE_MANAGE_LIST } from '@app/shared/routes.const';

export class RoleManageBaseComponent extends BaseFormComponent implements OnInit {
  /**
   * Текущая роль
   */
  protected role: Role = new Role();

  /**
   * Сервис
   */
  protected roleManage: RoleManageService;

  constructor(protected injector: Injector) {
    super(injector);

    this.roleManage = this.injector.get(RoleManageService);
  }

  /**
   * Инициализация
   */
  ngOnInit() {
    const roleId = this.session.getContext('roleId');
    const role = this.roleManage.role;

    // роль определена?
    if (roleId) {
      // роль ранее загружена?
      if (!role || !role.ROLEID) {
        // грузим
        this.loadRole(roleId);
      } else if (role.ROLEID === parseInt(roleId)) {
        this.role = role;
      } else {
        // ошибочный контекст
        this.routingState.navigate(ROUTE_ROLE_MANAGE_LIST);
      }
    } else {
      // ошибочный контекст
      this.routingState.navigate(ROUTE_ROLE_MANAGE_LIST);
    }

    this.initPage();
  }

  /**
   * Загружаем роль
   * @param roleId
   */
  private loadRole(roleId: number) {
    this.roleManage.roleLoad(roleId)
      .subscribe((res) => {
        this.role = res;
      });
  }

  /**
   * Метод инициализации страницы для переопределения
   */
  protected initPage() {
    this.logger.warn('need override page');
  }
}
