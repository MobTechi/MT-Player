import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { IonicModule } from '@ionic/angular';

import { BottomTabsPageRoutingModule } from './bottom-tabs-routing.module';

import { BottomTabsPage } from './bottom-tabs.page';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    IonicModule,
    BottomTabsPageRoutingModule
  ],
  declarations: [BottomTabsPage]
})
export class BottomTabsPageModule {}
