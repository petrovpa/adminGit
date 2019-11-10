/* tslint:disable */
import { enableProdMode, NgModuleRef } from '@angular/core';
import { disableDebugTools } from '@angular/platform-browser';
import { Environment } from './model';


enableProdMode();

export const environment: Environment = {
  production: true,
  baseUrl: '/',
  logLevel: 3, // только Error, Warn, Info
  restPath: '/docker/life/admrestws/rest/',
  fileUploadPath: '/docker/life/bivsberlossws/rest/boxproperty-gate/b2bfileupload',
  fileDownloadPath: '/docker/life/bivsberlossws/b2bfileupload',

  /** Angular debug tools in the dev console
   * https://github.com/angular/angular/blob/86405345b781a9dc2438c0fbe3e9409245647019/TOOLS_JS.md
   * @param modRef
   * @return {any}
   */
  decorateModuleRef(modRef: NgModuleRef<any>) {
    disableDebugTools();
    return modRef;
  },
  ENV_PROVIDERS: [

  ]
};
