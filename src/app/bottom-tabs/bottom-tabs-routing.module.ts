import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { BottomTabsPage } from './bottom-tabs.page';
import { HomePageComponent } from './home-page/home-page.component';
import { LibraryPageComponent } from './library-page/library-page.component';
import { SettingsPageComponent } from './settings-page/settings-page.component';

const routes: Routes = [
  {
    path: '',
    component: BottomTabsPage,
    children: [
      { path: 'home', component: HomePageComponent },
      { path: 'library', component: LibraryPageComponent },
      { path: 'settings', component: SettingsPageComponent },
      { path: '', redirectTo: '/bottom-tabs/home', pathMatch: 'full' },
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class BottomTabsPageRoutingModule {}
