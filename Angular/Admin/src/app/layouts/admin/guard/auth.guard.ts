import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, CanActivateChild } from '@angular/router';
import { LoginService } from '@app/pages/auth/services/login.service';
import { environment } from 'environments/environment';

@Injectable()
export class AuthGuard implements CanActivate, CanActivateChild {
  constructor(private loginService: LoginService, private _router: Router) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const url: string = state.url;
    return this.checkLogin(url);
  }

  canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    return this.canActivate(route, state);
  }

  checkLogin(url: string): boolean {
    if (this.loginService.isLoggedIn()) {
      return true;
    }

    // Store the attempted URL for redirecting
    this.loginService.redirectURL = url;

    // Navigate to the login page with extras
    this._router.navigate(['/login'], { queryParams: { r: url } });
    return false;
  }
}
