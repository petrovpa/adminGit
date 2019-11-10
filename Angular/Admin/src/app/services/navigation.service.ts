import { Injectable } from '@angular/core';

@Injectable()
export class NavigationService {
  /**
   * Кнопка назад
   */
  private _backClicked: boolean;
  get backClicked(): boolean {
    return this._backClicked;
  }

  set backClicked(value: boolean) {
    this._backClicked = value;
  }

  constructor() {
  }
}
