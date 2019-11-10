import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserShortInfoComponent } from './user-short-info.component';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    UserShortInfoComponent
  ],
  exports: [
    UserShortInfoComponent
  ]
})
export class UserShortInfoModule { }
