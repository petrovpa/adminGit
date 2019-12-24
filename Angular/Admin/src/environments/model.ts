import { NgModuleRef } from '@angular/core';

export interface Environment {
  production: boolean;
  baseUrl: string;
  logLevel: number;
  restPath: string;
  fileUploadPath: string;
  fileDownloadPath: string;
  controlShowError: boolean;

  ENV_PROVIDERS: any;
  decorateModuleRef(modRef: NgModuleRef<any>): NgModuleRef<any>;
}
