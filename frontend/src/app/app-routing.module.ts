
import { NgModule } from '@angular/core';
import { Routes, RouterModule, PreloadAllModules } from '@angular/router';
import { LoginService } from './services/login.service';
import { CatalogViewComponent } from './views/catalog-view/catalog-view.component';
import { CurrentStateComponent } from './views/current-state/current-state.component';
import { OrderViewComponent } from './views/order-view/order-view.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'catalog',
    pathMatch: 'full'
  },
  {
    path: 'catalog',
    component: CatalogViewComponent,
    canActivate: [LoginService]
  },
  {
    path: 'order',
    component: OrderViewComponent,
    canActivate: [LoginService]
  },
  {
    path: 'state',
    component: CurrentStateComponent,
    canActivate: [LoginService]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { useHash: true, preloadingStrategy: PreloadAllModules, relativeLinkResolution: 'legacy' })],
  exports: [RouterModule]
})
export class UkisRoutingModule { }