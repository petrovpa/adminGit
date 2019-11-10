import { Component, OnInit } from '@angular/core';
import { NgModule } from '@angular/core';
import { NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RestService } from '@core/services/common';
import { ModalDialogService, ModalType } from '@core/components/modal-dialog';

@Component({
  selector: 'autoimport-options',
  templateUrl: './autoimport-options.component.html',
  styleUrls: ['./autoimport-options.component.scss']
})

export class AutoimportOptionsComponent implements OnInit {

  constructor(private rest: RestService,
              private dialogService: ModalDialogService) { }

  protected Settings = {
    TIMESTART: '',
    TIMEFINISH: '',
    CATALOG: '',
    MAIL: ''
  };

  protected wasValidated: boolean = false;

  ngOnInit() {
    this.rest.doCall('admrestgate/readAutoImportSetting', {}).subscribe((res:any) =>
    {
      if (!res.Error) {
        var timeParse = this.getCorrectDataTime(res["KMSB_AUTOIMPORT_WORKTIME"]);
        this.Settings.TIMESTART = timeParse[0];
        this.Settings.TIMEFINISH = timeParse[1];
        this.Settings.CATALOG = res["KMSB_COMMON_FOLDER"];
        this.Settings.MAIL = res["KMSB_IMPORTRESULT_EMAIL"];
      }
    });
  }

  // необходимо преобразовать время импорта для вывода на интерфейс
  getCorrectDataTime(time: string) {
      var timeFrom = time.substring(time.indexOf('(')+1, time.indexOf(')'));
      time = time.substring(time.indexOf(')')+1);
      var timeTo = time.substring(time.indexOf('(')+1, time.indexOf(')'));
      var timeParse = [timeFrom, timeTo];
      return(timeParse);
  };

  /**
   * Сохранение введённых данных в таблицу CORE_SETTING
   * @private
   */
  doSave(f: NgForm) {
    this.wasValidated = true;
    if (f.valid) {
      var timeFrom = this.Settings.TIMESTART;
      var timeTo = this.Settings.TIMEFINISH;
      var time = 'FROM(' + timeFrom + ')TO(' + timeTo + ')';
      var params = {
        'KMSB_AUTOIMPORT_WORKTIME': time,
        'KMSB_COMMON_FOLDER': this.Settings.CATALOG,
        'KMSB_IMPORTRESULT_EMAIL': this.Settings.MAIL
      };
      return this.rest.doCall('admrestgate/saveAutoImportSettings', params).subscribe();
    } else {
      this.dialogService.alert(ModalType.Warning, 'Проверьте введенные данные');
    }
  };
}
