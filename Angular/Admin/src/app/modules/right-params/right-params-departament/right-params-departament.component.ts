import { Component, OnInit } from '@angular/core';
import { AngularTree } from '@app/classes/angular-tree.class';

import { RightParamsMenuBaseComponent } from '../base/right-params-menu-base.component';
import { RightParamsService } from '../services/right-params.service';

@Component({
  selector: 'right-params-departament',
  templateUrl: './right-params-departament.component.html',
  styleUrls: ['./right-params-departament.component.scss'],
})
export class RightParamsDepartamentComponent extends RightParamsMenuBaseComponent implements OnInit {
  /**
   * Список департаментов
   */
  protected departmentTree;
  /**
   * Выбранный эелемент
   */
  protected department;

  constructor(private rest: RightParamsService) {
    super();
  }

  ngOnInit() {
    this.getDepartmentTree();
  }

  /**
   * Получение дерева департаментов
   */
  private getDepartmentTree() {
    return this.rest.getDepartmentTree()
      .subscribe((depTree) => {
        if (!depTree.Error) {
          this.departmentTree = AngularTree.createNewArray(depTree);
        }
      });
  }

  /**
   * Обработчик выбора департамента
   * @param dep {any} - выбранный депертамент
   */
  protected selectDepartment(dep: any) {
    this.department = dep;
  }

  /**
   * Добавить
   */
  add() {
    if (this.department) {
      this.onComplete.emit({
        VALUE: this.department.name,
        VKEY: this.department.id
      });
    }
  }

  /**
   * Закрыть
   */
  close() {
    this.onClose.emit();
  }
}
