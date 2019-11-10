import { AfterContentInit, Component, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router, NavigationEnd } from '@angular/router';
import { Title } from '@angular/platform-browser';
import { Logger } from '@frontend/logger';
import { LocationStrategy } from '@angular/common';
import 'rxjs/add/operator/filter';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/mergeMap';

import { NavigationService } from '@app/services/navigation.service';
import { NavigationStart } from '@angular/router';
import { EventsService } from '@core/services/common';

@Component({
  selector: 'app',
  templateUrl: './app.component.html',
  styleUrls: [
    './app.component.scss'
  ]
})
export class AppComponent implements OnInit, AfterContentInit {
  constructor(private activatedRoute: ActivatedRoute,
              private logger: Logger,
              private router: Router,
              private titleService: Title,
              private location: LocationStrategy,
              private eventsService: EventsService,
              private navigationService: NavigationService) {
  }

  ngOnInit() {
    // навигация
    this.router.events
      .filter((event) => event instanceof NavigationEnd)
      .map(() => this.activatedRoute)
      .map((route) => {
        while (route.firstChild) route = route.firstChild;
        return route;
      })
      .filter((route) => route.outlet === 'primary')
      .mergeMap((route) => route.data)
      .subscribe((event) => this.titleService.setTitle(event['title']));

    // назад
    this.location.onPopState(() => {
      // set isBackButtonClicked to true.
      this.navigationService.backClicked = true;
      return false;
    });
  }

  ngAfterContentInit(): any {
    this.router.events
      .subscribe((event: any) => {
        if (event instanceof NavigationStart) {
          // загрузка bundle
          this.eventsService.publish('request:start', null, 1);
        } else {
          if (event instanceof NavigationEnd) {
            // загрузка bundle завершена
            this.eventsService.publish('request:finished', null, 0);
            setTimeout(() => {
              this.scrollTop();
            }, 50);
          }
        }
      });

    this.router.events
      .filter(event => event instanceof NavigationEnd)
      .subscribe((event: any) => setTimeout(() => {
        this.scrollTop();
      }, 150));
  }

  /**
   * Наверх страницы
   */
  scrollTop() {
    this.logger.info('Window scroll top');
    // FIXME: change to inject window
    window.scroll(0, 0);
  }
}
