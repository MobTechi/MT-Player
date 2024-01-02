import { NgModule } from '@angular/core';
import { PreloadAllModules, RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'bottom-tabs',
    pathMatch: 'full'
  },
  {
    path: 'bottom-tabs',
    loadChildren: () => import('./bottom-tabs/bottom-tabs.module').then( m => m.BottomTabsPageModule)
  }
];
@NgModule({
  imports: [
    RouterModule.forRoot(routes, { preloadingStrategy: PreloadAllModules })
  ],
  exports: [RouterModule]
})
export class AppRoutingModule {}
