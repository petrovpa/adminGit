import { Component, Injector, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { IServiceCaller } from '@core/components/form-control';
import { ModalType } from '@core/components/modal-dialog';
import { StringHelper } from '@core/shared/common';

import { ActiveDirectoryUser } from '@app/classes/active-directory-user.class';
import { UserAccountsService } from '../services/user-accounts.service';
import { UserBaseAccountComponent } from '../base/user-base-account.component';
import { ROUTE_USER_ACCOUNTS } from '@app/shared/routes.const';

@Component({
  selector: 'app-user-account-active-directory',
  templateUrl: './active-directory.component.html',
  styleUrls: ['./active-directory.component.scss']
})
export class ActiveDirectoryComponent extends UserBaseAccountComponent implements OnInit {

  adServiceCaller: ActiveDirectoryServiceCaller;

  selectedAdAccount: string;
  // выбранный аккаунт AD из поля "Поиск учетной записи AD"
  activeDirectoryUser: ActiveDirectoryUser;
  // список аккаунтов AD для поля "Поиск учетной записи AD"
  activeDirectoryUserList: ActiveDirectoryUser[] = [];
  // значение поля "Связанная учетная запись AD" (формат: ADUSERLOGIN (ADUSERPRINCIPALNAME))
  viewAdUserName: string;

  constructor(protected injector: Injector) {
    super(injector);
    this.adServiceCaller = new ActiveDirectoryServiceCaller(this.userAccountsService);
  }

  ngOnInit() {
    this.activeDirectoryUser = new ActiveDirectoryUser({});

    // есть контекст?
    if (this.userAccountId) {
      // уже загружен?
      if (!this.user.USERID) {
        this.userAccountsService.getUserInfoByAccountId(this.userAccountId)
          .subscribe((userInfo) => {
            this.user = userInfo;
            this.isBlock = userInfo.STATUS === 'BLOCKED';

            if (this.user) {
              this.setViewAdUserName(new ActiveDirectoryUser(this.user));
            }
          });
      } else {
        if (this.user) {
          this.setViewAdUserName(new ActiveDirectoryUser(this.user));
        }
      }
    } else {
      this.routingState.navigate(ROUTE_USER_ACCOUNTS);
    }
  }

  /**
   * Связать учетные записи ActiveDirectory и core_useraccount
   */
  bindAccounts() {
    const account: any = {
      ...this.user,
      ADUSERPRINCIPALNAME: this.activeDirectoryUser.ADUSERPRINCIPALNAME,
      ADUSERLOGIN: this.activeDirectoryUser.ADUSERLOGIN,
      USERACCOUNTID: this.userAccountId,
      // LOGIN: this.user.LOGIN,
      // USERTYPE: this.user.USERTYPE,
      // USERID: this.user.USERID,
      // STATUS: this.user.STATUS
    };

    this.userAccountsService.saveAccount(account).subscribe((res) => {
      if (res.Error) {
        this.dialogService.alert(ModalType.Error, StringHelper.nl2br(res.Error));
      } else {
        this.dialogService.alert(ModalType.Success, 'Пользователь успешно связан с учетной записью Active Directory');
      }
    });
  }

  /**
   * Удалить связь ActiveDirectory и core_useraccount
   */
  removeAccountBinding() {
    const account: any = {
      ...this.user,
      USERACCOUNTID: this.userAccountId,
      // LOGIN: this.user.LOGIN,
      // USERTYPE: this.user.USERTYPE,
      // USERID: this.user.USERID,
      // STATUS: this.user.STATUS,
      ADUSERLOGIN: null,
      ADUSERPRINCIPALNAME: null
    };

    this.userAccountsService.saveAccount(account).subscribe((res) => {
      if (res.Error) {
        this.dialogService.alert(ModalType.Error, StringHelper.nl2br(res.Error));
      } else {
        this.dialogService.alert(ModalType.Success, 'Связь с пользователем Active Directory успешно удалена');
      }
    });

    this.activeDirectoryUserSelect(new ActiveDirectoryUser({}));
  }

  /**
   * Установка отображаемого на интерфейсе значения для поля "Связанная учетная запись AD"
   * @param adAccount - аккаунт AD
   */
  private setViewAdUserName(adAccount: ActiveDirectoryUser) {
    if (adAccount && (adAccount.ADUSERLOGIN || adAccount.ADUSERPRINCIPALNAME)) {
      this.viewAdUserName = `${adAccount.ADUSERLOGIN} (${adAccount.ADUSERPRINCIPALNAME})`;
    } else {
      this.viewAdUserName = '';
    }
  }

  /**
   * Обработчик события "Выбор связанного аккаунта AD"
   * @param adAccount выбранный аккаунт AD
   */
  activeDirectoryUserSelect(adAccount: ActiveDirectoryUser) {
    this.activeDirectoryUser = adAccount;
    this.setViewAdUserName(adAccount);
  }

  /**
   * Функция поиска аккаунтов AD
   * @param term строка поиска
   */
  search = (term: string): Observable<ActiveDirectoryUser[]> =>
    this.userAccountsService.getActiveDirectoryUserList(term)
      .map(res => {
        return res.map(item => new ActiveDirectoryUser(item));
      });

}

class ActiveDirectoryServiceCaller implements IServiceCaller {

  constructor(private rest: UserAccountsService) {
  }

  call(inputStr: string): Observable<any> {
    if (!inputStr) {
      return Observable.of([]);
    }
    const callParams = { hideLoader: true }; // скрывать спиннер загрузки
    return this.rest.getActiveDirectoryUserList(inputStr, callParams);
  }

}
