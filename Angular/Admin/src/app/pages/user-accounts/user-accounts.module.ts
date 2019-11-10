import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { JournalModule } from '@core/modules/journal/src';

import { UserAccountsRoutingModule } from './user-accounts-routing.module';
import { UserAccountsComponent } from './user-accounts/user-accounts.component';
import { UserAccountsService } from '@app/pages/user-accounts-edit/services/user-accounts.service';

console.log('User accounts bundle loaded asynchronously');

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    UserAccountsRoutingModule,
    JournalModule
  ],
  declarations: [
    UserAccountsComponent
  ],
  providers: [
    UserAccountsService
  ]
})

export class UserAccountsModule { }
