import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'right-params-base',
  template: ''
})
export class RightParamsMenuBaseComponent {
  /**
   * Закрыть
   */
  @Output() onClose = new EventEmitter();
  /**
   * Добавить
   */
  @Output() onComplete = new EventEmitter();

  constructor() {
  }
}
