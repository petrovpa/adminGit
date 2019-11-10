import { Component, OnInit } from '@angular/core';
import { LoginService } from '../services/login.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-logout',
  template: '<strong>Выходим...</strong>',
})
export class LogoutComponent implements OnInit {

  constructor(private loginService: LoginService,
              private router: Router) {
  }

  ngOnInit() {
    this.loginService.logout();
    this.router.navigate(['/login']);
  }
}
