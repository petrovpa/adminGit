import { Injectable } from '@angular/core';
import { Logger } from '@frontend/logger';
//import { SettingsPassword } from '@app/classes/settings-password.class';
import { RestService } from '@core/services/common';

@Injectable()
export class AutoimportOptionsService {

  constructor(private logger: Logger,
              private rest: RestService) {
  }
}
