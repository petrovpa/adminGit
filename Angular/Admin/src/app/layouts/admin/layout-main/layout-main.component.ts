import { Component, OnInit, ViewChild, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';

import 'rxjs/add/operator/take';
import 'rxjs/add/operator/takeUntil';

import { ModalAlertComponent } from '@core/components/modal-dialog';
import { EventsService } from '@core/services/common';
import { EVENTS_MESSAGE_ERROR, EVENTS_REQUEST_SESSION_EXPIRED, EVENTS_REQUEST_ERROR } from '@core/shared/consts';

@Component({
  templateUrl: './layout-main.component.html',
  styleUrls: ['./layout-main.component.scss'],

})
export class LayoutMainComponent implements OnInit, OnDestroy {
  /**
   * Окно с ошибкой
   */
  @ViewChild('alert') private modalAlert: ModalAlertComponent;
  /**
   * Статус окна
   * @type {boolean}
   */
  private showAlert: boolean = false;

  constructor(private eventsService: EventsService,
              private router: Router) {
  }

  ngOnInit() {
    // сессия истекла - выходим
    this.eventsService.subscribe(EVENTS_REQUEST_SESSION_EXPIRED, () => {
      this.router.navigate(['/login'], {
        queryParams: {
          error: 'expired'
        }
      });
    });

    // вывод ошибки
    this.eventsService.subscribe(EVENTS_MESSAGE_ERROR, (message: string) => {
      this.showErrorDialog(message);
    });

    this.eventsService.subscribe(EVENTS_REQUEST_ERROR, (message: string) => {
      this.showErrorDialog(message);
    });
  }

  /**
   * Показываем диалоговое окно
   * @param {string} message
   */
  showErrorDialog(message: string) {
    if (!this.showAlert) {
      this.showAlert = true;
      this.modalAlert.setMessage(message);
      this.modalAlert.show();
    }
  }

  /**
   * Закрыли окно об ошибке
   */
  onAlertConfirmed() {
    this.showAlert = false;
  }

  ngOnDestroy() {
    this.eventsService.unsubscribe(EVENTS_REQUEST_ERROR);
    this.eventsService.unsubscribe(EVENTS_MESSAGE_ERROR);
    this.eventsService.unsubscribe(EVENTS_REQUEST_SESSION_EXPIRED);
  }
}
