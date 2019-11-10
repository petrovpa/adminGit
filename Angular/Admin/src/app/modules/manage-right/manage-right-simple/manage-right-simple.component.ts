import { Component, Injector, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';

import { Observable } from 'rxjs/Observable';

import { CacheService } from '@core/services/cache';
import { ModalType } from '@core/components/modal-dialog';
import { MESSAGE_ERROR_SAVE } from '@core/shared/consts/messages.const';
import { BaseFormComponent } from '@core/shared/base-form.component';

import { CACHE_RIGHT_LIST } from '@app/shared/cache.const';
import { ManageRightService } from '../services/manage-right.service';
import { Right } from '@app/classes/right.class';
import { StringHelper } from '@core/shared/common';

@Component({
  selector: 'app-manage-right-simple',
  templateUrl: './manage-right-simple.component.html',
  styleUrls: ['./manage-right-simple.component.scss']
})
export class ManageRightSimpleComponent extends BaseFormComponent implements OnInit {
  /**
   * Настройки права
   */
  protected rightId: number;
  protected rightObjectId: number;
  protected rightOwner: string;
  /**
   * Флаг редактирования, а не создания
   */
  protected isEdit: boolean = false;

  protected rightList$: Observable<any>;

  /**
   * Кеш сервис
   */
  protected cache: CacheService;
  protected manageRightService: ManageRightService;

  constructor(protected injector: Injector) {
    super(injector);

    this.cache = injector.get(CacheService);
    this.manageRightService = injector.get(ManageRightService);
  }

  ngOnInit() {
    this.rightObjectId = this.session.getContext('rightObjectId');
    this.rightOwner = this.session.getContext('rightOwner');
    if (this.rightObjectId) {
      this.initPage();
    } else {
      // ошибочный контекст
      this.navigateCancel();
    }
  }

  /**
   * Инициализация страницы
   */
  initPage() {
    this.rightId = this.session.getContext('rightId');
    if (this.rightId) {
      this.isEdit = true;

      // устанавливаем в список текущее право
      this.rightList$ = Observable.of([
        new Right({
          RIGHTNAME: this.session.getContext('rightName'),
          RIGHTSYSNAME: this.session.getContext('rightSysName'),
          RIGHTID: this.rightId
        })
      ]);
    } else {
      this.loadRightList();
    }
  }

  /**
   * Список прав
   */
  private loadRightList() {
    this.rightList$ = this.cache.get(CACHE_RIGHT_LIST + '_' + this.rightObjectId,
      () => this.manageRightService.rightAvailable(this.rightObjectId, 'rights', this.rightOwner));
  }

  /**
   * Отправка страницы
   */
  submitPage(f: NgForm) {
    this.manageRightService.rightAdd(this.rightObjectId, this.rightId, null, null, 'rights', this.rightOwner)
      .finally(() => {
        this.isServiceInProgress = false;
      })
      .subscribe((res) => {
        if (!res.Error) {
          this.navigateCancel();
        } else {
          this.dialogService.alert(ModalType.Error, res.Error ? StringHelper.nl2br(res.Error) : MESSAGE_ERROR_SAVE);
        }
      });
  }
}
