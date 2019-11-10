import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RoutingStateService, SessionService, RestService } from '@core/services/common';

@Component({
  selector: 'app-header',
  templateUrl: './app-header.component.html',
  styleUrls: ['./app-header.component.scss']
})
export class AppHeaderComponent implements OnInit {

  /**
   * Имя пользователя
   */
  protected fio: string;

  constructor(private sessionService: SessionService,
              private routingState: RoutingStateService,
              private rest: RestService) {
  }

  ngOnInit() {
    this.fio = this.sessionService.userFIO;
  }

  /**
   * Переходим на главную
   */
  openMain() {
    this.routingState.navigate(['']);
  }

  /**
   * Выход
   */
  logout() {
    this.rest.formCall("login/dsB2BLogOut", {}).subscribe((res:any) =>
    {

    })
    this.routingState.navigate(['/logout']);
  }
}
