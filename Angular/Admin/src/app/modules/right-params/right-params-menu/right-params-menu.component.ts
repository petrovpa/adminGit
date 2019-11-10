import { Component, OnInit } from '@angular/core';
import { RightParamsMenuBaseComponent } from '../base/right-params-menu-base.component';
import { RightParamsService } from '../services/right-params.service';
import { AngularTree } from '@app/classes/angular-tree.class';

@Component({
  selector: 'right-params-menu',
  templateUrl: './right-params-menu.component.html',
  styleUrls: ['./right-params-menu.component.scss']
})
export class RightParamsMenuComponent extends RightParamsMenuBaseComponent implements OnInit {
  /**
   * Список меню
   */
  protected menuTree;
  /**
   * Выбранный эелемент
   */
  protected menu;

  constructor(private rest: RightParamsService) {
    super();
  }

  ngOnInit() {
    this.getMenuTree();
  }

  /**
   * Получение дерева меню
   */
  private getMenuTree() {
    return this.rest.getMenuTree()
      .subscribe((res) => {
        if (!res.Error) {
          this.menuTree = AngularTree.createNewArray(res);
        }
      });
  }

  /**
   * Обработчик выбора меню
   * @param menu {any} - выбранный пункт
   */
  protected selectMenu(menu: any) {
    this.menu = menu;
  }

  /**
   * Добавить
   */
  add() {
    if (this.menu) {
      this.onComplete.emit({
        VALUE: this.menu.name,
        VKEY: this.menu.id
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
