import { Component, Injector, OnInit, ViewChild } from '@angular/core';
import * as _ from 'lodash';
import { Observable } from 'rxjs/Observable';

import { BaseFormComponent } from '@core/shared/base-form.component';
import { CacheService } from '@core/services/cache';
import { ModalDialogComponent, ModalType } from '@core/components/modal-dialog';
import { isFunction, StringHelper } from '@core/shared/common';
import { MESSAGE_ERROR_SAVE } from '@core/shared/consts/messages.const';
import { JOURNAL_BUTTON_ADD, JOURNAL_BUTTON_REMOVE } from '@core/modules/journal';

import { CACHE_PROFILE_RIGHT_LIST, CACHE_RIGHT_METADATA } from '@app/shared/cache.const';
import { ManageRightService } from '../services/manage-right.service';
import { Right } from '@app/classes/right.class';

@Component({
  selector: 'app-manage-right-profile',
  templateUrl: './manage-right-profile.component.html',
  styleUrls: ['./manage-right-profile.component.scss']
})
export class ManageRightProfileComponent extends BaseFormComponent implements OnInit {
  /**
   * Журнал
   */
  @ViewChild('rightParamsTable') rightParamsTable;

  /**
   * Диалог
   */
  @ViewChild('addDialog') protected addDialog: ModalDialogComponent;

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
  /**
   * Список прав
   */
  protected rightList$: Observable<any>;
  /**
   * Параметры права
   */
  protected rightMetadata = [];
  /**
   * Выбранный элемент
   */
  protected rightSelectItem;
  /**
   * Параметры профильного права
   */
  protected rightParamsList = [];
  /**
   * кнопки таблицы ролей пользователя
   */
  protected rightParamsTableButtons = [
    JOURNAL_BUTTON_ADD,
    JOURNAL_BUTTON_REMOVE
  ];
  /**
   * признак загрузки списка
   */
  protected isRightParamsLoading: boolean;
  /**
   * Выбранный параметр
   */
  protected rightParamSelect;

  /**
   * Кеш сервис
   */
  protected cache: CacheService;
  protected manageRightService: ManageRightService;

  protected modalConfig = {
    clickOutsideToClose: true,
    styles: { 'width': '950px', 'height': '500px', 'text-align': 'left', 'justify-content': 'flex-start' },
    isModal: true,
    enterTransitionDuration: 400,
    leaveTransitionDuration: 400
  };

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

      // загружаем настройки параметров права
      this.loadRightParamMetadata();

    } else {
      // загружаем параметры права
      this.loadRightList();
    }

    // кнопка добавления параметра
    this.rightParamsTableButtons[0].onClick = this.paramAdd;
    this.rightParamsTableButtons[0].disabled = () => {
      return !this.rightId;
    };

    // кнопка удаления параметра
    this.rightParamsTableButtons[1].onClick = this.paramRemove;
  }

  /**
   * Список прав
   */
  private loadRightList() {
    this.rightList$ = /*this.cache.get(CACHE_PROFILE_RIGHT_LIST + '_' + this.rightObjectId, () => */
      this.manageRightService.rightAvailable(this.rightObjectId, 'profileRights', this.rightOwner)
      .map((list) => {
        this.rightSelectItem = list.find((item) => item.RIGHTID === this.rightId);

        return list;
      });
  }

  /**
   * Загрузка настроек параметров права
   */
  private loadRightParamMetadata() {
    this.cache.get(CACHE_RIGHT_METADATA + '_' + this.rightId, () => this.manageRightService.rightMetadataLoad(this.rightId))
      .subscribe((res) => {
        this.rightMetadata = res;

        // загружаем параметры права
        this.loadParamList();
      });
  }

  /**
   * Список параметров
   */
  private loadParamList() {
    this.manageRightService.rightParamLoad(this.rightObjectId, this.rightId, this.rightOwner)
      .subscribe((res) => {
        if (!res.Error) {
          // TODO: сделать для множества параметров
          const filterValues = res[0].FILTERVALUES;

          if (filterValues) {
            this.rightParamsList = filterValues;

            // this.rightParamsList.forEach((item) => {
            //   item.SYSNAME = this.rightMetadata[0]['SYSNAME'];
            // });
          }
        }
      });
  }

  /**
   * Добавить параметр
   */
  private paramAdd = () => {
    this.addDialog.show();
    return false;
  };

  /**
   * Удалить параметр
   */
  private paramRemove = () => {
    this.dialogService.confirm(ModalType.Warning, 'Вы действительно хотите удалить выбранный параметр?')
      .subscribe(() => {
          this.rightParamsList = this.rightParamsList.filter((item) => {
            return !_.isEqual(item, this.rightParamSelect);
          });
        },
        (err: any) => {
        }
      );
  };

  /**
   * Закрыть модальное окно
   */
  protected closeModal() {
    this.isServiceInProgress = false;
    this.addDialog.close();
  }

  /**
   * Добавить параметр список и закрыть модальное окно
   */
  protected appendParam($event) {
    this.rightParamsList.push($event);
    // обновляем массив
    this.rightParamsList = this.rightParamsList.slice();

    this.isServiceInProgress = false;
    this.addDialog.close();
  }

  /**
   * Отправка формы
   */
  submitPage(f, callback) {
    this.manageRightService.rightAdd(
      this.rightObjectId, this.rightId, this.rightMetadata[0]['SYSNAME'], this.rightParamsList, 'profileRights', this.rightOwner
    )
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

  /**
   * Выбранный элемент
   * @param $event
   */
  protected onRightSelect($event) {
    this.rightSelectItem = $event;
    if (this.rightSelectItem) {
      this.rightMetadata = this.rightSelectItem.METADATA;
    } else {
      this.rightMetadata = [];
    }
  }

  protected onSelectParam($event) {
    this.rightParamSelect = $event.selected[0];
  }
}
