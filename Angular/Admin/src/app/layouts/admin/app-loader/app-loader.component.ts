import { Component, Inject, OnInit } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { EventsService } from '@core/services/common';

@Component({
  selector: 'app-loader',
  templateUrl: './app-loader.component.html',
  styleUrls: ['./app-loader.component.scss']
})
export class AppLoaderComponent implements OnInit {
  isShow: boolean = false;

  constructor(private eventsService: EventsService,
              @Inject(DOCUMENT) private document: any) {
  }

  ngOnInit() {
    const $this = this;

    // активность запросов
    this.eventsService.subscribe('request:start', (countRequest) => {
      $this.isShow = !!countRequest;
      $this.setCursor('progress');
    });

    // окончание запросов
    this.eventsService.subscribe('request:finished', (countRequest) => {
      $this.isShow = !!countRequest;
      $this.setCursor('auto');
    });
  }

  /**
   * Устанавливаем стиль курсора
   *
   * @param {string} style
   */
  setCursor(style: string) {
    this.document.body.style.cursor = style;
  }
}
