import { Inject, Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { SessionService } from '@core/services/common';
import { DOCUMENT } from '@angular/common';

@Injectable()
export class UserAccountEditGuard implements CanActivate {
  constructor(private session: SessionService,
              @Inject(DOCUMENT) private document: any) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (this.session.getContext('userAccountId')) {
      return true;
    } else {

      this.document.body.style.cursor = 'auto';
      return false;
    }
  }
}
