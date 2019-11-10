import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { FormControlLabelModule } from '@core/components/form-control-label';
import { FormControlModule } from '@core/components/form-control';

import { AutoimportOptionsComponent } from './autoimport-options/autoimport-options.component';
import { AutoimportOptionsService } from './autoimport-options.service';
import { AutoimportOptionsRoutingModule } from './autoimport-options-routing.module';


@NgModule({
  imports: [
  CommonModule,
  FormsModule,
  FormControlModule,
  FormControlLabelModule,
  AutoimportOptionsRoutingModule
  ],
  providers: [
    AutoimportOptionsService
  ],
  declarations: [AutoimportOptionsComponent]
})

export class AutoimportOptionsModule {}
