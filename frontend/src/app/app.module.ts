import { AlertService } from './components/global-alert/alert.service';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BrowserModule } from '@angular/platform-browser';
import { CatalogViewComponent, FilterOnIdPipe } from './views/catalog-view/catalog-view.component';
import { ClarityModule } from '@clr/angular';
import { CurrentStateComponent } from './views/current-state/current-state.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { GlobalAlertComponent } from './components/global-alert/global-alert.component';
import { GlobalProgressComponent } from './components/global-progress/global-progress.component';
import { HeaderComponent } from './components/header/header.component';
import { HttpClientModule } from '@angular/common/http';
import { LayerControlModule } from '@dlr-eoc/layer-control';
import { LoginComponent } from './components/login/login.component';
import { MapOlModule } from '@dlr-eoc/map-ol';
import { MultiOrderViewComponent } from './views/multi-order-view/multi-order-view.component';
import { NavResizeDirectiveDirective } from './directives/nav-resize-directive/nav-resize-directive.directive';
import { NgModule } from '@angular/core';
import { NgxFileDropModule } from 'ngx-file-drop';
import { OrderDropComponent } from './components/order-drop/order-drop.component';
import { OrderViewComponent } from './views/order-view/order-view.component';
import { ProgressService } from './components/global-progress/progress.service';
import { PulsarService } from './services/pulsar.service';
import { UkisRoutingModule } from './app-routing.module';
import { VerticalNavResizeComponent } from './components/vertical-nav-resize/vertical-nav-resize.component';

// loading an icon from the "core set" now must be done manually
import { ClarityIcons, coreCollectionIcons, essentialCollectionIcons, travelCollectionIcons } from '@cds/core/icon';
ClarityIcons.addIcons(...[...coreCollectionIcons, ...essentialCollectionIcons, ...travelCollectionIcons]);

@NgModule({
  declarations: [
    AppComponent,
    CatalogViewComponent,
    CurrentStateComponent,
    FilterOnIdPipe,
    GlobalAlertComponent,
    GlobalProgressComponent,
    HeaderComponent,
    LoginComponent,
    MultiOrderViewComponent,
    NavResizeDirectiveDirective,
    OrderDropComponent,
    OrderViewComponent,
    VerticalNavResizeComponent,
  ],
  imports: [
    BrowserAnimationsModule,
    BrowserModule,
    ClarityModule,
    FormsModule,
    HttpClientModule,
    HttpClientModule,
    LayerControlModule,
    MapOlModule,
    NgxFileDropModule,
    ReactiveFormsModule,
    UkisRoutingModule,
  ],
  providers: [
    AlertService,
    ProgressService,
    PulsarService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
